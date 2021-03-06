package com.ixuea.courses.mymusic.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.DensityUtil;
import com.ixuea.courses.mymusic.util.ImageUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kaka
 * On 2019/4/11
 */
public class RecordView extends View{
    /**
     * 黑胶唱片宽高比例
     */
    private static final float CD_SCALE = 1.333F;

    /**
     * 封面比例
     */
    //private static final float ALBUM_SCALE = 2.037F;
    private static final float ALBUM_SCALE = 2.1F;

    /**
     * 每16毫秒旋转的角度
     * <p>
     * 16毫秒是通过，每秒60帧计算出来的
     * 也就是1000/60=16，也就是说绘制一帧要在16毫秒中完成，不然就能感觉卡顿
     */
    public static final float ROTATION_PER = 0.2304F;

    /**
     * 画笔
     */
    private Paint paint;

    /**
     * 黑胶唱片bitmap
     */
    private Bitmap cd;

    /**
     * 黑胶唱片绘制坐标
     */
    private Point cdPoint = new Point();

    /**
     * 旋转点，都是在中点，所以一个就够了
     */
    private Point cdRotationPoint = new Point();

    /**
     * 封面的宽度
     */
    private int albumWidth;

    /**
     * 封面绘制坐标
     */
    private Point albumPoint = new Point();


    /**
     * 封面图
     */
    private String albumUri;


    /**
     * 封面bitmap
     */
    private Bitmap album;

    /**
     * 黑胶唱片矩阵
     */
    private Matrix cdMatrix = new Matrix();

    /**
     * 封面矩阵
     */
    private Matrix albumMatrix = new Matrix();


    /**
     * 旋转的角度
     */
    private float cdRotation = 0;

    /**
     * 计时器任务
     */
    private TimerTask timerTask;

    /**
     * 计算器，用来调度唱片，专辑转动
     */
    private Timer timer;

    public RecordView(Context context) {
        super(context);
        init();
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int widthHalf = measuredWidth/2;

        initResource();

        //黑胶
        int cdWidthHalf = cd.getWidth()/2;
        int cdLeft = widthHalf-cdWidthHalf;

        //它的top,应该有后面白圈的中心点算
        int cdBgWidth = (int)(measuredWidth/RecordBackgroundView.CD_BG_SCALE);
        int cdBgWidthHalf = cdBgWidth/2;
        int cdBgTop = DensityUtil.dip2px(getContext(),measuredWidth/RecordThumbView.CD_BG_TOP_SCALE);
        int cdBgCenterY = cdBgTop+cdBgWidthHalf;

        int cdTop = cdBgCenterY-cdWidthHalf;
        //黑胶绘制坐标，保存在这个点。
        cdPoint.set(cdLeft,cdTop);
        cdRotationPoint.set(widthHalf,cdWidthHalf+cdTop);

        //封面
        albumWidth = (int)(measuredWidth/ALBUM_SCALE);
        int albumWidthHalf = albumWidth/2;
        int albumLeft = widthHalf-albumWidthHalf;
        int albumTop = cdBgCenterY-albumWidthHalf;
        //封面绘制坐标，保存在这个点。
        albumPoint.set(albumLeft,albumTop);

        showAlbum();

    }
    /**
     * 设置歌曲封面
     *
     * @param uri
     */
    public void setAlbumUri(String uri) {
        this.albumUri=uri;
        showAlbum();
    }

    /**
     * 显示专辑
     */
    private void showAlbum() {
        if (albumWidth!=0){
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.circleCrop();
            //磁盘缓存策略，None表示不缓存
            //options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.override(albumWidth,albumWidth);
            Glide.with(this).asBitmap().load(ImageUtil.getImageURI(this.albumUri)).apply(options).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                      album = ImageUtil.resizeImage(resource,albumWidth,albumWidth);
                      //它会调用onDraw()方法，重新绘制。
                      invalidate();
                }
            });
        }
    }

    /**
     * 初始化视图
     */
    private void initResource() {
         if (cd==null){
             //cd背景,比值1.333F
             int cdWidth = (int)(getMeasuredWidth()/CD_SCALE);
             cd = ImageUtil.scaleBitmap(getResources(), R.drawable.cd_bg,cdWidth,cdWidth);
         }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        //绘制cd,旋转的角度，默认是0。
        cdMatrix.setRotate(cdRotation,cdRotationPoint.x,cdRotationPoint.y);
        //设置画笔从这里开始绘制图像。
        cdMatrix.preTranslate(cdPoint.x,cdPoint.y);
        canvas.drawBitmap(cd,cdMatrix,paint);

        //绘制封面，可能没有
        if (album!=null){
            albumMatrix.setRotate(cdRotation,cdRotationPoint.x,cdRotationPoint.y);
            albumMatrix.preTranslate(albumPoint.x,albumPoint.y);
            canvas.drawBitmap(album,albumMatrix,paint);
        }
        canvas.restore();
    }
    public void stopAlbumRotate(){
        cancelTask();
    }
    public void startAlbumRotate(){
        cancelTask();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (cdRotation>=360){
                    cdRotation=0;
                }
                //动画要流畅，一秒钟绘制60次，1000除以60，大概就是16毫秒。
                cdRotation+=ROTATION_PER;
                //这个方法可以在子线程调用，invalidate()方法只能在主线程调用。
                postInvalidate();
            }
        };
        timer = new Timer();
        //16毫秒
        timer.schedule(timerTask,0,16);
    }

    private void cancelTask() {
        if (timerTask!=null){
            timerTask.cancel();
            timerTask=null;
        }
        if (timer!=null){
            timer.cancel();
            timer.cancel();
        }
    }
}
