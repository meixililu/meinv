package com.messi.languagehelper.meinv.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.box.BoxHelper;
import com.messi.languagehelper.meinv.box.WebFilter;

import java.util.List;

public class AppUpdateUtil {

    public static void runCheckUpdateTask(Activity mActivity) {
        checkUpdate(mActivity);
        initXMLY(mActivity);
        getWebFilter();
//        PushManager.getInstance().initialize(mActivity.getApplicationContext(),null);
    }

    public static void initXMLY(Activity mActivity){
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        SystemUtil.SCREEN_WIDTH = dm.widthPixels;
        SystemUtil.SCREEN_HEIGHT = dm.heightPixels;
        SystemUtil.screen = SystemUtil.SCREEN_WIDTH + "x" + SystemUtil.SCREEN_HEIGHT;
    }

    public static void getWebFilter(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.AdFilter.AdFilter);
                query.whereContains(AVOUtil.AdFilter.category, "ca_novel");
                List<AVObject> list = null;
                try {
                    list = query.find();
                    if(list != null){
                        List<WebFilter> beans = DataUtil.toWebFilter(list);
                        BoxHelper.updateWebFilter(beans);
                    }
                } catch (AVException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void checkUpdate(final Activity mActivity) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.UpdateInfo.UpdateInfo);
        if(mActivity.getPackageName().equals(Setings.application_id_meixiu)){
            query.whereEqualTo(AVOUtil.UpdateInfo.AppCode, "meixiu");
        }else if(mActivity.getPackageName().equals(Setings.application_id_meinv)){
            query.whereEqualTo(AVOUtil.UpdateInfo.AppCode, "meinv");
        }else if(mActivity.getPackageName().equals(Setings.application_id_caricature)){
            query.whereEqualTo(AVOUtil.UpdateInfo.AppCode, "caricature");
        }else if(mActivity.getPackageName().equals(Setings.application_id_caricature_ecy)){
            query.whereEqualTo(AVOUtil.UpdateInfo.AppCode, "caricature_ecy");
        }else{
            query.whereEqualTo(AVOUtil.UpdateInfo.AppCode, "noupdate");
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (avObjects != null && avObjects.size() > 0) {
                    final AVObject mAVObject = avObjects.get(0);
                    saveSetting(mActivity,mAVObject);
                }
            }
        });
    }

    public static void saveSetting(Activity mActivity,AVObject mAVObject){
        SharedPreferences sp = Setings.getSharedPreferences(mActivity);
        LogUtil.DefalutLog(mAVObject.getString(AVOUtil.UpdateInfo.AppName));
        String app_advertiser = mAVObject.getString(AVOUtil.UpdateInfo.ad_type);
        String wyyx_url = mAVObject.getString(AVOUtil.UpdateInfo.wyyx_url);
        String uctt_url = mAVObject.getString(AVOUtil.UpdateInfo.uctt_url);
        String ucsearch_url = mAVObject.getString(AVOUtil.UpdateInfo.ucsearch_url);
        String ad_ids = mAVObject.getString(AVOUtil.UpdateInfo.ad_ids);
        String ad_csj = mAVObject.getString(AVOUtil.UpdateInfo.ad_csj);
        String ad_bd = mAVObject.getString(AVOUtil.UpdateInfo.ad_bd);
        String no_ad_channel = mAVObject.getString(AVOUtil.UpdateInfo.no_ad_channel);
        String adConf = mAVObject.getString(AVOUtil.UpdateInfo.adConf);
        String Caricature_channel = mAVObject.getString(AVOUtil.UpdateInfo.Caricature_channel);
        int Caricature_version = mAVObject.getInt(AVOUtil.UpdateInfo.Caricature_version);
        ADUtil.setAdConfig(adConf);
        TXADUtil.setADData(ad_ids);
        CSJADUtil.setADData(ad_csj);
        BDADUtil.setADData(ad_bd);
        Setings.saveSharedPreferences(sp,KeyUtil.APP_Advertiser,app_advertiser);
        Setings.saveSharedPreferences(sp,KeyUtil.Lei_DVideo,uctt_url);
        Setings.saveSharedPreferences(sp,KeyUtil.Lei_Novel,wyyx_url);
        Setings.saveSharedPreferences(sp,KeyUtil.Lei_UCSearch,ucsearch_url);
        Setings.saveSharedPreferences(sp,KeyUtil.Ad_Ids,ad_ids);
        Setings.saveSharedPreferences(sp,KeyUtil.Ad_Csj,ad_csj);
        Setings.saveSharedPreferences(sp,KeyUtil.Ad_Bd,ad_bd);
        Setings.saveSharedPreferences(sp,KeyUtil.No_Ad_Channel,no_ad_channel);
        Setings.saveSharedPreferences(sp,KeyUtil.VersionCode,
                mAVObject.getInt(AVOUtil.UpdateInfo.VersionCode));
        Setings.saveSharedPreferences(sp,KeyUtil.Caricature_version, Caricature_version);
        Setings.saveSharedPreferences(sp,KeyUtil.Caricature_channel, Caricature_channel);
        Setings.saveSharedPreferences(sp,KeyUtil.UpdateBean, mAVObject.toString());
        LogUtil.DefalutLog("saveSetting");
    }

    public static void isNeedUpdate(final Activity mActivity){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showUpdateDialog(mActivity);
            }
        }, 4000);
    }

    public static void showUpdateDialog(final Activity mActivity) {
        SharedPreferences sp = Setings.getSharedPreferences(mActivity);
        try {
            final AVObject mAVObject = AVObject.parseAVObject(sp.getString(KeyUtil.UpdateBean,""));
            if(mAVObject != null){
                String isValid = mAVObject.getString(AVOUtil.UpdateInfo.IsValid);
                if(!TextUtils.isEmpty(isValid) && isValid.equals("3")){
                    int newVersionCode = mAVObject.getInt(AVOUtil.UpdateInfo.VersionCode);
                    int oldVersionCode = Setings.getVersion(mActivity);
                    if (newVersionCode > oldVersionCode) {
                        String updateInfo = mAVObject.getString(AVOUtil.UpdateInfo.AppUpdateInfo);
                        String downloadType = mAVObject.getString(AVOUtil.UpdateInfo.DownloadType);
                        String apkUrl = "";
                        if (downloadType.equals("apk")) {
                            AVFile avFile = mAVObject.getAVFile(AVOUtil.UpdateInfo.Apk);
                            apkUrl = avFile.getUrl();
                        } else {
                            apkUrl = mAVObject.getString(AVOUtil.UpdateInfo.APPUrl);
                        }
                        final String downloadUrl = apkUrl;
                        LogUtil.DefalutLog("apkUrl:" + apkUrl);

                        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_update_info,null);
                        TextView updage_info = (TextView) view.findViewById(R.id.updage_info);
                        ImageView cancel_btn = (ImageView) view.findViewById(R.id.cancel_btn);
                        TextView update_btn = (TextView) view.findViewById(R.id.update_btn);
                        final AlertDialog dialog = new AlertDialog.Builder(mActivity).create();
                        dialog.setView(view);
                        dialog.setCancelable(false);
                        updage_info.setText(updateInfo);
                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        update_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                new AppDownloadUtil(mActivity,
                                        downloadUrl,
                                        mAVObject.getString(AVOUtil.UpdateInfo.AppName),
                                        mAVObject.getObjectId(),
                                        SDCardUtil.apkUpdatePath
                                ).DownloadFile();
                            }
                        });
                        dialog.show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
