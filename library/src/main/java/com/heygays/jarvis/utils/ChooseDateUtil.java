package com.heygays.jarvis.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.heygays.jarvis.R;
import com.heygays.jarvis.interfaces.ChooseDateInterface;

import java.lang.reflect.Field;
import java.util.Calendar;

public class ChooseDateUtil implements View.OnClickListener, NumberPicker.OnValueChangeListener {

    static Context context;
    AlertDialog dialog;
    ChooseDateInterface dateInterface;
    NumberPicker npYear, npMonth, npDay;
    FrameLayout tvCancel, tvSure;
    TextView npDayWordTv;
    int[] newDateArray = new int[3];

    public static ChooseDateUtil build() {
        return new ChooseDateUtil();
    }

    public void createDialog(Context context, String[] oldDateArray, ChooseDateInterface dateInterface) {
        createDialog(context, oldDateArray, false, dateInterface);
    }

    public void createDialog(Context context, String[] oldDateArray, boolean hideDay, ChooseDateInterface dateInterface) {
        this.context = context;
        this.dateInterface = dateInterface;
        initWidgets();
        //有时不需要“日”那一栏
        if (hideDay) {
            npDay.setVisibility(View.GONE);
            npDayWordTv.setVisibility(View.GONE);
        }
        try {
            newDateArray[0] = Integer.parseInt(oldDateArray[0]);
        } catch (Exception e) {
            newDateArray[0] = Calendar.getInstance().get(Calendar.YEAR);
        }
        try {
            newDateArray[1] = Integer.parseInt(oldDateArray[1]);
        } catch (Exception e) {
            newDateArray[1] = Calendar.getInstance().get(Calendar.MONTH) + 1;
        }
        try {
            newDateArray[2] = Integer.parseInt(oldDateArray[2]);
        } catch (Exception e) {
            newDateArray[2] = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        //设置选择器最小值、最大值
        npYear.setMinValue(1900);
        npYear.setMaxValue(2100);
        npMonth.setMinValue(1);
        npMonth.setMaxValue(12);
        npDay.setMinValue(1);
        npDay.setMaxValue(31);
        //设置选择器初始值
        npYear.setValue(newDateArray[0]);
        npMonth.setValue(newDateArray[1]);
        npDay.setValue(newDateArray[2]);
        //设置监听
        npYear.setOnValueChangedListener(this);
        npMonth.setOnValueChangedListener(this);
        npDay.setOnValueChangedListener(this);
        //去除分割线
        setNumberPickerDividerColor(npYear);
        setNumberPickerDividerColor(npMonth);
        setNumberPickerDividerColor(npDay);
        //设置字体颜色
        setNumberPickerTextColor(npYear, Color.parseColor("#656565"));
        setNumberPickerTextColor(npMonth, Color.parseColor("#656565"));
        setNumberPickerTextColor(npDay, Color.parseColor("#656565"));

    }

    private void initWidgets() {
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.sly_dialog_choose_date);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DimenUtil.dip2px(context, 300);
        window.setAttributes(params);
        //初始化控件
        tvCancel = (FrameLayout) window.findViewById(R.id.tvCancel);
        tvSure = (FrameLayout) window.findViewById(R.id.tvSure);
        tvCancel.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        npYear = (NumberPicker) window.findViewById(R.id.npYear);
        npMonth = (NumberPicker) window.findViewById(R.id.npMonth);
        npDay = (NumberPicker) window.findViewById(R.id.npDay);
        npDayWordTv = (TextView) window.findViewById(R.id.npDay_wordTv);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCancel) {
            dialog.dismiss();
        } else if (v.getId() == R.id.tvSure) {
            dialog.dismiss();
            dateInterface.sure(newDateArray);
        }
    }

    //选择器选择值监听
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker.getId() == R.id.npYear) {
            newDateArray[0] = newVal;
        } else if (picker.getId() == R.id.npMonth) {
            newDateArray[1] = newVal;
            npDay.setMaxValue(DateUtil.getNumberOfDays(newDateArray[0], newDateArray[1]));
        } else if (picker.getId() == R.id.npDay) {
            newDateArray[2] = newVal;
        }
    }

    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(Color.parseColor("#00000000")));// pf.set(picker, new Div)
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        boolean result = false;
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof TextView) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setTextSize(DimenUtil.sp2px(context, 16));
                    ((TextView) child).setTextColor(color);
                    ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    numberPicker.invalidate();
                    result = true;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
