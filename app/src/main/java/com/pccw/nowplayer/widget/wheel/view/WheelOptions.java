package com.pccw.nowplayer.widget.wheel.view;

import java.util.ArrayList;

import android.view.View;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.widget.wheel.adapter.ArrayWheelAdapter;
import com.pccw.nowplayer.widget.wheel.lib.WheelView;
import com.pccw.nowplayer.widget.wheel.listener.OnItemSelectedListener;


public class WheelOptions<T> {
    private View view;
    private WheelView wv_option1;
    private WheelView wv_option2;

    private ArrayList<T> mOptions1Items;
    private ArrayList<ArrayList<T>> mOptions2Items;

    private boolean linkage = false;
    private OnItemSelectedListener wheelListener_option1;
    private OnItemSelectedListener wheelListener_option2;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public WheelOptions(View view) {
        super();
        this.view = view;
        setView(view);
    }


    public void setPicker(ArrayList<T> options1Items,
                          ArrayList<ArrayList<T>> options2Items, boolean linkage) {
        setPicker(options1Items, options2Items, null, linkage);
    }

    public void setPicker(ArrayList<T> options1Items,
                          ArrayList<ArrayList<T>> options2Items,
                          ArrayList<ArrayList<ArrayList<T>>> options3Items,
                          boolean linkage) {
        this.linkage = linkage;
        this.mOptions1Items = options1Items;
        this.mOptions2Items = options2Items;
        int len = ArrayWheelAdapter.DEFAULT_LENGTH;
        if (this.mOptions2Items == null)
            len = 3;
        // 选项1
        wv_option1 = (WheelView) view.findViewById(R.id.options1);
        wv_option1.setAdapter(new ArrayWheelAdapter(mOptions1Items, len));// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据
        // 选项2
        wv_option2 = (WheelView) view.findViewById(R.id.options2);
        if (mOptions2Items != null)
            wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items.get(0)));// 设置显示数据
        wv_option2.setCurrentItem(wv_option1.getCurrentItem());// 初始化时显示的数据
        // 选项3

        int textSize = 25;
        wv_option1.setTextSize(textSize);
        wv_option2.setTextSize(textSize);
        wv_option1.setCyclic(false);
        wv_option2.setCyclic(false);
        if (this.mOptions2Items == null)
            wv_option2.setVisibility(View.GONE);

        // 联动监听器
        wheelListener_option1 = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(int index) {
                int opt2Select = 0;
                if (mOptions2Items != null) {
                    opt2Select = wv_option2.getCurrentItem();//上一个opt2的选中位置
                    //新opt2的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                    opt2Select = opt2Select >= mOptions2Items.get(index).size() - 1 ? mOptions2Items.get(index).size() - 1 : opt2Select;

                    wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items
                            .get(index)));
                    wv_option2.setCurrentItem(opt2Select);
                }
            }
        };
        wheelListener_option2 = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(int index) {
//				// TODO: 2016/6/6
            }
        };

//		// 添加联动监听
        if (options2Items != null && linkage)
            wv_option1.setOnItemSelectedListener(wheelListener_option1);
        if (options3Items != null && linkage)
            wv_option2.setOnItemSelectedListener(wheelListener_option2);
    }

    /**
     * 设置选项的单位
     *
     * @param label1
     * @param label2
     * @param label3
     */
    public void setLabels(String label1, String label2, String label3) {
        if (label1 != null)
            wv_option1.setLabel(label1);
        if (label2 != null)
            wv_option2.setLabel(label2);
    }

    /**
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wv_option1.setCyclic(cyclic);
        wv_option2.setCyclic(cyclic);
    }

    /**
     * @return
     */
    public int[] getCurrentItems() {
        int[] currentItems = new int[3];
        currentItems[0] = wv_option1.getCurrentItem();
        currentItems[1] = wv_option2.getCurrentItem();
        return currentItems;
    }

    public void setCurrentItems(int option1, int option2) {
        if (linkage) {
            itemSelected(option1, option2);
        }
        wv_option1.setCurrentItem(option1);
        wv_option2.setCurrentItem(option2);
    }

    private void itemSelected(int opt1Select, int opt2Select) {
        if (mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items
                    .get(opt1Select)));
            wv_option2.setCurrentItem(opt2Select);
        }
    }


}
