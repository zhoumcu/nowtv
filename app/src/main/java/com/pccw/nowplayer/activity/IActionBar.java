package com.pccw.nowplayer.activity;

import android.view.View;

/**
 * Created by Swifty on 5/19/2016.
 */
public interface IActionBar {
    ActionBar showActionBar();

    class ActionBar {
        public boolean showImage;
        public String title;
        public boolean showSearch;
        public String subtitle;
        public View.OnClickListener titleClickAction;
        public boolean showMenu;
        public View[] additionView;


        public ActionBar(boolean showImage, String title, boolean showSearch) {
            this(showImage, title, showSearch, null);
        }

        public ActionBar(boolean showImage, String title, boolean showSearch, String subtitle) {
            this(showImage, title, showSearch, subtitle, null);
        }

        public ActionBar(boolean showImage, String title, boolean showSearch, String subtitle, View.OnClickListener titleClickAction) {
            this(showImage, title, showSearch, subtitle, titleClickAction, true,
                    null);
        }

        public ActionBar(boolean showImage, String title, boolean showSearch, String subtitle, View.OnClickListener titleClickAction, boolean showMenu, View... additionView) {
            this.showImage = showImage;
            this.title = title;
            this.showSearch = showSearch;
            this.subtitle = subtitle;
            this.titleClickAction = titleClickAction;
            this.showMenu = showMenu;
            this.additionView = additionView;
        }

        public ActionBar(String title) {
            this.title = title;
        }
    }
}
