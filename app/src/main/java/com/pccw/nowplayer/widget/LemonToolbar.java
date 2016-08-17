package com.pccw.nowplayer.widget;

import android.app.ActionBar;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.utils.ViewUtils;


/**
 * 由于Google提供的Toolbar并没有标题居中的方法。所以在这里修改了处理过程逻辑。添加了标题居中的方式。
 * Created by Kevin on 2015/10/15.
 */
public class LemonToolbar extends Toolbar {


    TextView centerTitle;
    TextView centerSubTitle;
    LinearLayout centerLinearLayout;
    boolean isInCenter = false;
    boolean centering;
    boolean isLoad = false;
    /**
     * {@link android.support.v7.widget.Toolbar mTitleTextAppearance }
     */
    private int mTitleTextAppearance;
    private int mSubtitleTextAppearance;
    private int mSubtitleTextColor;
    private int mTitleTextColor;
    private int mTitleMarginStart;
    private int mTitleMarginEnd;
    private int mTitleMarginTop;
    private int mTitleMarginBottom;

    public LemonToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    public LemonToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.Toolbar, defStyleAttr, 0);
        mTitleTextAppearance = a.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
        mSubtitleTextAppearance = a.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);

        mTitleMarginStart = mTitleMarginEnd = mTitleMarginTop = mTitleMarginBottom =
                a.getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, 0);
        ViewUtils.addOnGlobalLayoutListener(this, new Runnable() {
            @Override
            public void run() {
                if (!isLoad) {
                    isLoad = true;
                }
            }
        });


    }

    View.OnClickListener navOnClickListener;

    @Override
    public void setNavigationOnClickListener(OnClickListener listener) {
        if (centering) {
            return;
        }

        navOnClickListener = listener;
        super.setNavigationOnClickListener(listener);
    }

    /**
     * 设置标题栏居中
     */
    public void setTitleInCenter() {
        String title = null;
        String subTitle = null;
        if (getTitle() != null) {
            title = getTitle().toString();
        }
        if (getSubtitle() != null) {
            subTitle = getSubtitle().toString();
        }

        final String finalTitle = title;
        final String finalSubTitle = subTitle;
        isInCenter = true;
        if (!isLoad) {
            ViewUtils.addOnGlobalLayoutListener(this, new Runnable() {
                @Override
                public void run() {
                    if (!isLoad || isInCenter) {
                        titleInCenter(finalTitle, finalSubTitle);
                    }
                }
            });
        } else {
            titleInCenter(finalTitle, finalSubTitle);
        }
    }

    public boolean titleIsCenter() {
        return isInCenter;
    }


    /**
     * 标题栏居中
     *
     * @param title
     * @param subTitle
     */
    public void titleInCenter(String title, String subTitle) {
        centering = true;
        final int currentOptions = getWrapper().getDisplayOptions();
        getWrapper().setDisplayOptions(0 & ActionBar.DISPLAY_SHOW_TITLE | currentOptions & ~ActionBar.DISPLAY_SHOW_TITLE);
        if (centerLinearLayout == null) {
            Toolbar.LayoutParams tlp = generateDefaultLayoutParams();
            tlp.gravity = Gravity.CENTER;
            centerLinearLayout = new LinearLayout(getContext());
            centerLinearLayout.setOrientation(LinearLayout.VERTICAL);
            centerLinearLayout.setLayoutParams(tlp);
        }

        super.setNavigationOnClickListener(navOnClickListener);
        createTitle(centerLinearLayout, title);
        createSubTitle(centerLinearLayout, subTitle);
        addView(centerLinearLayout);
        isInCenter = true;
    }


    /**
     * 设置标题栏居左(默认状态下)
     */
    public void setTitleInLeft() {
        final int currentOptions = getWrapper().getDisplayOptions();
        getWrapper().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE & ActionBar.DISPLAY_SHOW_TITLE | currentOptions & ~ActionBar.DISPLAY_SHOW_TITLE);
        if (centerLinearLayout != null) {
            if (centerTitle != null) {
                centerLinearLayout.removeView(centerTitle);
            }
            if (centerSubTitle != null) {
                centerLinearLayout.removeView(centerSubTitle);
            }
            removeView(centerLinearLayout);
        }
        centerLinearLayout = null;
        isInCenter = false;

    }


    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (!TextUtils.isEmpty(getTitle())) {
            if (centerTitle != null) {
                centerTitle.setText(title);
            }
        }
    }

    /**
     * 创建一个主标题栏居中的方法
     */
    private void createTitle(LinearLayout layout, String title) {
        if (!TextUtils.isEmpty(title)) {
            if (centerTitle == null) {
                final Context context = getContext();
                centerTitle = new TextView(context);
                if (mTitleTextAppearance != 0) {
                    centerTitle.setTextAppearance(context, mTitleTextAppearance);
                }
                centerTitle.setSingleLine();
                centerTitle.setEllipsize(TextUtils.TruncateAt.END);
                centerTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.toolbar_title_size));
                if (mTitleTextColor != 0) {
                    centerTitle.setTextColor(mTitleTextColor);
                }
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            centerTitle.setLayoutParams(layoutParams);

            layout.addView(centerTitle);
        } else if (centerTitle != null) {
            layout.removeView(centerTitle);
        }
        if (centerTitle != null) {
            centerTitle.setText(title);
        }
    }

    @Override
    public void setTitleTextColor(@ColorInt int color) {
        super.setTitleTextColor(color);
        mTitleTextColor = color;
        if (centerTitle != null) {
            centerTitle.setTextColor(color);
        }
    }

    @Override
    public void setSubtitleTextColor(@ColorInt int color) {
        super.setSubtitleTextColor(color);
        mSubtitleTextColor = color;
        if (centerSubTitle != null) {
            centerSubTitle.setTextColor(color);
        }

    }

    /**
     * 子标题栏居中
     *
     * @param subTitle
     */
    private void createSubTitle(LinearLayout layout, String subTitle) {
        if (!TextUtils.isEmpty(subTitle)) {
            if (centerSubTitle == null) {
                final Context context = getContext();
                centerSubTitle = new TextView(context);
                if (mSubtitleTextAppearance != 0) {
                    centerSubTitle.setTextAppearance(context, mSubtitleTextAppearance);
                }
                centerSubTitle.setSingleLine();
                centerSubTitle.setEllipsize(TextUtils.TruncateAt.END);
                centerSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.toolbar_subtitle_size));
                if (mSubtitleTextColor != 0) {
                    centerSubTitle.setTextColor(mSubtitleTextColor);
                }

            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            centerSubTitle.setLayoutParams(layoutParams);
            layout.addView(centerSubTitle);
        } else if (centerSubTitle != null) {
            layout.removeView(centerSubTitle);
        }
        if (centerSubTitle != null) {
            centerSubTitle.setText(subTitle);
        }
    }

    /**
     * 无用的代码
     *
     * @param view
     * @return
     */
    private boolean shouldLayout(View view) {
        return view != null && view.getParent() == this && view.getVisibility() != GONE;
    }
}
