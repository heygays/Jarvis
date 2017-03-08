package com.heygays.jarvisdemo;

import com.heygays.jarvis.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void setContentViewOver() {

    }

    @Override
    protected void initUI() {
        showLoading(R.layout.layout_loading);
    }

    @Override
    protected void initData() {

    }

}
