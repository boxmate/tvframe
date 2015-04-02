package reco.frame.demo.templet;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import reco.frame.demo.R;
import reco.frame.demo.adapter.DbListAdapter;
import reco.frame.demo.adapter.PageAdapter;
import reco.frame.demo.entity.AppInfo;
import reco.frame.demo.entity.Config;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.GridView;
import android.app.Activity;

public class TempletA extends Activity {


	private String TAG="TvBitmapActivity";
	private ViewPager vp_applist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_templet_a);
		vp_applist = (ViewPager) findViewById(R.id.vp_applist);
		load();
	}
	
	
	
	private void load(){
		
		TvHttp http=new TvHttp(this);
		http.get(Config.TEST_DATA_API, new AjaxCallBack<Object>() {
			
			@Override
			public void onSuccess(Object t) {
				
				try {
					List<AppInfo> appList=new ArrayList<AppInfo>();
					JSONArray jsonArray=new JSONObject(t.toString()).getJSONArray("data_list");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jObject=jsonArray.getJSONObject(i);
						AppInfo app=new AppInfo();
						app.title=jObject.getString("title");
						app.imageUrl=jObject.getString("icon_little_url");
						appList.add(app);
					}
					vp_applist.setAdapter(new PageAdapter(getApplicationContext(),vp_applist, appList));
					
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}
		});
		
	}

	

}
