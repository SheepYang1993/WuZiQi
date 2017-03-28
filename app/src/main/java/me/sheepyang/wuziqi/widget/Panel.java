package me.sheepyang.wuziqi.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.sheepyang.wuziqi.R;

/**
 * Created by SheepYang on 2017/3/27.
 */

public class Panel extends View {
    private Context mContext;
    private int mPanelWidth;//棋盘宽度
    private float mLineWidth;//棋盘格子宽度
    private static final int MAX_LINE = 10;//棋盘总行数
    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private boolean mIsWhiteRound;// 是否为白棋的回合
    private List<Point> mWhiteList = new ArrayList<>();
    private List<Point> mBlackList = new ArrayList<>();

    private static final float mRatioPieceOfLine = 3f / 4;//棋子与格子的比例
    private Point mCurrentPoint;

    public Panel(Context context) {
        this(context, null);
    }

    public Panel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Panel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setBackgroundColor(0x44ff0000);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.white_piece);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.black_piece);

        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = Math.min(widthSize, heightSize);
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(widthSize, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                width = heightSize;
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = Math.min(widthSize, heightSize);
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(widthSize, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = widthSize;
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
    }

    private void drawBoard(Canvas canvas) {
        float startX;
        float startY;
        float endX;
        float endY;

        //绘制横线
        startX = 0.5f * mLineWidth;
        endX = mPanelWidth - 0.5f * mLineWidth;
        for (int i = 0; i < MAX_LINE; i++) {
            startY = (i + 0.5f) * mLineWidth;
            endY = startY;
            canvas.drawLine(startX, startY, endX, endY, mPaint);//绘制横线
            canvas.drawLine(startY, startX, endY, endX, mPaint);//绘制竖线
        }




/*        float left = (1f - mRatioPieceOfLine) * mLineWidth;
        float top = (1f - mRatioPieceOfLine) * mLineWidth;
        canvas.drawBitmap(mWhitePiece, left, top, mPaint);
        mPaint.setColor(0xff00ff00);
        mPaint.setStrokeWidth(10);
        canvas.drawPoint(left, top, mPaint);
        mPaint.setColor(0x88000000);*/
        if (mCurrentPoint != null) {
            if (mIsWhiteRound) {
                canvas.drawBitmap(mWhitePiece, mCurrentPoint.x, mCurrentPoint.y, mPaint);
            } else {
                canvas.drawBitmap(mBlackPiece, mCurrentPoint.x, mCurrentPoint.y, mPaint);
            }
        }

        for (Point point : mWhiteList) {

        }

        for (Point point : mBlackList) {

        }

        //绘制竖线
//        startY = 0.5f * mLineWidth;
//        endY = mPanelWidth - 0.5f * mLineWidth;
//        for (int i = 0; i < MAX_LINE; i++) {
//            startX = (i + 0.5f) * mLineWidth;
//            endX = startX;
//            canvas.drawLine(startX, startY, endX, endY, mPaint);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mCurrentPoint == null) {
                    mCurrentPoint = new Point(x, y);
                } else {
                    mCurrentPoint.set(x, y);
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentPoint == null) {
                    mCurrentPoint = new Point(x, y);
                } else {
                    mCurrentPoint.set(x, y);
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Point point = getValidPoint(mCurrentPoint);
                if (mWhiteList.contains(point) || mBlackList.contains(point)) {
                    mCurrentPoint = null;
                    Toast.makeText(mContext, "这点已经有棋子了！", Toast.LENGTH_SHORT).show();
                    invalidate();
                    return true;
                }
                if (mIsWhiteRound) {
                    mWhiteList.add(point);
                } else {
                    mBlackList.add(point);
                }
                mCurrentPoint = null;
                mIsWhiteRound = !mIsWhiteRound;
                invalidate();
                return true;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineWidth), (int) (y / mLineWidth));
    }

    private Point getValidPoint(Point point) {
        return new Point((int) (point.x / mLineWidth), (int) (point.y / mLineWidth));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineWidth = mPanelWidth / (float) MAX_LINE;

        int peaceWidth = (int) (mRatioPieceOfLine * mLineWidth);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, peaceWidth, peaceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, peaceWidth, peaceWidth, false);
    }


    ///////////////////////////////////////////////////////////////
    private void logMode(int widthMode, int heightMode) {
        String msg = "";
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                msg += "widthMode:EXACTLY";
                break;
            case MeasureSpec.AT_MOST:
                msg += "widthMode:AT_MOST";
                break;
            case MeasureSpec.UNSPECIFIED:
                msg += "widthMode:UNSPECIFIED";
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                msg += ", heightMode:EXACTLY";
                break;
            case MeasureSpec.AT_MOST:
                msg += ", heightMode:AT_MOST";
                break;
            case MeasureSpec.UNSPECIFIED:
                msg += ", heightMode:UNSPECIFIED";
                break;
        }
        Log.i("SheepYang", msg);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
