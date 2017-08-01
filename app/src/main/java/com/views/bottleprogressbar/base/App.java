package com.views.bottleprogressbar.base;

import android.app.Application;

/**
 * application
 * Created by Administrator on 2017/7/28.
 */
public class App extends Application {

    /**
     * application实例
     */
    private static App app;
    /**
     * 获得application实例
     */

    int width;
    int height;

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
