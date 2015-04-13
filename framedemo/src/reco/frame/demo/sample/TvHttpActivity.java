package reco.frame.demo.sample;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import reco.frame.demo.R;
import reco.frame.demo.entity.Config;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.HttpHandler;
import reco.frame.tv.view.TvButton;
import reco.frame.tv.view.TvMarqueeText;
import reco.frame.tv.view.TvProgressBar;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class TvHttpActivity extends Activity {

	private TvButton tb_get;
	private TvButton tb_post;
	private TvButton tb_download;
	private TvMarqueeText tmv_get;
	private TextView tv_post;
	private TvProgressBar tpb_progress;
	private TvHttp tvHttp;
	private HttpHandler<File> handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tvhttp);
		init();
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
	
	
	private void init() {

		tvHttp = new TvHttp(getApplicationContext());

		tb_get = (TvButton) findViewById(R.id.tb_get);
		tb_post = (TvButton) findViewById(R.id.tb_post);
		tb_download = (TvButton) findViewById(R.id.tb_download);
		tmv_get = (TvMarqueeText) findViewById(R.id.tv_get);
		tv_post = (TextView) findViewById(R.id.tv_post);
		tpb_progress = (TvProgressBar) findViewById(R.id.tpb_progress);

		tb_get.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				testGet();
			}
		});

		tb_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				testPost();
			}
		});

		tb_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (handler == null) {
					testDownload();
				} else {
					Log.e("t=", handler.isStop() + "");
					if (!handler.isStop()) {
						handler.stop();
					} else {
						testDownload();
					}
				}

			}
		});

	}

	/**
	 * get简单示例 *此处获得文件地址 用于后面的 下载示例
	 */
	private void testGet() {
		tvHttp.get(Config.TEST_GET_API, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {

				// 获取文件下载路径
				try {
					JSONObject jsonObject = new JSONObject(t.toString());
					Config.TEST_FILE_URL = jsonObject.getString("apk_url") + "";
					
					tmv_get.setText(Config.TEST_FILE_URL);
					tmv_get.startMarquee();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				Toast.makeText(getApplicationContext(), "请求失败!!!", 1).show();
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}

	/**
	 * post简单示例
	 */
	private void testPost() {

		tvHttp.post(Config.TEST_POST_API, new AjaxCallBack<Object>() {

			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				tv_post.setText(t.toString());
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				Toast.makeText(getApplicationContext(), "请求失败!!!", 1).show();
				super.onFailure(t, errorNo, strMsg);
			}
		});

	}

	/**
	 * 文件断点续示例 *先通过GET方法得到 文件下载地址
	 */
	private void testDownload() {

		// 指定文件保存目录  考虑到部份盒子 没有SD卡 建议保存至getFilesDir()
		String savePath = getFilesDir()+ "/" + "boxmate.apk";
		handler = tvHttp.download(Config.TEST_FILE_URL, savePath, true,
				new AjaxCallBack<File>() {

					@Override
					public void onLoading(long count, long current) {
						int progress = (int) (current * 100 / count);
						tpb_progress.setProgress(progress);
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(File t) {
						Toast.makeText(getApplicationContext(), "下载成功!!!", 1)
								.show();
						super.onSuccess(t);
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						Toast.makeText(getApplicationContext(), strMsg, 1)
								.show();
						super.onFailure(t, errorNo, strMsg);
					}
				});

	}

	public static String getSDPath() {
		File sdDir = null;
		try {
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
			// 判断sd卡是否存在
			if (sdCardExist) {
				sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
				return sdDir.toString();
			} else {
				return "/data/data/";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/data/";

	}

}
