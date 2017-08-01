package com.views.bottleprogressbar.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.views.bottleprogressbar.R;
import com.views.bottleprogressbar.base.App;

/**
 * 自定义ProgressBar
 * Created by Administrator on 2017/7/26.
 */
public class BottleProgressBar extends ProgressBar {
    /**
     * 绘制贝塞尔圆形曲线的控制点与数据点计算常量
     */
    private static final float C = 0.551915024494f;
    /**
     * 默认瓶口和瓶身高度比例
     * */
    private final static float DEFAULT_PROPORTION =  0.4f;
    /**
     * 默认path圆角大小
     */
    private final static float DEFAULT_PAINT_CORNER = 0.5f;
    /**
     * 瓶身镜像角度
     */
    private final static float MIRROR_DEGREE = 180.0f;
    /**
     * 默认画笔粗细
     */
    private final int DEFAULT_PAINT_STROKE_WIDTH = dp2px(2);
    /**
     * 瓶口宽度一半
     */
    private final int DEFAULT_MOUTH_HALF_LINE = dp2px(4);
    /**
     * 瓶身画笔
     */
    Paint mPaint;
    /**
     * 水画笔
     */
    Paint mWaterPaint;
    Paint textPaint;

    PathMeasure mPathMeasure;
    /**
     * 控件宽高
     */
    int mWidth, mHeight;
    /**
     * 瓶身路径
     */
    Path mPath;
    Path innerCirclePath;
    Path wavePath;
    float mouth_half_value = DEFAULT_MOUTH_HALF_LINE;
    /**瓶口和瓶身高度比例*/
    float proportion = DEFAULT_PROPORTION;
    /**画笔圆角大小*/
    float paintCorner = DEFAULT_PAINT_CORNER;
    /**画笔圆角粗细*/
    int paintStroke = DEFAULT_PAINT_STROKE_WIDTH;
    /**
     * 开口还是闭口
     */
    boolean isOpen = false;
    /**
     * 是否显示进度百分比文字
     */
    boolean showPercentText = true;
    RectF waterRectF;
    RectF bottleneckWaterRectF = new RectF();
    int r;
    int r1;
    int startPx;
    int startPy;
    float endLineOneX;
    /**
     * 瓶颈高度
     */
    int bottleneckHeight;
    /**
     * 瓶身高度
     */
    int bottleHeight;
    float cubeStep;

    /**数据点*/
    int circleTopCenterX;
    int circleTopCenterY;
    int circleLeftCenterX;
    int circleLeftCenterY;
    int circleRightCenterX;
    int circleRightCenterY;
    int circleBottomCenterX;
    int circleBottomCenterY;

    /**数据点TEMP*/
    int circleTopCenterXTemp;
    int circleTopCenterYTemp;
    int circleLeftCenterXTemp;
    int circleLeftCenterYTemp;
    int circleRightCenterXTemp;
    int circleRightCenterYTemp;
    int circleBottomCenterXTemp;
    int circleBottomCenterYTemp;

    /**控制点*/
    float control2X;
    float control2Y;
    float control3X;
    float control3Y;
    float control4X;
    float control4Y;
    float control5X;
    float control5Y;

    /**控制点TEMP*/
    float control1XTemp;
    float control1YTemp;
    float control2XTemp;
    float control2YTemp;
    float control3XTemp;
    float control3YTemp;
    float control4XTemp;
    float control4YTemp;
    float control5XTemp;
    float control5YTemp;
    float control6XTemp;
    float control6YTemp;
    float control7XTemp;
    float control7YTemp;
    float control8XTemp;
    float control8YTemp;

    int bottleColor = Color.rgb(169, 108, 108);
    int waterColor = Color.rgb(159, 68, 72);
    int gradientWaterColor = Color.rgb(208, 119, 72);
    /**
     * 字体颜色
     */
    int textColor = Color.WHITE;
    /**
     * 字体大小
     */
    float textSize = dp2px(15);
    /**
     * 是否已经镜像
     */
    boolean isMirror = false;
    RadialGradient radialGradient;
    /**
     * 是否开启波动动画
     */
    boolean openFlash = true;
    /**
     * 波浪个数
     */
    int waveCount;
    /**
     * 浪的宽度
     */
    int waveWidth = 200;
    ValueAnimator animator;
    float move;
    int flashDuration = 1000;
    /**
     * 浪的高度
     */
    int waveHeight = 10;

    public BottleProgressBar(Context context) {
        this(context, null);
    }

    public BottleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initPaint();
        if(openFlash){
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * 初始化属性
     * @param attrs 属性集合
     */
    private void initAttrs(AttributeSet attrs){
        final TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.BottleProgressBar);
        isOpen = attributes.getBoolean(R.styleable.BottleProgressBar_isOpen, isOpen);
        showPercentText = attributes.getBoolean(R.styleable.BottleProgressBar_showPercentText, showPercentText);
        mouth_half_value = (int) attributes.getDimension(R.styleable.BottleProgressBar_bottleMouthHalf, DEFAULT_MOUTH_HALF_LINE);
        proportion = attributes.getFloat(R.styleable.BottleProgressBar_proportion, proportion);
        paintCorner = attributes.getFloat(R.styleable.BottleProgressBar_corner, paintCorner);
        paintStroke = (int) attributes.getDimension(R.styleable.BottleProgressBar_bottleThickness, paintStroke);
        bottleColor = attributes.getColor(R.styleable.BottleProgressBar_bottleColor, bottleColor);
        waterColor = attributes.getColor(R.styleable.BottleProgressBar_waterColor, waterColor);
        gradientWaterColor = attributes.getColor(R.styleable.BottleProgressBar_brightColor, gradientWaterColor);
        openFlash = attributes.getBoolean(R.styleable.BottleProgressBar_openFlash, openFlash);
        flashDuration = attributes.getInt(R.styleable.BottleProgressBar_flashDuration, flashDuration);
        waveWidth = attributes.getInt(R.styleable.BottleProgressBar_waveWidth, waveWidth);
        waveHeight = attributes.getInt(R.styleable.BottleProgressBar_waveHeight, waveHeight);
        attributes.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(bottleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(paintStroke);
        mPaint.setPathEffect(new CornerPathEffect(paintCorner));

        mPath = new Path();
        innerCirclePath = new Path();
        wavePath = new Path();
        mPathMeasure = new PathMeasure();

        mWaterPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mWaterPaint.setStyle(Paint.Style.FILL);
        mWaterPaint.setColor(waterColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthDimension = resolveWidth(widthMode, widthSize);
        int heightDimension = resolveHeight(heightMode, heightSize);

        int realValue = Math.max(widthDimension, heightDimension);
        setMeasuredDimension(realValue, realValue);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        startPx = mWidth / 2;
        startPy = paintStroke;
        endLineOneX = startPx - mouth_half_value;
        bottleneckHeight = (int)(mHeight * proportion);
        bottleHeight = mHeight - bottleneckHeight;

        waveCount = (int) Math.round(mWidth / waveWidth + 1.6);

        //圆外接矩形坐标
        int tempRectFTopX = (mWidth - (mHeight - bottleneckHeight - startPy)) / 2;
        int tempRectFTopY = bottleneckHeight;
        int tempRectFBottomX = tempRectFTopX + (mHeight - bottleneckHeight - startPy);
        int tempRectFBottomY = mHeight - startPy;
        if(waterRectF == null){
            waterRectF = new RectF(tempRectFTopX + paintStroke, tempRectFTopY + paintStroke, tempRectFBottomX - paintStroke, tempRectFBottomY - paintStroke);
        }
        r = (tempRectFBottomY - tempRectFTopY) / 2;
        cubeStep = r * C;

        //数据点
        circleTopCenterX = tempRectFTopX + r;
        circleTopCenterY = tempRectFTopY;
        circleLeftCenterX = tempRectFTopX;
        circleLeftCenterY = tempRectFTopY + r;
        circleRightCenterX = tempRectFTopX + r * 2;
        circleRightCenterY = circleLeftCenterY;
        circleBottomCenterX = circleTopCenterX + 2;
        circleBottomCenterY = tempRectFBottomY;

        //控制点
        control2X = circleTopCenterX - cubeStep;
        control2Y = circleTopCenterY;
        control3X = circleLeftCenterX;
        control3Y = circleLeftCenterY - cubeStep;
        control4X = circleLeftCenterX;
        control4Y = circleLeftCenterY + cubeStep;
        control5X = circleBottomCenterX - cubeStep;
        control5Y = circleBottomCenterY;

        r1 = r - paintStroke - 1;
        float tempCubeStep = r1 * C;
        circleTopCenterXTemp = circleTopCenterX;
        circleTopCenterYTemp = circleTopCenterY + paintStroke + 1;
        circleLeftCenterXTemp = circleLeftCenterX + paintStroke + 1;
        circleLeftCenterYTemp = circleLeftCenterY;
        circleRightCenterXTemp = circleRightCenterX - paintStroke - 1;
        circleRightCenterYTemp = circleRightCenterY;
        circleBottomCenterXTemp = circleBottomCenterX;
        circleBottomCenterYTemp = circleBottomCenterY - paintStroke - 1;

        control1XTemp = circleTopCenterXTemp  + tempCubeStep;
        control1YTemp = circleTopCenterYTemp;
        control2XTemp = circleTopCenterXTemp - tempCubeStep;
        control2YTemp = circleTopCenterYTemp;
        control3XTemp = circleLeftCenterXTemp;
        control3YTemp = circleLeftCenterYTemp - tempCubeStep;
        control4XTemp = circleLeftCenterXTemp;
        control4YTemp = circleLeftCenterYTemp + tempCubeStep;
        control5XTemp = circleBottomCenterXTemp - tempCubeStep;
        control5YTemp = circleBottomCenterYTemp;
        control6XTemp = circleBottomCenterXTemp + tempCubeStep;
        control6YTemp = circleBottomCenterYTemp;
        control7XTemp = circleRightCenterXTemp;
        control7YTemp = circleRightCenterYTemp + tempCubeStep;
        control8XTemp = circleRightCenterXTemp;
        control8YTemp = circleRightCenterYTemp - tempCubeStep;
    }

    /**
     * 解析控件宽度
     * @param mode 模式
     * @param size 大小
     * @return 宽度
     */
    private int resolveWidth(int mode, int size){
        int result = 0;
        switch (mode){
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = App.getInstance().getWidth() / 5;
                break;
        }
        return result;
    }

    /**
     * 解析控件高度
     * @param mode 模式
     * @param size 大小
     * @return 高度
     */
    private int resolveHeight(int mode, int size){
        int result = 0;
        switch (mode){
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = App.getInstance().getWidth() / 5;
                break;
        }
        return result;
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //画瓶身
        if(!isOpen){
            mPath.moveTo(startPx, startPy);
            mPath.lineTo(endLineOneX, startPy);
        } else {
            mPath.moveTo(endLineOneX, startPy);
        }
        mPath.lineTo(endLineOneX, bottleneckHeight);
        mPath.cubicTo(control2X, control2Y, control3X, control3Y, circleLeftCenterX, circleLeftCenterY);
        mPath.cubicTo(control4X, control4Y, control5X, control5Y, circleBottomCenterX, circleBottomCenterY);
        mPathMeasure.setPath(mPath, false);
        float[] points = new float[2];
        float pathLength = mPathMeasure.getLength();
        if(!isMirror){
            mirrorPath(mPath);
            isMirror = true;
        }
        canvas.drawPath(mPath, mPaint);

        //画进度
        float radio = getProgress() * 1.0f / getMax();
        float rectArea = mouth_half_value * 2 * bottleneckHeight;
        double circleArea = Math.PI * r * r;
        double p = circleArea / (circleArea + rectArea);
        float threshold = Float.valueOf(String.format("%.2f", p));
        float pathLengthRadio = 1 - ((radio - 1) == 0 ? radio : radio * (mHeight - mouth_half_value) / pathLength);
        mPathMeasure.getPosTan(pathLengthRadio * pathLength, points, null);
        if (radialGradient == null){
            radialGradient = new RadialGradient(waterRectF.centerX(), waterRectF.centerY(), waterRectF.left, gradientWaterColor, waterColor, Shader.TileMode.CLAMP);
        }
        mWaterPaint.setShader(radialGradient);
        if(!openFlash){
            //下面这块代码可以用clipPath限定范围来简单实现，这里已经写好了就不改了
            float startAngle;
            float endAngle;
            if (points[1] > circleLeftCenterY) {
                startAngle = (int) (Math.toDegrees((Math.atan2(points[1] - circleLeftCenterY, mWidth / 2 - points[0]))));
                endAngle = 180 - startAngle * 2;
                canvas.drawArc(waterRectF, startAngle, endAngle, false, mWaterPaint);
            } else if (points[1] == circleLeftCenterY) {
                startAngle = 0;
                endAngle = 180;
                canvas.drawArc(waterRectF, startAngle, endAngle, false, mWaterPaint);
            } else {
                if (points[1] < circleLeftCenterY) {
                    drawFullCircle(canvas, threshold, points, radio);
                }
            }
        }else{
            if(radio < threshold){
                wavePath.reset();
                innerCirclePath.reset();
                innerCirclePath.moveTo(circleTopCenterXTemp, circleTopCenterYTemp);
                innerCirclePath.cubicTo(control2XTemp, control2YTemp, control3XTemp, control3YTemp, circleLeftCenterXTemp, circleLeftCenterYTemp);
                innerCirclePath.cubicTo(control4XTemp, control4YTemp, control5XTemp, control5YTemp, circleBottomCenterXTemp, circleBottomCenterYTemp);
                innerCirclePath.cubicTo(control6XTemp, control6YTemp, control7XTemp, control7YTemp, circleRightCenterXTemp, circleRightCenterYTemp);
                innerCirclePath.cubicTo(control8XTemp, control8YTemp, control1XTemp, control1YTemp, circleTopCenterXTemp, circleTopCenterYTemp);
                canvas.clipPath(innerCirclePath, Region.Op.REPLACE);
                wavePath.moveTo(move + points[0] - waveWidth, points[1]);
                for (int i = 0; i < waveCount; i++) {
                    wavePath.quadTo(points[0] - waveWidth / 4 * 3 + (i * waveWidth) + move, points[1] - waveHeight, points[0] - waveWidth / 2 + (i * waveWidth) + move, points[1]);
                    wavePath.quadTo(points[0] - waveWidth / 4 + (i * waveWidth) + move, points[1] + waveHeight, points[0] + (i * waveWidth) + move, points[1]);
                }
                wavePath.lineTo(mWidth, mHeight);
                wavePath.lineTo(0, mHeight);
                wavePath.close();
                canvas.drawPath(wavePath, mWaterPaint);
                if(animator != null && animator.isPaused()){
                    animator.resume();
                }else{
                    startAnimation(waveWidth);
                }
            }else{
                if(animator != null && animator.isRunning()){
                    animator.pause();
                }
                drawFullCircle(canvas, threshold, points, radio);
            }
        }

        //画进度文字
        if(showPercentText && radio != 0){
            String txt = (int)(radio * 100) +"%";
            int startX = (int) (mWidth / 2 - textPaint.measureText(txt)/ 2);
            float textHeightHalf = ((textPaint.descent() + textPaint.ascent()) / 2);
            int startY = (int) (points[1] < circleLeftCenterY ? circleLeftCenterY - textHeightHalf :  points[1] - textHeightHalf > mHeight * 0.9 ? mHeight * 0.9 : points[1] - textHeightHalf);
            canvas.drawText(txt,startX, startY, textPaint);
        }
    }

    /**
     * 当圆弧大于180时
     * @param canvas 画布
     * @param threshold 阈值
     * @param points 圆上点坐标
     * @param radio 进度
     */
    private void drawFullCircle(Canvas canvas, float threshold, float[] points, float radio){
        float startAngle = -(int) (Math.toDegrees((Math.atan2(circleLeftCenterY - points[1], mWidth / 2 - points[0]))));
        float endAngle = 180 + (-startAngle * 2);
        canvas.drawArc(waterRectF, startAngle, endAngle, false, mWaterPaint);
        if (points[1] < circleTopCenterY) {
            int temp = (int) (100.0 - threshold * 100);
            float tempP = radio * 100 - threshold * 100;
            float tempLength = bottleneckHeight * 1.0f / temp;
            bottleneckWaterRectF.set(startPx - paintStroke, bottleneckHeight - tempP * tempLength + startPy, startPx + paintStroke, mWidth / 2);
            canvas.drawRect(bottleneckWaterRectF, mWaterPaint);
        }
    }

    /**
     * 开启动画
     * @param waveWidth 波浪宽度
     */
    private void startAnimation(float waveWidth) {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0, waveWidth);
            animator.setDuration(flashDuration);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    move = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animator.start();
        }
    }

    /**
     * 镜像瓶身
     * @param path 路径
     */
    private void mirrorPath(Path path){
        Camera camera = new Camera();
        Matrix matrix = new Matrix();
        camera.save();
        camera.rotateY(MIRROR_DEGREE);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-mWidth / 2, -mHeight / 2);
        matrix.postTranslate(mWidth / 2, mHeight / 2);
        Path rightPath = new Path();
        rightPath.addPath(path);
        path.addPath(rightPath, matrix);
    }

    protected int dp2px(int dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(animator != null){
            animator.cancel();
            animator = null;
        }
    }
}
