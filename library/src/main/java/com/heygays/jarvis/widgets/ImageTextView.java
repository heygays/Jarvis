package com.heygays.jarvis.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heygays.jarvis.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Administrator
 * @description: 图片文字view, 文字可在图片的上下左右
 * @date: 2016-03-08
 * @time: 17:32
 */
public class ImageTextView extends LinearLayout {
    private ImageView centerImg;
    private TextView topTv;
    private TextView bottomTv;
    private TextView leftTv;
    private TextView rightTv;
    private List<TextView> visibleTvList = new ArrayList<TextView>();
    private int defaultImg;
    private int checkedImg;
    private int defaultColor;
    private int checkedColor;
    private int innerPadding;
    private int textSize;
    private String topText;
    private String bottomText;
    private String leftText;
    private String rightText;
    private boolean isChecked = false;

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.sly_image_text_view, this, true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView);
        isChecked = a.getBoolean(R.styleable.ImageTextView_checkedStatus, false);
        defaultImg = a.getResourceId(R.styleable.ImageTextView_defaultImg, R.drawable.ic_launcher);
        checkedImg = a.getResourceId(R.styleable.ImageTextView_checkedImg, R.drawable.ic_launcher);
        defaultColor = a.getColor(R.styleable.ImageTextView_defaultTextColor, 0xff000000);
        checkedColor = a.getColor(R.styleable.ImageTextView_checkedTextColor, 0xff000000);
        innerPadding = (int) a.getDimension(R.styleable.ImageTextView_innerPadding, 0);
        topText = a.getString(R.styleable.ImageTextView_topText);
        bottomText = a.getString(R.styleable.ImageTextView_bottomText);
        leftText = a.getString(R.styleable.ImageTextView_leftText);
        rightText = a.getString(R.styleable.ImageTextView_rightText);
        textSize = (int) a.getDimension(R.styleable.ImageTextView_textSize, sp2px(14));
        a.recycle();
        initView();
    }


    private void initView() {
        centerImg = (ImageView) findViewById(R.id.center_img);
        topTv = (TextView) findViewById(R.id.top_txt);
        bottomTv = (TextView) findViewById(R.id.bottom_txt);
        leftTv = (TextView) findViewById(R.id.left_txt);
        rightTv = (TextView) findViewById(R.id.right_txt);
        if (!TextUtils.isEmpty(topText)) {
            setTopTxt(topText);
        }
        if (!TextUtils.isEmpty(bottomText)) {
            setBottomTxt(bottomText);
        }
        if (!TextUtils.isEmpty(leftText)) {
            setLeftTxt(leftText);
        }
        if (!TextUtils.isEmpty(rightText)) {
            setRightTxt(rightText);
        }
        setCenterImg(defaultImg);
        if (innerPadding != 0) {
            LayoutParams lp = (LayoutParams) centerImg.getLayoutParams();
            lp.setMargins(innerPadding, innerPadding, innerPadding, innerPadding);
        }

        setChecked(isChecked);
    }

    public void setCenterImg(int resId) {
        centerImg.setImageResource(resId);
    }

    public TextView setTopTxt(String text) {
        topTv.setVisibility(View.VISIBLE);
        topTv.setText(text);
        topTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        visibleTvList.remove(topTv);
        visibleTvList.add(topTv);
        return topTv;
    }

    public TextView setBottomTxt(String text) {
        bottomTv.setVisibility(View.VISIBLE);
        bottomTv.setText(text);
        bottomTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        visibleTvList.remove(bottomTv);
        visibleTvList.add(bottomTv);
        return bottomTv;
    }

    public TextView setLeftTxt(String text) {
        leftTv.setVisibility(View.VISIBLE);
        leftTv.setText(text);
        leftTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        visibleTvList.remove(leftTv);
        visibleTvList.add(leftTv);
        return leftTv;
    }

    public TextView setRightTxt(String text) {
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText(text);
        rightTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        visibleTvList.remove(rightTv);
        visibleTvList.add(rightTv);
        return rightTv;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setCheckedColor(int checkedColor) {
        this.checkedColor = checkedColor;
    }

    public void setDefaultImg(int resId) {
        defaultImg = resId;
    }

    public void setCheckedImg(int resId) {
        checkedImg = resId;
    }

    public void setChecked(boolean isChecked) {
        if (isChecked) {
            centerImg.setImageResource(checkedImg);
            if (checkedColor != 0xff000000) {
                for (TextView visibleTv : visibleTvList) {
                    visibleTv.setTextColor(checkedColor);
                }
            }
        } else {
            centerImg.setImageResource(defaultImg);
            if (defaultColor != 0xff000000) {
                for (TextView visibleTv : visibleTvList) {
                    visibleTv.setTextColor(defaultColor);
                }
            }
        }
        this.isChecked = isChecked;
    }

    public boolean getChecked() {
        return isChecked;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
