package com.brzhang.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.tencent.campusx.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by brzhang on 16/6/23.
 * Description : 雷达搜索好友视图
 */
public class RadarView extends View {


    private String mImageUrl;
    private boolean threadIsRunning = false;
    private int     start           = 0;
    private RadarThread radarThread;

    private Paint  mPaintBitmap;//换中间图片的画笔
    private Paint  mPaintLine;//话圆圈的画笔
    private Paint  mPaintCircle; //画雷达的画笔
    private Matrix matrix; //重点在这了，通过矩阵变换，做出扫描效果。

    private Bitmap mBitmap; //用户自定义图片的圆角图
    private float mBitmapWidth    = 150;//中心图片默认的宽度，这里是px
    private float mCircleMargin   = 30;//距离宽度，默认是px
    private float mCircleWidth    = 2;//圆圈的画笔宽度，默认px
    private int   mCircleColor    = Color.RED; //最内层圈圈的颜色，下面依次
    private int   mCircleColorx   = Color.RED;
    private int   mCircleColorxx  = Color.RED;
    private int   mCircleColorxxx = Color.RED;
    private int   mScanColor      = Color.RED;

    private int mdefaultImage;//默认设置的中心图片
    private Bitmap mDefaultBitmap;//默认生成的中心图片的圆角图


    float mWidth;//自定义控件的宽度px
    float mHeight;//自定义控件的高度px


    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RadarView, defStyleAttr, 0);
        attributes.recycle();
        //注意这里拿到的dimen单位都会自动转换，比如你天的dp，实际上会转化为设备对应的px。
        mBitmapWidth = attributes.getDimension(R.styleable.RadarView_image_width, mBitmapWidth);
        mCircleMargin = attributes.getDimension(R.styleable.RadarView_circle_margin, mCircleMargin);
        mCircleWidth = attributes.getDimension(R.styleable.RadarView_circle_width, mCircleWidth);
        mCircleColor = attributes.getColor(R.styleable.RadarView_circle_color, mCircleColor);
        mCircleColorx = attributes.getColor(R.styleable.RadarView_circle_colorx, mCircleColor);
        mCircleColorxx = attributes.getColor(R.styleable.RadarView_circle_colorxx, mCircleColor);
        mCircleColorxxx = attributes.getColor(R.styleable.RadarView_circle_colorxxx, mCircleColor);
        mScanColor = attributes.getColor(R.styleable.RadarView_saner_color, mScanColor);
        mdefaultImage = attributes.getResourceId(R.styleable.RadarView_default_image,0);

        initView();
    }


    private void initView() {
        mPaintBitmap = new Paint();
        mPaintLine = new Paint();
        mPaintLine.setColor(mCircleColor);
        mPaintLine.setStrokeWidth(mCircleWidth);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintCircle = new Paint();
        mPaintCircle.setColor(mScanColor);
        //mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setAntiAlias(true);
        matrix = new Matrix();
        //start();
        if (mdefaultImage != 0) {//生成默认图片
            getBitmapFromGlide(mdefaultImage);
        }
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
        getBitmapFromGlide(this.mImageUrl);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2 - mBitmapWidth / 2, mHeight / 2 - mBitmapWidth / 2);
        if (mBitmap != null) {
            mPaintBitmap.reset();
           /* // 通过Bitmap和指定x,y方向的平铺方式构造出BitmapShader对象
            BitmapShader mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP);
            // 将BitmapShader设置到当前的Paint对象中
            mPaintBitmap.setShader(mBitmapShader);

            canvas.drawCircle(mBitmapWidth / 2, mBitmapWidth / 2, mBitmapWidth / 2, mPaintBitmap);*/

            canvas.drawBitmap(mBitmap,  0,  0, mPaintBitmap);

        }else if(mDefaultBitmap != null){
            mPaintBitmap.reset();
            // 通过Bitmap和指定x,y方向的平铺方式构造出BitmapShader对象
            BitmapShader mBitmapShader = new BitmapShader(mDefaultBitmap, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP);
            // 将BitmapShader设置到当前的Paint对象中
            mPaintBitmap.setShader(mBitmapShader);

            canvas.drawCircle(mBitmapWidth / 2, mBitmapWidth / 2, mBitmapWidth / 2, mPaintBitmap);
        }
        canvas.translate(mBitmapWidth / 2, mBitmapWidth / 2);
        // 画4个圆圈
        mPaintLine.setColor(mCircleColor);
        canvas.drawCircle(0, 0, mBitmapWidth / 2 + mCircleMargin, mPaintLine);
        mPaintLine.setColor(mCircleColorx);
        canvas.drawCircle(0, 0, mBitmapWidth / 2 + mCircleMargin * 2, mPaintLine);
        mPaintLine.setColor(mCircleColorxx);
        canvas.drawCircle(0, 0, mBitmapWidth / 2 + mCircleMargin * 3, mPaintLine);
        mPaintLine.setColor(mCircleColorxxx);
        canvas.drawCircle(0, 0, mBitmapWidth / 2 + mCircleMargin * 4, mPaintLine);

        // 绘制渐变圆

        Shader shader = new SweepGradient(0, 0, Color.TRANSPARENT, mScanColor);

        mPaintCircle.setShader(shader);

        //增加旋转动画，用矩阵帮忙

        canvas.concat(matrix);

        canvas.drawCircle(0, 0, mBitmapWidth / 2 + mCircleMargin * 4 + 100, mPaintCircle);

    }


    public void start() {
        if (threadIsRunning) {
            return;
        }
        threadIsRunning = true;
        radarThread = new RadarThread();
        radarThread.start();
    }

    public void stop() {
        threadIsRunning = false;
    }


    private void getBitmapFromGlide(int resource){
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.MarginLayoutParams((int)mBitmapWidth,(int)mBitmapWidth));
        Glide.with(getContext()).load(resource).asBitmap().centerCrop().transform(new RoundImageTransform(getContext())).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                mDefaultBitmap = resource;
            }
        });
    }
    private void getBitmapFromGlide(String url){
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.MarginLayoutParams((int)mBitmapWidth,(int)mBitmapWidth));
        Glide.with(getContext()).load(url).asBitmap().centerCrop().transform(new RoundImageTransform(getContext())).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                mBitmap = resource;
            }
        });
    }

    class RadarThread extends Thread {

        @Override
        public void run() {
            while (threadIsRunning) {
                RadarView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        start = start + 1;
                        matrix.setRotate(start, 0, 0); //因为我对画笔进行了平移，0，0表示绕圆的中心点转动
                        RadarView.this.invalidate();
                    }
                });
                try {
                    Thread.sleep(5);
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }


}