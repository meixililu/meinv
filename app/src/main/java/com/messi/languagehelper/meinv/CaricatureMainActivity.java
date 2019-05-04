package com.messi.languagehelper.meinv;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.messi.languagehelper.meinv.util.AppUpdateUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CaricatureMainActivity extends BaseActivity {

    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    private Fragment engFragment;
    private Fragment categoryFragment;
    private Fragment radioHomeFragment;
    private Fragment webviewFragment;
    private long exitTime = 0;
    private SharedPreferences sp;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    hideAllFragment();
                    getSupportFragmentManager().beginTransaction().show(engFragment).commit();
                    return true;
                case R.id.navigation_category:
                    hideAllFragment();
                    getSupportFragmentManager().beginTransaction().show(categoryFragment).commit();
                    return true;
                case R.id.navigation_history:
                    hideAllFragment();
                    getSupportFragmentManager().beginTransaction().show(radioHomeFragment).commit();
                    return true;
                case R.id.navigation_novel:
                    hideAllFragment();
                    getSupportFragmentManager().beginTransaction().show(webviewFragment).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caricature_main);
        ButterKnife.bind(this);
        init();
        initFragment();
        initSDKAndPermission();
        AppUpdateUtil.isNeedUpdate(this);
    }

    private void init(){
        LogUtil.DefalutLog("CaricatureMainActivity---init");
        sp = Setings.getSharedPreferences(this);
        if(Setings.appVersion >= sp.getInt(KeyUtil.Caricature_version,0) &&
                sp.getString(KeyUtil.Caricature_channel,"").contains(Setings.appChannel)){
            Setings.IsShowNovel = false;
        }else {
            Setings.IsShowNovel = true;
        }
        Setings.IsShowNovel = true;
    }

    private void initFragment(){
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.app_id));
        if(Setings.IsShowNovel){
            navigation.inflateMenu(R.menu.caricature_main_novel_tabs);
        }else {
            navigation.inflateMenu(R.menu.caricature_main_tabs);
        }
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        engFragment = CaricatureHomeFragment.newInstance();
        categoryFragment = CaricatureCategoryMainFragment.getInstance();
        radioHomeFragment = CaricatureBookShelfFragment.newInstance();
        webviewFragment = CaricatureNovelHomeFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, engFragment)
                .add(R.id.content, categoryFragment)
                .add(R.id.content, radioHomeFragment)
                .add(R.id.content, webviewFragment)
                .commit();
        hideAllFragment();
        getSupportFragmentManager()
                .beginTransaction().show(engFragment).commit();
    }

    private void hideAllFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(radioHomeFragment)
                .hide(engFragment)
                .hide(categoryFragment)
                .hide(webviewFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.exit_program), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void initSDKAndPermission(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CaricatureMainActivityPermissionsDispatcher.showPermissionWithPermissionCheck(CaricatureMainActivity.this);
            }
        }, 1 * 1000);
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION})
    void showPermission() {
        LogUtil.DefalutLog("showPermission");
    }

    @OnShowRationale({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION})
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

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION})
    void onPerDenied() {
        ToastUtil.diaplayMesShort(this,"没有授权，部分功能将无法使用！");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CaricatureMainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}
