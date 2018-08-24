package com.messi.languagehelper.meinv.util;

import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;

import com.messi.languagehelper.meinv.R;


public class ShareUtil {
	
	public static void shareText(Context mContext, String dstString){
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);    
			intent.setType("text/plain"); // 纯文本     
			intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.share));
			intent.putExtra(Intent.EXTRA_TEXT, dstString);    
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
			mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.share)));
		} catch (Exception e) {
			e.printStackTrace();
		}    
	}

	/**
	 * 复制按钮
	 */
	public static void copy(Context mContext, String dstString){
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(dstString);
		ToastUtil.diaplayMesShort(mContext,mContext.getResources().getString(R.string.copy_success));
	}
}
