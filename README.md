# raderView
####直接上图看xml preview 静态效果了：

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/1019822-a7e3c3d95b0f0b7b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**最近更新了一个 水波纹的搜索效果**

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/1019822-689f935e935b4e6a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


###目前已经支持在xml中配置：
1. 设置默认中间图片
2. 每个圈圈颜色，圈圈的width
3. 以及扫描块的颜色。

###使用方法非常简单：
如果你想设置中间的图片 ，可以在activity中这么做：

```java
public class RecommentFriendsActivity extends BaseActivity {

    private RadarView mRadarView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomend_paoyou);
        mRadarView = (RadarView) findViewById(R.id.radar_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRadarView.start();
        mRadarView.setmImageUrl("http://p5.qhimg.com/t01ba4f7909f15de5fc.jpg");
    }
}
```

~~会启动一个线程去下载图片。下载好之后替换默认的图。~~

现在更新成使用glide加载图片，生成圆形图片，不在自己使用多线程下载图片，处理图片。代码如下：

```java
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
```
唯一的不足时这里new了一个没用的ImageView对象，如果你有更加好的方法，请告诉我。


