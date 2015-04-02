package reco.frame.demo.adapter;

import java.util.List;


import reco.frame.demo.R;
import reco.frame.demo.entity.AppInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DbListAdapter extends BaseAdapter {

	
	private List<AppInfo> appList;
	private LayoutInflater inflater;
	
	public DbListAdapter(Context context,List<AppInfo> appList){
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
			contentView=inflater.inflate(R.layout.item_db, null);
			holder=new ViewHolder();
			holder.tv_title=(TextView) contentView.findViewById(R.id.tv_title);
			holder.tv_imageUrl=(TextView) contentView.findViewById(R.id.tv_imageUrl);
			contentView.setTag(holder);
		}else{
			holder=(ViewHolder) contentView.getTag();
		}
		AppInfo app=appList.get(position);
		
		holder.tv_title.setText("id="+app.id+"--num="+app.num+"--"+app.title+"");
		holder.tv_imageUrl.setText(app.imageUrl);
		
		return contentView;
	}

	
	static class ViewHolder{
		TextView tv_title;
		TextView tv_imageUrl;
	}
}
