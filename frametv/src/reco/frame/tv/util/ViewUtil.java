package reco.frame.tv.util;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

public class ViewUtil {

	
	public static void setBorderParams(RelativeLayout.LayoutParams params,
			View view, View border) {
		int leftOffset = 9;
		int topOffset = 9; // (int)r.getDimension(R.dimen.px11);
		int rightOffset = 9; // (int)r.getDimension(R.dimen.px24);
		int bottomOffset = 9; // (int)r.getDimension(R.dimen.px28);

		int left = view.getLeft();
		int top = view.getTop();

		int coverLeft = left - leftOffset;
		int coverTop = top - topOffset;

		border.layout(coverLeft, coverTop, view.getRight() + rightOffset,
				view.getBottom() + bottomOffset);

		params.leftMargin = coverLeft;
		params.topMargin = coverTop;
		params.width = leftOffset + view.getWidth() + rightOffset;
		params.height = topOffset + view.getHeight() + bottomOffset;
		// border.setLayoutParams(params);

	}

	public static void setBorderParams(RelativeLayout.LayoutParams params,
			View view) {

		params.addRule(RelativeLayout.ALIGN_LEFT, view.getId());
		params.addRule(RelativeLayout.ALIGN_TOP, view.getId());

		int leftOffset = 10;
		int topOffset = 9; // (int)r.getDimension(R.dimen.px11);
		int rightOffset = 10; // (int)r.getDimension(R.dimen.px24);
		int bottomOffset = 11; // (int)r.getDimension(R.dimen.px28);

		int coverLeft = 0 - leftOffset;
		int coverTop = 0 - topOffset;// offset;¶î

		params.leftMargin = coverLeft;
		params.topMargin = coverTop;

		params.width = leftOffset + view.getLayoutParams().width + rightOffset;
		params.height = topOffset + view.getLayoutParams().height
				+ bottomOffset;

	}

	public static int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5);
	}
}
