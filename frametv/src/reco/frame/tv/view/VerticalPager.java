//package reco.frame.tv.view;
//
//
//import reco.frame.tv.R;
//import android.content.Context;
//import android.graphics.Rect;
//import android.os.Handler;
//import android.os.Message;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.FocusFinder;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.VelocityTracker;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Scroller;
//import android.widget.Toast;
//
//public class VerticalPager extends ViewGroup {
//
//	private final String TAG = "VerticalPager";
//	private final int ACTION_START_SCROLL = 0;
//	private boolean scollFlag, initFlag;
//	private final int DELAY = 231, DURATION = 570;
//	private Scroller mScroller;
//	private VelocityTracker mVelocityTracker;
//	private int mLastMotionY;
//	public final static int PAGE_DESK = 1, PAGE_TOOL = 0, PAGE_PUSH = 2;
//	private MyScrollPageChangerListener myScrollPageChangerListener;
//	public int curPage, initPage;
//	private int pageHeight;
//	private int maxPage;
//
//	private Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//
//			switch (msg.what) {
//			case ACTION_START_SCROLL:
//				int page = (Integer) msg.obj;
//				if (scollFlag) {
//					scollFlag = false;
//					if (page > -1 && page < maxPage) {
//						setCurrentPage(page);
//					}
//				}
//
//				break;
//			}
//
//		};
//	};
//
//	public VerticalPager(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		init(context);
//	}
//
//	public VerticalPager(Context context) {
//		super(context);
//		init(context);
//	}
//
//	public VerticalPager(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		init(context);
//	}
//
//	private void init(Context context) {
//		mScroller = new Scroller(context);
//		this.initFlag = true;
//		this.initPage = 0;
//		this.scollFlag = false;
//		this.curPage = 0;
//		this.pageHeight = (int) getResources().getDimension(R.dimen.px630);
//
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int width = MeasureSpec.getSize(widthMeasureSpec);
//		int height = MeasureSpec.getSize(heightMeasureSpec);
//		int count = getChildCount();
//		this.maxPage = count;
//		for (int i = 0; i < count; i++) {
//			getChildAt(i).measure(width, height);
//		}
//		setMeasuredDimension(width, height);
//	}
//
//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		this.pageHeight = b;
//		int totalHeight = -initPage * b;
//		int count = getChildCount();
//
//		for (int i = 0; i < count; i++) {
//			View childView = getChildAt(i);
//			childView.layout(l, totalHeight, r, totalHeight + b);
//			totalHeight += b;
//		}
//
//	}
//
//	public void setInitPage(int page) {
//		this.initPage = page;
//		this.curPage = page;
//	}
//
//	
//	@Override
//	public boolean dispatchKeyEventPreIme(KeyEvent event) {
//		//Log.e(TAG, "curPage=" + curPage + "---" + scollFlag);
//		if (event.getAction() == KeyEvent.ACTION_DOWN) {
//
//			int direction=0;
//			switch (event.getKeyCode()) {
//			case KeyEvent.KEYCODE_DPAD_DOWN:
//				direction=View.FOCUS_DOWN;
//				break;
//			case KeyEvent.KEYCODE_DPAD_RIGHT:
//				direction=View.FOCUS_RIGHT;
//				break;
//			case KeyEvent.KEYCODE_DPAD_UP:
//				direction=View.FOCUS_UP;
//				break;
//			case KeyEvent.KEYCODE_DPAD_LEFT:
//				direction=View.FOCUS_LEFT;
//				break;
//			}
//			View focused = findFocus();
//			if (focused!=null&&direction!=0) {
//				View next=focused.focusSearch(direction);
//				if (next!=null) {
//					String focusTagNow = focused.getTag().toString()+"";
//					String focusTagNext = next.getTag().toString()+"";
//					if (!"".equals(focusTagNext)
//							&&!"".equals(focusTagNow)
//							&&!focusTagNow.equals(focusTagNext)) {
//						int nextPage=Integer.parseInt(focusTagNext);
//						if (nextPage>0&& !scollFlag
//								&& mScroller.isFinished()) {
//							// 先清除按钮动画
//							scollFlag = true;
//							Message msg = handler.obtainMessage();
//							msg.obj = nextPage-1;
//							msg.what = ACTION_START_SCROLL;
//							handler.sendMessageDelayed(msg, DELAY);
//						} else {
//							return true;
//						}
//					}
//				}
//			}
//			
//			
//		}
//				
//			
//
//		return super.dispatchKeyEventPreIme(event);
//	}
//
//	@Override
//	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//		if (t > oldt) {
//			this.myScrollPageChangerListener.onPageChange(curPage - 1, curPage);
//		} else if (t < oldt) {
//			this.myScrollPageChangerListener.onPageChange(curPage + 1, curPage);
//		}
//		super.onScrollChanged(l, t, oldl, oldt);
//	}
//
//	/**
//	 * 调用此方法滚动到目标位置
//	 * 
//	 * @param fx
//	 * @param fy
//	 */
//	public void smoothScrollTo(int fx, int fy) {
//		int dx = fx - mScroller.getFinalX();
//		int dy = fy - mScroller.getFinalY();
//		smoothScrollBy(dx, dy);
//	}
//
//	/**
//	 * 调用此方法设置滚动的相对偏移
//	 * 
//	 * @param dx
//	 * @param dy
//	 */
//	public void smoothScrollBy(int dx, int dy) {
//
//		// 设置mScroller的滚动偏移量
//		mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
//				dy);
//		invalidate();
//	}
//
//	/**
//	 * 跳转至指定页面
//	 * 
//	 * @param page
//	 */
//	public void setCurrentPage(int page) {
//
//		if (page < 0 || page > maxPage - 1) {
//			return;
//		}
//		if (page == curPage) {
//			return;
//		}
//		if (page < curPage) {
//			int distance=(page-curPage)*pageHeight;
//			mScroller.startScroll(0, mScroller.getFinalY(), 0, distance,
//					DURATION);
//		} else {
//			int distance=(page-curPage)*pageHeight;
//			mScroller.startScroll(0, mScroller.getFinalY(), 0, distance,
//					DURATION);
//		}
//		
//		
//		curPage = page;
//		invalidate();
//
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//
//		// if (mVelocityTracker == null) {
//		// mVelocityTracker = VelocityTracker.obtain();
//		// }
//		// mVelocityTracker.addMovement(event);
//		//
//		// int action = event.getAction();
//		//
//		// float y = event.getY();
//		//
//		// switch (action) {
//		// case MotionEvent.ACTION_DOWN:
//		// if (!mScroller.isFinished()) {
//		// mScroller.abortAnimation();
//		// }
//		// mLastMotionY = (int) y;
//		//
//		// break;
//		// case MotionEvent.ACTION_MOVE:
//		// int deltaY = (int) (mLastMotionY - y);
//		// scrollBy(0, deltaY);
//		// invalidate();
//		//
//		// mLastMotionY = (int) y;
//		// break;
//		// case MotionEvent.ACTION_UP:
//		// if (mVelocityTracker != null) {
//		// mVelocityTracker.recycle();
//		// mVelocityTracker = null;
//		// }
//		// // Log.e("montion", "" +
//		// // getScrollY()+"==="+getHeight()+"==="+mLastMotionY);
//		// if (getScrollY() < 0) {
//		// mScroller.startScroll(0, -mLastMotionY, 0, -getScrollY());
//		// } else if (getScrollY() > (getHeight() * (getChildCount() - 1))) {
//		// View lastView = getChildAt(getChildCount() - 1);
//		// mScroller.startScroll(0, lastView.getTop() + getHeight(), 0,
//		// -300);
//		// } else {
//		// int position = getScrollY() / getHeight();
//		// int mod = getScrollY() % getHeight();
//		//
//		// if (mod > getHeight() / 3) {
//		// View positionView = getChildAt(position + 1);
//		// mScroller.startScroll(0, positionView.getTop() - 300, 0,
//		// +300);
//		// } else {
//		// View positionView = getChildAt(position);
//		// mScroller.startScroll(0, positionView.getTop() + 300, 0,
//		// -300);
//		// }
//		//
//		// }
//		// invalidate();
//		// break;
//		// }
//
//		return true;
//	}
//
//	@Override
//	public void computeScroll() {
//		super.computeScroll();
//
//		// 先判断mScroller滚动是否完成
//		if (mScroller.computeScrollOffset()) {
//
//			// 这里调用View的scrollTo()完成实际的滚动
//			scrollTo(0, mScroller.getCurrY());
//			// 必须调用该方法，否则不一定能看到滚动效果
//			postInvalidate();
//		}
//		super.computeScroll();
//	}
//
//	public void setPageChangeListener(MyScrollPageChangerListener myListener) {
//		this.myScrollPageChangerListener = myListener;
//	}
//
//	public interface MyScrollPageChangerListener {
//		public void onPageChange(int pageBefore, int pageCurrent);
//	}
//
//}