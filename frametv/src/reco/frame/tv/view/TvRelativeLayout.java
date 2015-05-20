package reco.frame.tv.view;

import reco.frame.tv.R;
import reco.frame.tv.TvBitmap;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 专用于电视，其父容器须为 RelativeLayout
 * 
 * @author reco
 * 
 */
public class TvRelativeLayout extends RelativeLayout {
	/**
	 * 光标
	 */
	private ImageView cursor;
	private final String cursorTag = "TvRelativeLayout";
	/**
	 * 光标资源
	 */
	private int cursorRes;
	/**
	 * 可否缩放
	 */
	private boolean scalable;
	/**
	 * 放大比率
	 */
	private float scale;
	/**
	 * 光标飘移动画 默认无效果
	 */
	private int animationType;
	public final static int ANIM_DEFAULT = 0;// 无效果
	public final static int ANIM_TRASLATE = 1;// 平移
	/**
	 * 放大用时
	 */
	private int durationLarge = 100;
	/**
	 * 缩小用时
	 */
	private int durationSmall = 100;
	/**
	 * 触发延迟
	 */
	private int delay = 110;
	/**
	 * 光标边框宽度 包括阴影
	 */
	private int boarder;
	/**
	 * 光标左边框宽度 含阴影
	 */
	private int boarderLeft;
	/**
	 * 光标顶边框宽度 含阴影
	 */
	private int boarderTop;
	/**
	 * 光标右边框宽度 含阴影
	 */
	private int boarderRight;
	/**
	 * 光标底边框宽度 含阴影
	 */
	private int boarderBottom;

	private boolean initFlag;

	public TvRelativeLayout(Context context) {
		this(context, null);
	}

	public TvRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TvRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray custom = getContext().obtainStyledAttributes(attrs,
				R.styleable.TvRelativeLayout);
		this.cursorRes = custom.getResourceId(
				R.styleable.TvRelativeLayout_cursorRes, 0);
		this.scalable = custom.getBoolean(
				R.styleable.TvRelativeLayout_scalable, true);
		this.scale = custom.getFloat(R.styleable.TvRelativeLayout_scale, 1.1f);
		this.animationType = custom.getInt(
				R.styleable.TvRelativeLayout_animationType, 0);
		this.delay = custom.getInteger(R.styleable.TvRelativeLayout_delay, 10);
		this.durationLarge = custom.getInteger(
				R.styleable.TvRelativeLayout_durationLarge, 100);
		this.durationSmall = custom.getInteger(
				R.styleable.TvRelativeLayout_durationSmall, 100);
		this.boarder = (int) custom.getDimension(
				R.styleable.TvRelativeLayout_boarder, 0)
				+ custom.getInteger(R.styleable.TvRelativeLayout_boarderInt, 0);

		if (boarder == 0) {
			this.boarderLeft = (int) custom.getDimension(
					R.styleable.TvRelativeLayout_boarderLeft, 0)
					+ custom.getInteger(
							R.styleable.TvRelativeLayout_boarderLeftInt, 0);
			this.boarderTop = (int) custom.getDimension(
					R.styleable.TvRelativeLayout_boarderTop, 0)
					+ custom.getInteger(
							R.styleable.TvRelativeLayout_boarderTopInt, 0);
			this.boarderRight = (int) custom.getDimension(
					R.styleable.TvRelativeLayout_boarderRight, 0)
					+ custom.getInteger(
							R.styleable.TvRelativeLayout_boarderRightInt, 0);
			this.boarderBottom = (int) custom.getDimension(
					R.styleable.TvRelativeLayout_boarderBottom, 0)
					+ custom.getInteger(
							R.styleable.TvRelativeLayout_boarderBottomInt, 0);
		} else {
			this.boarderLeft = boarder;
			this.boarderTop = boarder;
			this.boarderRight = boarder;
			this.boarderBottom = boarder;
		}

		/**
		 * 此处需小心，若光标有圆角 且边框线极细 电视分辨率不同 其圆弧处放大后与按钮不能吻合 故需针对两种主流分辨率
		 * 让美工制作两张.9.png格式选中图，针对两种主流分辨率；
		 * 
		 */
		// int screenType = Integer.parseInt(getResources().getString(
		// R.string.screen_type));
		// if (cursorRes == 0) {
		// switch (screenType) {
		// case SCREEN_720:
		// cursorRes = R.drawable.cursor_720;
		// break;
		//
		// case SCREEN_1080:
		// cursorRes = R.drawable.cursor_1080;
		// break;
		// }
		// }
		custom.recycle();

		// 默认可得焦点
		setFocusable(true);

		// 关闭子控件动画缓存 使嵌套动画更流畅
		setAnimationCacheEnabled(false);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		if (focused) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					moveCover();
				}
			}, delay);

		} else {
			removeCover();
		}
	}

	/**
	 * 光标移动 到达后 与控件同时放大
	 */
	private void moveCover() {

		cursor = (ImageView) ((RelativeLayout) getParent())
				.findViewWithTag(cursorTag);
		if (cursor == null) {
			cursor = new ImageView(getContext());
			cursor.setTag(cursorTag);
			cursor.setBackgroundResource(cursorRes);

			((RelativeLayout) getParent()).addView(cursor);
		}
		switch (animationType) {
		case ANIM_DEFAULT:
			setBorderParams();
			this.bringToFront();
			cursor.bringToFront();
			if (scalable) {
				scaleToLarge();
			}
			break;

		// case ANIM_TRASLATE:
		// ObjectAnimator transAnimatorX = ObjectAnimator.ofFloat(cursor,
		// "x", this.getX());
		// ObjectAnimator transAnimatorY = ObjectAnimator.ofFloat(cursor,
		// "y", this.getY());
		// break;

		}

	}

	/**
	 * 还原控件状态
	 */
	public void removeCover() {
		if (cursor != null) {
			cursor.setVisibility(View.INVISIBLE);
		}

		if (scalable) {
			scaleToNormal();
		}
	}

	private AnimatorSet animatorSet;
	private ObjectAnimator largeX;

	private void scaleToLarge() {

		if (!this.isFocused()) {
			return;
		}

		animatorSet = new AnimatorSet();
		largeX = ObjectAnimator.ofFloat(this, "ScaleX", 1f, scale);
		ObjectAnimator largeY = ObjectAnimator.ofFloat(this, "ScaleY", 1f,
				scale);
		ObjectAnimator cursorX = ObjectAnimator.ofFloat(cursor, "ScaleX", 1f,
				scale);
		ObjectAnimator cursorY = ObjectAnimator.ofFloat(cursor, "ScaleY", 1f,
				scale);

		animatorSet.setDuration(durationLarge);
		animatorSet.play(largeX).with(largeY).with(cursorX).with(cursorY);

		animatorSet.start();
	}

	public void scaleToNormal() {
		if (animatorSet == null) {
			return;
		}
		// float scaleNow=(Float) largeX.getAnimatedValue("ScaleX");
		// if (scaleNow<=0) {
		// scaleNow=1;
		// }
		if (animatorSet.isRunning()) {
			animatorSet.cancel();
		}
		// Log.e(VIEW_LOG_TAG, "scaleNow="+scaleNow);

		ObjectAnimator oa = ObjectAnimator.ofFloat(this, "ScaleX", 1f);
		oa.setDuration(durationSmall);
		oa.start();
		ObjectAnimator oa2 = ObjectAnimator.ofFloat(this, "ScaleY", 1f);
		oa2.setDuration(durationSmall);
		oa2.start();
	}

	/**
	 * 配置网络图片地址
	 * 
	 * @param url
	 */
	public void configImageUrl(String url) {

		TvBitmap.create(getContext()).display(this, url);

	}

	/**
	 * 指定光标相对位置
	 */
	private void setBorderParams() {
		cursor.clearAnimation();
		cursor.setVisibility(View.VISIBLE);

		// �ж�����

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cursor
				.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_LEFT, this.getId());
		params.addRule(RelativeLayout.ALIGN_TOP, this.getId());

		int coverLeft = 0 - boarderLeft;
		int coverTop = 0 - boarderTop;

		params.leftMargin = coverLeft;
		params.topMargin = coverTop;

		params.width = boarderLeft + getWidth() + boarderRight;
		params.height = boarderBottom + getHeight() + boarderTop;

		cursor.setLayoutParams(params);

	}

}
