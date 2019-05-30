package com.messi.languagehelper.meinv.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

/**
 * Created by luli on 2018/4/24.
 */

public class TXADUtil {

    public static String jgmh = "1107933661#8020142786299103#8050747736694164#1050444716395115#7020143736598196#9090443766690187#4090045766994148#7060841776091179";

    public static String TxAppId;
    public static String TxAdKp;
    public static String TxAdXXLSTXW;
    public static String TxAdXXLSWXT;
    public static String TxAdXXLZWYT;
    public static String TxAdCDT;
    public static String TxAdCDTZX;
    public static String TxAdSXT;

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
                setADData(sp.getString(KeyUtil.Ad_Ids,idstr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setADData(String idstr){
        try {
            if(!TextUtils.isEmpty(idstr) && idstr.contains("#")){
                String[] ides = idstr.split("#");
                if(ides.length >= 8){
                    TxAppId = ides[0];
                    TxAdKp = ides[1];
                    TxAdXXLSTXW = ides[2];
                    TxAdXXLSWXT = ides[3];
                    TxAdXXLZWYT = ides[4];
                    TxAdCDT = ides[5];
                    TxAdCDTZX = ides[6];
                    TxAdSXT = ides[7];
//                    LogUtil.DefalutLog("initTXADID:"+idstr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showKaipingAD(Activity activity,
                                     ViewGroup adContainer,
                                     View skipContainer,
                                     SplashADListener listener){
        SplashAD splashAD = new SplashAD(activity, adContainer, skipContainer,
                TxAppId, TxAdKp, listener, 3000);
    }

    public static void showCDT(Activity activity,
                               NativeExpressAD.NativeExpressADListener listener){
        NativeExpressAD nativeExpressAD = new NativeExpressAD(activity,
                new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), TxAppId, TxAdCDT, listener);
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build()); //
        nativeExpressAD.loadAD(1);
    }

    public static void showCDTZX(Activity activity,
                                 NativeExpressAD.NativeExpressADListener listener){
        LogUtil.DefalutLog("showCDTZX:"+ TxAdCDTZX);
        NativeExpressAD nativeExpressAD = new NativeExpressAD(activity,
                new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), TxAppId, TxAdCDTZX, listener);
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build()); //
        nativeExpressAD.loadAD(1);
    }

    public static void showXXL(Activity activity,
                               NativeExpressAD.NativeExpressADListener listener){
        int num = NumberUtil.getRandomNumber(13);
        String postID = TxAdXXLZWYT;
        if(num > 8){
            postID = TxAdXXLSWXT;
        }else if(num > 3){
            postID = TxAdSXT;
        }
        NativeExpressAD nativeExpressAD = new NativeExpressAD(activity,
                new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), TxAppId, postID, listener);
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI环境下可以自动播放视频
                .setAutoPlayMuted(true)// 自动播放时为静音
                .build()); //
        nativeExpressAD.loadAD(1);
    }

    public static void showXXL_ZWYT(Activity activity,
                                    NativeExpressAD.NativeExpressADListener listener){
        NativeExpressAD nativeExpressAD = new NativeExpressAD(activity,
                new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), TxAppId, TxAdXXLZWYT, listener);
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build()); //
        nativeExpressAD.loadAD(1);
    }

    public static void showXXL_STXW(Activity activity,
                                    NativeExpressAD.NativeExpressADListener listener){
        NativeExpressAD nativeExpressAD = new NativeExpressAD(activity,
                new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), TxAppId, TxAdXXLSTXW, listener);
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build()); //
        nativeExpressAD.loadAD(1);
    }

    public static void showXXLAD(Activity activity,int type,
                                 NativeExpressAD.NativeExpressADListener listener){
        if(type == 0){
            showCDT(activity,listener);
        }else if(type == 1){
            showCDTZX(activity,listener);
        }else if(type == 2){
            showXXL(activity,listener);
        }
    }
}
