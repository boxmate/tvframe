package reco.frame.tv.view.component;

import java.util.List;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FragmentAdapter extends PagerAdapter{
	private List<Fragment> fragments; // 每个Fragment对应一个Page
	private FragmentManager fragmentManager;
	private int currentPageIndex = 0; // 当前page索引（切换之前）

	public FragmentAdapter(FragmentManager fragmentManager,
			List<Fragment> fragments) {
		this.fragments = fragments;
		this.fragmentManager = fragmentManager;
	}

	public int getCount() {
		return fragments.size();
	}

	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(fragments.get(position).getView()); // 移出viewpager两边之外的page布局
	}

	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = fragments.get(position);
		if (!fragment.isAdded()) { // 如果fragment还没有added
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(fragment, fragment.getClass().getSimpleName());
			ft.commit();
			/**
			 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
			 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
			 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
			 */
			fragmentManager.executePendingTransactions();
		}
		
		if (fragment.getView().getParent() == null) {
			container.addView(fragment.getView()); // 为viewpager增加布局
		}
		

		return fragment.getView();
	}

	/**
	 * 当前page索引（切换之前）
	 * 
	 * @return
	 */
	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

}
