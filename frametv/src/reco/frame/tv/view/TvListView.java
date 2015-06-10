package reco.frame.tv.view;

import reco.frame.tv.R;
import reco.frame.tv.view.component.RecycleBin;
import reco.frame.tv.view.component.TvBaseAdapter;
import reco.frame.tv.view.component.TvUtil;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.graphics.Bitmap;
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

public class TvListView extends RelativeLayout {

	/**
	 * 光标
	 */
	private ImageView cursor;
	private int cursorId;
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
	 * 放大延迟
	 */
	private int delay = 0;
	/**
	 * 滚动速度
	 */
	private int scrollDelay = 0;
	/**
	 * 滚动速度
	 */
	private int scrollDuration = 0;
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
	 * 刷新延迟
	 */
	private final int DELAY = 231;
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
	private SparseArray<Integer> itemIds, focusIds;

	private OnItemSelectListener onItemSelectListener;
	private OnItemClickListener onItemClickListener;
	public AdapterDataSetObservable mDataSetObservable;
	private TvBaseAdapter adapter;
	private AnimatorSet animatorSet;
	private ObjectAnimator largeX;
	private WindowManager wm;
	private Scroller mScroller;
	private RecycleBin mRecycleBin;
	private boolean isInit = true;
	private boolean canAdd = true;
	/**
	 * 以1280为准,其余分辨率放大比率 用于适配
	 */
	private float screenScale = 1;
	/**
	 * 焦点优先级
	 */
	private int focusOption = 0;
	/**
	 * 
	 */
	private final static int PARENT_ONLY = 0;
	private final static int CHILD_ONLY = 1;
	private final static int PARENT_FIRST = 2;
	private final static int CHILD_FIRST = 3;

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

	public TvListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TvListView(Context context) {
		super(context);
	}

	public TvListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray custom = getContext().obtainStyledAttributes(attrs,
				R.styleable.TvListView);
		this.cursorRes = custom.getResourceId(R.styleable.TvListView_cursorRes,
				0);
		this.scalable = custom
				.getBoolean(R.styleable.TvListView_scalable, true);
		this.scale = custom.getFloat(R.styleable.TvListView_scale, 1.1f);
		this.animationType = custom.getInt(
				R.styleable.TvListView_animationType, 0);
		this.focusOption = custom.getInt(R.styleable.TvListView_focusOption, 0);
		this.delay = custom.getInteger(R.styleable.TvListView_delay, 110);
		this.scrollDelay = custom.getInteger(
				R.styleable.TvGridView_scrollDelay, 171);
		this.scrollDuration = custom.getInteger(
				R.styleable.TvGridView_scrollDuration, 371);
		this.durationLarge = custom.getInteger(
				R.styleable.TvListView_durationLarge, 100);
		this.durationSmall = custom.getInteger(
				R.styleable.TvListView_durationSmall, 100);
		this.spaceVert = (int) custom.getDimension(
				R.styleable.TvListView_spaceVert, 10);

		itemWidth = (int) custom.getDimension(R.styleable.TvListView_itemWidth,
				10);
		itemHeight = (int) custom.getDimension(
				R.styleable.TvListView_itemHeight, 10);
		rowHeight = itemHeight + spaceVert;
		rowWidth = itemWidth + spaceHori;

		paddingLeft = (int) custom.getDimension(
				R.styleable.TvListView_paddingLeft, 0);
		paddingTop = (int) custom.getDimension(
				R.styleable.TvListView_paddingTop, 2);

		this.boarder = (int) custom.getDimension(
				R.styleable.TvListView_boarder, 0)
				+ custom.getInteger(R.styleable.TvListView_boarderInt, 0);

		if (boarder == 0) {
			this.boarderLeft = (int) custom.getDimension(
					R.styleable.TvListView_boarderLeft, 0)
					+ custom.getInteger(R.styleable.TvListView_boarderLeftInt,
							0);
			this.boarderTop = (int) custom.getDimension(
					R.styleable.TvListView_boarderTop, 0)
					+ custom.getInteger(R.styleable.TvListView_boarderTopInt, 0);
			this.boarderRight = (int) custom.getDimension(
					R.styleable.TvListView_boarderRight, 0)
					+ custom.getInteger(R.styleable.TvListView_boarderRightInt,
							0);
			this.boarderBottom = (int) custom.getDimension(
					R.styleable.TvListView_boarderBottom, 0)
					+ custom.getInteger(
							R.styleable.TvListView_boarderBottomInt, 0);
		} else {
			this.boarderLeft = boarder;
			this.boarderTop = boarder;
			this.boarderRight = boarder;
			this.boarderBottom = boarder;
		}

		if (cursorRes == 0) {
			switch (getResources().getDisplayMetrics().widthPixels) {
			case TvUtil.SCREEN_1280:
				cursorRes = custom.getResourceId(
						R.styleable.TvListView_cursorRes_1280, 0);
				break;

			case TvUtil.SCREEN_1920:
				cursorRes = custom.getResourceId(
						R.styleable.TvListView_cursorRes_1920, 0);
				break;
			case TvUtil.SCREEN_2560:
				cursorRes = custom.getResourceId(
						R.styleable.TvListView_cursorRes_2560, 0);
				break;
			case TvUtil.SCREEN_3840:
				cursorRes = custom.getResourceId(
						R.styleable.TvListView_cursorRes_3840, 0);
				break;
			}
		}
		custom.recycle();
		// 关闭子控件动画缓存 使嵌套动画更流畅
		// setAnimationCacheEnabled(false);

		init();
	}

	private void init() {

		itemIds = new SparseArray<Integer>();
		focusIds = new SparseArray<Integer>();
		mScroller = new Scroller(getContext());

		wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);

		mDataSetObservable = new AdapterDataSetObservable();
		mRecycleBin = new RecycleBin(getContext().getCacheDir()
				.getAbsolutePath());

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
		if (isInit) {
			initBuild();
			isInit = false;
		}

		Message msg = handler.obtainMessage();
		msg.what = ACTION_INIT_ITEMS;
		handler.sendMessageDelayed(msg, DELAY);
	}

	private void clear() {
		itemIds.clear();
		focusIds.clear();
		this.removeAllViews();
		this.clearDisappearingChildren();
		this.destroyDrawingCache();
		mScroller.setFinalY(0);
		parentLayout = false;
		currentChildCount = 0;
	}

	/**
	 * 首次加载屏幕可见行数*2
	 */
	public void initBuild() {
		// 重设参数
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
		RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
				params.width, params.height);
		this.setPadding((int) (boarderLeft * scale),
				(int) (boarderTop * scale), boarderRight, boarderBottom);

		newParams.setMargins(params.leftMargin, params.topMargin,
				params.rightMargin, params.bottomMargin);
		this.setLayoutParams(newParams);

	}

	private void initItems() {
		// 避免冲突
		if (getChildCount() > 0) {
			return;
		}

		int screenHeight = wm.getDefaultDisplay().getHeight();
		int initRows = screenHeight % rowHeight == 0 ? screenHeight / rowHeight
				: screenHeight / rowHeight + 1;

		int initLength = Math.min(adapter.getCount(), initRows * 2);
		for (int i = 0; i < initLength; i++) {
			int left = 0;
			int top = i * (spaceVert + itemHeight);
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					itemWidth, itemHeight);
			if (initLength == 1) {
				rlp.setMargins(left, top, paddingLeft, 0);
			} else {
				rlp.setMargins(left, top, 0, 0);
			}

			View child = adapter.getView(i, null, this);
			bindEvent(child, i);

			this.addView(child, rlp);

		}

		cursor = new ImageView(getContext());
		cursorId = TvUtil.buildId();
		cursor.setId(cursorId);
		cursor.setBackgroundResource(cursorRes);
		this.addView(cursor);
		cursor.setVisibility(View.INVISIBLE);

		View focus = ((ViewGroup) getParent()).findFocus();
		if (focus == null) {
			View item = getChildAt(0);
			if (item != null) {
				item.requestFocus();
			}
		}

	}

	private void addNewItems() {

		currentChildCount = getChildCount();
		parentLayout = false;
		int start = itemIds.size();
		int end = Math.min(start + screenMaxRow * 2, adapter.getCount());

		for (int i = start; i < end; i++) {
			int left = 0;
			int top = i * (spaceVert + itemHeight);
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					itemWidth, itemHeight);
			rlp.setMargins(left, top, 0, 0);
			View child = adapter.getView(i, null, this);

			int viewId = child.getId();
			if (viewId == -1) {
				viewId = TvUtil.buildId();
				// 此处硬设置id同时建议开发者不用此范围id
			}
			child.setId(viewId);
			this.addView(child, rlp);
			itemIds.put(viewId, i);
			bindEvent(child, i);

		}

		canAdd = true;

	}

	/**
	 * 绑定事件
	 * 
	 * @param child
	 */
	private void bindEvent(final View item, final int index) {

		int viewId = item.getId();
		if (viewId == -1) {
			viewId = TvUtil.buildId();
			// 此处硬设置id同时建议开发者不用此范围id
		}
		item.setId(viewId);
		itemIds.put(viewId, index);

		// 根据焦点优先级设定选中效果
		switch (focusOption) {
		case PARENT_ONLY:
			focusIds.put(viewId, index);
			item.setFocusable(true);
			item.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(final View child, boolean focus) {

					if (focus) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								moveCover(item, child);
							}
						}, delay);
						// 选中事件
						if (onItemSelectListener != null) {
							onItemSelectListener
									.onItemSelect(item, index);
						}
						

					} else {
						returnCover(child);
						// 失去焦点
						if (onItemSelectListener != null) {
							onItemSelectListener
									.onItemDisSelect(item, index);
						}
					}
				}
			});

			if (onItemClickListener != null) {
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View item) {
						onItemClickListener.onItemClick(item, index);

					}
				});
			}

			break;
		case CHILD_ONLY:
			item.setFocusable(false);
			ViewGroup itemGroup = ((ViewGroup) item);

			for (int i = 0; i < itemGroup.getChildCount(); i++) {

				focusIds.put(viewId, index);
				View itemChild = itemGroup.getChildAt(i);
				if (!itemChild.isFocusable()) {
					continue;
				}

				itemChild.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(final View child, boolean focus) {

						if (focus) {
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									moveCover(item, child);
								}
							}, delay);
							// 选中事件
							if (onItemSelectListener != null) {
								onItemSelectListener.onItemSelect(item,
										index);
							}

						} else {
							returnCover(child);
							// 失去焦点
							if (onItemSelectListener != null) {
								onItemSelectListener.onItemDisSelect(item,
										selectIndex);
							}
						}
					}
				});

				if (onItemClickListener != null) {
					itemChild.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View item) {
							onItemClickListener.onItemClick(item, index);

						}
					});
				}

			}

			itemGroup = null;

			break;
		case PARENT_FIRST:

			break;
		case CHILD_FIRST:

			break;
		}

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
		// + cCount + "---" + parentLayout);
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
			// Log.e(VIEW_LOG_TAG, "onLayout=" + currentChildCount + "----"
			// + itemIds.size());
			for (int i = start; i < cCount; i++) {

				// 跳过光标子项
				int index = i;
				if (currentChildCount != 0) {
					index = i - 1;
				}
				if (index < itemIds.size()) {
					View childView = findViewById(itemIds.keyAt(index));
					if (childView != null) {
						// Log.e(VIEW_LOG_TAG, "index" + index);
						// Log.e(VIEW_LOG_TAG, cursorId + "---" + childView.getId()
						// + "---" + itemIds.keyAt(index));
						cWidth = childView.getMeasuredWidth();
						cHeight = childView.getMeasuredHeight();

						int cl = 0, ct = 0, cr = 0, cb = 0;
						cl = 0;
						ct = index * (spaceVert + itemHeight);

						cr = cl + cWidth;
						cb = cHeight + ct;
						childView.layout(cl + paddingLeft, ct + paddingTop, cr
								+ paddingLeft, cb + paddingTop);
					}
				}

			}
			screenMaxRow = getHeight() % rowHeight == 0 ? getHeight()
					/ rowHeight : getHeight() / rowHeight + 1;
		}

	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			boolean flag = false;
			View focused = this.findFocus();
			View next = null;
			int focusIndex = 0;
			switch (focusOption) {
			case PARENT_ONLY:
				focusIndex = itemIds.get(focused.getId());
				break;

			case CHILD_ONLY:
				focusIndex = itemIds.get(((View) focused.getParent()).getId());
				break;
			case PARENT_FIRST:
				focusIndex = itemIds.get(focused.getId());
				break;
			case CHILD_FIRST:
				focusIndex = itemIds.get(((View) focused.getParent()).getId());
				break;
			}
			int direction = 0;

			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_DOWN:
				direction = View.FOCUS_DOWN;
				// 获得目标焦点控件
				if (focusIndex < itemIds.size() - 1) {
					next = findViewById(itemIds.keyAt(focusIndex + 1));
				}
				if (!canAdd) {
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				direction = View.FOCUS_RIGHT;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if (focusIndex > 0) {
					next = findViewById(itemIds.keyAt(focusIndex - 1));
				}
				direction = View.FOCUS_UP;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				direction = View.FOCUS_LEFT;
				break;
			}

			if (direction != 0) {

				if (next != null) {

					Integer temp = itemIds.get(next.getId());
					// 焦点切出容器时
					if (temp != null) {
						selectIndex = temp;
					} else {
						parentLayout = true;
						return super.dispatchKeyEventPreIme(event);
					}

					int nextIndex = temp;

					// 向下到达最后一完整行时,可滚动; 向上到达最上一行完整行时,可滚动

					if (nextIndex > focusIndex) {
						if ((next.getTop() - mScroller.getFinalY()) >= (rowHeight * (screenMaxRow - 1))
								+ paddingTop) {
							flag = true;
						}
					} else if (nextIndex < focusIndex && nextIndex != 0) {
						if ((next.getTop() - mScroller.getFinalY()) < rowHeight
								+ paddingTop
								&& focusIndex != 0) {
							flag = true;
						}
					}
					// 建缓冲区允许快速翻动
					selectIndex = nextIndex;
					if (flag) {
						if (nextIndex > -1 && !scrollable
								&& mScroller.isFinished()) {
							// 先清除按钮动画
							scrollable = true;
							Message msg = handler.obtainMessage();
							msg.obj = direction;
							msg.what = ACTION_START_SCROLL;
							handler.sendMessageDelayed(msg, scrollDelay);
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
			if (t > oldt) {
				// 下翻加载 当剩余行数小于一屏时
				if ((itemIds.size() - selectIndex) < screenMaxRow) {
					canAdd = false;
					Message msg = handler.obtainMessage();
					msg.what = ACTION_ADD_ITEMS;
					handler.sendMessageDelayed(msg, DELAY);
				}

			}

		}

		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * 翻页
	 * 
	 * @param page
	 */
	private void scrollByRow(final int direction) {

		if (selectIndex < 0 || selectIndex > itemIds.size() - 1) {
			return;
		}
		if (direction == View.FOCUS_UP) {
			mScroller.startScroll(0, mScroller.getFinalY(), 0, -rowHeight,
					scrollDuration);
		} else if (direction == View.FOCUS_DOWN) {
			mScroller.startScroll(0, mScroller.getFinalY(), 0, rowHeight,
					scrollDuration);
		}

		invalidate();

		// 滚动同时进行回收操作 避免卡顿
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				//recycle(direction);

			}
		}, scrollDuration);

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
	 * 回收
	 * 
	 * @param page
	 */
	private void recycle(int direction) {

		if (selectIndex < 0 || selectIndex > itemIds.size() - 1) {
			return;
		}
		if (direction == View.FOCUS_UP) {

			int reloadIndex = selectIndex - screenMaxRow + 1;

			// 上翻刷新 并重新选中行前一屏的行
			if (reloadIndex > -1) {
				// 4Log.e(VIEW_LOG_TAG, "reloadRow="+reloadRow);
				mRecycleBin.reloadView(findViewById(itemIds
						.keyAt(reloadIndex)));

			}

			// 回收行数大于selectIndex 2倍屏数的子项
			int recyleIndex = selectIndex + screenMaxRow * 2;
			if (recyleIndex < itemIds.size() - 1) {
				// Log.e(VIEW_LOG_TAG, "recyleRow="+recyleRow);
				mRecycleBin
						.recycleView(findViewById(itemIds.keyAt(recyleIndex)));

			}

		} else if (direction == View.FOCUS_DOWN) {

			// 重新加载选中行以下一屏行数
			int reloadIndex = selectIndex + screenMaxRow - 1;

			// 上翻刷新 并重新选中行前一屏的行
			if (reloadIndex > -1) {
				// 4Log.e(VIEW_LOG_TAG, "reloadRow="+reloadRow);
				mRecycleBin.reloadView(findViewById(itemIds
						.keyAt(reloadIndex)));

			}

			// 回收行数小于selectIndex 2倍屏数的子项
			int recyleIndex = selectIndex - screenMaxRow * 2;
			if (recyleIndex>-1&&recyleIndex < itemIds.size() - 1) {
				// Log.e(VIEW_LOG_TAG, "recyleRow="+recyleRow);
				mRecycleBin
						.recycleView(findViewById(itemIds.keyAt(recyleIndex)));

			}

		}

	}

	/**
	 * 光标移动 到达后 与控件同时放大
	 */
	private void moveCover(View item, View focus) {

		if (cursor == null) {
			return;
		}

		setBorderParams(item, focus);
		focus.bringToFront();
		cursor.bringToFront();
		if (scalable) {
			scaleToLarge(focus);
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
	private void setBorderParams(View item, View focus) {
		cursor.clearAnimation();
		cursor.setVisibility(View.VISIBLE);

		// 判断类型
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item
				.getLayoutParams();

		RelativeLayout.LayoutParams focusParams = (RelativeLayout.LayoutParams) focus
				.getLayoutParams();
		int l = 0, t = 0, r = 0, b = 0;

		if (item == focus) {
			l = params.leftMargin + paddingLeft - boarderLeft;
			t = params.topMargin + paddingTop - boarderTop;
			r = l + itemWidth + boarderRight + boarderLeft;
			b = t + itemHeight + boarderBottom + boarderTop;
		} else {
			l = params.leftMargin + focus.getLeft() + paddingLeft - boarderLeft;
			t = params.topMargin + focus.getTop() + paddingTop - boarderTop;
			r = l + focusParams.width + boarderRight + boarderLeft;
			b = t + focusParams.height + boarderBottom + boarderTop;
		}

		cursor.layout(l, t, r, b);

	}

	public void setOnItemSelectListener(OnItemSelectListener myListener) {
		this.onItemSelectListener = myListener;
	}

	public void setOnItemClickListener(OnItemClickListener myListener) {
		this.onItemClickListener = myListener;
	}

	public interface OnItemSelectListener {
		/**
		 * 子项获得焦点
		 * 
		 * @param item
		 * @param position
		 */
		public void onItemSelect(View item, int position);
		
		/**
		 * 子项失去焦点
		 * 
		 * @param item
		 * @param position
		 */
		public void onItemDisSelect(View item, int position);

	
	}

	public interface OnItemClickListener {
		public void onItemClick(View item, int position);
	}

	public class AdapterDataSetObservable extends DataSetObservable {
		@Override
		public void notifyChanged() {
			// 数据改变 若已翻至末端 则立即调用addNewItems
			Log.i(VIEW_LOG_TAG, "收到数据改变通知");
			
			if (adapter.getCount()<=itemIds.size()) {
				//删减刷新
				clear();
				clear();
				Message msg = handler.obtainMessage();
				msg.what = ACTION_INIT_ITEMS;
				handler.sendMessageDelayed(msg, DELAY);
				
					
			}else{
				//添加刷新
				if ((itemIds.size() - selectIndex) < screenMaxRow) {
					canAdd = false;
					Message msg = handler.obtainMessage();
					msg.what = ACTION_ADD_ITEMS;
					handler.sendMessageDelayed(msg, DELAY);
				}
				
			}
			super.notifyChanged();
			
		}

		@Override
		public void notifyInvalidated() {
			super.notifyInvalidated();
		}
	}
}
