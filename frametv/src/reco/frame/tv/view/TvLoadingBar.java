package reco.frame.tv.view;

import reco.frame.tv.R;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 待完善 斟酌使用 *加载图宽与高须为奇数 即以圆心完全对称
 * 
 * @author reco
 * 
 */
public class TvLoadingBar extends RelativeLayout {

	private final static int FLUSH = 0;
	/**
	 * 当前进度
	 */
	private int progress;
	/**
	 * 
	 */
	private final static int MAX = 100;
	/**
	 * 是否显示中间的进度
	 */
	private boolean textDisplayable;
	/**
	 * 中间进度百分比的字符串的颜色
	 */
	private int textColor;

	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textSize;
	private boolean clockWise;
	/**
	 * 动画周期 越小转动越快
	 */
	private int period;
	private int delay;
	private ObjectAnimator rotateAnimator;
	private View loadingBar;
	private int imageRes;
	private TextView tv_progress;

	public TvLoadingBar(Context context) {
		this(context, null);
	}

	public TvLoadingBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TvLoadingBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray custom = context.obtainStyledAttributes(attrs,
				R.styleable.TvLoadingBar);

		// 获取自定义属性和默认值
		imageRes = custom.getResourceId(R.styleable.TvLoadingBar_imageRes, 0);
		clockWise = custom.getBoolean(R.styleable.TvLoadingBar_clockwise, true);
		period = custom.getInteger(R.styleable.TvLoadingBar_period, 1000);
		delay=custom.getInteger(R.styleable.TvLoadingBar_delay, 1);
		textDisplayable = custom.getBoolean(
				R.styleable.TvLoadingBar_textDisplayable, false);
		textColor = custom.getColor(R.styleable.TvLoadingBar_textColor,
				Color.GREEN);
		textSize = custom.getDimension(R.styleable.TvLoadingBar_textSize, 15);
		
		custom.recycle();

	}

	private boolean initFlag=true;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (initFlag) {
			initFlag=false;
			initChild();
		}
		super.onLayout(changed, l, t, r, b);
	}

	private void initChild() {
		
		loadingBar = new View(getContext());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_LEFT);
		params.addRule(RelativeLayout.ALIGN_TOP);
		params.setMargins(1, 1, 1, 1);
		loadingBar.setBackgroundResource(imageRes);
		this.addView(loadingBar, params);

		tv_progress = new TextView(getContext());
		tv_progress.setTextSize(textSize);
		tv_progress.setTextColor(textColor);
		tv_progress.setGravity(Gravity.CENTER);
		this.addView(tv_progress, params);
		startAnim();

	}

	/**
	 * 启动旋转
	 */
	public void startAnim() {
		rotateAnimator = ObjectAnimator.ofFloat(loadingBar, "rotation", 0.0F,
				359.0F).setDuration(period);
		rotateAnimator.setRepeatCount(-1);
		rotateAnimator.setInterpolator(new LinearInterpolator());
		rotateAnimator.setStartDelay(delay);
		rotateAnimator.start();

	}

	/**
	 * 停止旋转
	 */
	public void stopAnim() {
		if(rotateAnimator!=null)
		rotateAnimator.cancel();
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * 设置进度
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		if (progress < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progress > MAX) {
			progress = MAX;
		}
		if (progress <= MAX&&textDisplayable) {
			this.progress = progress;
			if (progress < 10) {
				tv_progress.setText(" " + progress + "%");
			} else {
				tv_progress.setText(progress + "%");
			}
		}
	}

}
