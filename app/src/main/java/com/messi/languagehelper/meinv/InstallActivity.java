package com.messi.languagehelper.meinv;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.messi.languagehelper.meinv.util.AppUpdateUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.ToastUtil;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class InstallActivity extends Activity {

	public static final String Action_Install = "install";
	public static final String Action_Install_local = "install_local";

	public String apkPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent() != null){
			String action = getIntent().getStringExtra(KeyUtil.Type);
			if(!TextUtils.isEmpty(action)){
				if(action.equals(Action_Install)){
					AppUpdateUtil.checkUpdate(this);
					finish();
				}else if(action.equals(Action_Install_local)){
					apkPath = getIntent().getStringExtra(KeyUtil.ApkPath);
					installApk(apkPath);
				}
			}else {
				finish();
			}
		}else {
			finish();
		}
	}

	@NeedsPermission(android.Manifest.permission.REQUEST_INSTALL_PACKAGES)
	public void installApk(String filePath){
		startActivity(getInstallApkIntent(filePath));
		finish();
	}

	public Intent getInstallApkIntent(String filePath){
		LogUtil.DefalutLog("getInstallApkIntent---filePath:"+filePath);
		Intent i = new Intent(Intent.ACTION_VIEW);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri imageUri = FileProvider.getUriForFile(this, Setings.getProvider(this), new File(filePath));
			i.setDataAndType(imageUri, "application/vnd.android.package-archive");
		}else {
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
		}
		return i;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		InstallActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
	}

	@OnShowRationale(android.Manifest.permission.REQUEST_INSTALL_PACKAGES)
	void onShowRationale(final PermissionRequest request) {
		new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
				.setTitle("温馨提示")
				.setMessage("需要授权才能使用。")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						request.proceed();
					}
				}).show();
	}

	@OnPermissionDenied(Manifest.permission.REQUEST_INSTALL_PACKAGES)
	void onPerDenied() {
		ToastUtil.diaplayMesShort(this,"没有授权，部分功能将无法使用！");
	}
}
