# BottleProgressBar
效果
-----------
###1
<br>
![bottleprogress_flash](https://github.com/workdawn/BottleProgressBar/tree/master/raw/bottleprogress_flash.png)

---------
###2
<br>
![bottleprogress_normal](https://github.com/workdawn/BottleProgressBar/tree/master/raw/bottleprogress_normal.png)

------
<br>

说明
-------
支持的属性如下
```
<!-- 瓶口是开口还是闭口 -->
<attr name="isOpen" format="boolean"/>
<!-- 是否显示进度百分比 -->
<attr name="showPercentText" format="boolean"/>
<!-- 瓶口大小一半-->
<attr name="bottleMouthHalf" format="dimension"/>
<!-- 瓶口和瓶身高度比例-->
<attr name="proportion" format="float"/>
<!-- 拐角处弧度大小-->
<attr name="corner" format="float"/>
<!-- 瓶子厚度-->
<attr name="bottleThickness" format="dimension"/>
<!-- 瓶身颜色-->
<attr name="bottleColor" format="color"/>
<!-- 进度颜色-->
<attr name="waterColor" format="color"/>
<!-- 光源颜色-->
<attr name="brightColor" format="color"/>
<!-- 波浪个数-->
<attr name="waveCount" format="integer"/>
<!-- 波浪宽度-->
<attr name="waveWidth" format="integer"/>
<!-- 波浪的高度-->
<attr name="waveHeight" format="integer"/>
<!-- 是否开启波动动画-->
<attr name="openFlash" format="boolean"/>
<!-- 波浪速度 单位：毫秒-->
<attr name="flashDuration" format="integer"/>

```
使用
------
```
<com.views.bottleprogressbar.widget.BottleProgressBar
        android:id="@+id/progress"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:progress="50" />
        
```