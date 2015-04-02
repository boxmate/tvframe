package reco.frame.demo.sample;

import java.util.Timer;
import java.util.TimerTask;

import reco.frame.demo.R;
import reco.frame.tv.view.TvLoadingBar;
import reco.frame.tv.view.TvProgressBar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class TvLoadingBarActivity extends Activity {

	private final static String TAG="TvLoadingBarActivity";
	private TvLoadingBar tlb_a,tlb_b;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			if (progress % 2==0) {
				tlb_a.setProgress(progress);
			}
			if (progress % 3==0) {
			}
			if(progress % 5==0){
				tlb_b.setProgress(progress);
			}
			

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loadingbar);
		load();

	}

	private void load() {
	 tlb_a = (TvLoadingBar) findViewById(R.id.tlb_a);
	 tlb_b = (TvLoadingBar) findViewById(R.id.tlb_b);
		
	}
	
	@Override
	protected void onResume() {
		show();
		super.onResume();
	}

	private int progress;

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
		
	}

}
