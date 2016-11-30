package com.heygays.jarvis.widgets;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 下拉刷新布局，如果其中包含了listview或者RecyclerView还会提供加载更多的功能
 * 1.SwipeRefreshLayout支持的模型：SwipeRefreshLayout ------> ListView/GirdView/RecycleView/ScrollView（可滚动view）
 * 2.RefreshLayout支持的模型：
 * 2.1兼容上面
 * 2.2RefreshLayout ------> LinearLayout------> ListView/GirdView/RecycleView/ScrollView+otherView
 */
public class RefreshLayout extends SwipeRefreshLayout {

    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;
    /**
     * listview实例
     */
    private AbsListView mListView;

    private RecyclerView mRecyclerView;
    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;
    /**
     * 自动刷新监听器
     */
    private OnRefreshListener mListener;
    /**
     * 加载更多底部布局，这里可以自定义上拉加载的样式
     */
    // private View mListViewFooter;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mYup;
    /**
     * 用于判断用户手指初始移动方向，判断上拉或者下来
     */
    private int mYMove;
    /**
     * 初始移动方向0没移动，1向下，-1向上
     */
    private int mDirection;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;
    /**
     * listview滑动监听器
     */
    private ListScrollListener listScrollListener;
    /**
     * Recyclerview滑动监听器
     */
    private RecyclerScrollListener recyclerScrollListener;

    /**
     * @param context
     */
    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // 到达底部时的加载布局,改成了和下拉刷新一样的顶部转圈
        // mListViewFooter=LayoutInflater.from(context).inflate(R.layout.sly_myfooter,null,false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mListView == null && mRecyclerView == null) {
            getScrollView();
        }
    }


    /**
     * 获取包裹的view是否是可以滚动的对象
     */
    private void getScrollView() {
        int childs = getChildCount();
        //这里注意：因为SwipeRefreshLayout本身自带了一个CircleImageView就是那个转动的圈圈，所以找我们的们要从第1个开始
        if (childs > 1) {
            View childView = getChildAt(1);
            if (childView instanceof LinearLayout) {
                LinearLayout container = (LinearLayout) childView;
                for (int i = 0; i < container.getChildCount(); i++) {
                    View sonView = container.getChildAt(i);
                    if (checkChild(sonView)) {
                        break;
                    }
                }
            } else {
                checkChild(childView);
            }
        }

    }

    private boolean checkChild(View child) {
        boolean b = false;
        if (child instanceof AbsListView) {
            mListView = (ListView) child;
            b = true;
        } else if (child instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) child;
            /**
             * 使用 RecyclerView 加SwipeRefreshLayout下拉刷新的时候，如果绑定的 List 对象在更新数据之前进行了 clear，
             * 而这时用户紧接着迅速上滑 RecyclerView，就会造成崩溃，而且异常不会报到我们的代码上，属于RecyclerView内部错误。
             * 目前的解决方案：在下拉刷新时，也就是 clear 的同时，让 RecyclerView 暂时不能够滑动，
             */
            mRecyclerView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return isRefreshing();
                }
            });
            b = true;
        }
        return b;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                mDirection = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mListView != null && listScrollListener == null) {
                    listScrollListener = new ListScrollListener();
                    mListView.setOnScrollListener(listScrollListener);
                }
                if (mRecyclerView != null && recyclerScrollListener == null) {
                    recyclerScrollListener = new RecyclerScrollListener();
                    mRecyclerView.addOnScrollListener(recyclerScrollListener);
                }
                // 移动
                if (mDirection == 0) {//未确定方向
                    mYMove = (int) event.getRawY();
                    if (mYMove - mYDown > 0) {
                        mDirection = 1;
                    } else if (mYMove - mYDown < 0) {
                        mDirection = -1;
                    } else {
                        mDirection = 0;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // 抬起
                mYup = (int) event.getRawY();
                if (canLoad()) {
                    loadData();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */

    private boolean canLoad() {
        boolean isCanLoad = false;
        if (mListView != null) {
            isCanLoad = isListViewBottom() && !isLoading && !isRefreshing() && isPullUp();
        }
        if (mRecyclerView != null) {
            isCanLoad = isRecyclerViewBottom() && !isLoading && !isRefreshing() && isPullUp();
        }
        return isCanLoad;
    }

    /**
     * 判断Listview是否到了最底部
     */
    private boolean isListViewBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 是否是上拉动作
     *
     * @return
     */
    private boolean isPullUp() {
        return mDirection == -1 && (mYDown - mYup) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            mOnLoadListener.onLoad();
        }
    }

    /**
     * listview的滑动监听器
     */
    class ListScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            boolean enable = false;
            if (mListView != null && mListView.getChildCount() > 0) {
                boolean firstItemVisible = mListView.getFirstVisiblePosition() == 0;
                boolean topOfFirstItemVisible = mListView.getChildAt(0).getTop() == 0;
                boolean enable = firstItemVisible && topOfFirstItemVisible;
                setEnabled(enable);
            }
            // 滚动时到了最底部也可以加载更多
            if (canLoad()) {
                loadData();
            }

        }
    }

    /**
     * recyclerview的滑动监听
     */
    class RecyclerScrollListener extends RecyclerView.OnScrollListener {
        public RecyclerScrollListener() {
            super();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (canLoad()) {
                loadData();
            }
        }
    }

    /**
     * 判断RecyclerView是否滑倒底部
     *
     * @return
     */
    private boolean isRecyclerViewBottom() {
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null) {
            RecyclerView.LayoutManager rlm = mRecyclerView.getLayoutManager();
            if (rlm instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) rlm;
                return llm.findLastVisibleItemPosition() == llm.getItemCount() - 1;
            } else if (rlm instanceof GridLayoutManager) {
                GridLayoutManager glm = (GridLayoutManager) rlm;
                return glm.findLastVisibleItemPosition() == glm.getItemCount() - 1;
            } else if (rlm instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager slm = (StaggeredGridLayoutManager) rlm;
                int span = slm.getSpanCount();
                boolean isBottom = false;
                for (int i = 0; i < span; i++) {
                    if (slm.findLastVisibleItemPositions(null)[i] == slm.getItemCount() - 1) {
                        isBottom = true;
                        break;
                    }
                }
                return isBottom;
            }
        }
        return false;
    }

    /**
     * "加载更多"监听器
     */
    public interface OnLoadListener {
        void onLoad();

    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;

    }


    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            // mListView.addFooterView(mListViewFooter);//添加自己自定义的底部view
            setRefreshing(true);// 启用顶部转圈
        } else {
            // mListView.removeFooterView(mListViewFooter);//移除自己自定义的底部view
            setRefreshing(false);// 停止顶部转圈
            mYDown = 0;
            mYup = 0;
        }
    }


    public void setAutoRefresh() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                mListener.onRefresh();
            }
        }, 0);
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mListener = listener;
        super.setOnRefreshListener(listener);
    }


}
