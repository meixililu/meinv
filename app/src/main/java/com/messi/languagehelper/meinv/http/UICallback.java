package com.messi.languagehelper.meinv.http;

import android.app.Activity;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public abstract class UICallback implements Callback {
	
	private Activity context;
	private String responseString;
	
	public UICallback(Activity context){
		this.context = context;
	}

	@Override
	public void onFailure(Call call, IOException e) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onFinished();
				onFailured();
			}
		});
	}

	@Override
	public void onResponse(Call call,final Response mResponse) throws IOException {
		if (mResponse != null && mResponse.isSuccessful()){
			responseString = mResponse.body().string();
		}
		if(context != null && !TextUtils.isEmpty(responseString)){
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onFinished();
					onResponsed(responseString);
				}
			});
		}
	}
	
	public abstract void onFailured();
	public abstract void onFinished();
	public abstract void onResponsed(String responseString);


}
