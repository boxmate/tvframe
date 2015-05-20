package reco.frame.tv.view.component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class TvUtil {
	private final static String TAG = "TvUtil";

	public final static int SCREEN_1280 = 1280, SCREEN_1920 = 1920,
			SCREEN_2560 = 2560, SCREEN_3840 = 3840;

	public static int startId = 1000001;
	public static int freeId = 1000001;

	public static int buildId() {
		freeId++;
		return freeId;
	}

	private Map<String, SoftReference<Bitmap>> imageCache;
	private String cachedDir;

	public TvUtil(Context context, Map<String, SoftReference<Bitmap>> imageCache) {
		this.imageCache = imageCache;
		this.cachedDir = "";
	}



	/**
	 * MD5 加密
	 */
	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
			// System.out.println("result: " + buf.toString());// 32位的加密
			// System.out.println("result: " + buf.toString().substring(8,
			// 24));// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}
}
