package reco.frame.demo.adapter;

import java.util.ArrayList;
import java.util.List;
import reco.frame.demo.R;
import reco.frame.demo.entity.AppInfo;
import reco.frame.tv.view.TvImageView;
import reco.frame.tv.view.TvRelativeLayout;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PageAdapter extends PagerAdapter {

	private List<List<AppInfo>> pageList;
	private Context context;
	private int width, height, space, margin,padding;
	private LayoutInflater mInflater;
	private int marginTop;
	private int column=4;

	public PageAdapter(Context context,ViewPager vp_container,List<AppInfo> itemList) {

		this.context = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.width = (int) context.getResources().getDimension(R.dimen.px546);
		this.height = (int) context.getResources().getDimension(R.dimen.px320);
		this.margin = (int) context.getResources().getDimension(R.dimen.px40);
		this.space = (int) context.getResources().getDimension(R.dimen.px20);
		init(vp_container,itemList);

	}
	private void init(ViewPager vp_container,List<AppInfo> itemList){
		pageList = new ArrayList<List<AppInfo>>();
		List<AppInfo> page = new ArrayList<AppInfo>();
		for (int i = 0; i < itemList.size(); i++) {

			AppInfo app = itemList.get(i);
			page.add(app);

			if (page.size() >= column * 2) {
				pageList.add(page);
				page = new ArrayList<AppInfo>();

			} else if (i == (itemList.size() - 1)) {
				pageList.add(page);
				break;
			}

		}
		for (int i = 0; i < pageList.size(); i++) {
			vp_container.addView(createView(pageList.get(i), i + 1));
		}
	}

	@Override
	public int getCount() {
		return pageList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		View pageView = null;

		try {
			pageView = createView(pageList.get(position),position + 1);
			((ViewPager) container).addView(pageView);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pageView;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	private RelativeLayout createView(List<AppInfo> appList,final int pageNum) {

		ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		final RelativeLayout page = new RelativeLayout(context);
		page.setPadding(space, space, space, space);
		page.setLayoutParams(vlp);
		page.setClipChildren(false);
		page.setClipToPadding(false);

		for (int i = 0; i < appList.size(); i++) {
			final AppInfo app = appList.get(i);
			final TvRelativeLayout scaleButton=(TvRelativeLayout) mInflater
					.inflate(R.layout.item_bitmap, null);
			TvImageView tiv_icon = (TvImageView) scaleButton
					.findViewById(R.id.tiv_icon);
			TextView tv_title = (TextView) scaleButton
					.findViewById(R.id.tv_title);
			int vId = pageNum * 10 + i;
			scaleButton.setId(vId);
			
			tv_title.setText(app.title);
			tiv_icon.configImageUrl(app.imageUrl);
				
			int size=230;
			if (i == 0) {
				
				RelativeLayout.LayoutParams appLpFirst = new RelativeLayout.LayoutParams(
						size, size);
				appLpFirst.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				appLpFirst.setMargins(0, 0, 0, 0);
				page.addView(scaleButton, appLpFirst);

			} else if (i < column) {
				RelativeLayout.LayoutParams appLpRight = new RelativeLayout.LayoutParams(
						size, size);
				appLpRight.addRule(RelativeLayout.RIGHT_OF, vId - 1);
				appLpRight.setMargins(space, 0, 0, 0);
				page.addView(scaleButton, appLpRight);

			} else if (i == column) {
				RelativeLayout.LayoutParams appLpRight = new RelativeLayout.LayoutParams(
						size, size);
				appLpRight.addRule(RelativeLayout.BELOW, vId - column);
				appLpRight.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				appLpRight.setMargins(0, space, 0, 0);
				page.addView(scaleButton, appLpRight);

			} else {
				RelativeLayout.LayoutParams appLpRight = new RelativeLayout.LayoutParams(
						size, size);
				appLpRight.addRule(RelativeLayout.BELOW, vId - column);
				appLpRight.addRule(RelativeLayout.RIGHT_OF, vId - 1);
				appLpRight.setMargins(space, space, 0, 0);
				page.addView(scaleButton, appLpRight);

			}

			if (pageNum == 1 && i == 0) {
				scaleButton.requestFocus();
			}

		}

		return page;
	}

}
