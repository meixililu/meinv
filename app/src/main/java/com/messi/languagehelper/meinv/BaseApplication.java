package com.messi.languagehelper.meinv;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.messi.languagehelper.meinv.box.BoxHelper;
import com.messi.languagehelper.meinv.dao.DaoMaster;
import com.messi.languagehelper.meinv.dao.DaoSession;
import com.messi.languagehelper.meinv.db.LHContract;
import com.messi.languagehelper.meinv.db.SQLiteOpenHelper;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.BDADUtil;
import com.messi.languagehelper.meinv.util.CSJADUtil;
import com.messi.languagehelper.meinv.util.SDCardUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.SystemUtil;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;

public class BaseApplication extends Application {

    public static HashMap<String, Object> dataMap = new HashMap<String, Object>();
	private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    public static BaseApplication mInstance;


    @Override  
    public void onCreate() {  
        super.onCreate();
        initAVOS();
    }

    private void initAVOS(){
        if(mInstance == null)  mInstance = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SystemUtil.setPacketName(BaseApplication.this);
                    Fresco.initialize(BaseApplication.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    AVOSCloud.initialize(BaseApplication.this,"3fg5ql3r45i3apx2is4j9on5q5rf6kapxce51t5bc0ffw2y4", "twhlgs6nvdt7z7sfaw76ujbmaw7l12gb8v6sdyjw1nzk9b1a");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initLearnCloudChannel();
                TXADUtil.init(BaseApplication.this);
                CSJADUtil.init(BaseApplication.this);
                BDADUtil.init(BaseApplication.this);
                BoxHelper.init(BaseApplication.this);
                ADUtil.initTXADID(BaseApplication.this);
            }
        }).run();
    }

    private void initLearnCloudChannel(){
        try {
//          UMConfigure.setLogEnabled(true);
            Setings.appVersion = Setings.getVersion(getApplicationContext());
            Setings.appChannel = Setings.getMetaData(getApplicationContext(),"UMENG_CHANNEL");
            setAPPData();
            UMConfigure.init(mInstance,Setings.UmengAPPId,Setings.appChannel,UMConfigure.DEVICE_TYPE_PHONE,"");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setAPPData(){
        if(getPackageName().equals(Setings.application_id_caricature)){
            SDCardUtil.rootPath = "/jgmanhua/";
            Setings.UmengAPPId = "5b83ed2aa40fa3221500000e";
        }else if(getPackageName().equals(Setings.application_id_caricature_ecy)){
            Setings.UmengAPPId = "5b83ed2aa40fa3221500000e";
        }else if(getPackageName().equals(Setings.application_id_meixiu)){
            SDCardUtil.rootPath = "/meinv/";
            Setings.UmengAPPId = "5b7ed6a08f4a9d303c000060";
        }else if(getPackageName().equals(Setings.application_id_meinv)){
            SDCardUtil.rootPath = "/meinv/";
            Setings.UmengAPPId = "5b7ed6a08f4a9d303c000060";
        }else if(getPackageName().equals(Setings.application_id_browser)){
            SDCardUtil.rootPath = "/xybrowser/";
            Setings.UmengAPPId = "5b83ed2aa40fa3221500000e";
        }
    }

}
