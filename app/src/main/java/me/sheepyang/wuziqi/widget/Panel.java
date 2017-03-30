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
    private static final int MAX_LINE = 10;//棋盘总行数
    private static final int MAX_COUNT_IN_LINE = 5;//胜利条件，5子连线
    private static final float mRatioPieceOfLine = 3f / 4;//棋子与格子的比例
    private Context mContext;
    private int mPanelWidth;//棋盘宽度
    private float mLineWidth;//棋盘格子宽度
    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private boolean mIsWhiteRound;// 是否为白棋的回合
    private List<Point> mWhiteList = new ArrayList<>();
    private List<Point> mBlackList = new ArrayList<>();

    private Point mCurrentPoint;
    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;

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

//        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.white_piece);
//        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.black_piece);
//        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.white_p);
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.white_p2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.black_p);

        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void restartGame() {
        mWhiteList = new ArrayList<>();
        mBlackList = new ArrayList<>();
        mIsWhiteRound = false;// 是否为白棋的回合
        mCurrentPoint = null;
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }

    public void undo() {
        if (!mIsGameOver) {
            if (mIsWhiteRound) {
                if (mBlackList != null && mBlackList.size() > 0) {
                    mBlackList.remove(mBlackList.size() - 1);
                    mIsWhiteRound = !mIsWhiteRound;
                } else {
                    Toast.makeText(mContext, R.string.cant_undo, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mWhiteList != null && mWhiteList.size() > 0) {
                    mWhiteList.remove(mWhiteList.size() - 1);
                    mIsWhiteRound = !mIsWhiteRound;
                } else {
                    Toast.makeText(mContext, R.string.cant_undo, Toast.LENGTH_SHORT).show();
                }
            }
            invalidate();
        } else {
            Toast.makeText(mContext, R.string.game_over_restart, Toast.LENGTH_SHORT).show();
        }
    }

    public void giveUp() {
        if (!mIsGameOver) {
            mIsWhiteWinner = !mIsWhiteRound;
            mIsGameOver = true;
            String msg = mIsWhiteWinner ? mContext.getString(R.string.white_winner) : mContext.getString(R.string.black_winner);
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.game_over_restart, Toast.LENGTH_SHORT).show();
        }
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
        drawBoard(canvas);//绘制棋盘
        drawPiece(canvas);//绘制棋子
        checkGameState();//检查游戏是否结束
    }

    private void checkGameState() {
        boolean winnerWhite = checkFiveLine(mWhiteList);
        boolean winnerBlack = checkFiveLine(mBlackList);

        if (winnerWhite || winnerBlack) {
            mIsWhiteWinner = winnerWhite;
            mIsGameOver = true;
            String msg = mIsWhiteWinner ? mContext.getString(R.string.white_winner) : mContext.getString(R.string.black_winner);
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveLine(List<Point> list) {
        for (Point point : list) {
            int x = point.x;
            int y = point.y;
            boolean isWinner;
            isWinner = checkHorizontal(x, y, list);//横向检测
            if (isWinner) return true;

            isWinner = checkVertical(x, y, list);//纵向检测
            if (isWinner) return true;

            isWinner = checkLeftDiagonal(x, y, list);//左斜检测
            if (isWinner) return true;

            isWinner = checkRightDiagonal(x, y, list);//右斜检测
            if (isWinner) return true;
        }
        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> list) {
        int count = 1;
        //往右上数
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (list.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //往左下数
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (list.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> list) {
        int count = 1;
        //往左上数
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (list.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //往右下数
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (list.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> list) {
        int count = 1;
        //往上数
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (list.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //往下数
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (list.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> list) {
        int count = 1;
        //往左数
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (list.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //往右数
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (list.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private void drawPiece(Canvas canvas) {
        float offset = mLineWidth * (1 - mRatioPieceOfLine) / 2;
        for (Point point : mWhiteList) {
            canvas.drawBitmap(mWhitePiece, point.x * mLineWidth + offset, point.y * mLineWidth + offset, null);
        }

        for (Point point : mBlackList) {
            canvas.drawBitmap(mBlackPiece, point.x * mLineWidth + offset, point.y * mLineWidth + offset, null);
        }

        if (mCurrentPoint != null) {
            if (mIsWhiteRound) {
                canvas.drawBitmap(mWhitePiece, mCurrentPoint.x - 2 * offset, mCurrentPoint.y - 2 * offset, mPaint);
            } else {
                canvas.drawBitmap(mBlackPiece, mCurrentPoint.x - 2 * offset, mCurrentPoint.y - 2 * offset, mPaint);
            }
        }
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.i("SheepYang", "onTouchEvent: x=" + x + ", y=" + y);
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
                Log.i("SheepYang", "point: x=" + point.x + ", y=" + point.y);
                if (mWhiteList.contains(point) || mBlackList.contains(point)) {
                    mCurrentPoint = null;
                    Log.i("SheepYang", "这点已经有棋子了！");
                    invalidate();
                    return false;
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
