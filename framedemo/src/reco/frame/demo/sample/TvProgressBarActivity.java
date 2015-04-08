package reco.frame.demo.sample;

import java.util.Timer;
import java.util.TimerTask;

import com.umeng.analytics.MobclickAgent;

import reco.frame.demo.R;
import reco.frame.tv.view.TvProgressBar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class TvProgressBarActivity extends Activity {

	private final static String TAG="TvProgressBarActivity";
	private TvProgressBar tpb_ring_a,tpb_ring_b,tpb_fan_a,tpb_fan_b,tpb_rect_a,tpb_rect_b;
	private final static int RING_A = 0, RING_B = 1, FAN = 2;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			if (progress % 2==0) {
				tpb_ring_a.setProgress(progress);
			}
			if (progress % 3==0) {
				tpb_fan_a.setProgress(progress);
				tpb_rect_b.setProgress(progress);
			}
			if(progress % 5==0){
				tpb_ring_b.setProgress(progress2);
			}
			tpb_rect_a.setProgress(progress);
			tpb_fan_b.setProgress(progress);
			

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progressbar);
		load();

	}

	
	@Override
	protected void onResume() {
		show();
		MobclickAgent.onResume(this);
		super.onResume();
	}
	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	private void load() {
		tpb_ring_a = (TvProgressBar) findViewById(R.id.tpb_ring_a);
		tpb_ring_b = (TvProgressBar) findViewById(R.id.tpb_ring_b);
		tpb_fan_a = (TvProgressBar) findViewById(R.id.tpb_fan_a);
		tpb_fan_b = (TvProgressBar) findViewById(R.id.tpb_fan_b);
		tpb_rect_a = (TvProgressBar) findViewById(R.id.tpb_rect_a);
		tpb_rect_b = (TvProgressBar) findViewById(R.id.tpb_rect_b);
	}
	

	private int progress,progress2;

	private void show() {

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {

				progress++;
				if (progress == 100) {
					progress = 0;
				}
				Message msg = handler.obtainMessage();
				msg.sendToTarget();

			}
		}, 0, 100);
		
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {

				progress2++;
				if (progress2 > 1000) {
					progress2 = 0;
				}
				Message msg = handler.obtainMessage();
				//msg.sendToTarget();

			}
		}, 0, 30);
	}

}
