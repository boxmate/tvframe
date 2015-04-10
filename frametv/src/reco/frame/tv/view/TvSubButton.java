package reco.frame.tv.view;


import reco.frame.tv.R;
import reco.frame.tv.TvBitmap;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TvSubButton extends Button {
	private ImageView cursor;
	private int cursorRes;
	private RelativeLayout parentView;
	/**
	 * ¹â±ê±ß¿ò¿í¶È °üÀ¨ÒõÓ°
	 */
	private int boarder;
	/**
	 * ¹â±ê×ó±ß¿ò¿í¶È º¬ÒõÓ°
	 */
	private int boarderLeft;
	/**
	 * ¹â±ê¶¥±ß¿ò¿í¶È º¬ÒõÓ°
	 */
	private int boarderTop;
	/**
	 * ¹â±êÓÒ±ß¿ò¿í¶È º¬ÒõÓ°
	 */
	private int boarderRight;
	/**
	 * ¹â±êµ×±ß¿ò¿í¶È º¬ÒõÓ°
	 */
	private int boarderBottom;
	private ImageView btnCover;
	public TvSubButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.init();
		
	}

	public TvSubButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray custom = getContext().obtainStyledAttributes(attrs,
				R.styleable.TvSubButton);
		this.cursorRes = custom.getResourceId(
				R.styleable.TvSubButton_cursorRes, 0);
		this.boarder = (int) custom.getDimension(R.styleable.TvSubButton_boarder, 0)
				+ custom.getInteger(R.styleable.TvSubButton_boarderInt, 0);

		if (boarder == 0) {
			this.boarderLeft = (int) custom.getDimension(
					R.styleable.TvSubButton_boarderLeft, 0)
					+ custom.getInteger(R.styleable.TvSubButton_boarderLeftInt, 0);
			this.boarderTop = (int) custom.getDimension(
					R.styleable.TvSubButton_boarderTop, 0)
					+ custom.getInteger(R.styleable.TvSubButton_boarderTopInt, 0);
			this.boarderRight = (int) custom.getDimension(
					R.styleable.TvSubButton_boarderRight, 0)
					+ custom.getInteger(R.styleable.TvSubButton_boarderRightInt, 0);
			this.boarderBottom = (int) custom.getDimension(
					R.styleable.TvSubButton_boarderBottom, 0)
					+ custom.getInteger(R.styleable.TvSubButton_boarderBottomInt,
							0);
		} else {
			this.boarderLeft = boarder;
			this.boarderTop = boarder;
			this.boarderRight = boarder;
			this.boarderBottom = boarder;
		}

		custom.recycle();
		this.init();
	}

	public TvSubButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// init Animation
		this.init();
	}
	private void init(){
		this.btnCover = new ImageView(this.getContext());
		this.btnCover.setBackgroundResource(cursorRes);
		//this.getContext().getResources().getDrawable(R.drawable.border_selected);
		//this.btnCover.setBackground();
		
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		if (focused) {
			this.bringToFront();
			this.addCover();
		} else {
			this.btnCover.clearAnimation();
			this.removeCover();
		}
	}
	private void addCover(){
		if(this.btnCover==null) {
			return ;
		}
		this.parentView = (RelativeLayout)getParent();
		
		if(this.parentView==null) {
			return ;
		}
		
		if(this.btnCover.getParent()==null) {
			parentView.addView(this.btnCover);
		} else {
			return;
		}
		btnCover.bringToFront();
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)this.btnCover.getLayoutParams();
		setBorderParams(params, this,btnCover);
	}
	private void removeCover(){
		this.parentView.removeView(this.btnCover);
	}
	
	/**
	 * ÅäÖÃÍøÂçÍ¼Æ¬µØÖ·
	 * 
	 * @param url
	 */
	public void configImageUrl(String url) {

		TvBitmap.create(getContext()).display(this, url);

	}
	
	/**
	 * ¿ÉÅäÖÃÍøÂçÍ¼Æ¬ 
	 * @param url
	 * @param loadingRes
	 */
	public void configImageUrl(String url,int loadingRes) {

		TvBitmap.create(getContext()).display(this, url, 
				BitmapFactory.decodeResource(getResources(), loadingRes));

	}

	
	


	private  void setBorderParams(RelativeLayout.LayoutParams params,
			View view, View border) {

		int left = view.getLeft();
		int top = view.getTop();

		int coverLeft = left - boarderLeft;
		int coverTop = top - boarderTop;// offset;¶î

		border.layout(coverLeft, coverTop, view.getRight() + boarderRight,
				view.getBottom() + boarderBottom);

		params.leftMargin = coverLeft;
		params.topMargin = coverTop;
		params.width = boarderLeft + view.getWidth() + boarderRight;
		params.height = boarderTop + view.getHeight() + boarderBottom;

	}
}
