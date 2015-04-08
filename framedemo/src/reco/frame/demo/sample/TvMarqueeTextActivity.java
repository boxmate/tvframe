package reco.frame.demo.sample;

import com.umeng.analytics.MobclickAgent;

import reco.frame.demo.R;
import reco.frame.tv.view.TvMarqueeText;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TvMarqueeTextActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tvmarqueetext);
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
		TvMarqueeText mt_1=(TvMarqueeText) findViewById(R.id.mt_1);
		TvMarqueeText mt_2=(TvMarqueeText) findViewById(R.id.mt_2);
		TvMarqueeText mt_3=(TvMarqueeText) findViewById(R.id.mt_3);
		mt_1.startMarquee();
		mt_2.startMarquee();
		mt_3.startMarquee();
	}


}
