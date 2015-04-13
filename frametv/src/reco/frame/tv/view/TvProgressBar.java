package reco.frame.tv.view;

import reco.frame.tv.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 专用于电视，有扇形 环形 两种风格
 * 
 * @author xiaanming
 * 
 */
public class TvProgressBar extends View {

	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 圆环的颜色
	 */
	private int backgroundColor;

	/**
	 * 圆环进度的启始颜色
	 */
	private int progressStartColor;
	/**
	 * 圆环进度的终末颜色
	 */
	private int progressEndColor;

	/**
	 * 中间进度百分比的字符串的颜色
	 */
	private int textColor;

	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textSize;

	/**
	 * 圆环的宽度
	 */
	private float roundWidth;

	/**
	 * 圆角半径 默认为0 此时横线进度条为方角
	 */
	private float rectRadius;

	/**
	 * 最大进度
	 */
	private int max;
	/**
	 * 进度单位
	 */
	private int progressPercent;

	/**
	 * 当前进度
	 */
	private int progress;
	private int targetProgress;
	/**
	 * 是否显示中间的进度
	 */
	private boolean textDisplayable;

	/**
	 * 进度条风格:环形 扇形 或 长条
	 */
	private int style;

	public static final int RING = 0;
	public static final int FAN = 1;
	public static final int RECT = 2;

	public TvProgressBar(Context context) {
		this(context, null);
	}

	public TvProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TvProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);


		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.TvProgressBar);

		// 获取自定义属性和默认值
		backgroundColor = mTypedArray.getColor(
				R.styleable.TvProgressBar_backgroundColor, Color.TRANSPARENT);
		progressStartColor = mTypedArray.getColor(
				R.styleable.TvProgressBar_progressStartColor, Color.GREEN);
		progressEndColor = mTypedArray.getColor(
				R.styleable.TvProgressBar_progressEndColor, 0);
		textColor = mTypedArray.getColor(R.styleable.TvProgressBar_textColor,
				Color.GREEN);
		textSize = mTypedArray.getDimension(R.styleable.TvProgressBar_textSize,
				15);
		// textSize=CommonUtil.Px2Dp(context, textSize);//PX 转 DP
		rectRadius = mTypedArray.getDimension(
				R.styleable.TvProgressBar_rectRadius, 0);
		roundWidth = mTypedArray.getDimension(
				R.styleable.TvProgressBar_roundWidth, 5);
		max = mTypedArray.getInteger(R.styleable.TvProgressBar_max, 100);
		progressPercent = max / 100;
		textDisplayable = mTypedArray.getBoolean(
				R.styleable.TvProgressBar_textDisplayable, false);
		style = mTypedArray.getInt(R.styleable.TvProgressBar_style, 0);

		mTypedArray.recycle();
		
		 
	}

	private boolean init = true;

	private Shader sweep;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// /**
		// * 用于进度平滑
		// */
		// if (targetProgress > progress) {
		// progress+=progressPercent;
		// if (progress>=targetProgress) {
		// progress=targetProgress;
		// }
		// }else if(targetProgress<progress){
		// progress=0;
		// }
		paint=new Paint();
		int centre = getWidth() / 2; // 获取圆心的x坐标

		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setColor(backgroundColor);
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);

		switch (style) {
		case RING: {
			int radius = (int) (centre - roundWidth / 2); // 圆环的半径
			/**
			 * 画环底色
			 */
			canvas.drawCircle(centre, centre, radius, paint); // 画出圆环

			/**
			 * 画环进度
			 */
			paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
			// 设置进度的颜色
			if (progressEndColor == 0) {
				paint.setColor(progressStartColor);
			} else {
				// 渐变色
				Shader  sweep = new SweepGradient(centre, centre, 
						new int[] {progressStartColor,progressEndColor,progressStartColor
						}, null); 
				paint.setShader(sweep);
			}

			RectF oval = new RectF(centre - radius, centre - radius, centre
					+ radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
			paint.setStyle(Paint.Style.STROKE);
			if (progress != 0)
				canvas.drawArc(oval, -90, 360 * progress / max, false, paint); // 根据进度画圆弧
			break;
		}
		case FAN: {
			int radius = (int) (centre - roundWidth / 2) + 1; // 圆环的半径
			paint.setStyle(Paint.Style.FILL);
			/**
			 * 画扇底色
			 */
			if (backgroundColor!=0) {
				canvas.drawCircle(centre, centre, radius, paint); // 画出圆环
			}
			/**
			 * 画扇进度
			 */
			// 设置进度的颜色
			if (progressEndColor == 0) {
				paint.setColor(progressStartColor);
			} else {
				// 渐变色
				Shader sweep = new SweepGradient(centre, centre, 
						new int[] {progressStartColor,progressEndColor,progressStartColor
						}, null); 
				paint.setShader(sweep);
			}
			RectF oval = new RectF(centre - radius, centre - radius, centre
					+ radius, centre + radius); // 定义的圆弧的形状和大小的界限
			if (progress != 0)
				canvas.drawArc(oval, -90, 360 * progress / max, true, paint); // 根据进度画圆弧
				
				
			break;
		}

		case RECT: {
			paint.setStyle(Paint.Style.FILL);
			int current = getWidth() * progress / max;
			/**
			 * 画线底色
			 */
			if (backgroundColor!=0) {
				if (rectRadius>getHeight()) {
					rectRadius=getHeight();
				}
				RectF rectF = new RectF(0, 0, getWidth(), getHeight());
				canvas.drawRoundRect(rectF, rectRadius, rectRadius, paint);
			}
			
			/**
			 * 画线进度
			 */
			// 设置进度的颜色
			if (progressEndColor == 0) {
				paint.setColor(progressStartColor);
			} else {
				// 渐变色
				Shader shader = new LinearGradient(0, 0, current, 0,
						new int[]{progressStartColor, progressEndColor},
						null,Shader.TileMode.REPEAT);
				paint.setShader(shader);
			}

			RectF rectF2 = new RectF(0, 0, current, getHeight());
			canvas.drawRoundRect(rectF2, rectRadius, rectRadius, paint);
			break;
		}
		}

		/**
		 * 画进度文字
		 */
		paint.setStrokeWidth(0);
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体
		int percent = (int) (((float) progress / (float) max) * 100); // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
		float textWidth = paint.measureText(percent + "%"); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间

		if (textDisplayable && percent != 0) {

			if (percent < 10) {
				canvas.drawText(" " + percent + "%", centre - textWidth / 2,
						centre + textSize / 2, paint); // 画出进度百分比
			} else {
				canvas.drawText(percent + "%", centre - textWidth / 2, centre
						+ textSize / 2, paint); // 画出进度百分比
			}
		}

	}

	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * 
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if (max < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if (progress < 0) {
			progress=0;
		}
		if (progress > max) {
			progress = max;
		}
		if (progress <= max) {
			// this.targetProgress=progress;
			this.progress = progress;
			postInvalidate();
		}

	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(int cricleColor) {
		this.backgroundColor = cricleColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

}
