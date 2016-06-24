# raderView
####直接上图看xml preview 静态效果了：

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/1019822-a7e3c3d95b0f0b7b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


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
        mRadarView.setmImageUrl("http://p5.qhimg.com/t01ba4f7909f15de5fc.jpg");
    }
}
```

会启动一个线程去下载图片。下载好之后替换默认的图。
