package com.tencent.campusx.comm.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.tencent.campusx.R;
import com.tencent.campusx.comm.utils.RoundImageTransform;
import com.tencent.campusx.comm.utils.SystemUtils;


/**
 * Created by brzhang on 16/6/23.
 * Description : 水波纹雷达搜索好友视图
 */
public class WaterRadarView extends View {


    private String mImageUrl;
    private boolean threadIsRunning = false;
    private int     start           = 0;
    private RadarThread radarThread;

    private Paint  mPaintBitmap;//换中间图片的画笔
    private Paint  mPaintCircle; //画雷达的画笔
    private Matrix matrix; //重点在这了，通过矩阵变换，做出扫描效果。

    private LinearInterpolator     mLinearInterpolator;
    private DecelerateInterpolator mDecelerateInterpolator;

    private Bitmap mBitmap; //用户自定义图片的圆角图
    private float mBitmapWidth = 150;//中心图片默认的宽度，这里是px
    private float mCircleWidth = 2;//圆圈的画笔宽度，默认px
    private int   mCircleColor = Color.RED; //最内层圈圈的颜色，下面依次

    private int mCircleCount = 6;

    private int    mdefaultImage;//默认设置的中心图片
    private Bitmap mDefaultBitmap;//默认生成的中心图片的圆角图


    float mWidth;//自定义控件的宽度px
    float mHeight;//自定义控件的高度px

    private int screenWidth;


    public WaterRadarView(Context context) {
        this(context, null);
    }

    public WaterRadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterRadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RadarView, defStyleAttr, 0);
        //注意这里拿到的dimen单位都会自动转换，比如你天的dp，实际上会转化为设备对应的px。
        mBitmapWidth = attributes.getDimension(R.styleable.RadarView_image_width, mBitmapWidth);
        mCircleWidth = attributes.getDimension(R.styleable.RadarView_circle_width, mCircleWidth);
        mCircleColor = attributes.getColor(R.styleable.RadarView_circle_color, mCircleColor);
        mdefaultImage = attributes.getResourceId(R.styleable.RadarView_default_image, 0);
        attributes.recycle();
        mLinearInterpolator = new LinearInterpolator();
        mDecelerateInterpolator = new DecelerateInterpolator();
        screenWidth = SystemUtils.getScreenWidth(context);
        initView();
    }


    private void initView() {
        mPaintBitmap = new Paint();
        mPaintCircle = new Paint();
        mPaintCircle.setColor(mCircleColor);
        mPaintCircle.setAntiAlias(true);
        matrix = new Matrix();
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

        canvas.translate(mWidth / 2, mHeight / 2);
        // 画4个圆圈 ,一次最最大，次大，到最小

        drawCircleIntellgent(canvas, start % mCircleCount);

        canvas.translate(-mBitmapWidth / 2, -mBitmapWidth / 2);

        if (mBitmap != null) {
            mPaintBitmap.reset();
            canvas.drawBitmap(mBitmap, 0, 0, mPaintBitmap);

        } else if (mDefaultBitmap != null) {
            mPaintBitmap.reset();
            canvas.drawBitmap(mDefaultBitmap, 0, 0, mPaintBitmap);
        }

    }

    private void drawCircleIntellgent(Canvas canvas, int flag) {

        for (int i = 1; i < flag; i++) {
            mPaintCircle.setColor(mCircleColor);
            float alpha = Math.min(255f, mLinearInterpolator.getInterpolation((mCircleCount - i) * 1f / (mCircleCount - 1)) * 255);
            mPaintCircle.setAlpha((int) alpha);
            float radius = mDecelerateInterpolator.getInterpolation(i * 1f / (mCircleCount - 1)) * (mBitmapWidth / 2 + screenWidth / 2 - 150);
            canvas.drawCircle(0, 0, radius, mPaintCircle);
        }
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


    private void getBitmapFromGlide(int resource) {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.MarginLayoutParams((int) mBitmapWidth, (int) mBitmapWidth));
        Glide.with(getContext()).load(resource).asBitmap().centerCrop().transform(new RoundImageTransform(getContext())).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                mDefaultBitmap = resource;
            }
        });
    }

    private void getBitmapFromGlide(String url) {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.MarginLayoutParams((int) mBitmapWidth, (int) mBitmapWidth));
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
                WaterRadarView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        start = start + 1;
                        matrix.setRotate(start, 0, 0); //因为我对画笔进行了平移，0，0表示绕圆的中心点转动
                        WaterRadarView.this.invalidate();
                    }
                });
                try {
                    Thread.sleep(250);
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

}
