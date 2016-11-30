package com.heygays.jarvis.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 倒计时Button
 */
public class CountDownButton extends Button {

    private int allTimeLenght;
    private String oldTxt;
    private Timer timer;

    public CountDownButton(Context context) {
        this(context, null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 开始倒计时，默认60秒
     */
    public void startCountDown() {
        startCountDown(60);
    }

    /**
     * 开始倒计时
     *
     * @param timeLenght 指定时间 单位S（秒）
     */
    public void startCountDown(int timeLenght) {
        final DecimalFormat df = new DecimalFormat("#00");
        allTimeLenght = timeLenght;
        oldTxt = getText().toString();
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                post(new Runnable() {
                    public void run() {
                        setText("(" + df.format(--allTimeLenght) + ")后重发");
                        setEnabled(false);
                        if (allTimeLenght < 0) {
                            stopCountDown();
                        }
                    }
                });
            }
        }, 0, 1000);

    }

    public void stopCountDown() {
        timer.cancel();
        setEnabled(true);
        setText(oldTxt);
    }
}
