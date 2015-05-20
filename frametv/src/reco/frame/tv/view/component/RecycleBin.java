package reco.frame.tv.view.component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.renderscript.Int2;
import android.support.v4.util.LruCache;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RecycleBin {
	private final static String TAG = "RecycleBin";
	public final static int STATE_ACTIVE = 0, STATE_SCRAP = 1;

	public static LruCache<String, Bitmap> mLruCache;

	private String cachedDir;
	// 线程池
	private static ExecutorService sExecutorService;
	// 通知UI线程图片获取ok时使用
	private Handler handler;
	// 存储处于回收状态的item ID 及其中图片控件ID
	// private static SparseIntArray recycleIds;
	// 状态记录
	private static SparseIntArray itemStates;
	// 存储处于回收状态的item ID 及其中图片控件ID
	private Map<Integer, HashSet<Integer>> recycleIds;

	private final static int STATE_RECYCLING = 1, STATE_RELOADING = 2;

	public RecycleBin(String cachedDir) {
		this.cachedDir = cachedDir;
		handler = new Handler();
		// recycleIds = new SparseIntArray();
		itemStates = new SparseIntArray();
		initLruCache();
		startThreadPoolIfNecessary();

		recycleIds = new HashMap<Integer, HashSet<Integer>>();

	}

	private static void initLruCache() {
		if (mLruCache == null) {
			// 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
			// LruCache通过构造函数传入缓存值，以KB为单位。
			int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
			// 使用最大可用内存值的1/6作为缓存的大小。
			int cacheSize = maxMemory / 6;
			mLruCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// 重写此方法来衡量每张图片的大小，默认返回图片数量。
					return bitmap.getByteCount() / 1024;
				}
			};
		}

	}

	/** 开启线程池 */
	public static void startThreadPoolIfNecessary() {
		if (sExecutorService == null || sExecutorService.isShutdown()
				|| sExecutorService.isTerminated()) {
			sExecutorService = Executors.newFixedThreadPool(3);
		}
	}

	public void recycleChild(HashSet<Integer> idSet,
			final View scrapView, int itemId) {

		// 销毁图片
		if (scrapView instanceof ImageView) {
			((ImageView) scrapView).setImageResource(0);
			// if (front != null && front instanceof BitmapDrawable) {
			// BitmapDrawable bitmapDrawable = (BitmapDrawable) front;
			// Bitmap bitmap = bitmapDrawable.getBitmap();
			// if (bitmap != null && !bitmap.isRecycled()) {
			// recycleBitmap(bitmap);
			// }
			// }

		}

		Drawable background = scrapView.getBackground();
		if (background != null) {

			BitmapDrawable bitmapDrawable = null;

			if (background instanceof TransitionDrawable) {
				background = ((TransitionDrawable) background).getDrawable(1);
			}
			bitmapDrawable = (BitmapDrawable) background;
			final Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap != null && !bitmap.isRecycled()) {
				// 保存成功则将childId添加集合中
				if (saveBitmap(itemId, scrapView.getId(), bitmap)) {
					idSet.add(scrapView.getId());
					handler.post(new Runnable() {

						@Override
						public void run() {
							scrapView.setBackgroundResource(0);

						}
					});
				}
			}
			

		}

	}

	public void recycleView(final View item) {

		sExecutorService.submit(new Runnable() {

			@Override
			public void run() {
				if (itemStates.get(item.getId(), -1) != -1) {
					return;
				}

				itemStates.put(item.getId(), STATE_RECYCLING);
				HashSet<Integer> set = new HashSet<Integer>();
				if (item instanceof ViewGroup) {
					ViewGroup container = (ViewGroup) item;

					for (int i = 0; i < container.getChildCount(); i++) {
						View child = container.getChildAt(i);
						recycleChild(set, child, item.getId());
					}
					if (!set.isEmpty()) {
						recycleIds.put(item.getId(), set);
					}
				}

			}
		});

	}

	public void reloadView(final View item) {

		if (item == null) {
			return;
		}
		final HashSet<Integer> childIds = (HashSet<Integer>) recycleIds
				.get(item.getId());

		if (childIds == null) {
			return;
		}

		int state = itemStates.get(item.getId()) + 0;
		if (state != STATE_RECYCLING) {
			return;
		}

		itemStates.delete(item.getId());
		itemStates.put(item.getId(), STATE_RELOADING);

		// 自缓存获取图片

		Object[] childIdArray = childIds.toArray();
		for (int i = 0; i < childIdArray.length; i++) {
			final int childId = (Integer) childIdArray[i];
			// 如背景为图 则默认为第一张
			final Bitmap bmp = getBitmapFromMemory(childId + "");
			if (bmp != null) {

				if (childId == item.getId()) {
					// item有图

				} else {
					// 子类有图
					Log.d(TAG, "成功自缓存读取图片" + childId + "--" + item.getId());
					View child = item.findViewById(childId);
					// ??为何为空
					child.setBackgroundDrawable(new BitmapDrawable(bmp));
				}
				childIds.remove(childId);
			} else {
				sExecutorService.submit(new Runnable() {
					@Override
					public void run() {

						final Bitmap bmp = getBitmapFromDisk(item.getId());
						Log.d(TAG, "成功自磁盘读取图片" + childId + "--" + item.getId()
								+ "---" + bmp);
						if (bmp == null)
							return;

						setBitmapToMemory(item.getId() + "", bmp);

						handler.post(new Runnable() {
							@Override
							public void run() {

								if (childId == item.getId()) {
									// item有图

								} else {
									// 子类有图

									View child = item.findViewById(childId);
									child.setBackgroundDrawable(new BitmapDrawable(
											bmp));
								}

								childIds.remove(childId);
							}
						});
					}
				});
			}
		}

		recycleIds.remove(item.getId());
		itemStates.delete(item.getId());

	}

	/**
	 * 从内存缓存中获取bitmap
	 * 
	 * @param url
	 * @return bitmap or null.
	 */
	public static Bitmap getBitmapFromMemory(String key) {
		return mLruCache.get(TvUtil.md5(key));
	}

	/**
	 * 从外部文件缓存中获取bitmap
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFromDisk(int viewId) {
		Bitmap bitmap = null;
		String fileName = TvUtil.md5(viewId + "");

		String filePath = cachedDir + "/" + fileName;

		try {
			FileInputStream fis = new FileInputStream(filePath);
			bitmap = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 存储图到到缓存中
	 * 
	 * @param key
	 * @param bitmap
	 */
	public static void setBitmapToMemory(String key, Bitmap bitmap) {
		String md5 = TvUtil.md5(key);
		if (mLruCache.get(md5) == null) {
			mLruCache.put(md5, bitmap);
		}
	}

	public boolean saveBitmap(int itemId, int childId, Bitmap bmp) {

		String key = TvUtil.md5(itemId + "");
		// 缓存bitmap至内存软引用中
		setBitmapToMemory(key, bmp);

		try {

			String filePath = this.cachedDir + "/" + key;
			FileOutputStream fos = new FileOutputStream(filePath);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
			Log.d(TAG, "成功存储图片到本地=" + itemId);
			return true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 异步加载图片完毕的回调接口
	 */
	public interface DrawableCallback {
		/**
		 * 回调函数
		 * 
		 * @param bitmap
		 *            : may be null!
		 * @param imageUrl
		 */
		public void onDrawableLoaded(Drawable draw);

	}

}