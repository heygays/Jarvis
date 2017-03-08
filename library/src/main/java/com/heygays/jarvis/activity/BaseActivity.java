package com.heygays.jarvis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.heygays.jarvis.R;
import com.heygays.jarvis.dialog.DialogHelper;

/**
 * 最基本的Activity
 *
 * @author HeyGays
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected LinearLayout baseRootView;
    protected FrameLayout baseContentLayout;
    protected LinearLayout baseLoadingLayout;
    protected AppBarLayout baseAppBarLayout;
    protected Toolbar baseToolBar;
    private View splitLineV;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sly_activity_base);
        Log.d("【Activity】", getClass().getSimpleName());
        initView();
    }


    private void initView() {
        baseRootView = (LinearLayout) findViewById(R.id.root_view);
        baseAppBarLayout = (AppBarLayout) findViewById(R.id.ac_base_appBarLayout);
        baseToolBar = (Toolbar) findViewById(R.id.ac_base_toolbar);
        baseToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.finish();
            }
        });
        baseContentLayout = (FrameLayout) findViewById(R.id.ac_base_contentlayout);
        baseLoadingLayout = (LinearLayout) findViewById(R.id.ac_base_loadinglayout);
        splitLineV = findViewById(R.id.ac_base_splitLine);
        //5.0以下appbar没有阴影所以用view划一条线
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            splitLineV.setVisibility(View.GONE);
        } else {
            splitLineV.setVisibility(View.VISIBLE);
        }
        if (getContentView() != 0) {
            addContentView(getContentView());
        }
        setContentViewOver();
        initUI();
        initData();
    }

    /**
     * 添加内容
     *
     * @param layoutId
     */
    private void addContentView(int layoutId) {
        LayoutInflater mInflater = LayoutInflater.from(this);
        baseContentLayout.setVisibility(View.VISIBLE);
        mInflater.inflate(layoutId, baseContentLayout);
    }

    /**
     * 加载内容
     *
     * @return
     */
    protected abstract int getContentView();

    /**
     * 布局加载完成(因为ButterKnife能找到所有的view必须在setContentView之后)
     */
    protected abstract void setContentViewOver();

    /**
     * 初始化控件及其行为
     */
    protected abstract void initUI();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 打开指定Activity
     *
     * @param clz
     */
    public void openActivity(Class<? extends Activity> clz) {
        Intent intent = new Intent(this, clz);
        startActivity(intent);
    }

    /**
     * 隐藏appbar的阴影
     */
    public void hideAppBarElevation() {
        //Tm的很日怪啊，xml里面不设置elevation才有阴影，设置不会有，所以要隐藏就设置一个
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            baseAppBarLayout.setTargetElevation(1);
        }
        splitLineV.setVisibility(View.GONE);

    }

    public Toolbar getBaseToolbar() {
        return baseToolBar;
    }


    /**
     * 显示loading
     *
     * @param layoutId
     */
    public void showLoading(int layoutId) {
        baseLoadingLayout.removeAllViews();
        baseLoadingLayout.setVisibility(View.VISIBLE);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mInflater.inflate(layoutId, baseLoadingLayout);
    }

    /**
     * 隐藏loading
     */
    public void dismissLoading() {
        baseLoadingLayout.setVisibility(View.GONE);
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showSnack(String text) {
        Snackbar.make(baseRootView, text, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 简单的提示对话框
     *
     * @param msg
     */
    public void showSimpleDialog(String msg) {
        DialogHelper.create(this)
                .setMsg(msg)
                .setRightBtn("确定", null)
                .show();
    }


/*----------------------Fragment相关-------------------------------*/

    /**
     * 获取管理器
     *
     * @return
     */
    public FragmentManager getBaseFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        return fragmentManager;
    }

    /**
     * 获取事务
     *
     * @return
     */
    public FragmentTransaction getFragmentTransaction() {
        return getBaseFragmentManager().beginTransaction();
    }

    /**
     * 替换一个Fragment并设置是否加入回退栈
     *
     * @param containerViewId
     * @param fragment
     * @param isAddToBackStack
     */
    public void replaceFragment(int containerViewId, BaseFragment fragment, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction();
        fragmentTransaction.replace(containerViewId, fragment, fragment.getClass().getSimpleName());
        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();

    }

    /**
     * 添加一个Fragment
     *
     * @param containerViewId
     * @param fragment
     */
    public void addFragment(int containerViewId, BaseFragment fragment, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction();
        fragmentTransaction.add(containerViewId, fragment, fragment.getClass().getSimpleName());
        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();
    }

    /**
     * 从Activity中移除一个Fragment，
     * 如果被移除的Fragment没有添加到回退栈，这个Fragment实例将会被销毁
     *
     * @param fragment
     */
    public void removeFragment(BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    /**
     * 会将view从UI中移除
     * 和remove()不同,此时fragment的状态依然由FragmentManager维护
     * 只是销毁其视图结构，实例并不会被销毁
     *
     * @param fragment
     */
    public void detachFragment(BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction();
        fragmentTransaction.detach(fragment);
        fragmentTransaction.commit();
    }

    /**
     * 显示一个Fragment
     *
     * @param fragment
     */
    public void showFragment(BaseFragment fragment) {
        if (fragment.isHidden()) {
            FragmentTransaction fragmentTransaction = getFragmentTransaction();
            fragmentTransaction.show(fragment);
            fragmentTransaction.commit();
        }
    }

    /**
     * 隐藏一个Fragment
     *
     * @param fragment
     */
    public void hideFragment(BaseFragment fragment) {
        if (!fragment.isHidden()) {
            FragmentTransaction fragmentTransaction = getFragmentTransaction();
            fragmentTransaction.hide(fragment);
            fragmentTransaction.commit();
        }
    }
}
