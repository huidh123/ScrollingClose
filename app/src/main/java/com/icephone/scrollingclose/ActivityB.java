package com.icephone.scrollingclose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ScrollingClose.ScrollCloseActivity;

/**
 * Created by 晨晖 on 2016-02-22.
 */
public class ActivityB extends ScrollCloseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        initScrollView();

        findViewById(R.id.img2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityB.this, "点击事件", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
