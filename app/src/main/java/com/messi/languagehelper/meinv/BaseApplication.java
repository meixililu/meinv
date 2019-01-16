package com.messi.languagehelper.meinv;

import android.app.Application;
import android.content.Context;
import android.os.Process;

import com.avos.avoscloud.AVOSCloud;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.messi.languagehelper.meinv.box.BoxHelper;
import com.messi.languagehelper.meinv.dao.DaoMaster;
import com.messi.languagehelper.meinv.dao.DaoSession;
import com.messi.languagehelper.meinv.db.LHContract;
import com.messi.languagehelper.meinv.db.SQLiteOpenHelper;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.SDCardUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.umeng.commonsdk.UMConfigure;

public class BaseApplication extends Application {

	private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    public static BaseApplication mInstance;


    @Override  
    public void onCreate() {  
        super.onCreate();
        if(mInstance == null)  mInstance = this;
        initAVOS();
    }

    private void initAVOS(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UMConfigure.setLogEnabled(true);
                BoxHelper.init(BaseApplication.this);
                Setings.appVersion = Setings.getVersion(getApplicationContext());
                Setings.appChannel = Setings.getMetaData(getApplicationContext(),"UMENG_CHANNEL");
                LogUtil.DefalutLog("appVersion:"+Setings.appVersion+"---"+"appChannel:"+Setings.appChannel);
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Fresco.initialize(BaseApplication.this);
                AVOSCloud.initialize(mInstance,"3fg5ql3r45i3apx2is4j9on5q5rf6kapxce51t5bc0ffw2y4", "twhlgs6nvdt7z7sfaw76ujbmaw7l12gb8v6sdyjw1nzk9b1a");
                if(getPackageName().equals(Setings.application_id_meixiu)){
                    SDCardUtil.rootPath = "/meinv/";
                    UMConfigure.init(mInstance,"5b7ed6a08f4a9d303c000060",Setings.appChannel,UMConfigure.DEVICE_TYPE_PHONE,"");
                }else if (getPackageName().equals(Setings.application_id_meinv)) {
                    SDCardUtil.rootPath = "/meinv/";
                    UMConfigure.init(mInstance,"5b7ed6a08f4a9d303c000060",Setings.appChannel,UMConfigure.DEVICE_TYPE_PHONE,"");
                } else if (getPackageName().equals(Setings.application_id_caricature)) {
                    SDCardUtil.rootPath = "/jgmanhua/";
                    UMConfigure.init(mInstance,"5b83ed2aa40fa3221500000e",Setings.appChannel,UMConfigure.DEVICE_TYPE_PHONE,"");
                } else if (getPackageName().equals(Setings.application_id_browser)) {
                    SDCardUtil.rootPath = "/xybrowser/";
                    UMConfigure.init(mInstance,"5b83ed2aa40fa3221500000e",Setings.appChannel,UMConfigure.DEVICE_TYPE_PHONE,"");
                }
            }
        }).run();
    }

	/**
     * 取得DaoMaster
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            SQLiteOpenHelper helper = new SQLiteOpenHelper(context, LHContract.DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

}
