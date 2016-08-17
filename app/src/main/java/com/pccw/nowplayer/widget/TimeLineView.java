package com.pccw.nowplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowplayer.utils.ViewUtils;

import java.util.Calendar;

/**
 * Created by Swifty on 5/13/2016.
 */
public class TimeLineView extends LinearLayout {
    private static final int MINHEIGHT = 40;
    private boolean includeTmr = true;
    private int totalWidth;
    public long onedayts = 24 * 60 * 60 * 1000 + (includeTmr ? 30 * 60 * 1000 : 0);
    private int arrowPos;
    private ImageView arrow;
    private RelativeLayout relativeLayout;
    private LinearLayout container;
    private MyHorizontalScrollView horizontalScrollView;
    private int CellWidth = 100;
    OnScollListener onScollListener;
    private View line;
    private View nowButton;
    private boolean showArrow;
    //this offset is the request offset by current time.
    private long needOffset;
    //this one is caculate from needOffset and view, it is truly offset from views.
    private long trueOffset;
    private boolean startFromNow;
    private RelativeLayout maskContainer;

    public void setOnScollListener(OnScollListener onScollListener) {
        this.onScollListener = onScollListener;
    }

    public TimeLineView(Context context) {
        super(context);
        initViews();
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        this.setOrientation(HORIZONTAL);
        removeAllViews();
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        maskContainer = new RelativeLayout(getContext());
        maskContainer.setLayoutParams(params);

        horizontalScrollView = new MyHorizontalScrollView(getContext());
        horizontalScrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TypeUtils.dpToPx(getContext(), 40)));

        container = new LinearLayout(getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.HORIZONTAL);
        generateContainer(container);
        relativeLayout.addView(container);

        horizontalScrollView.addView(relativeLayout);
        maskContainer.addView(horizontalScrollView);
        generateMask();
        nowButton = genarateNowButton();
        this.addView(maskContainer);
        this.addView(nowButton);
        generateArrowAndLine(relativeLayout);

        horizontalScrollView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    horizontalScrollView.startScrollerTask();
                }
                return false;
            }
        });
        horizontalScrollView.setOnScrollStoppedListener(new MyHorizontalScrollView.OnScrollStoppedListener() {
            public void onScrollStopped() {
                if (needShowNowView(horizontalScrollView.getScrollX())) {
                    toggleNowButton(true);
                } else {
                    toggleNowButton(false);
                }
                if (onScollListener != null)
                    onScollListener.onScroll(horizontalScrollView.getScrollX(), caculateTimeStamp(horizontalScrollView.getScrollX()));
            }
        });
    }

    private void generateMask() {
        View leftMask = new View(getContext());
        RelativeLayout.LayoutParams leftLp =new RelativeLayout.LayoutParams(TypeUtils.dpToPx(getContext(), 40), TypeUtils.dpToPx(getContext(), 40));
        leftLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftMask.setLayoutParams(leftLp);
        leftMask.setBackgroundResource(R.drawable.mask_black_trans_left_to_right);
        maskContainer.addView(leftMask);
        View rightMask = new View(getContext());
        RelativeLayout.LayoutParams rightLp = new RelativeLayout.LayoutParams(TypeUtils.dpToPx(getContext(), 40), TypeUtils.dpToPx(getContext(), 40));
        rightLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightMask.setLayoutParams(rightLp);
        rightMask.setBackgroundResource(R.drawable.mask_black_trans_right_to_left);
        maskContainer.addView(rightMask);
    }

    private View genarateNowButton() {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(0, TypeUtils.dpToPx(getContext(), 40)));
        textView.setTextColor(getResources().getColor(R.color.orange));
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_left_orange_18dp, 0, 0, 0);
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine();
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalScrollView.smoothScrollTo(getCurrentTimePos() - TypeUtils.dpToPx(getContext(), CellWidth), 0);
                toggleNowButton(false);
            }
        });
        textView.setText(getContext().getString(R.string.now));
        textView.setAllCaps(true);
        return textView;
    }

    private boolean needShowNowView(int scrollX) {
        if (showArrow && (scrollX > getCurrentTimePos() || scrollX < getCurrentTimePos() - horizontalScrollView.getWidth())) {
            return true;
        }
        return false;
    }

    private long caculateTimeStamp(int x) {
        return (long) (x / (float) relativeLayout.getWidth() * (onedayts - trueOffset) + trueOffset);
    }


    private View generateBottomLine() {
        View bottomLine = new View(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(TypeUtils.dpToPx(getContext(), CellWidth), TypeUtils.dpToPx(getContext(), 1));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomLine.setLayoutParams(layoutParams);
        bottomLine.setBackgroundResource(R.color.orange);
        return bottomLine;
    }

    private void generateArrowAndLine(final RelativeLayout relativeLayout) {
        arrow = new ImageView(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        arrow.setLayoutParams(layoutParams);
        arrow.setImageResource(R.drawable.ic_arrow_down);
        line = generateBottomLine();
        relativeLayout.addView(arrow);
        relativeLayout.addView(line);
        post(new Runnable() {
            @Override
            public void run() {
                totalWidth = relativeLayout.getWidth();
                arrowPos = getCurrentTimePos();
                ((RelativeLayout.LayoutParams) arrow.getLayoutParams()).setMargins(arrowPos - arrow.getWidth() / 2, 0, 0, 0);
                ((RelativeLayout.LayoutParams) line.getLayoutParams()).setMargins(arrowPos - TypeUtils.dpToPx(getContext(), CellWidth), 0, 0, 0);
                arrow.requestLayout();
                line.requestLayout();
                horizontalScrollView.scrollTo(arrowPos - TypeUtils.dpToPx(getContext(), CellWidth), 0);
            }
        });
        toggleShowArrow(showArrow);
    }

    private int getCurrentTimePos() {
        long nowts = System.currentTimeMillis() - trueOffset;
        long todayts = getTodayTs();
        float rate = (nowts - todayts) / (float) (onedayts - trueOffset);
        return (int) (totalWidth * rate);
    }

    public long getTodayTs() {
        Calendar calendar = Calendar.getInstance();
        long nowts = System.currentTimeMillis();
        calendar.setTimeInMillis(nowts);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayts = calendar.getTimeInMillis();
        return todayts;
    }

    /**
     * default is not start from now
     *
     * @param startFromNow
     */
    public void setStartFromNow(boolean startFromNow) {
        if (this.startFromNow == startFromNow) return;
        this.startFromNow = startFromNow;
        if (startFromNow) {
            needOffset = (int) (System.currentTimeMillis() - 30 * 60 * 1000 - getTodayTs());
            initViews();
        } else {
            needOffset = 0;
            initViews();
        }
    }

    private void generateContainer(LinearLayout container) {
        container.removeAllViews();
        int initHalfHour = (int) needOffset / (30 * 60 * 1000);
        trueOffset = initHalfHour * (30 * 60 * 1000);
        for (int i = initHalfHour; i < 48; i++) {
            String time;
            String minutes;
            String hour;
            if (i > 23) {
                time = " pm";
            } else {
                time = " am";
            }
            if (i % 2 == 0) {
                minutes = ":00";
            } else {
                minutes = ":30";
            }
            if ((i % 24) / 2 == 0) {
                hour = "12";
            } else {
                hour = String.valueOf((i % 24) / 2);
            }
            container.addView(generateCell(hour + minutes + time));
        }
        if (includeTmr)
            container.addView(generateCell("12:00 am"));
    }

    public void toggleShowArrow(boolean show) {
        this.showArrow = show;
        if (showArrow) {
            arrow.setVisibility(VISIBLE);
            line.setVisibility(VISIBLE);
        } else {
            arrow.setVisibility(INVISIBLE);
            line.setVisibility(INVISIBLE);
            toggleNowButton(false);
        }
    }

    private void toggleNowButton(boolean show) {
        if (show) {
            ViewUtils.showViewX(nowButton, TypeUtils.dpToPx(getContext(), 60));
        } else {
            ViewUtils.dismissViewX(nowButton);
        }
    }

    private TextView generateCell(String s) {
        TextView cell = new TextView(getContext());
        cell.setText(s);
        cell.setTextColor(getContext().getResources().getColor(R.color.white));
        cell.setMinHeight(TypeUtils.dpToPx(getContext(), MINHEIGHT));
        cell.setWidth(TypeUtils.dpToPx(getContext(), CellWidth));
        cell.setGravity(Gravity.CENTER);
        return cell;
    }

    public interface OnScollListener {
        void onScroll(long currentX, long currentScrollTimeStampStartByDay);
    }

    public long getCurrentTimeStampStartByDay() {
        return caculateTimeStamp(horizontalScrollView.getScrollX());
    }
}
