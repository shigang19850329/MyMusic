package com.ixuea.courses.mymusic.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.DensityUtil;
import com.ixuea.courses.mymusic.util.ImageUtil;

/**
 * Created by kaka
 * On 2019/4/11
 */
public class RecordThumbView extends View implements ValueAnimator.AnimatorUpdateListener {
    /**
     * CD白背景的缩放比
     */
    public static final float CD_BG_SCALE = 1.333F;
    /**
     * 指针下面那条线高度
     */
    private static final int CD_THUMB_LINE_HEIGHT = 1;

    /**
     * 指针在停止时候的，旋转角度
     */
    private static final float THUMB_ROTATION_PAUSE = -25F;

    /**
     * 指针在播放时候旋转的角度
     */
    private static final float THUMB_ROTATION_PLAY = 0F;

    /**
     * 指针动画的播放时间
     */
    private static final long THUMB_DURATION = 300;

    /**
     * 指针宽度和1080的比值
     */
    private static final float THUMB_WIDTH_SCALE = 2.7F;

    /**
     * 指针的旋转角度
     * 默认，是不播放状态
     */
    private float thumbRotation = THUMB_ROTATION_PAUSE;

    /**
     * 绘制使用的画笔
     */
    private Paint paint;

    /**
     * 指针上面的那条线
     */
    private Drawable cdThumbLine;

    /**
     * 开始播放指针的移动动画
     */
    private ValueAnimator playThumbAnimator;

    /**
     * 停止播放指针的移动动画
     */
    private ValueAnimator pauseThumbAnimator;

    /**
     * CD白圈背景到顶部的比例
     */
    public static final float CD_BG_TOP_SCALE = 17.052F;

    /**
     * 指针上面那个原点的宽度，dp
     */
    private static final int THUMB_CIRCLE_WIDTH = 33;

    /**
     * 指针的高度，原图px
     */
    private static final int THUMB_HEIGHT = 138;

    /**
     * 指针绘制的坐标
     */
    private Point thumbPoint;

    /**
     * 指针旋转的坐标
     */
    private Point thumbRotationPoint;

    /**
     * 指针的bitmap
     */
    private Bitmap cdThumb;
    /**
     * 白圈
     */
    private Drawable cdBg;

    /**
     * 指针旋转使用的矩阵
     */
    private Matrix thumbMatrix = new Matrix();

    /**
     * 指针的宽度，px
     */
    private static final int THUMB_WIDTH = 92;

    public RecordThumbView(Context context) {
        super(context);
        init();
    }

    public RecordThumbView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordThumbView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RecordThumbView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //画笔
        paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);

        //最上面那条白线
        cdThumbLine = getResources().getDrawable(R.drawable.shape_cd_thumb_line);
        //设置背景图片
        cdBg = getResources().getDrawable(R.drawable.shape_cd_bg);
        //创建指针的属性动画
        playThumbAnimator = ValueAnimator.ofFloat(THUMB_ROTATION_PAUSE, THUMB_ROTATION_PLAY);
        playThumbAnimator.setDuration(THUMB_DURATION);
        playThumbAnimator.addUpdateListener(this);

        pauseThumbAnimator = ValueAnimator.ofFloat(THUMB_ROTATION_PLAY, THUMB_ROTATION_PAUSE);
        pauseThumbAnimator.setDuration(300);
        pauseThumbAnimator.addUpdateListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int widthHalf = measuredWidth / 2;

        int cdBgWidth = (int) (measuredWidth / CD_BG_SCALE);
        int cdBgWidthHalf = cdBgWidth / 2;

        //设置线
        cdThumbLine.setBounds(0, 0, measuredWidth, DensityUtil.dip2px(getContext(), CD_THUMB_LINE_HEIGHT));
        //cd背景
        int cdBgleft = widthHalf - cdBgWidthHalf;
        int cdBgTop = DensityUtil.dip2px(getContext(), measuredWidth / CD_BG_TOP_SCALE);
        cdBg.setBounds(cdBgleft, cdBgTop, cdBgleft + cdBgWidth, cdBgTop + cdBgWidth);

        //顶部白圈的宽度，这个宽度是截图量出来的。
        int topCircleWidth = DensityUtil.dip2px(getContext(), THUMB_CIRCLE_WIDTH);

        //设置背景，指针绘制定点坐标
        thumbPoint = new Point(measuredWidth / 2 - topCircleWidth / 2, -topCircleWidth / 2);
        thumbRotationPoint = new Point(measuredWidth / 2, 0);

        //指针，onMeasure方法会执行多次，如果不判断，会多次解码BitMap
        if (cdThumb == null) {
            initBitmap();
        }
    }

    private void initBitmap() {
        //获取Bitmap，需要用到View宽度的，所以要在onMeasure中
        int measureWidth = getMeasuredWidth();

        //Thumb的高度
        int imageHeight = (int) (measureWidth / THUMB_WIDTH_SCALE);

        double scale = imageHeight * 1.0 / DensityUtil.dip2px(getContext(), THUMB_HEIGHT);
        //Thumb的宽度
        int imageWidth = (int) (scale * DensityUtil.dip2px(getContext(), THUMB_WIDTH));

        //获取到的Bitmap可以比需要的大，要进行调整
        cdThumb = ImageUtil.scaleBitmap(getResources(), R.drawable.cd_thumb, imageWidth, imageHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画之前将原来的保存一下
        canvas.save();
        /**
         * 可以通过SurfaceVView来实现局部绘制
         * 因为旋转指针时，背景和上面那条线不用再重新绘制了
         * 但View不行，以为每一次View都是一个全新的Canvas
         * 绘制线，前面设定了setBounds，决定了它的位置和大小。
         */
        cdThumbLine.draw(canvas);

        //绘制背景
        cdBg.draw(canvas);

        //绘制指针，用到了矩阵，设置旋转。第一个参数是旋转的角度，第二个参数是点的x,y坐标
        thumbMatrix.setRotate(thumbRotation, thumbRotationPoint.x, thumbRotationPoint.y);
        //将画板移动到这里去画。
        thumbMatrix.preTranslate(thumbPoint.x, thumbPoint.y);
        //在这个位置画一个Bitmap。
        canvas.drawBitmap(cdThumb, thumbMatrix, paint);
        //最后再恢复一下，这样不会对之前的造成影响。
        canvas.restore();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        //在旋转的过程中，动态的去改变图像的位置。
        thumbRotation = (float) animation.getAnimatedValue();
        invalidate();
    }

    /**
     * 播放暂停的动画
     */
    public void stopThumbAnimation() {
        pauseThumbAnimator.start();
    }

    /**
     * 播放开始的动画
     */
    public void startThumbAnimation() {
        playThumbAnimator.start();
    }
}
