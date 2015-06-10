package reco.frame.demo.adapter;

import java.util.List;

import reco.frame.demo.R;
import reco.frame.demo.entity.AppInfo;
import reco.frame.tv.view.TvImageView;
import reco.frame.tv.view.component.TvBaseAdapter;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TvListAdapter extends TvBaseAdapter {

	
	private List<AppInfo> appList;
	private LayoutInflater inflater;
	
	public TvListAdapter(Context context,List<AppInfo> appList){
		this.inflater=LayoutInflater.from(context);
		this.appList=appList;
	}
	
	@Override
	public int getCount() {
		return appList.size();
	}

	@Override
	public Object getItem(int position) {
		return appList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View contentView, ViewGroup parent) {
		ViewHolder holder=null;
		if (contentView==null) {
			contentView=inflater.inflate(R.layout.item_list, null);
			holder=new ViewHolder();
			holder.tv_title=(TextView) contentView.findViewById(R.id.tv_title);
			holder.iv_icon=(ImageView) contentView.findViewById(R.id.iv_icon);
			holder.bt_launcher=(Button) contentView.findViewById(R.id.bt_launcher);
			holder.bt_uninstall=(Button) contentView.findViewById(R.id.bt_uninstall);
			contentView.setTag(holder);
		}else{
			holder=(ViewHolder) contentView.getTag();
		}
		
		AppInfo app=appList.get(position);
		holder.tv_title.setText(app.getTitle());
		holder.iv_icon.setBackgroundResource(R.drawable.icon_reco);
		

		
		return contentView;
	}
	
	public void addItem(AppInfo item) {
		appList.add(item);
	}

	public void clear() {
		appList.clear();
	}

	public void flush(List<AppInfo> appListNew) {
		appList = appListNew;
	}

	
	static class ViewHolder{
		TextView tv_title;
		ImageView iv_icon;
		Button bt_launcher;
		Button bt_uninstall;
	}
}
