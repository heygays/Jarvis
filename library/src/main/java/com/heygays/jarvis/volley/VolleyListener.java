package com.heygays.jarvis.volley;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.android.volley.VolleyError;

/**
 * volley监听接口
 *
 * @version 1.0.0
 * @tip 应该根据业务需求实现部分统一的onXXX处理逻辑
 * @time 2016/01/12
 */
public abstract class VolleyListener {

    public Context mContext;
    /**
     * Fragment还是Activity，当想使用反射时，它有用
     */
    public Object ob_this;

    /**
     * 子类不需要重写
     *
     * @param ob_this
     */
    public void setThis(Object ob_this) {
        this.ob_this = ob_this;
        if (ob_this instanceof Fragment) {
            mContext = ((Fragment) ob_this).getActivity();
        } else {
            mContext = ((Context) ob_this);
        }
    }


    /**
     * 请求成功
     */
    public abstract void onSuccess(String result);

    /**
     * 请求失败
     */
    public abstract void onFail(VolleyError error);

    /**
     * 正在请求
     *
     * @tip 这里可以转圈...
     */
    public void onLoading() {
    }


    /**
     * 请求完成
     *
     * @tip 取消转圈...
     */
    public void onFinish() {
    }

    /**
     * 无法连接网络
     *
     * @tip 弹出设置对话框||显示失败界面...
     */
    public void onNoNetwork() {
    }


    /**
     * 连接超时
     *
     * @tip 显示失败界面...
     */
    public void onTimeOut() {
    }


    /**
     * 连接服务器失败
     *
     * @tip 显示失败界面...
     */
    public void onServiceError() {
    }

}
