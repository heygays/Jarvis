package com.heygays.jarvis.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageButton;

import com.heygays.jarvis.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义SwitchButton(仿Android 5.0自带)
 *
 * @author HeyGays
 */
public class SwitchButton extends ImageButton {

    private final int ROUND_MARGIN = 2;// 圆点按钮的外边距dp，避免圆点边缘被遮盖

    private Paint paint;// 画圆点的笔
    private boolean isOn;// 开关状态，左关右开
    private float touchX = 0;// 手指按下的x坐标
    private int touchSlop;// 最小滑动距离
    private int backHeight;// 背景高度dp
    private int backWidth;// 背景宽度dp
    private int backPadding;// 背景的padding dp
    private int backColor_Off;// 关闭状态背景的填充颜色
    private int backColor_On;// 打开状态背景的填充颜色
    private int backRoundRadius;// 背景的圆角半径dp
    private int defaultRoundBtnColor;//关闭状态圆点的颜色
    private int opendRoundBtnColor;//打开状态圆点的颜色
    private int m = ROUND_MARGIN;// 相对于最左的位移距离 dp
    private int m_Max;// m的最大坐标
    private int round_Size;// 圆点的大小
    private GradientDrawable gd;// 背景drawble
    private RectF roundBtnRect = new RectF();// 圆点所在矩形范围

    private OnSwitchChanged onSwitchChanged;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        backPadding = (int) a.getDimension(R.styleable.SwitchButton_backPadding, 5);
        backColor_On = a.getColor(R.styleable.SwitchButton_backColorOn, Color.DKGRAY);
        backColor_Off = a.getColor(R.styleable.SwitchButton_backColorOn, Color.GRAY);
        defaultRoundBtnColor = a.getColor(R.styleable.SwitchButton_defaultRoundBtnColor, Color.LTGRAY);
        opendRoundBtnColor = a.getColor(R.styleable.SwitchButton_openedRoundBtnColor, Color.LTGRAY);
        a.recycle();
    }

    /**
     * 初始化一些尺寸
     * 只需要给出高度就自动计算合适的宽度
     */
    private void initSize() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        //背景的半径等于高度
        backRoundRadius = backHeight;
        // 根据高度计算圆点大小
        round_Size = backHeight - 2 * ROUND_MARGIN;
        // 根据圆点大小计算开关宽度
        backWidth = 2 * (round_Size + ROUND_MARGIN);
        // 根据宽度计算最大位移坐标
        m_Max = backWidth - ROUND_MARGIN - round_Size;
    }

    /**
     * 初始化背景
     */
    private void initBack() {
        int padding = dp2px(backPadding);
        gd = new GradientDrawable();
        gd.setColor(backColor_Off);
        gd.setCornerRadius(dp2px(backRoundRadius));
        if (backColor_Off == Color.WHITE || padding == 0) {
            gd.setStroke(1, Color.parseColor("#bdbdbd"));
        }
        setPadding(padding, padding, padding, padding);
        setImageDrawable(gd);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);// 关闭硬件加速
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(defaultRoundBtnColor);
        paint.setStyle(Style.FILL);
        paint.setDither(true);
        paint.setShadowLayer(1, 0, 0, Color.DKGRAY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            backHeight = px2dp(heightSize);
        } else {
            backHeight = 24;
        }
        initSize();
        initBack();
        initPaint();
        setMeasuredDimension(dp2px(backWidth), dp2px(backHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        roundBtnRect.left = dp2px(m);
        roundBtnRect.top = dp2px(ROUND_MARGIN);
        roundBtnRect.right = roundBtnRect.left + dp2px(round_Size);
        roundBtnRect.bottom = roundBtnRect.top + dp2px(round_Size);
        canvas.drawArc(roundBtnRect, 0, 360, false, paint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                toChange();
                break;
        }
        return true;
    }


    /**
     * 改变状态
     */
    private void toChange() {
        Timer timer = new Timer();
        if (!isOn) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (m < m_Max) {
                        m++;
                        postInvalidate();
                    } else {
                        cancel();
                        post(new Runnable() {
                            @Override
                            public void run() {
                                opend();
                            }
                        });
                    }
                }
            }, 0, backWidth / 10);
        } else {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (m > ROUND_MARGIN) {
                        m--;
                        postInvalidate();
                    } else {
                        cancel();
                        post(new Runnable() {
                            @Override
                            public void run() {
                                closed();
                            }
                        });
                    }
                }
            }, 0, backWidth / 10);
        }
    }

    private void opend() {
        m = m_Max;
        isOn = true;
        gd.setColor(backColor_On);
        if (backColor_On == Color.WHITE || backPadding == 0) {
            gd.setStroke(1, Color.parseColor("#bdbdbd"));
        }
        setImageDrawable(gd);
        setRoundBtnColor(opendRoundBtnColor);
        if (onSwitchChanged != null) {
            onSwitchChanged.opened(this);
        }
    }

    private void closed() {
        m = ROUND_MARGIN;
        isOn = false;
        gd.setColor(backColor_Off);
        if (backColor_Off == Color.WHITE || backPadding == 0) {
            gd.setStroke(1, Color.parseColor("#bdbdbd"));
        }
        setImageDrawable(gd);
        setRoundBtnColor(defaultRoundBtnColor);
        if (onSwitchChanged != null) {
            onSwitchChanged.closed(this);
        }
    }

    /**
     * dip转为 px
     */
    private int dp2px(int dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int px2dp(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 设置圆点的颜色
     *
     * @param roundBtnColor
     */
    public void setRoundBtnColor(int roundBtnColor) {
        paint.setColor(roundBtnColor);
        invalidate();
    }


    /**
     * 设置背景padding
     *
     * @param backPadding
     */
    public void setBackPadding(int backPadding) {
        if (backPadding <= 0) {
            backPadding = 0;
        }
        if (backPadding > backHeight) {
            backPadding = this.backPadding;
        }
        this.backPadding = backPadding;
        initBack();
    }

    /**
     * 设置关闭的背景颜色
     *
     * @parambackColor_On
     */
    public void setBackColorWithOff(int backColor_Off) {
        this.backColor_Off = backColor_Off;
        initBack();
    }

    /**
     * 设置打开的背景颜色
     *
     * @param backColor_On
     */
    public void setBackColorWithOn(int backColor_On) {
        this.backColor_On = backColor_On;
        initBack();
    }

    /**
     * 返回开关状态
     *
     * @return
     */
    public boolean isOn() {
        return isOn;
    }

    public void setOnSwithChanged(OnSwitchChanged onSwitchChanged) {
        this.onSwitchChanged = onSwitchChanged;
    }

    public interface OnSwitchChanged {
        void opened(SwitchButton switchButton);

        void closed(SwitchButton switchButton);
    }
}
