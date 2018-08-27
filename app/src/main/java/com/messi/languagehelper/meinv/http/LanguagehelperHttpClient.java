package com.messi.languagehelper.meinv.http;

import android.content.Context;
import android.text.TextUtils;

import com.messi.languagehelper.meinv.impl.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LanguagehelperHttpClient {
	
	public static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
	private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
	public static final String Header = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
	public static OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(15, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(15, TimeUnit.SECONDS)
			.build();
	
	public static OkHttpClient initClient(Context mContext){
		File baseDir = mContext.getCacheDir();
		File cacheDir = new File(baseDir,"HttpResponseCache");
		if(cacheDir != null){
			client = new OkHttpClient.Builder()
					.connectTimeout(15, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS)
					.writeTimeout(15, TimeUnit.SECONDS)
					.cache(new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE))
					.build();
		}
		return client;
	}

	public static Response get(String url) {
		Response mResponse = null;
		try {
			Request request = new Request.Builder()
					.url(url)
					.build();
			mResponse = client.newCall(request).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResponse;
	}
	
	public static void get(String url, Callback mCallback) {
		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", Header)
			.build();
		client.newCall(request).enqueue(mCallback);
	}

	public static Response get(String url,ProgressListener progressListener) {
		Response mResponse = null;
		try {
			Request request = new Request.Builder()
					.url(url)
					.build();
			OkHttpClient clone = LanguagehelperHttpClient.addProgressResponseListener(progressListener);
			mResponse = clone.newCall(request).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResponse;
	}

	public static void get(HttpUrl mHttpUrl, String apikey, Callback mCallback) {
		if (TextUtils.isEmpty(apikey)) {
			Request request = new Request.Builder()
					.url(mHttpUrl)
					.header("User-Agent", Header)
					.build();
			client.newCall(request).enqueue(mCallback);
		}else {
			Request request = new Request.Builder()
					.url(mHttpUrl)
					.addHeader("apikey",apikey)
					.header("User-Agent", Header)
					.build();
			client.newCall(request).enqueue(mCallback);
		}
	}

	public static Response post(String url, RequestBody params, Callback mCallback) {
		Request request = new Request.Builder()
			.url(url)
			.post(params)
			.build();
		return executePost(request,mCallback);
	}

	public static Response executePost(Request request,Callback mCallback){
		Response mResponse = null;
		if(mCallback != null){
			client.newCall(request).enqueue(mCallback);
		}else {
			try {
				mResponse = client.newCall(request).execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mResponse;
	}


	public static OkHttpClient addProgressResponseListener(final ProgressListener progressListener){
		// 增加拦截器
		OkHttpClient clone = new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				// 拦截
				Response originalResponse = chain.proceed(chain.request());
				// 包装响应体并返回
				return originalResponse
						.newBuilder()
						.body(new ProgressResponseBody(originalResponse.body(), progressListener))
						.build();
			}
		}).build();
        return clone;
    }

}
