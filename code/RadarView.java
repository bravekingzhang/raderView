package com.brzhang.widget;

import android.content.Context;
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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

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

    private Paint  mPaintBitmap;
    private Paint  mPaintLine;
    private Paint  mPaintCircle;
    private Matrix matrix;

    private Bitmap mBitmap;
    private Bitmap mCircleBitmap;
    private float mBitmapWidth    = 150;
    private float mCircleMargin   = 30;
    private float mCircleWidth    = 2;
    private int   mCircleColor    = Color.RED;
    private int   mCircleColorx   = Color.RED;
    private int   mCircleColorxx  = Color.RED;
    private int   mCircleColorxxx = Color.RED;
    private int   mScanColor      = Color.RED;

    private Drawable mdefaultImage;
    private Bitmap mDefaultBitmap;
    private boolean  isBitMapLoadSuccess;


    float mWidth;
    float mHeight;


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
        mBitmapWidth = attributes.getDimension(R.styleable.RadarView_image_width, mBitmapWidth);
        mCircleMargin = attributes.getDimension(R.styleable.RadarView_circle_margin, mCircleMargin);
        mCircleWidth = attributes.getDimension(R.styleable.RadarView_circle_width, mCircleWidth);
        mCircleColor = attributes.getColor(R.styleable.RadarView_circle_color, mCircleColor);
        mCircleColorx = attributes.getColor(R.styleable.RadarView_circle_colorx, mCircleColor);
        mCircleColorxx = attributes.getColor(R.styleable.RadarView_circle_colorxx, mCircleColor);
        mCircleColorxxx = attributes.getColor(R.styleable.RadarView_circle_colorxxx, mCircleColor);
        mScanColor = attributes.getColor(R.styleable.RadarView_saner_color, mScanColor);
        mdefaultImage = attributes.getDrawable(R.styleable.RadarView_default_image);

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
        start();

        if (mdefaultImage != null) {//生成默认图片
            new DefaultCircleThread().start();
        }
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
        new ImageDownLoadThread().start();
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
        if (isBitMapLoadSuccess && mBitmap != null) {
            mPaintBitmap.reset();
            // 通过Bitmap和指定x,y方向的平铺方式构造出BitmapShader对象
            BitmapShader mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP);
            // 将BitmapShader设置到当前的Paint对象中
            mPaintBitmap.setShader(mBitmapShader);

            canvas.drawCircle(mBitmapWidth / 2, mBitmapWidth / 2, mBitmapWidth / 2, mPaintBitmap);

            //canvas.drawBitmap(mBitmap,  - mBitmapWidth / 2,  - mBitmapWidth / 2, mPaintBitmap);//透明绘制成 黑色，妈蛋

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


    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, (int) mBitmapWidth, (int) mBitmapWidth);
            drawable.draw(canvas);
            return bitmap;
        }
    }

    private Bitmap createCircleBitmap() {
        if (mCircleBitmap == null) {
            mCircleBitmap = Bitmap.createBitmap((int) mBitmapWidth, (int) mBitmapWidth,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mCircleBitmap);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mBitmapWidth / 2, mBitmapWidth / 2, mBitmapWidth / 2, paint);
        }
        return mCircleBitmap;
    }


    private static Bitmap bitmapFromUrl(String url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }

    class DefaultCircleThread extends Thread{
        @Override
        public void run() {
            super.run();
            mDefaultBitmap = drawableToBitmap(mdefaultImage);
        }
    }

    class ImageDownLoadThread extends Thread {

        @Override
        public void run() {
            if (!TextUtils.isEmpty(mImageUrl)) {
                try {
                    Bitmap bitmap = bitmapFromUrl(mImageUrl);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    // 计算缩放比例
                    float scaleWidth = mBitmapWidth / width;
                    float scaleHeight = mBitmapWidth / height;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    // 得到新的图片
                    mBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                    // 这种方式绘制透明区域被绘制成黑色。start
                    /*Bitmap maskBitmap = getCircleBitmap();
                    Canvas canvas = new Canvas(mBitmap);
                    bitmap.recycle();
                    mPaintBitmap.reset();
                    mPaintBitmap.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                    canvas.drawBitmap(maskBitmap, 0, 0, mPaintBitmap);
                    mPaintBitmap.setXfermode(null);*/
                    // 这种方式绘制透明区域被绘制成黑色。end
                    isBitMapLoadSuccess = true;
                } catch ( IOException e ) {
                    e.printStackTrace();
                    mBitmap = null;
                    isBitMapLoadSuccess = false;
                }
            }
        }
    }

    class RadarThread extends Thread {

        @Override
        public void run() {
            while (threadIsRunning) {
                RadarView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        start = start + 1;
                        matrix.setRotate(start, 0, 0); //因为我对画笔进行了平移
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
