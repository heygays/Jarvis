package com.heygays.jarvis.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

/**
 * @author: HeyGays
 * @description: 针对对话框的封装
 * @date: 2016-08-19
 * @time: 13:05
 */
public final class DialogHelper {

    private Activity context;
    private AlertDialog alertDialog;

    public static DialogHelper create(Activity context) {
        return new DialogHelper(context);
    }

    private DialogHelper(Activity context) {
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
    }

    public DialogHelper setTitle(String title) {
        alertDialog.setTitle(title);
        return this;
    }

    public DialogHelper setIcon(int resId) {
        alertDialog.setIcon(ContextCompat.getDrawable(context, resId));
        return this;
    }

    public DialogHelper setMsg(String msg) {
        alertDialog.setMessage(msg);
        return this;
    }

    public DialogHelper setLeftBtn(String leftTxt, DialogInterface.OnClickListener listener) {
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, leftTxt, listener);
        return this;
    }

    public DialogHelper setRightBtn(String rightTxt, DialogInterface.OnClickListener listener) {
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, rightTxt, listener);
        return this;
    }

    public DialogHelper setView(View view) {
        alertDialog.setView(view);
        return this;
    }

    public DialogHelper setCancelable(boolean flag) {
        alertDialog.setCancelable(flag);
        return this;
    }


    public void show() {
        alertDialog.show();
    }

    public void showResizeDialog(int w, int h) {
        show();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = w;
        params.height = h;
        alertDialog.getWindow().setAttributes(params);
    }

    public void showExit() {
        setTitle("提示")
                .setMsg("确定退出吗?")
                .setLeftBtn("否", null)
                .setRightBtn("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        context.finish();
                    }
                })
                .show();
    }

    public void showNotice(String noticeMsg) {
        setTitle("提示")
                .setMsg(noticeMsg)
                .setRightBtn("我知道了", null)
                .show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}
