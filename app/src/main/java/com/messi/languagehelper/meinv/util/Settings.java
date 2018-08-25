package com.messi.languagehelper.meinv.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.view.KeyEvent;

import com.messi.languagehelper.meinv.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Settings {

	private static final int RequestCode = 1;

	public static String sougouApi = "http://pic.sogou.com/pics/channel/getAllRecomPicByTag.jsp?category=美女&len=9&tag=";
	// uc search
	public static final String UCSearch = "https://yz.m.sm.cn/s?q=%E7%A5%9E%E9%A9%AC%E6%96%B0%E9%97%BB%E6%A6%9C%E5%8D%95&from=wm845578";

	public static final String Email = "mzxbkj@163.com";

	public static final int page_size = 10;

	public static final String application_id_meixiu = "com.messi.languagehelper.meixiu";
	public static final String application_id_meinv = "com.messi.languagehelper.meinv";
	public static final String application_id_caricature = "com.messi.languagehelper.google";
	public static final String application_id_browser = "com.messi.learnenglish";


	public static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	public static String[] PERMISSIONS_RECORD_AUDIO = {
			Manifest.permission.RECORD_AUDIO
	};
    public static HashMap<String, Object> dataMap = new HashMap<String, Object>();


	
	/**time interval
	 * @param mSharedPreferences
	 * @param interval
	 * @return
	 */
	public static boolean isEnoughTime(SharedPreferences mSharedPreferences, long interval){
		long now = System.currentTimeMillis();
		long lastTime = mSharedPreferences.getLong(KeyUtil.IsEnoughIntervalTime, 0);
		saveSharedPreferences(mSharedPreferences,KeyUtil.IsEnoughIntervalTime,now);
		if(now - lastTime > interval){
			return true;
		}else{
			return false;
		}
	}
	
	/**获取配置文件类
	 * @param context
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
	}
	
	/**
	 * 保存配置信息
	 *
	 * @param value
	 * @param key
	 */
	public static void saveSharedPreferences(SharedPreferences sharedPrefs, String key, String value) {
		LogUtil.DefalutLog("key-value:" + key + "-" + value);
		Editor editor = sharedPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * 保存配置信息
	 * 
	 * @param value
	 * @param key
	 */
	public static void saveSharedPreferences(SharedPreferences sharedPrefs, String key, boolean value) {
		LogUtil.DefalutLog("key-value:" + key + "-" + value);
		Editor editor = sharedPrefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}


	/**
	 * 保存配置信息
	 * 
	 * @param value
	 * @param key
	 */
	public static void saveSharedPreferences(SharedPreferences sharedPrefs, String key, long value) {
		LogUtil.DefalutLog("key-value:" + key + "-" + value);
		Editor editor = sharedPrefs.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	/**
	 * 保存配置信息
	 * 
	 * @param value
	 * @param key
	 */
	public static void saveSharedPreferences(SharedPreferences sharedPrefs, String key, int value) {
		LogUtil.DefalutLog("key-value:" + key + "-" + value);
		Editor editor = sharedPrefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void contantUs(Context mContext){
		try {
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("message/rfc822");
			emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { Email });
			mContext.startActivity(emailIntent);
		} catch (Exception e) {
			copy(mContext,Email);
			e.printStackTrace();
		}
	}
	
	/**
	 * 复制按钮
	 */
	public static void copy(Context mContext,String dstString){
		try {
			// 得到剪贴板管理器
			ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(dstString);
			ToastUtil.diaplayMesShort(mContext, mContext.getResources().getString(R.string.copy_success));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getVersion(Context mContext) {
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public static String getIpAddress(Context mContext){
		//获取wifi服务  
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);  
        //判断wifi是否开启  
//        if (!wifiManager.isWifiEnabled()) {
//        	wifiManager.setWifiEnabled(true);
//        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
        int ipAddress = wifiInfo.getIpAddress();   
        return intToIp(ipAddress);   
	}
	
	public static String intToIp(int i) {       
      return (i & 0xFF ) + "." +       
      ((i >> 8 ) & 0xFF) + "." +       
      ((i >> 16 ) & 0xFF) + "." +       
      ( i >> 24 & 0xFF) ;  
   }  
	


	public static void verifyStoragePermissions(final Activity activity,final String[] permissions) {
		try{
			AndPermission.with(activity)
					.runtime()
					.permission(permissions)
					.onGranted(new Action<List<String>>() {
						@Override
						public void onAction(List<String> data) {

						}
					})
					.onDenied(new Action<List<String>>() {
						@Override
						public void onAction(List<String> data) {
							LogUtil.DefalutLog("onDenied");
							AlertDialog.Builder builder = new AlertDialog.Builder(activity);
							builder.setTitle("温馨提示");
							builder.setMessage("软件需要一些权限才能正常运行。");
							builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ActivityCompat.requestPermissions(
											activity,
											permissions,
											RequestCode
									);
								}
							});
							AlertDialog dialog = builder.create();
							dialog.show();
						}
					})
					.start();

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void adjustStreamVolume(Context mContext, int action){
		AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		if(action == KeyEvent.KEYCODE_VOLUME_UP){
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		}else{
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
		}
	}

	public static String getUUID(Context context){
		String uniqueId = "1234567890";
		try {
			int permissionCheck = ContextCompat.checkSelfPermission(context,
					Manifest.permission.READ_PHONE_STATE);
			if(permissionCheck ==  PackageManager.PERMISSION_GRANTED){
				final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				final String tmDevice, tmSerial, tmPhone, androidId;
				tmDevice = "" + tm.getDeviceId();
				tmSerial = "" + tm.getSimSerialNumber();
				androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
				UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
				uniqueId = deviceUuid.toString();
			}else {
				AndPermission.with(context)
						.runtime()
						.permission(Permission.Group.PHONE)
						.onGranted(new Action<List<String>>() {
							@Override
							public void onAction(List<String> data) {

							}
						})
						.onDenied(new Action<List<String>>() {
							@Override
							public void onAction(List<String> data) {

							}
						})
						.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtil.DefalutLog("uuid:"+uniqueId);
		return uniqueId;
	}

	public static String getProvider(Context appContext) {
		ApplicationInfo applicationInfo = null;
		try {
			applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
			if(applicationInfo == null){
				return appContext.getPackageName() + ".provider";
			}
			return applicationInfo.metaData.getString("ProviderId");
		} catch (Exception e) {
			e.printStackTrace();
			return appContext.getPackageName() + ".provider";
		}
	}

	public static String getMetaData(Context appContext,String name) {
		ApplicationInfo applicationInfo = null;
		try {
			applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
			if(applicationInfo == null){
				return "";
			}
			return applicationInfo.metaData.getString(name);
		} catch (Exception e) {
			e.printStackTrace();
			return appContext.getPackageName() + ".provider";
		}
	}

	public static String getVersionName(Context mContext) {
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "x.x";
		}
	}
}
