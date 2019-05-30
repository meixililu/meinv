package com.messi.languagehelper.meinv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

public class CSJADUtil {

    public static String zyhy = "5002882#802882204#902882431#902882951#902882166#902882914#902882729";
    public static String yys = "5002883#802883247#902883928#902883146#902883057#902883337#902883111";
    public static String yyj = "5002884#802884474#902884182#902884797#902884816#902884915#902884064";
    public static String ywcd = "5016775#816775566#916775026#916775647#916775978#916775383#916775753";
    public static String jgmh = "5018718#818718196#918718358#918718855#918718516#918718675#918718086";

    public static String CSJ_APPID;
    public static String CSJ_KPID;
    public static String CSJ_XXL;
    public static String CSJ_XXLSP;
    public static String CSJ_BANNer2ID;
    public static String CSJ_QPSP;
    public static String CSJ_DrawXXLSP;

    public static boolean sInit;

    public static TTAdManager get() {
        if (!sInit) {
            return null;
        }
        return TTAdSdk.getAdManager();
    }

    public static void init(Context context) {
        try {
            initData(context);
            doInit(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context) {
        try {
            if (!sInit) {
                TTAdSdk.init(context, buildConfig(context));
                sInit = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TTAdConfig buildConfig(Context context) {
        return new TTAdConfig.Builder()
                .appId(CSJ_APPID)
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName(context.getPackageName())
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_LIGHT)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合
                .supportMultiProcess(false)
//                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .build();
    }

    public static void initData(Context context){
        SharedPreferences sp = Setings.getSharedPreferences(context);
        String idstr = "";
        SystemUtil.PacketName = context.getPackageName();
        if(SystemUtil.PacketName.equals(Setings.application_id_caricature)){
            idstr = jgmh;
        }else if(SystemUtil.PacketName.equals(Setings.application_id_caricature_ecy)){
            idstr = jgmh;
        }
        if(sp != null && !TextUtils.isEmpty(idstr)){
            setADData(sp.getString(KeyUtil.Ad_Csj,idstr));
        }
    }

    public static void setADData(String idstr){
        try {
            if(!TextUtils.isEmpty(idstr) && idstr.contains("#")){
                String[] ids = idstr.split("#");
                if(ids.length >= 7){
                    CSJ_APPID = ids[0];
                    CSJ_KPID = ids[1];
                    CSJ_XXL = ids[2];
                    CSJ_XXLSP = ids[3];
                    CSJ_BANNer2ID = ids[4];
                    CSJ_QPSP = ids[5];
                    CSJ_DrawXXLSP = ids[6];
                }
//                LogUtil.DefalutLog("initCSJADID:"+idstr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
