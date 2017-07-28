package com.guagua.defineview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guagua.defineview.R;


/**
 * Created by android on 7/17/17.
 */

public class ShapeImageView extends ImageView {

    private final int CIRCLE_SHAPE = 0;
    private final int HEART_SHAPE = 1;
    private final int ROUND_SHAPE = 2;

    private final int m_borderColor;
    private final float m_borderSize;
    private int m_shape;
    private final int m_placeHolderImage;
    private final Context m_Context;
    private final int m_roundRadius;
    private Paint m_paint;
    private int m_width;
    private int m_height;
    private Bitmap bitmap;
    private PorterDuffXfermode porterDuffXfermode;
    private int bitmapWidth;
    private String m_imageUrl;
    private boolean m_shadow;

    private Path m_path_one;
    private Path m_path_two;
    private float centerX1;
    private float centerX2;
    private float centerY1;
    private float centerY2;


    private int defaultWidth;
    private int defaultHeight;



    public ShapeImageView(@NonNull Context context) {
        this(context, null);
    }

    public ShapeImageView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        m_Context = context;
        initPaint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView);
        m_borderColor = typedArray.getColor(R.styleable.ShapeImageView_border_color, -1);
        m_borderSize = typedArray.getDimension(R.styleable.ShapeImageView_border, 0);
        m_shape = typedArray.getInt(R.styleable.ShapeImageView_shape, 0);
        m_placeHolderImage = typedArray.getResourceId(R.styleable.ShapeImageView_placeholder_image, -1);
        m_roundRadius = typedArray.getInt(R.styleable.ShapeImageView_round_radius, 10);
        typedArray.recycle();
    }

    /**
     * 图片地址
     *
     * @param url
     */
    public void setImageUrl(@NonNull String url) {
        this.m_imageUrl = url;
    }

    /**
     * 阴影
     *
     * @param shadow
     */
    public void setShadow(boolean shadow) {
        this.m_shadow = shadow;
    }


    private void initPaint() {
        m_paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        m_paint.setStyle(Paint.Style.FILL);
        m_paint.setStrokeWidth(5);

        m_path_one = new Path();
        m_path_two = new Path();

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    }

    public Bitmap scale(Bitmap b, float x, float y) {
        int w = b.getWidth();
        int h = b.getHeight();
        float sx = (float) x / w;//要强制转换，不转换我的在这总是死掉。
        float sy = (float) y / h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
                h, matrix, true);
        return resizeBmp;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wdtSize = MeasureSpec.getSize(widthMeasureSpec);
        int hgtSize = MeasureSpec.getSize(heightMeasureSpec);

        defaultWidth = getDefaultSize(wdtSize, widthMeasureSpec);
        defaultHeight = getDefaultSize(hgtSize, heightMeasureSpec);

        setMeasuredDimension(defaultWidth, defaultHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.m_width = w;
        this.m_height = h;


        bitmap = BitmapFactory.decodeResource(getResources(), m_placeHolderImage);
        bitmap = scale(bitmap, m_width / 2, m_width / 2);
        bitmapWidth = bitmap.getWidth();
        loadImage();

    }

    private Bitmap scaleBitmap(Bitmap origin, float newWidth, float newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    private void loadImage() {
        Glide.with(m_Context).load("http://img5.imgtn.bdimg.com/it/u=2473389672,1267947029&fm=26&gp=0.jpg").asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                float ratio_one = m_width / m_height;
                float ratio_two = m_height / m_width;
                float newWidth = 0;
                float newHeight = 0;
                switch (m_shape) {
                    case HEART_SHAPE:
                        if (ratio_one > ratio_two) {
                            newWidth = m_width / 2 + m_width * ratio_two;
                            newHeight = centerY2 - centerY1 + m_height * ratio_two;
                        } else {
                            newWidth = m_width / 2 + m_width * ratio_one;
                            newHeight = centerY2 - centerY1 + m_height * ratio_one;
                        }
                        break;
                    case CIRCLE_SHAPE:
                    case ROUND_SHAPE:
                        newWidth = m_width;
                        newHeight = m_height;
                        break;
                }
                Bitmap bitmap = scaleBitmap(resource, newWidth, newHeight + 5);
                ShapeImageView.this.bitmap = bitmap;
                bitmapWidth = ShapeImageView.this.bitmap.getWidth();
                postInvalidate();
            }
        });
    }

    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, float newWidth, float newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    private void runPath() {
        centerX1 = m_width / 2;
        centerX2 = centerX1;
        centerY1 = m_height / 2 - m_height * 0.3f;
        centerY2 = centerY1 + m_height / 3;

        switch (m_shape) {
            case HEART_SHAPE:
                m_path_one.moveTo(centerX1, centerY1);
                m_path_one.quadTo(centerX1 + m_width / 2
                        , centerY1 - 50, centerX2, centerY2);
                m_path_one.moveTo(centerX1, centerY1);
                m_path_one.quadTo(centerX1 - m_width / 2, centerY1 - 50, centerX2, centerY2);

                m_path_two.moveTo(centerX1, centerY1);
                m_path_two.quadTo(centerX1 + m_width / 2
                        , centerY1 - 50, centerX2, centerY2);
                m_path_two.moveTo(centerX1, centerY1);
                m_path_two.quadTo(centerX1 - m_width / 2, centerY1 - 50, centerX2, centerY2);
                break;
            case CIRCLE_SHAPE:
                m_path_one.addCircle(m_width / 2, m_height / 2, m_width / 2, Path.Direction.CW);
                m_path_two.addCircle(m_width / 2, m_height / 2, m_width / 2, Path.Direction.CW);
                break;

            case ROUND_SHAPE:
                RectF rectF = new RectF(0, 0, m_width, m_height);
                m_path_one.addRoundRect(rectF, m_roundRadius, m_roundRadius, Path.Direction.CW);
                m_path_two.addRoundRect(rectF, m_roundRadius, m_roundRadius, Path.Direction.CW);
                break;

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        runPath();
        m_paint.setStyle(Paint.Style.FILL);
        m_paint.setColor(Color.WHITE);
        canvas.drawPath(m_path_one, m_paint);
        m_paint.setXfermode(porterDuffXfermode);
        switch (m_shape) {
            case HEART_SHAPE:
                canvas.drawBitmap(bitmap, centerX1 - bitmapWidth / 2, centerY1 - 10, m_paint);
                break;
            case CIRCLE_SHAPE:
            case ROUND_SHAPE:
                canvas.drawBitmap(bitmap, new Matrix(), m_paint);
                break;
        }
        m_paint.setXfermode(null);
        m_paint.setStyle(Paint.Style.STROKE);
        m_paint.setColor(Color.RED);
        m_paint.setStrokeWidth(m_borderSize);
        canvas.drawPath(m_path_two, m_paint);
        super.onDraw(canvas);
    }

}
