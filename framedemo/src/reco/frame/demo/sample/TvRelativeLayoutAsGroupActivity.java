package reco.frame.demo.sample;

import com.umeng.analytics.MobclickAgent;

import reco.frame.demo.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TvRelativeLayoutAsGroupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relativelayoutasgroup);
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
}
