package reco.frame.tv.view;

import reco.frame.tv.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Int2;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * 跑马灯,专用于电视应用 无需焦点也能跑动
 * 
 * @author reco
 * 
 */
public class TvMarqueeText extends TextView {
	/** 是否停止滚动 */
	private boolean mStopMarquee;
	private String mText;
	private float mCoordinateX;
	private float mCoordinateY;
	private float mTextWidth, mTextHeight;
	/**
	 * 滚动周期
	 */
	private int period;
	/**
	 * 边缘淡出
	 */
	private boolean vague;
	/**
	 * 背景色 默认为透明色
	 */
	private int backgroundColor;
	
	/**
	 * 字体
	 */
	private int textStyle;
	
	public int getTextStyle() {
		return textStyle;
	}

	public void setTextStyle(int textStyle) {
		this.textStyle = textStyle;
	}

	private final static int STYLE_DEFAULT=0,STYLE_LTH=1;
	
	private static Typeface type;

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public boolean isVague() {
		return vague;
	}

	public void setVague(boolean vague) {
		this.vague = vague;
	}
	
	public void setText(String text) {
		this.mText = text;
		init();
	}
	
	public String getText(){
		return mText;
	}

	public TvMarqueeText(Context context) {
		this(context, null);
	}
	

	public TvMarqueeText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TvMarqueeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray custom = getContext().obtainStyledAttributes(attrs,
				R.styleable.TvMarqueeText);
		this.mText = custom.getString(R.styleable.TvMarqueeText_text)+"";
		this.period = custom.getInteger(R.styleable.TvMarqueeText_period, 30);
		this.vague = custom.getBoolean(R.styleable.TvMarqueeText_vague, true);
		this.textStyle=custom.getInt(R.styleable.TvMarqueeText_textStyle, STYLE_DEFAULT);
		mCoordinateX = 3;
		mCoordinateY = 15;
		backgroundColor=Color.TRANSPARENT;
		mTextWidth = getPaint().measureText(mText);
		custom.recycle();
		init();
	}
	
	private void init(){
		mTextWidth = getPaint().measureText(mText);
		if (textStyle==STYLE_LTH) {
			changeStyle();
		}
		
	}


	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (changed) {
			
			ColorDrawable colorDrawable=(ColorDrawable) getBackground();
			if (colorDrawable!=null) {
				backgroundColor=colorDrawable.getColor();
			}
			
		}
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		FontMetrics fontMetrics = getPaint().getFontMetrics();
		mCoordinateY = getHeight()
				- (getHeight() - (fontMetrics.bottom - fontMetrics.top)) / 2
				- fontMetrics.bottom;

		// 边缘淡出
		if (vague) {
			Shader shader = new LinearGradient(0, 0, getWidth(), 0, new int[] {
					backgroundColor, getTextColors().getDefaultColor(),
					getTextColors().getDefaultColor(), backgroundColor },
					new float[] { 0, 0.15f, 0.85f, 1.0f },
					Shader.TileMode.CLAMP);
			getPaint().setShader(shader);
		}
		
		canvas.clipRect(new Rect(0, 0, getWidth(), getHeight()));
		
		if (mText != null && !mText.equals(""))
			canvas.drawText(mText, mCoordinateX, mCoordinateY, getPaint());
			
		//Log.i(VIEW_LOG_TAG, mCoordinateX+"---"+mCoordinateY+"---"+mText.length()+"---"+getWidth()+"---"+getHeight());
		

	}
	
	
	private synchronized void changeStyle(){
		if (type==null) {
			try {
				type=Typeface.createFromAsset(getContext().getAssets(), "fonts/lth.ttf");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			
		}
		if (type!=null) {
			this.setTypeface(type);
		}
		
	}
	
	/**
	 * 开启跑马灯
	 */
	public void startMarquee() {
		mStopMarquee = false;
		if (mText != null && !mText.equals(""))
			mHandler.sendEmptyMessageDelayed(0, 300);
	}
	/**
	 * 停止跑马灯
	 */
	public void stopMarquee() {
		mStopMarquee = true;
		if (mHandler.hasMessages(0))
			mHandler.removeMessages(0);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (Math.abs(mCoordinateX) > (mTextWidth + 30)) {
					mCoordinateX = getWidth() - 10;
					invalidate();
					if (!mStopMarquee) {
						sendEmptyMessageDelayed(0, period);
					}
				} else {
					mCoordinateX -= 1;
					invalidate();
					if (!mStopMarquee) {
						sendEmptyMessageDelayed(0, period);
					}
				}

				break;
			}
			super.handleMessage(msg);
		}
	};

}
