package com.messi.languagehelper.meinv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.baidu.mobads.AdView;

public class BDADUtil {

    public static String zyhy = "f60acdfd#6057037#6070553#6070635#6070637#6066399";
    public static String yys = "aeea712c#6199229#6199230#6199231#6199238#6199245";
    public static String yyj = "e7db9df7#6199246#6199250#6199252#6199256#6199268";
    public static String ywcd = "b033a03e#6199247#6199251#6199253#6199258#6199269";
    public static String jgmh = "b033a03e#6199247#6199251#6199253#6199258#6199269";

    public static String BD_APPID;
    public static String BD_KPID;
    public static String BD_BANNer_XXL;
    public static String BD_BANNer;
    public static String BD_BANNer_XXL2;
    public static String BD_SP;

    public static void init(Context context){
        try {
            SharedPreferences sp = Setings.getSharedPreferences(context);
            String idstr = "";
            SystemUtil.PacketName = context.getPackageName();
            if(SystemUtil.PacketName.equals(Setings.application_id_caricature)){
                idstr = jgmh;
            }else if(SystemUtil.PacketName.equals(Setings.application_id_caricature_ecy)){
                idstr = jgmh;
            }
            if(sp != null && !TextUtils.isEmpty(idstr)){
                setADData(sp.getString(KeyUtil.Ad_Bd,idstr));
                AdView.setAppSid(context,BD_APPID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setADData(String idstr) {
        try {
            if(!TextUtils.isEmpty(idstr) && idstr.contains("#")){
                String[] ids = idstr.split("#");
                if(ids.length >= 6){
                    BD_APPID = ids[0];
                    BD_KPID = ids[1];
                    BD_BANNer_XXL = ids[2];
                    BD_BANNer = ids[3];
                    BD_BANNer_XXL2 = ids[4];
                    BD_SP = ids[5];
                }
//                LogUtil.DefalutLog("initBDADID:"+idstr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
