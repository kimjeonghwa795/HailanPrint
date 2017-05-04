package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.hailan.HaiLanPrint.models.CustomDate;
import com.hailan.HaiLanPrint.utils.DateUtils;

/**
 * 自定义日历卡
 */
public class CalendarCard extends View {

    private static final int TOTAL_COL = 21; // 7列
    private static final int TOTAL_ROW = 2; // 6行

    private Paint mMonthPaint; // 绘制月份的画笔
    private Paint mWeekdayPaint; // 绘制星期的画笔
    private Paint mCirclePaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔
    private int mViewWidth; // 视图的宽度
    private int mViewHeight; // 视图的高度
    private int mCellSpace; // 单元格间距
    private Row rows[] = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
    private static CustomDate mShowDate; // 自定义的日期，包括year,month,day
    private OnCellClickListener mCellClickListener; // 单元格点击回调事件
    private int touchSlop; //
    private boolean callBackCellSpace;

    private int mXAxisCalendarStartDrawPosition;
    private int mYAxisCalendarStartDrawPosition;

    private String weeks[] = {"日", "一", "二", "三", "四", "五", "六"};

    private String months[] = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};

    private Cell mClickCell;
    private float mDownX;
    private float mDownY;

    /**
     * 单元格点击的回调接口
     *
     * @author wuwenjie
     */
    public interface OnCellClickListener {
        void clickDate(CustomDate date); // 回调点击的日期

        void changeDate(CustomDate date); // 回调滑动ViewPager改变的日期
    }

    public CalendarCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalendarCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarCard(Context context) {
        super(context);
        init(context);
    }

    public CalendarCard(Context context, OnCellClickListener listener) {
        super(context);
        this.mCellClickListener = listener;
        init(context);
    }

    private void init(Context context) {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mMonthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMonthPaint.setColor(Color.parseColor("#ffffff"));

        mWeekdayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWeekdayPaint.setColor(Color.parseColor("#000000"));

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.parseColor("#F24949")); // 红色圆形

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//        setBackgroundColor(Color.parseColor("#303F9F"));
        mXAxisCalendarStartDrawPosition = dp2px(context, 40);
        mYAxisCalendarStartDrawPosition = dp2px(context, 5);

        initDate();
    }

    private void initDate() {
        mShowDate = new CustomDate();
        fillDate();
    }

    private void fillDate() {
        int monthDay = DateUtils.getCurrentMonthDay(); // 今天
        int lastMonthDays = DateUtils.getMonthDays(mShowDate.year,
                mShowDate.month - 1); // 上个月的天数
        int currentMonthDays = DateUtils.getMonthDays(mShowDate.year,
                mShowDate.month); // 当前月的天数
        int firstDayWeek = DateUtils.getWeekDayFromDate(mShowDate.year,
                mShowDate.month);
        boolean isCurrentMonth = false;
        if (DateUtils.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        int day = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置
                // 这个月的
                if (position >= firstDayWeek
                        && position < firstDayWeek + currentMonthDays) {
                    day++;
                    rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(
                            mShowDate, day), State.CURRENT_MONTH_DAY, i, j);
                    // 今天
                    if (isCurrentMonth && day == monthDay) {
                        CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
                        rows[j].cells[i] = new Cell(date, State.TODAY, i, j);
                    }

                    if (isCurrentMonth && day > monthDay) { // 如果比这个月的今天要大，表示还没到
                        rows[j].cells[i] = new Cell(
                                CustomDate.modifiDayForObject(mShowDate, day),
                                State.UNREACH_DAY, i, j);
                    }

                    // 过去一个月
                } else if (position < firstDayWeek) {
                    rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year,
                            mShowDate.month - 1, lastMonthDays
                            - (firstDayWeek - position - 1)),
                            State.PAST_MONTH_DAY, i, j);
                    // 下个月
                } else if (position >= firstDayWeek + currentMonthDays) {
                    rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year,
                            mShowDate.month + 1, position - firstDayWeek
                            - currentMonthDays + 1)),
                            State.NEXT_MONTH_DAY, i, j);
                }
            }
        }
//        mCellClickListener.changeDate(mShowDate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画月份
        String month = months[mShowDate.month - 1];

        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.parseColor("#FF4081"));
        Rect rect = new Rect((int) (mXAxisCalendarStartDrawPosition / 2 - mMonthPaint.measureText(month)),
                (int) (mXAxisCalendarStartDrawPosition / 2 - mMonthPaint.measureText(month) * 1.5),
                (int) (mXAxisCalendarStartDrawPosition / 2 + mMonthPaint.measureText(month)),
                (int) (mXAxisCalendarStartDrawPosition / 2 + mMonthPaint.measureText(month) * 1.5));
        canvas.drawRect(rect, rectPaint);

        canvas.drawText(month,
                (float) (mXAxisCalendarStartDrawPosition / 2 - mMonthPaint.measureText(month) / 2),
                (float) (mXAxisCalendarStartDrawPosition / 2 - mMonthPaint.measureText(month, 0, 1) / 2), mMonthPaint);

        String monthString = "月";
        canvas.drawText(monthString,
                (float) (mXAxisCalendarStartDrawPosition / 2 - mMonthPaint.measureText(monthString) / 2),
                (float) (mXAxisCalendarStartDrawPosition / 2 + mMonthPaint.measureText(monthString, 0, 1)),
                mMonthPaint);

        // 画农历年的生肖

        // 画星期
        for (int i = 0; i < TOTAL_COL; i++) {
            String week = weeks[i % weeks.length];
            canvas.drawText(week,
                    (float) (mXAxisCalendarStartDrawPosition + (i + 0.5) * mCellSpace - mWeekdayPaint
                            .measureText(week) / 2),
                    (float) (1.3 * mCellSpace - mWeekdayPaint
                            .measureText(week, 0, 1) / 2), mWeekdayPaint);
        }

        // 画日期
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCellSpace = Math.min(mViewHeight / TOTAL_ROW, (mViewWidth - mXAxisCalendarStartDrawPosition) / TOTAL_COL);
        if (!callBackCellSpace) {
            callBackCellSpace = true;
        }
        mTextPaint.setTextSize(mCellSpace / 3);
        mWeekdayPaint.setTextSize(mCellSpace / 2);
        mMonthPaint.setTextSize(mCellSpace / 3 * 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpace);
                    int row = (int) (mDownY / mCellSpace);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 计算点击的单元格
     *
     * @param col
     * @param row
     */
    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW)
            return;
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null) {
            mClickCell = new Cell(rows[row].cells[col].date,
                    rows[row].cells[col].state, rows[row].cells[col].i,
                    rows[row].cells[col].j);

            CustomDate date = rows[row].cells[col].date;
            date.week = col;
            mCellClickListener.clickDate(date);

            // 刷新界面
            update();
        }
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setTime(int year, int month) {
        mShowDate.year = year;
        mShowDate.month = month;
        update();
    }

    // 从左往右划，上一个月
    public void leftSlide() {
        if (mShowDate.month == 1) {
            mShowDate.month = 12;
            mShowDate.year -= 1;
        } else {
            mShowDate.month -= 1;
        }
        update();
    }

    // 从右往左划，下一个月
    public void rightSlide() {
        if (mShowDate.month == 12) {
            mShowDate.month = 1;
            mShowDate.year += 1;
        } else {
            mShowDate.month += 1;
        }
        update();
    }

    public void update() {
        fillDate();
        invalidate();
    }

    /**
     * 组元素
     *
     * @author wuwenjie
     */
    class Row {
        public int j;

        Row(int j) {
            this.j = j;
        }

        public Cell[] cells = new Cell[TOTAL_COL];

        // 绘制单元格
        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                }
            }
        }
    }

    /**
     * 单元格元素
     *
     * @author wuwenjie
     */
    class Cell {
        public CustomDate date;
        public State state;
        public int i;
        public int j;

        public Cell(CustomDate date, State state, int i, int j) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
        }

        public void drawSelf(Canvas canvas) {
            switch (state) {
                case TODAY: // 今天
                    mTextPaint.setColor(Color.BLACK);
//                    mTextPaint.setColor(Color.parseColor("#fffffe"));
//                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
//                            (float) ((j + 0.5) * mCellSpace), mCellSpace / 3,
//                            mCirclePaint);
                    break;
                case CURRENT_MONTH_DAY: // 当前月日期
                    mTextPaint.setColor(Color.BLACK);
                    break;
                case PAST_MONTH_DAY: // 过去一个月
                case NEXT_MONTH_DAY: // 下一个月
                    mTextPaint.setColor(Color.parseColor("#00ffffff"));
                    break;
                case UNREACH_DAY: // 还未到的天
                    mTextPaint.setColor(Color.BLACK);
                    break;
                default:
                    break;
            }
            // 绘制文字
            String content = date.day + "";
            canvas.drawText(content,
                    (float) (mXAxisCalendarStartDrawPosition + (i + 0.5) * mCellSpace - mTextPaint
                            .measureText(content) / 2),
                    (float) ((j + 1.7) * mCellSpace - mTextPaint
                            .measureText(content, 0, 1) / 2), mTextPaint);

            ChineseLunarUtils chineseLunarUtils = new ChineseLunarUtils(date.getYear(), date.getMonth(),
                    date.getDay());

            // 画农历
            String chineseLunarDay = chineseLunarUtils.getChineseDay();
            canvas.drawText(chineseLunarDay,
                    (float) (mXAxisCalendarStartDrawPosition + (i + 0.5) * mCellSpace - mTextPaint
                            .measureText(chineseLunarDay) / 2),
                    (float) ((j + 2.1) * mCellSpace - mTextPaint
                            .measureText(chineseLunarDay, 0, 1) / 2), mTextPaint);
        }
    }

    /**
     * @author wuwenjie 单元格的状态 当前月日期，过去的月的日期，下个月的日期
     */
    enum State {
        TODAY, CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, UNREACH_DAY;
    }

}
