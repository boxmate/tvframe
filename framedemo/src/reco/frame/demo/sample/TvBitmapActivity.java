package reco.frame.demo.sample;


import com.umeng.analytics.MobclickAgent;

import reco.frame.demo.R;
import reco.frame.tv.view.TvButton;
import reco.frame.tv.view.TvImageView;
import reco.frame.tv.view.TvRelativeLayout;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class TvBitmapActivity extends Activity {

	private final String TAG="TvBitmapActivity";
	private TvImageView tiv_imageiv;
	private TvButton tb_image;
	private TvRelativeLayout trl_image;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitmap);
		tiv_imageiv = (TvImageView) findViewById(R.id.tiv_image);
		tb_image = (TvButton) findViewById(R.id.tb_image);
		trl_image = (TvRelativeLayout) findViewById(R.id.trl_image);
		
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
	
	/**
	 * 一行代码完成加载  此URL来源网络 如失效劳烦自行替换
	 * @param v
	 */
	public void loadImage(View v){
		
		tiv_imageiv.configImageUrl("http://pic.baike.soso.com/p/20131121/20131121173117-340686565.jpg");
		tb_image.configImageUrl("http://att.gamefy.cn/files/201503/142570966171539.jpg");
		trl_image.configImageUrl("http://img5.imgtn.bdimg.com/it/u=272745822,2496803195&fm=21&gp=0.jpg");
		
	}

}
