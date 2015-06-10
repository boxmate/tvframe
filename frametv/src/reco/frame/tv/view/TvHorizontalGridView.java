package reco.frame.tv.view;

import reco.frame.tv.R;
import reco.frame.tv.view.component.RecycleBin;
import reco.frame.tv.view.component.TvBaseAdapter;
import reco.frame.tv.view.component.TvUtil;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 横向的gridview 自带选中框效果
 * 
 * @author keYence
 * 
 */
public class TvHorizontalGridView extends RelativeLayout {

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
	 * 平移用时
	 */
	private int durationTraslate = 100;
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
	 * 焦点移出容器
	 */
	private boolean focusIsOut;

	/**
	 * 除光标外 当前子类数
	 */
	private int currentChildCount = 0;

	/**
	 * 可否滚动
	 */
	private final int ACTION_START_SCROLL = 0, ACTION_INIT_ITEMS = 1,
			ACTION_ADD_ITEMS = 2;

	/**
	 * 刷新延迟
	 */
	private final int DELAY = 371;
	/**
	 * 行数
	 */
	private int rows;
	private int colCount, selectCol;
	/**
	 * 屏幕可显示最大列数
	 */
	private int screenMaxColumns;
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
	private SparseArray<Integer> itemIds;

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
	private boolean initFocus;
	/**
	 * 以1280为准,其余分辨率放大比率 用于适配
	 */
	private float screenScale = 1;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case ACTION_START_SCROLL:
				int direction = (Integer) msg.obj;
				scrollByRow(direction);

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

	public TvHorizontalGridView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public TvHorizontalGridView(Context context) {
		super(context);
	}

	public TvHorizontalGridView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray custom = getContext().obtainStyledAttributes(attrs,
				R.styleable.TvHorizontalGridView);
		this.cursorRes = custom.getResourceId(R.styleable.TvHorizontalGridView_cursorRes,
				0);
		this.scalable = custom
				.getBoolean(R.styleable.TvHorizontalGridView_scalable, true);
		this.initFocus = custom.getBoolean(R.styleable.TvHorizontalGridView_initFocus,
				true);

		this.scale = custom.getFloat(R.styleable.TvHorizontalGridView_scale, 1.1f);
		this.animationType = custom.getInt(
				R.styleable.TvHorizontalGridView_animationType, ANIM_DEFAULT);
		this.delay = custom.getInteger(R.styleable.TvHorizontalGridView_delay, 110);
		this.scrollDelay = custom.getInteger(
				R.styleable.TvHorizontalGridView_scrollDelay, 171);
		this.scrollDuration = custom.getInteger(
				R.styleable.TvHorizontalGridView_scrollDuration, 371);
		this.durationLarge = custom.getInteger(
				R.styleable.TvHorizontalGridView_durationLarge, 230);
		this.durationTraslate=custom.getInteger(
				R.styleable.TvHorizontalGridView_durationTranslate, 170);
		this.durationSmall = custom.getInteger(
				R.styleable.TvHorizontalGridView_durationSmall, 230);

		this.rows = custom.getInteger(R.styleable.TvHorizontalGridView_rows, 2);
		this.spaceHori = (int) custom.getDimension(
				R.styleable.TvHorizontalGridView_spaceHori, 10);
		this.spaceVert = (int) custom.getDimension(
				R.styleable.TvHorizontalGridView_spaceVert, 10);

		itemWidth = (int) custom.getDimension(R.styleable.TvHorizontalGridView_itemWidth,
				10);
		itemHeight = (int) custom.getDimension(
				R.styleable.TvHorizontalGridView_itemHeight, 10);
		rowWidth = itemHeight + spaceVert;
		rowWidth = itemWidth + spaceHori;

		paddingLeft = (int) custom.getDimension(
				R.styleable.TvHorizontalGridView_paddingLeft, 0);
		paddingTop = (int) custom.getDimension(
				R.styleable.TvHorizontalGridView_paddingTop, 0);

		this.boarder = (int) custom.getDimension(
				R.styleable.TvHorizontalGridView_boarder, 0)
				+ custom.getInteger(R.styleable.TvHorizontalGridView_boarderInt, 0);

		if (boarder == 0) {
			this.boarderLeft = (int) custom.getDimension(
					R.styleable.TvHorizontalGridView_boarderLeft, 0)
					+ custom.getInteger(R.styleable.TvHorizontalGridView_boarderLeftInt,
							0);
			this.boarderTop = (int) custom.getDimension(
					R.styleable.TvHorizontalGridView_boarderTop, 0)
					+ custom.getInteger(R.styleable.TvHorizontalGridView_boarderTopInt, 0);
			this.boarderRight = (int) custom.getDimension(
					R.styleable.TvHorizontalGridView_boarderRight, 0)
					+ custom.getInteger(R.styleable.TvHorizontalGridView_boarderRightInt,
							0);
			this.boarderBottom = (int) custom.getDimension(
					R.styleable.TvHorizontalGridView_boarderBottom, 0)
					+ custom.getInteger(
							R.styleable.TvHorizontalGridView_boarderBottomInt, 0);
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
						R.styleable.TvHorizontalGridView_cursorRes_1280, 0);
				break;

			case TvUtil.SCREEN_1920:
				cursorRes = custom.getResourceId(
						R.styleable.TvHorizontalGridView_cursorRes_1920, 0);
				break;
			case TvUtil.SCREEN_2560:
				cursorRes = custom.getResourceId(
						R.styleable.TvHorizontalGridView_cursorRes_2560, 0);
				break;
			case TvUtil.SCREEN_3840:
				cursorRes = custom.getResourceId(
						R.styleable.TvHorizontalGridView_cursorRes_3840, 0);
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
			initGridView();
			isInit = false;
		}

		Message msg = handler.obtainMessage();
		msg.what = ACTION_INIT_ITEMS;
		handler.sendMessageDelayed(msg, DELAY);
	}

	private void clear() {
		itemIds.clear();
		this.removeAllViews();
		this.clearDisappearingChildren();
		this.destroyDrawingCache();
		mScroller.setFinalY(0);
		parentLayout = false;
		focusIsOut=true;
		currentChildCount = 0;
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
				(int) (boarderTop * scale), (int)(boarderRight*scale), boarderBottom);
		this.setClipChildren(false);
		this.setClipToPadding(false);
		newParams.setMargins(params.leftMargin, params.topMargin,
				params.rightMargin, params.bottomMargin);
		this.setLayoutParams(newParams);

		// switch (getResources().getDisplayMetrics().widthPixels) {
		// case TvConfig.SCREEN_1280:
		// screenScale = 1f;
		// break;
		// case TvConfig.SCREEN_1920:
		// screenScale = 1.5f;
		// break;
		// case TvConfig.SCREEN_2560:
		// screenScale = 2f;
		// break;
		// case TvConfig.SCREEN_3840:
		// screenScale = 3f;
		// break;
		// }
		//
		// paddingLeft = (int) (screenScale*boarderLeft + itemWidth
		// * (scale - 1) / 2 );
		// paddingTop = (int) (boarderTop * screenScale + itemHeight * (scale -
		// 1) / 2);

	}

	private void initItems() {
		// 避免冲突
		if (getChildCount() > 0) {
			return;
		}

		int screenWidth = wm.getDefaultDisplay().getWidth();
		int initCols = screenWidth % rowWidth == 0 ? screenWidth / rowWidth
				: screenWidth / rowWidth + 1;

		int initLength = Math.min(adapter.getCount(), initCols * 2 * rows);
		for (int i = 0; i < initLength; i++) {
			int left = (i / rows) * (itemWidth + spaceHori);
			int top = (i % rows) * (spaceVert + itemHeight);
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					itemWidth, itemHeight);
			if (initLength == 1) {
				rlp.setMargins(left, top, 0, paddingTop*2);
			} else {
				rlp.setMargins(left, top, 0, 0);
			}

			View child = adapter.getView(i, null, this);
			this.addView(child, rlp);
			int viewId = child.getId();
			if (viewId == -1) {
				viewId = TvUtil.buildId();
				// 此处硬设置id同时建议开发者不用此范围id
			}
			child.setId(viewId);
			itemIds.put(viewId, i);
			bindEventOnChild(child, i);
			layoutFlag=true;
		}
		colCount = itemIds.size() % rows == 0 ? itemIds.size() / rows : itemIds
				.size() / rows + 1;

		cursor = new ImageView(getContext());
		cursorId = TvUtil.buildId();
		cursor.setId(cursorId);
		cursor.setBackgroundResource(cursorRes);
		this.addView(cursor);
		cursor.setVisibility(View.INVISIBLE);

		if (initFocus) {
			View focus = ((ViewGroup) getParent()).findFocus();
			if (focus == null) {
				View item = getChildAt(0);
				if (item != null) {
					item.requestFocus();
				}
			}
		}

	}

	/**
	 * layout辅助标记 表面有添加新的子项
	 */
	private boolean layoutFlag=false;
	private void addNewItems() {

		currentChildCount = getChildCount();
		parentLayout = false;
		int start = itemIds.size();
		int end = Math.min(start + screenMaxColumns * rows * 2,
				adapter.getCount());

		for (int i = start; i < end; i++) {
			int left = (i / rows) * (itemWidth + spaceHori);
			int top = (i % rows) * (spaceVert + itemHeight);
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					itemWidth, itemHeight);
			rlp.setMargins(left, top, 0, 0);
			View child = adapter.getView(i, null, this);
			this.addView(child, rlp);
			int viewId = child.getId();
			if (viewId == -1) {
				viewId = TvUtil.buildId();
				// 此处硬设置id同时建议开发者不用此范围id
			}
			child.setId(viewId);
			itemIds.put(viewId, i);
			bindEventOnChild(child, i);
			layoutFlag=true;
		}

		colCount = itemIds.size() % rows == 0 ? itemIds.size() / rows : itemIds
				.size() / rows + 1;

	}

	private void bindEventOnChild(View child, final int index) {
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
						onItemSelectListener.onItemSelect(item, index);
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
					onItemClickListener.onItemClick(item, index);
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

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		if (parentLayout) {
			parentLayout = false;
			return;
		}

		if (changed||layoutFlag) {
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
						cWidth = childView.getMeasuredWidth();
						cHeight = childView.getMeasuredHeight();

						int cl = 0, ct = 0, cr = 0, cb = 0;
						cl = (index / rows) * (itemWidth + spaceHori);
						ct = (index % rows) * (spaceVert + itemHeight);

						cr = cl + cWidth;
						cb = cHeight + ct;
						childView.layout(cl + paddingLeft, ct + paddingTop, cr
								+ paddingLeft, cb + paddingTop);
					}
				}

			}
			screenMaxColumns = getWidth() % rowWidth == 0 ? getWidth()
					/ rowWidth : getWidth() / rowWidth + 1;
			layoutFlag=false;
		}

	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (!mScroller.isFinished()) {
				return true;
			}
			boolean flag = false;
			int direction = 0;
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_DOWN:
				direction = View.FOCUS_DOWN;
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
						focusIsOut=false;
					} else {
						parentLayout = true;
						focusIsOut=true;
						return super.dispatchKeyEventPreIme(event);
					}

					int nextCol = 0;

					selectCol = focusIndex / rows;
					nextCol = selectIndex / rows;

					// 向下到达最后一完整行时,可滚动; 向上到达最上一行完整行时,可滚动
					if (nextCol > selectCol) {
						if ((next.getLeft() - mScroller.getFinalX()) >= (rowWidth * (screenMaxColumns - 1))
								+ paddingLeft) {
							flag = true;
						}
					} else if (nextCol < selectCol) {
						if ((next.getLeft() - mScroller.getFinalX()) < paddingLeft
								&& selectCol != 0) {
							flag = true;

						}
					}

					if (flag) {
						if (nextCol > -1 && mScroller.isFinished()) {
							selectCol = nextCol;
							// 先清除按钮动画
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

		if (l == mScroller.getFinalX()) {
			if (l > oldl) {
				// 下翻加载 当剩余行数小于一屏时
				Log.i(VIEW_LOG_TAG, "添加更多条目=" + colCount + "--" + selectCol
						+ "--" + screenMaxColumns);
				if ((colCount - selectCol) < screenMaxColumns) {

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
		if (selectCol < 0 || selectCol > colCount - 1) {
			return;
		}
		if (direction == View.FOCUS_LEFT) {
			// mScroller.startScroll(0, mScroller.getFinalY(), 0, -rowWidth,
			// scrollDuration);
			
			mScroller.startScroll(mScroller.getFinalX(), 0, -rowWidth, 0,
					scrollDuration);
		} else if (direction == View.FOCUS_RIGHT) {
			// mScroller.startScroll(0, mScroller.getFinalY(), 0, rowWidth,
			// scrollDuration);
			mScroller.startScroll(mScroller.getFinalX(), 0, rowWidth, 0,
					scrollDuration);
		}

		invalidate();

		// 滚动时进行回收操作 避免卡顿
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				 recycle(direction);
//			}
//		}, scrollDuration);

	}

	/**
	 * 回收
	 * 
	 * @param page
	 */
	private void recycle(int direction) {

		if (selectCol < 0 || selectCol > colCount - 1) {
			return;
		}
		if (direction == View.FOCUS_LEFT) {

			int reloadRow = selectCol - screenMaxColumns + 1;
			// 上翻刷新 并重新选中行前一屏的行
			if (reloadRow > -1) {
				View reloadView = null;
				int reloadIndex = 0;
				for (int i = 0; i < rows; i++) {
					reloadIndex = reloadRow * rows + i;
					reloadView = findViewById(itemIds.keyAt(reloadIndex));
					mRecycleBin.reloadView(reloadView);
				}

			}

			// 回收行数大于selectCol 2倍屏数的子项
			int recyleRow = selectCol + screenMaxColumns * 2;
			if (recyleRow < colCount - 1) {
				View recyleView = null;
				int recyleIndex = 0;
				for (int i = 0; i < rows; i++) {
					recyleIndex = recyleRow * rows + i;
					recyleView = findViewById(itemIds.keyAt(recyleIndex));
					mRecycleBin.recycleView(recyleView);
				}

			}

		} else if (direction == View.FOCUS_RIGHT) {

			// 重新加载选中行以下一屏行数
			int reloadRow = selectCol + screenMaxColumns - 1;

			if (reloadRow < colCount - 1) {
				// Log.e(VIEW_LOG_TAG, "reloadRow="+reloadRow);
				View reloadView = null;
				int reloadIndex = 0;
				for (int i = 0; i < rows; i++) {
					reloadIndex = reloadRow * rows + i;
					reloadView = findViewById(itemIds.keyAt(reloadIndex));
					mRecycleBin.reloadView(reloadView);
				}

			}

			// 回收行数小于selectCol 2倍屏数的子项
			int recyleRow = selectCol - screenMaxColumns * 2 - 1;
			if (recyleRow > -1) {
				// Log.e(VIEW_LOG_TAG, "recyleRow="+recyleRow);
				View recyleView = null;
				int recyleIndex = 0;
				for (int i = 0; i < rows; i++) {
					recyleIndex = recyleRow * rows + i;
					recyleView = findViewById(itemIds.keyAt(recyleIndex));
					mRecycleBin.recycleView(recyleView);
				}

			}

		}

	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		// 先判断mScroller滚动是否完成
		if (mScroller.computeScrollOffset()) {

			// 这里调用View的scrollTo()完成实际的滚动
			scrollTo(mScroller.getCurrX(), 0);
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
		if (!item.isFocused()) {
			return;
		}

		setBorderParams(item);
		

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
	private void setBorderParams(final View item) {
		cursor.clearAnimation();
		cursor.setVisibility(View.VISIBLE);
		final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item
				.getLayoutParams();
		final int l = params.leftMargin + paddingLeft - boarderLeft;
		final int t = params.topMargin + paddingTop - boarderTop;
		final int r = l + itemWidth + boarderRight;
		final int b = t + itemHeight + boarderBottom;
		// 判断动画类型
		switch (animationType) {
		case ANIM_DEFAULT:
			cursor.layout(l, t, r, b);
			item.bringToFront();
			cursor.bringToFront();
			if (scalable) {
				scaleToLarge(item);
			}
			break;
		case ANIM_TRASLATE:
			ValueAnimator transAnimatorX = ObjectAnimator.ofFloat(cursor,
					"x",  cursor.getLeft(), l);
			ValueAnimator transAnimatorY = ObjectAnimator.ofFloat(cursor,
					"y", cursor.getTop(), t);
			cursor.layout(l, t, r, b);
			item.bringToFront();
			cursor.bringToFront();
			if (scalable) {
				scaleToLarge(item);
			}
			if (focusIsOut) {
				return;
			}
			
			animatorSet = new AnimatorSet();
			animatorSet.play(transAnimatorY).with(transAnimatorX);
			animatorSet.setDuration(durationTraslate);
			animatorSet.setInterpolator(new DecelerateInterpolator(1));
//			animatorSet.addListener(new AnimatorListener() {
//
//				@Override
//				public void onAnimationStart(Animator arg0) {
//				}
//
//				@Override
//				public void onAnimationRepeat(Animator arg0) {
//				}
//
//				@Override
//				public void onAnimationEnd(Animator arg0) {
//					cursor.layout(l, t, r, b);
//					item.bringToFront();
//					cursor.bringToFront();
//					if (scalable) {
//						scaleToLarge(item);
//					}
//				}
//
//				@Override
//				public void onAnimationCancel(Animator arg0) {
//				}
//			});
			animatorSet.start();
			
			break;
			
			
		}

		
		

	}

	public void setOnItemSelectListener(OnItemSelectListener myListener) {
		this.onItemSelectListener = myListener;
	}

	public void setOnItemClickListener(OnItemClickListener myListener) {
		this.onItemClickListener = myListener;
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

			if (adapter.getCount() <= itemIds.size()) {
				// 删减刷新
				clear();
				Message msg = handler.obtainMessage();
				msg.what = ACTION_INIT_ITEMS;
				handler.sendMessageDelayed(msg, DELAY);

			} else {
				// 添加刷新
				if ((colCount - selectCol) < screenMaxColumns) {
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
