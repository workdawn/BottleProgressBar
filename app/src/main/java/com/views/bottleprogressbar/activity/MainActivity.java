package com.views.bottleprogressbar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.views.bottleprogressbar.R;
import com.views.bottleprogressbar.base.App;
import com.views.bottleprogressbar.utils.DeviceInformation;
import com.views.bottleprogressbar.widget.BottleProgressBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 测试页面
 * Created by Administrator on 2017/7/28.
 */
public class MainActivity extends Activity{
    int i = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int[] p = DeviceInformation.getScreenResolution(this);
        App.getInstance().setWidth(p[0]);
        App.getInstance().setHeight(p[1] + DeviceInformation.getBottomStatusHeight(this));

        setContentView(R.layout.activity_main);

        final BottleProgressBar bar = (BottleProgressBar) findViewById(R.id.progress);

        Button plus_btn = (Button) findViewById(R.id.plus);
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i += 1;
                bar.setProgress( i );
            }
        });

        Button cut_btn = (Button) findViewById(R.id.cut);
        cut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i -= 1;
                bar.setProgress(i);
            }
        });
    }

}
