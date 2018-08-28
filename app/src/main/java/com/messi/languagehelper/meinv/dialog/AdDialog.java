package com.messi.languagehelper.meinv.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.XFYSAD;

public class AdDialog extends Dialog {

	private PopViewItemOnclickListener listener;
	private Activity context;
	private String[] tempText;
	private FrameLayout xx_ad_layout;
	private FrameLayout action_cancel;
	private FrameLayout action_go;

	public void setListener(PopViewItemOnclickListener listener) {
		this.listener = listener;
	}

	public AdDialog(Activity context, int theme) {
	    super(context, theme);
	    this.context = context;
	}

	public AdDialog(Activity context) {
	    super(context);
	    this.context = context;
	}

	/**
	 * 更改TextView的提示内容
	 * @param context
	 * @param tempText
	 */
	public AdDialog(Activity context, String[] tempText) {
		super(context, R.style.mydialog);
		this.context = context;
		this.tempText = tempText;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_ad);
	    xx_ad_layout = (FrameLayout) findViewById(R.id.xx_ad_layout);
		action_cancel = (FrameLayout) findViewById(R.id.action_cancel);
		action_go = (FrameLayout) findViewById(R.id.action_go);
		action_cancel.setOnClickListener(onClickListener);
		action_go.setOnClickListener(onClickListener);
		XFYSAD mXFYSAD = new XFYSAD(context, xx_ad_layout, ADUtil.MRYJYSNRLAd);
		mXFYSAD.showAD();
	}
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.action_cancel:
				 if(listener != null){
					 listener.onFirstClick(v);
				 }
				 AdDialog.this.dismiss();
				 break;
			case R.id.action_go:
				 if(listener != null){
					 listener.onSecondClick(v);
				 }					
				 AdDialog.this.dismiss();
				 break;
			}
		}
	};
	
	public void setPopViewPosition(){
		WindowManager windowManager = this.getWindow().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (int)(display.getWidth() * 0.9);
		lp.height = (int)(lp.width / 1.77);
		getWindow().setAttributes(lp);
		this.setCanceledOnTouchOutside(true);
	}
	
	public interface PopViewItemOnclickListener{
		public void onFirstClick(View v);
		public void onSecondClick(View v);
	}
}
