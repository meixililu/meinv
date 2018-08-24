package com.messi.languagehelper.meinv.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.avos.avoscloud.AVObject;
import com.messi.languagehelper.meinv.JokeActivity;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.http.LanguagehelperHttpClient;
import com.messi.languagehelper.meinv.impl.ProgressListener;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

public class AppDownloadUtil {

	private Context mContext;
	private int record = -2;
	private String url;
	private String ContentTitle;
	private String Ticker;
	private String appFileName;
	private String appLocalFullName;
	private String AVObjectId;
	private String path;
	private NotificationManager mNotifyManager;
	private Builder mBuilder;
	
	public AppDownloadUtil(Context mContext, String url, String appName, String AVObjectId, String path){
		this.mContext = mContext;
		this.url = url;
		this.ContentTitle = appName + "下载通知";
		this.Ticker = appName + "开始下载";
		this.appFileName = getFileName(url);
		this.AVObjectId = AVObjectId;
		this.path = path;
		this.appLocalFullName = getLocalFile(appFileName);
	}
	
	public void DownloadFile(){
		if(isFileExist()){
			installApk(mContext,appLocalFullName);
		}else if(!TextUtils.isEmpty(url)){
			new Thread(new Runnable() {
				@Override
				public void run() {
					mNotifyManager  = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
					mBuilder = new Builder(mContext);
					mBuilder.setContentTitle(ContentTitle).setContentText("开始下载").setSmallIcon(R.drawable.ic_get_app_white_36dp).setTicker(Ticker).setAutoCancel(true);
					Intent intent = new Intent (mContext, JokeActivity.class);
					intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent pend = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent (pend);
					mNotifyManager.notify(0, mBuilder.build());
					try {
						Response response = LanguagehelperHttpClient.get(url,progressListener);
						if(response.isSuccessful()){
							LogUtil.DefalutLog("---DownloadFile success");
							DownLoadUtil.saveFile(mContext,path,appFileName,response.body().bytes());
							PendingIntent pendUp = PendingIntent.getActivity(mContext, 0, getInstallApkIntent(mContext,appLocalFullName),
									PendingIntent.FLAG_UPDATE_CURRENT);
							mBuilder.setContentIntent (pendUp);
				            mNotifyManager.notify(0, mBuilder.build());
				            installApk(mContext,appLocalFullName);
				            updateDownloadTime();
						}else{
							LogUtil.DefalutLog("---DownloadFile onFailure");
							mBuilder.setContentText("下载失败,请稍后重试").setProgress(0,0,false);
							mNotifyManager.notify(0, mBuilder.build());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	final ProgressListener progressListener = new ProgressListener() {
		@Override
		public void update(long bytesRead, long contentLength, boolean done) {
			try {
				int percent = (int) ((100 * bytesRead) / contentLength);
				if(percent != 100 && record != percent){
					record = percent;
					mBuilder.setProgress(100, percent, false);
					mBuilder.setContentText("更新进度"+percent+"%");
					mNotifyManager.notify(0, mBuilder.build());
				}else if(percent == 100){
					if(done){
						mBuilder.setContentText("下载完成").setProgress(0,0,false);
						mNotifyManager.notify(0, mBuilder.build());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private void updateDownloadTime(){
		if(path.equals(SDCardUtil.apkPath)){
			AVObject post = AVObject.createWithoutData(AVOUtil.AppRecommendDetail.AppRecommendDetail, AVObjectId);
			post.increment(AVOUtil.AppRecommendDetail.DownloadTimes);
			post.saveInBackground();
		}else if(path.equals(SDCardUtil.apkUpdatePath)){
			AVObject post = AVObject.createWithoutData(AVOUtil.UpdateInfo.UpdateInfo, AVObjectId);
			post.increment(AVOUtil.UpdateInfo.DownloadTimes);
			post.saveInBackground();
		}
	} 
	
	/**安装apk**/
	public void installApk(Context mContext,String filePath){
		mContext.startActivity(getInstallApkIntent(mContext,filePath));
	}
	
	public Intent getInstallApkIntent(Context mContext,String filePath){
		LogUtil.DefalutLog("getInstallApkIntent---filePath:"+filePath);
		Intent i = new Intent(Intent.ACTION_VIEW);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri imageUri = FileProvider.getUriForFile(mContext, Settings.getProvider(mContext), new File(filePath));
			i.setDataAndType(imageUri, "application/vnd.android.package-archive");
		}else {
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
		}
		return i;
	}
	
	public String getFileName(String url){
		String name = url.substring(url.lastIndexOf("/")+1);
		LogUtil.DefalutLog("FileName:"+name);
		return name;
	}
	
	public String getLocalFile(String fileName){
		String tPath = SDCardUtil.getDownloadPath(path);
		return tPath + fileName;
	}
	
	public boolean isFileExist(){
		String filePath = getLocalFile(appFileName);
		File file = new File(filePath);
		return file.exists();
	}
	
}
