package com.messi.languagehelper.meinv;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.qq.e.ads.splash.SplashADListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingActivity extends AppCompatActivity {

    @BindView(R.id.ad_source)
    TextView ad_source;
    @BindView(R.id.skip_view)
    TextView skip_view;
    @BindView(R.id.ad_img)
    SimpleDraweeView ad_img;
    @BindView(R.id.splash_container)
    FrameLayout splash_container;
    private SharedPreferences mSharedPreferences;
    private Handler mHandler;
    private boolean isAdExposure;
    private boolean isAdClicked;
    private boolean notJump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        try {
            TransparentStatusbar();
            setContentView(R.layout.loading_activity);
            ButterKnife.bind(this);
            TXADUtil.initTXADID(this);
            ADUtil.loadAd(this);
            init();
        } catch (Exception e) {
            onError();
            e.printStackTrace();
        }
    }

    private void TransparentStatusbar() {
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            //透明状态栏 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void init() {
        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mHandler = new Handler();
        initPermissions();
        if(!mSharedPreferences.getBoolean(KeyUtil.IsTXADPermissionReady,false)){
            ADUtil.Advertiser = ADUtil.Advertiser_XF;
        }else {
            ADUtil.Advertiser = mSharedPreferences.getString(KeyUtil.APP_Advertiser,ADUtil.Advertiser_XF);
        }
        LogUtil.DefalutLog("Advertiser:"+ADUtil.Advertiser);
        if(ADUtil.IsShowAD){
            if(ADUtil.Advertiser.equals(ADUtil.Advertiser_XF)){
                loadXFAD();
            }else {
                loadTXAD();
            }
        }
        startTask();
    }

    private void loadXFAD() {
        IFLYNativeAd nativeAd = new IFLYNativeAd(this, ADUtil.KaiPingYSAD, mListener);
        nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
        nativeAd.loadAd(1);
    }

    IFLYNativeListener mListener = new IFLYNativeListener() {
        @Override
        public void onAdFailed(AdError error) { // 广告请求失败
            if(ADUtil.Advertiser.equals(ADUtil.Advertiser_XF)){
                loadTXAD();
            }else {
                onADFail();
            }
        }

        @Override
        public void onADLoaded(List<NativeADDataRef> lst) { // 广告请求成功
            onAdReceive();
            setADData(lst);
        }

        @Override
        public void onCancel() { // 下载类广告，下载提示框取消
            toNextPage();
        }

        @Override
        public void onConfirm() { // 下载类广告，下载提示框确认
            toNextPage();
        }
    };

    private void setADData(List<NativeADDataRef> lst) {
        if (lst != null && lst.size() > 0) {
            final NativeADDataRef mNativeADDataRef = lst.get(0);
            if (mNativeADDataRef != null) {
                setAD(mNativeADDataRef);
            }
        }
    }

    private void setAD(final NativeADDataRef mNativeADDataRef) {
        ad_img.setImageURI(mNativeADDataRef.getImage());
        boolean loadingExposure = mNativeADDataRef.onExposured(ad_img);
        LogUtil.DefalutLog("loadingExposure：" + loadingExposure);
        ad_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAd();
                boolean onClicked = mNativeADDataRef.onClicked(view);
                LogUtil.DefalutLog("onClicked:" + onClicked);
            }
        });
    }

    private void onClickAd() {
        isAdClicked = true;
        cancleRunable();
    }

    private void loadTXAD() {
        TXADUtil.showKaipingAD(this, splash_container, skip_view,
                new SplashADListener() {
                    @Override
                    public void onADDismissed() {
                        LogUtil.DefalutLog("onADDismissed");
                        if(!notJump){
                            toNextPage();
                        }
                    }

                    @Override
                    public void onNoAD(com.qq.e.comm.util.AdError adError) {
                        LogUtil.DefalutLog(adError.getErrorMsg());
                        if(ADUtil.Advertiser.equals(ADUtil.Advertiser_TX)){
                            loadXFAD();
                        }else {
                            onADFail();
                        }
                    }

                    @Override
                    public void onADPresent() {
                        LogUtil.DefalutLog("onADPresent");
                        skip_view.setVisibility(View.VISIBLE);
                        isAdExposure = true;
                    }

                    @Override
                    public void onADClicked() {
                        LogUtil.DefalutLog("onADClicked");
                        onClickAd();
                    }

                    @Override
                    public void onADTick(long l) {
                        LogUtil.DefalutLog("onADTick:"+l);
                        if(l < 2500){
                            if(!notJump){
                                toNextPage();
                            }
                        }
                    }
                    @Override
                    public void onADExposure() {

                    }
                });
    }

    private void onADFail() {
        if (ADUtil.isHasLocalAd()) {
            onAdReceive();
            setAD(ADUtil.getRandomAd());
        } else {
            onError();
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(m3Runnable);
            mHandler.removeCallbacks(mRunnableFinal);
            toNextPage();
        }
    };

    private Runnable mRunnableFinal = new Runnable() {
        @Override
        public void run() {
            LogUtil.DefalutLog("LoadingActivity---mRunnableFinal");
            toNextPage();
        }
    };

    private Runnable m3Runnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.DefalutLog("LoadingActivity---m3Runnable---isAdExposure:" + isAdExposure);
            if (!isAdExposure) {
                mHandler.removeCallbacks(mRunnableFinal);
                toNextPage();
            }
        }
    };

    private void onAdReceive() {
        ad_source.setVisibility(View.VISIBLE);
        skip_view.setVisibility(View.VISIBLE);
        isAdExposure = true;
        if (mHandler != null) {
            mHandler.postDelayed(mRunnableFinal, 4300);
        }
    }

    private void onError() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    //启动页加载总时常，防止广告一直加载中等待过久
    private void startTask() {
        mHandler.postDelayed(m3Runnable, 3800);
    }

    private void toNextPage() {
        Class mclass = JokeActivity.class;
        if(getPackageName().equals(Setings.application_id_meinv)){
            mclass = JokeActivity.class;
        }
        Intent intent = new Intent(LoadingActivity.this, mclass);
        startActivity(intent);
        finish();
    }

    private void cancleRunable() {
        if (m3Runnable != null) {
            mHandler.removeCallbacks(mRunnableFinal);
            mHandler.removeCallbacks(m3Runnable);
            mHandler.removeCallbacks(mRunnable);
        }
    }

    //防止用户返回键退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.DefalutLog("onResume");
        notJump = false;
        if(isAdClicked) {
            toNextPage();
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        notJump = true;
        LogUtil.DefalutLog("onPause");
        MobclickAgent.onPause(this);
    }

    private void initPermissions(){
        if (VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        }
    }

    @TargetApi(VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
//        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
//            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
//        }
//        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
//            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
        if (lackedPermission.size() == 0) {
            Setings.saveSharedPreferences(mSharedPreferences,KeyUtil.IsTXADPermissionReady,true);
        } else {
            Setings.saveSharedPreferences(mSharedPreferences,KeyUtil.IsTXADPermissionReady,false);
        }
    }

}
