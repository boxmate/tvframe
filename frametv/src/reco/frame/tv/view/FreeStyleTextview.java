package reco.frame.tv.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;



/**
 * 此控件暂只支持兰亭黑 尚未完善 请斟酌使用
 * @author keYence
 *
 */
public class FreeStyleTextView extends TextView {
	
	private static Typeface type;

	public FreeStyleTextView(Context context) {
		super(context);
	}

	public FreeStyleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FreeStyleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private synchronized void changeStyle(){
		if (type==null) {
			try {
				type=Typeface.createFromAsset(getContext().getAssets(), "fonts/lth.ttf");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			
		}
		if (type!=null) {
			this.setTypeface(type);
		}
		
	}
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		changeStyle();
	}
}
