package com.pccw.nowplayer.utils;

import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import com.pccw.nowplayer.R;

public class TextUtil {


    private static final int DEFAULT_OFFSET = 3;
    public static int MAX_LINES = 3;

    public static void setMore(final TextView textV, String content, final String ellipsis, final String strmore) {
        textV.setText(content);
        setMore(textV, ellipsis, strmore);
    }

    public static void setMore(final TextView textV, final String ellipsis,
                               final String strmore) {

        if (textV == null) {
            return;
        }
        textV.setEllipsize(TruncateAt.END);
        textV.setVerticalScrollBarEnabled(false);
        textV.setTag(false);

        ViewTreeObserver vto = textV.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Object value = textV.getTag();
                Boolean b = false;
                if (value instanceof Boolean) {
                    b = (Boolean) value;
                }
                if (!b) {
                    int maxLines = MAX_LINES;
                    int lines = textV.getLineCount();

                    if (lines > maxLines) {
                        Layout layout = textV.getLayout();
                        String str = layout.getText().toString();
                        int end = layout.getLineEnd(maxLines - 1);
                        str = str.substring(0, end - ellipsis.length() - strmore.length() - DEFAULT_OFFSET);
                        String strall = textV.getText().toString();
                        textV.setTag(true);
                        SpannableString spanstr;
                        spanstr = new SpannableString(str + ellipsis + strmore);
                        spanstr.setSpan(new MyClickableSpan(strall, textV
                                        .getResources().getColor(
                                        R.color.more_color)),
                                spanstr.length() - strmore.length(),
                                spanstr.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textV.setText(spanstr);
                        textV.setHighlightColor(textV.getResources().getColor(
                                R.color.more_color));
                        textV.setMovementMethod(LinkMovementMethod
                                .getInstance());
                    }
                }
            }
        });

    }

    static class MyClickableSpan extends ClickableSpan {
        private int color;
        private String str;

        public MyClickableSpan(String str, int color) {
            this.str = str;
            this.color = color;
        }

        @Override
        public void onClick(View view) {
            ((TextView) view).setMovementMethod(new ScrollingMovementMethod());
            ((TextView) view).setText(str);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(color); // ���á��鿴���ࡱ������ɫ
            ds.setUnderlineText(false); // ���á��鿴���ࡱ���»��ߣ�Ĭ����
            ds.clearShadowLayer();
        }
    }
}