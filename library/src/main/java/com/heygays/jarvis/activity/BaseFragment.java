package com.heygays.jarvis.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author: HeyGays
 * @description:
 * @date: 2016-06-20
 * @time: 17:17
 */
public abstract class BaseFragment extends Fragment {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    private BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        bindView(view);
        initView(view, savedInstanceState);
        return view;
    }

    /**
     * 获取布局文件
     *
     * @return
     */
    public abstract int getLayoutRes();

    /**
     * 针对buterrknife的方法，在initview之前要先 ButterKnife.bind(this, getView());
     *
     * @param view
     */
    public void bindView(View view) {

    }

    /**
     * 初始化视图,如果使用了ButterKnife，这部分其实没啥用
     */
    public abstract void initView(View view, Bundle savedInstanceState);

    /**
     * 获取当前Fragment状态
     *
     * @return true为正常 false为未加载或正在删除
     */
    public boolean getStatus() {
        return (isAdded() && !isRemoving());
    }

    /**
     * 获取Activity
     *
     * @return
     */
    public BaseActivity getBaseActivity() {
        if (mActivity == null) {
            mActivity = (BaseActivity) getActivity();
        }
        return mActivity;
    }

    /**
     * 我们知道了发生Fragment重叠的根本原因在于FragmentState没有保存Fragment的显示状态，
     * 即mHidden，导致页面重启后，该值为默认的false，即show状态，所以导致了Fragment的重叠
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }
}
