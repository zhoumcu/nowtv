package com.pccw.nowplayer.widget;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


import com.pccw.nowplayer.R;
import com.pccw.nowplayer.widget.wheel.view.WheelOptions;

import java.util.ArrayList;


/**
 * @author Kevin
 */
public class SettingDialog<T> extends Dialog implements View.OnClickListener {

    public static final int SHOW = 1;
    public static final int DISMISS = 0;
    Activity context;
    WheelOptions wheelOptions;
    Button bt_done;
    private OnOptionsSelectListener optionsSelectListener;


    ArrayList<T> options1Items;
    ArrayList<ArrayList<String>> options2Items;
    int option1, option2;

    public SettingDialog(Activity context) {
        super(context, R.style.setting);
        this.context = context;


    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_picker);
        bt_done = (Button) findViewById(R.id.bt_done);
        bt_done.setOnClickListener(this);
        final View optionspicker = findViewById(R.id.optionspicker);
        wheelOptions = new WheelOptions(optionspicker);

        wheelOptions.setPicker(options1Items, options2Items, false);
        wheelOptions.setCurrentItems(option1, option2);
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = display.getWidth();
        lp.height = display.getHeight();
        this.getWindow().setAttributes(lp);
    }

    public void setPicker(ArrayList<T> options1Items,
                          ArrayList<ArrayList<String>> options2Items) {
        this.options1Items = options1Items;
        this.options2Items = options2Items;
    }

    /**
     * 设置选中的item位置
     *
     * @param option1
     */
    public void setSelectOptions(int option1) {
        wheelOptions.setCurrentItems(option1, 0);
    }

    /**
     * 设置选中的item位置
     *
     * @param option1
     * @param option2
     */
    public void setSelectOptions(int option1, int option2) {
        this.option1 = option1;
        this.option2 = option2;
    }

    public interface OnOptionsSelectListener {
        void onOptionsSelect(int options1, int option2);

    }

    public void setOnoptionsSelectListener(
            OnOptionsSelectListener optionsSelectListener) {
        this.optionsSelectListener = optionsSelectListener;
    }

    @Override
    public void onClick(View v) {
        if (optionsSelectListener != null) {
            int[] optionsCurrentItems = wheelOptions.getCurrentItems();
            optionsSelectListener.onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1]);
        }
        dismiss();
        return;
    }

}
