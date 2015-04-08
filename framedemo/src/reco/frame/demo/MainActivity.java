package reco.frame.demo;

import com.umeng.analytics.MobclickAgent;

import reco.frame.demo.sample.TvBitmapActivity;
import reco.frame.demo.sample.TvButtonActivity;
import reco.frame.demo.sample.TvDbActivity;
import reco.frame.demo.sample.TvGridViewActivity;
import reco.frame.demo.sample.TvHttpActivity;
import reco.frame.demo.sample.TvLoadingBarActivity;
import reco.frame.demo.sample.TvMarqueeTextActivity;
import reco.frame.demo.sample.TvProgressBarActivity;
import reco.frame.demo.sample.TvRelativeLayoutActivity;
import reco.frame.demo.sample.TvTabHostActivity;
import reco.frame.demo.templet.TempletA;
import reco.frame.tv.view.TvButton;
import reco.frame.tv.view.TvProgressBar;
import reco.frame.tv.view.TvTabHost;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		load();
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}
	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	private void load(){
		findViewById(R.id.tb_tv_relativeLayout).setOnClickListener(mClickListener);
		findViewById(R.id.tb_tv_progressbar).setOnClickListener(mClickListener);
		findViewById(R.id.tb_tv_marqueetext).setOnClickListener(mClickListener);
		findViewById(R.id.tb_tv_gridview).setOnClickListener(mClickListener);
		findViewById(R.id.tb_tv_button).setOnClickListener(mClickListener);
		findViewById(R.id.tb_tv_bitmap).setOnClickListener(mClickListener);
		findViewById(R.id.tb_tv_db).setOnClickListener(mClickListener);
		findViewById(R.id.tb_tv_http).setOnClickListener(mClickListener);
		findViewById(R.id.tb_tv_tabhost).setOnClickListener(mClickListener);
	}

	
	private OnClickListener mClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tb_tv_relativeLayout:
				
				startActivity(new Intent(MainActivity.this,TvRelativeLayoutActivity.class));
				break;
			case R.id.tb_tv_button:
				
				startActivity(new Intent(MainActivity.this,TvButtonActivity.class));
				break;
			case R.id.tb_tv_progressbar:
				
				startActivity(new Intent(MainActivity.this,TvProgressBarActivity.class));
				break;
			case R.id.tb_tv_marqueetext:
				
				startActivity(new Intent(MainActivity.this,TvMarqueeTextActivity.class));
				break;
			case R.id.tb_tv_gridview:
				startActivity(new Intent(MainActivity.this,TvGridViewActivity.class));
				break;
			case R.id.tb_tv_tabhost:
				startActivity(new Intent(MainActivity.this,TvTabHostActivity.class));
				break;
			case R.id.tb_tv_bitmap:
				startActivity(new Intent(MainActivity.this,TvBitmapActivity.class));
				break;
			case R.id.tb_tv_db:
				startActivity(new Intent(MainActivity.this,TvDbActivity.class));
				break;
			case R.id.tb_tv_http:
				startActivity(new Intent(MainActivity.this,TvHttpActivity.class));
				break;

			}
		
			
		}
	};

}
