package reco.frame.tv.view;

import java.util.HashMap;
import java.util.Map;
import reco.frame.tv.R;
import reco.frame.tv.view.component.TvBaseAdapter;
import reco.frame.tv.view.component.TvConfig;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class TvGridView extends RelativeLayout {

	/**
	 * 光标
	 */
	private ImageView cursor;
	/**
	 * 光标资源
	 */
	private int cursorRes;
	/**
	 * item可否缩放
	 */
	private boolean scalable;
	/**
	 * 放大比率
	 */
	private float scale;
	/**
	 * 光标飘移动画 默认无效果(尚未实现)
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

	/**
	 * 外层容器布局是否改变
	 */
	private boolean parentLayout = true;

	/**
	 * 除光标外 当前子类数
	 */
	private int currentChildCount = 0;

	/**
	 * 可否滚动
	 */
	private final int ACTION_START_SCROLL = 0, ACTION_INIT_ITEMS = 1,
			ACTION_ADD_ITEMS = 2;
	private boolean scrollable;
	/**
	 * 滚动延时及滚动用时
	 */
	private final int DELAY = 231, DURATION = 570;
	/**
	 * 列数
	 */
	private int columns;
	private int rowCount, selectRow;
	/**
	 * 屏幕可显示最大行数
	 */
	private int screenMaxRow;
	/**
	 * 当前选中子项下示
	 */
	private int selectIndex;
	private int paddingLeft, paddingTop;
	private int spaceHori;
	private int spaceVert;
	/**
	 * item宽高 不包括纵横间距
	 */
	private int itemWidth, itemHeight;
	/**
	 * item真实宽高 包括纵横间距
	 */
	private int rowWidth, rowHeight;
	private Map<Integer, Integer> itemIds;

	private OnItemSelectListener onItemSelectListener;
	private OnItemClickListener onItemClickListener;
	public AdapterDataSetObservable mDataSetObservable;
	private TvBaseAdapter adapter;
	private AnimatorSet animatorSet;
	private ObjectAnimator largeX;
	private WindowManager wm;
	private Scroller mScroller;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case ACTION_START_SCROLL:
				int direction = (Integer) msg.obj;
				if (scrollable) {
					scrollable = false;
					scrollByRow(direction);
				}

				break;
			case ACTION_INIT_ITEMS:
				initItems();
				break;
			case ACTION_ADD_ITEMS:
				addNewItems();
				break;
			}

		};
	};

	public TvGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TvGridView(Context context) {
		super(context);
	}

	public TvGridView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray custom = getContext().obtainStyledAttributes(attrs,
				R.styleable.TvGridView);
		this.cursorRes = custom.getResourceId(R.styleable.TvGridView_cursorRes,
				0);
		this.scalable = custom
				.getBoolean(R.styleable.TvGridView_scalable, true);
		this.scale = custom.getFloat(R.styleable.TvRelativeLayout_scale, 1.1f);
		this.animationType = custom.getInt(
				R.styleable.TvGridView_animationType, 0);
		this.delay = custom.getInteger(R.styleable.TvRelativeLayout_delay, 110);
		this.durationLarge = custom.getInteger(
				R.styleable.TvGridView_durationLarge, 100);
		this.durationSmall = custom.getInteger(
				R.styleable.TvGridView_durationSmall, 100);

		this.columns = custom.getInteger(R.styleable.TvGridView_columns, 2);
		this.spaceHori = (int) custom.getDimension(
				R.styleable.TvGridView_spaceHori, 10);
		this.spaceVert = (int) custom.getDimension(
				R.styleable.TvGridView_spaceVert, 10);

		itemWidth = (int) custom.getDimension(R.styleable.TvGridView_itemWidth,
				10);
		itemHeight = (int) custom.getDimension(
				R.styleable.TvGridView_itemHeight, 10);
		rowHeight = itemHeight + spaceVert;
		rowWidth = itemWidth + spaceHori;

		this.boarder = (int) custom.getDimension(R.styleable.TvGridView_boarder, 0)
				+ custom.getInteger(R.styleable.TvGridView_boarderInt, 0);

		if (boarder == 0) {
			this.boarderLeft = (int) custom.getDimension(
					R.styleable.TvGridView_boarderLeft, 0)
					+ custom.getInteger(R.styleable.TvGridView_boarderLeftInt, 0);
			this.boarderTop = (int) custom.getDimension(
					R.styleable.TvGridView_boarderTop, 0)
					+ custom.getInteger(R.styleable.TvGridView_boarderTopInt, 0);
			this.boarderRight = (int) custom.getDimension(
					R.styleable.TvGridView_boarderRight, 0)
					+ custom.getInteger(R.styleable.TvGridView_boarderRightInt, 0);
			this.boarderBottom = (int) custom.getDimension(
					R.styleable.TvGridView_boarderBottom, 0)
					+ custom.getInteger(R.styleable.TvGridView_boarderBottomInt,
							0);
		} else {
			this.boarderLeft = boarder;
			this.boarderTop = boarder;
			this.boarderRight = boarder;
			this.boarderBottom = boarder;
		}

		custom.recycle();
		// 关闭子控件动画缓存 使嵌套动画更流畅
		// setAnimationCacheEnabled(false);

		
		init();
	}

	private void init() {
		itemIds = new HashMap<Integer, Integer>();
		mScroller = new Scroller(getContext());

		wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);

		mDataSetObservable = new AdapterDataSetObservable();

	}

	/**
	 * 设置适配器
	 * 
	 * @param adapter
	 */
	public void setAdapter(TvBaseAdapter adapter) {
		this.adapter = adapter;
		if (adapter != null) {
			adapter.registerDataSetObservable(mDataSetObservable);
		}
		// 清理原先数据
		clear();
		initGridView();
	}

	private void clear() {
		itemIds.clear();
		this.removeAllViews();
		// this.removeAllViewsInLayout();
	}

	/**
	 * 首次加载屏幕可见行数*2
	 */
	public void initGridView() {
		// 重设参数
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();

		RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
				params.width, params.height);
		this.setPadding((int) (boarderLeft * scale),
				(int) (boarderTop * scale), boarderRight, boarderBottom);
		newParams.setMargins(params.leftMargin, params.topMargin,
				params.rightMargin, params.bottomMargin);
		this.setLayoutParams(newParams);

		paddingLeft = (int) (boarderLeft * scale + itemWidth * (scale - 1) / 2
				+ 3 + this.getPaddingLeft());
		paddingTop = (int) (boarderTop * scale + itemHeight * (scale - 1) / 2
				 + this.getPaddingTop())+3;

		Message msg = handler.obtainMessage();
		msg.what = ACTION_INIT_ITEMS;
		handler.sendMessageDelayed(msg, DELAY);

	}

	private boolean canAdd = true;

	private void initItems() {
		int screenHeight = wm.getDefaultDisplay().getHeight();
		int initRows = screenHeight % rowHeight == 0 ? screenHeight / rowHeight
				: screenHeight / rowHeight + 1;
		// Log.e(VIEW_LOG_TAG, "screenRows"+initRows);
		int initLength = Math.min(adapter.getCount(), initRows * 2 * columns);
		for (int i = 0; i < initLength; i++) {
			int left = (i % columns) * (itemWidth + spaceHori);
			int top = (i / columns) * (spaceVert + itemHeight);
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					itemWidth, itemHeight);
			if (initLength==1) {
				rlp.setMargins(left, top, paddingLeft, 0);
			}else{
				rlp.setMargins(left, top, 0, 0);
			}
			
			View child = adapter.getView(i, null, this);
			this.addView(child, rlp);
			int viewId = child.getId();
			if (viewId == -1) {
				viewId = TvConfig.buildId();
				// 此处硬设置id同时建议开发者不用此范围id
			}
			child.setId(viewId);
			itemIds.put(viewId, i);
			bindEventOnChild(child);

		}
		rowCount = itemIds.size() % columns == 0 ? itemIds.size() / columns
				: itemIds.size() / columns + 1;

		cursor = new ImageView(getContext());
		cursor.setId(TvConfig.buildId());
		cursor.setBackgroundResource(cursorRes);
		this.addView(cursor);
		cursor.setVisibility(View.INVISIBLE);
		
		View focus=((ViewGroup)getParent()).findFocus();
		if (focus==null) {
			View item=getChildAt(0);
			if (item!=null) {
				item.requestFocus();
			}
		}

	}
	

	private void addNewItems() {
		currentChildCount = getChildCount();
		// Log.e(VIEW_LOG_TAG, "添加数据" + currentChildCount);
		parentLayout = false;
		int start = itemIds.size();
		int end = Math.min(start + screenMaxRow * 2, adapter.getCount());
		for (int i = start; i < end; i++) {
			int left = (i % columns) * (itemWidth + spaceHori);
			int top = (i / columns) * (spaceVert + itemHeight);
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					itemWidth, itemHeight);
			rlp.setMargins(left, top, 0, 0);
			View child = adapter.getView(i, null, this);

			int viewId = child.getId();
			if (viewId == -1) {
				viewId = TvConfig.buildId();
				// 此处硬设置id同时建议开发者不用此范围id
			}
			child.setId(viewId);
			this.addView(child, rlp);
			itemIds.put(viewId, i);
			bindEventOnChild(child);

		}

		rowCount = itemIds.size() % columns == 0 ? itemIds.size() / columns
				: itemIds.size() / columns + 1;
		canAdd = true;

	}

	private void bindEventOnChild(View child) {
		child.setFocusable(true);
		child.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(final View item, boolean focus) {

				if (focus) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							moveCover(item);
						}
					}, delay);
					// 选中事件
					if (onItemSelectListener != null) {
						onItemSelectListener.onItemSelect(item, selectIndex);
					}

				} else {
					returnCover(item);
				}
			}
		});

		child.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View item) {
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(item, selectIndex);
				}

			}
		});

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		/**
		 * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
		 */
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

		// 计算出所有的childView的宽和高
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		/**
		 * 记录如果是wrap_content是设置的宽和高
		 */
		int width = 0;
		int height = 0;

		int cCount = getChildCount();

		int cWidth = 0;
		int cHeight = 0;
		MarginLayoutParams cParams = null;
		/**
		 * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
		 */

		// Log.e(VIEW_LOG_TAG, "onMeasure=" + currentChildCount + "---cCount="
		// + cCount);
		for (int i = currentChildCount; i < cCount; i++) {
			View childView = getChildAt(i);
			cWidth = childView.getMeasuredWidth();
			cHeight = childView.getMeasuredHeight();
			cParams = (MarginLayoutParams) childView.getLayoutParams();

			// 上面两个childView
			width += cWidth + cParams.leftMargin + cParams.rightMargin;
			height += cHeight + cParams.topMargin + cParams.bottomMargin;

		}

		/**
		 * 如果是wrap_content设置为我们计算的值 否则：直接设置为父容器计算的值
		 */
		setMeasuredDimension(
				(widthMode == MeasureSpec.EXACTLY || width == 0) ? sizeWidth
						: width,
				(heightMode == MeasureSpec.EXACTLY || height == 0) ? sizeHeight
						: height);
		// Log.e(VIEW_LOG_TAG, "onMeasure----" + width + "----" + height + "---"
		// + getHeight());

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		if (parentLayout) {
			parentLayout = false;
			return;
		}

		if (changed) {
			int cCount = getChildCount();
			int cWidth = 0;
			int cHeight = 0;
			// boolean cursorFlag=false;
			/**
			 * 遍历所有childView根据其宽和高，以及margin进行布局
			 */
			int start = currentChildCount;
			// Log.e(VIEW_LOG_TAG, "onLayout=" + currentChildCount +
			// "---cCount="
			// + cCount);
			for (int i = start; i < cCount; i++) {
				View childView = getChildAt(i);
				// 跳过光标子项
				int index = i;
				if (currentChildCount != 0) {
					index = i - 1;
				}
				cWidth = childView.getMeasuredWidth();
				cHeight = childView.getMeasuredHeight();

				int cl = 0, ct = 0, cr = 0, cb = 0;
				cl = (index % columns) * (itemWidth + spaceHori);
				ct = (index / columns) * (spaceVert + itemHeight);

				cr = cl + cWidth;
				cb = cHeight + ct;
				childView.layout(cl + paddingLeft, ct + paddingTop, cr
						+ paddingLeft, cb + paddingTop);
			}
			screenMaxRow = getHeight() % rowHeight == 0 ? getHeight()
					/ rowHeight : getHeight() / rowHeight + 1;
		}

	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			boolean flag = false;
			int direction = 0;
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_DOWN:
				direction = View.FOCUS_DOWN;
				if (!canAdd) {
					return true;
				} else {

				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				direction = View.FOCUS_RIGHT;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				direction = View.FOCUS_UP;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				direction = View.FOCUS_LEFT;
				break;
			}

			View focused = this.findFocus();
			if (focused != null && direction != 0) {
				View next = focused.focusSearch(direction);
				// 根据下标算出所在行
				if (next != null) {

					int focusIndex = itemIds.get(focused.getId());

					Integer temp = itemIds.get(next.getId());

					// 焦点切出容器时
					if (temp != null) {
						selectIndex = temp;
					} else {
						parentLayout = true;
						return super.dispatchKeyEventPreIme(event);
					}

					int nextRow = 0;

					selectRow = focusIndex / columns;
					nextRow = selectIndex / columns;

					// 向下到达最后一完整行时,可滚动; 向上到达最上一行完整行时,可滚动

					if (nextRow > selectRow) {
						if ((next.getTop() - mScroller.getFinalY()) >= (rowHeight * (screenMaxRow - 1))
								+ paddingTop) {
							flag = true;
						}
					} else if (nextRow < selectRow && nextRow != 0) {
						if ((next.getTop() - mScroller.getFinalY()) < rowHeight
								+ paddingTop
								&& selectRow != 0) {
							flag = true;
						}
					}
					selectRow = nextRow;
					if (flag) {
						if (nextRow > -1 && !scrollable
								&& mScroller.isFinished()) {
							// 先清除按钮动画
							scrollable = true;
							Message msg = handler.obtainMessage();
							msg.obj = direction;
							msg.what = ACTION_START_SCROLL;
							handler.sendMessageDelayed(msg, DELAY);
						} else {
							return true;
						}
					}

				}

			}

		}

		return super.dispatchKeyEventPreIme(event);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {

		if (t == mScroller.getFinalY()) {
			// Log.e(VIEW_LOG_TAG,"screenMaxRow="+screenMaxRow+
			// "---selectRow="+selectRow+"---selectIndex="+selectIndex+"---rowCount="+rowCount);
			if (t > oldt) {
				// 下翻加载 当剩余行数小于一屏时
				if ((rowCount - selectRow) < screenMaxRow) {
					canAdd = false;
					Message msg = handler.obtainMessage();
					msg.what = ACTION_ADD_ITEMS;
					handler.sendMessageDelayed(msg, DELAY);
				}

			} else if (oldt > t) {
				// 上翻刷新
			}

		}

		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * 翻页
	 * 
	 * @param page
	 */
	private void scrollByRow(int direction) {

		if (selectRow < 0 || selectRow > rowCount - 1) {
			return;
		}
		if (direction == View.FOCUS_UP) {
			mScroller.startScroll(0, mScroller.getFinalY(), 0, -rowHeight,
					DURATION);
		} else if (direction == View.FOCUS_DOWN) {
			mScroller.startScroll(0, mScroller.getFinalY(), 0, rowHeight,
					DURATION);
		}

		invalidate();

	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		// 先判断mScroller滚动是否完成
		if (mScroller.computeScrollOffset()) {

			// 这里调用View的scrollTo()完成实际的滚动
			scrollTo(0, mScroller.getCurrY());
			// 必须调用该方法，否则不一定能看到滚动效果
			postInvalidate();
		}
		super.computeScroll();
	}

	/**
	 * 光标移动 到达后 与控件同时放大
	 */
	private void moveCover(View item) {
		if (cursor == null) {
			return;
		}

		setBorderParams(item);
		item.bringToFront();
		cursor.bringToFront();
		if (scalable) {
			scaleToLarge(item);
		}

	}

	/**
	 * 还原控件状态
	 */

	private void returnCover(View item) {
		if (cursor == null) {
			return;
		}
		cursor.setVisibility(View.INVISIBLE);
		if (scalable) {
			scaleToNormal(item);
		}
	}

	private void scaleToLarge(View item) {

		if (!item.isFocused()) {
			return;
		}

		animatorSet = new AnimatorSet();
		largeX = ObjectAnimator.ofFloat(item, "ScaleX", 1f, scale);
		ObjectAnimator largeY = ObjectAnimator.ofFloat(item, "ScaleY", 1f,
				scale);
		ObjectAnimator cursorX = ObjectAnimator.ofFloat(cursor, "ScaleX", 1f,
				scale);
		ObjectAnimator cursorY = ObjectAnimator.ofFloat(cursor, "ScaleY", 1f,
				scale);

		animatorSet.setDuration(durationLarge);
		animatorSet.play(largeX).with(largeY).with(cursorX).with(cursorY);

		animatorSet.start();
	}

	private void scaleToNormal(View item) {
		if (animatorSet == null) {
			return;
		}
		if (animatorSet.isRunning()) {
			animatorSet.cancel();
		}
		ObjectAnimator oa = ObjectAnimator.ofFloat(item, "ScaleX", 1f);
		oa.setDuration(durationSmall);
		oa.start();
		ObjectAnimator oa2 = ObjectAnimator.ofFloat(item, "ScaleY", 1f);
		oa2.setDuration(durationSmall);
		oa2.start();
	}

	/**
	 * 指定光标相对位置
	 */
	private void setBorderParams(View item) {
		cursor.clearAnimation();
		cursor.setVisibility(View.VISIBLE);

		// 判断类型

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item
				.getLayoutParams();

		// Log.e(VIEW_LOG_TAG, params.leftMargin + "---" + params.topMargin
		// + "---" + boarderLeft + "---" + boarderTop
		// + "---" + paddingLeft + "---" + paddingTop);
		int l, t, r, b;
		l = params.leftMargin + paddingLeft - boarderLeft;
		t = params.topMargin + paddingTop - boarderTop;
		r = l + itemWidth + boarderRight;
		b = t + itemHeight + boarderBottom;
		cursor.layout(l, t, r, b);

	}

	public void setOnItemSelectListener(OnItemSelectListener myListener) {
		this.onItemSelectListener = myListener;
	}

	public void setOnItemClickListener(OnItemClickListener myListener) {
		this.onItemClickListener = myListener;
	}

	public static interface RecyclerListener {
		/**
		 * Indicates that the specified View was moved into the recycler's scrap
		 * heap. The view is not displayed on screen any more and any expensive
		 * resource associated with the view should be discarded.
		 * 
		 * @param view
		 */
		void onMovedToScrapHeap(View view);
	}

	public class RecycleBin {
		public final static int STATE_ACTIVE = 0, STATE_SCRAP = 1;
		private RecyclerListener mRecyclerListener;

		public void recycleSingleView(View scrapView) {
			// 销毁图片
			if (scrapView instanceof ImageView) {
				Drawable front = ((ImageView) scrapView).getDrawable();
				if (front != null && front instanceof BitmapDrawable) {
					BitmapDrawable bitmapDrawable = (BitmapDrawable) front;
					Bitmap bitmap = bitmapDrawable.getBitmap();
					if (bitmap != null && !bitmap.isRecycled()) {
						recycleBitmap(bitmap);
					}
				}

			}
			Drawable background = ((ImageView) scrapView).getBackground();
			if (background != null && background instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) background;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				if (bitmap != null && !bitmap.isRecycled()) {
					recycleBitmap(bitmap);
				}
			}

		}

		public void recycleView(View item) {

			if (item instanceof ViewGroup) {
				ViewGroup container = (ViewGroup) item;
				for (int i = 0; i < container.getChildCount(); i++) {
					View child = container.getChildAt(i);
					recycleSingleView(child);
				}
			}

		}

		private void recycleBitmap(final Bitmap bitmap) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					bitmap.recycle();
					postInvalidate();

				}
			}).start();
		}

		private void reload(int position) {
			adapter.getView(position, getChildAt(position), TvGridView.this);
		}

	}

	public interface OnItemSelectListener {
		public void onItemSelect(View item, int position);
	}

	public interface OnItemClickListener {
		public void onItemClick(View item, int position);
	}

	public class AdapterDataSetObservable extends DataSetObservable {
		@Override
		public void notifyChanged() {
			// 数据改变 若已翻至末端 则立即调用addNewItems
			Log.i(VIEW_LOG_TAG, "收到数据改变通知");

			if ((rowCount - selectRow) < screenMaxRow) {
				canAdd = false;
				Message msg = handler.obtainMessage();
				msg.what = ACTION_ADD_ITEMS;
				handler.sendMessageDelayed(msg, DELAY);
			}
			super.notifyChanged();
		}

		@Override
		public void notifyInvalidated() {
			super.notifyInvalidated();
		}
	}
}
