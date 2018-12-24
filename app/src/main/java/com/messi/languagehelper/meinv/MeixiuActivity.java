package com.messi.languagehelper.meinv;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.messi.languagehelper.meinv.adapter.JokePageAdapter;
import com.messi.languagehelper.meinv.impl.FragmentProgressbarListener;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.AppUpdateUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.ToastUtil;

import java.util.List;

import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.callback.FindCallback;
import cn.leancloud.convertor.ObserverBuilder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MeixiuActivity extends BaseActivity implements FragmentProgressbarListener {

    private TabLayout tablayout;
    private ViewPager viewpager;
    private JokePageAdapter pageAdapter;
    private long exitTime = 0;
    private String category = "bizhi";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joke_activity);
        initViews();
        getTagData();
        initSDKAndPermission();
        AppUpdateUtil.runCheckUpdateTask(this);
    }

    private void initViews() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.app_id));
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void setPageAdapter(AVObject object){
        int ServerVersion = object.getInt(AVOUtil.MeinvCategory.version);
        String ServerChannel = object.getString(AVOUtil.MeinvCategory.channel);
        String verifyTags = object.getString(AVOUtil.MeinvCategory.verifyTags);
        String normalTags = object.getString(AVOUtil.MeinvCategory.normalTags);
        String[] tags = verifyTags.split("#");
        if(Setings.appVersion >= ServerVersion){
            if(ServerChannel.contains(Setings.appChannel)){
                category = "bizhi";
                ADUtil.IsShowAD = false;
            }else {
                category = "meinv";
                tags = normalTags.split("#");
            }
        }else {
            category = "meinv";
            tags = normalTags.split("#");
        }
        LogUtil.DefalutLog("category:"+category);
        pageAdapter = new JokePageAdapter(getSupportFragmentManager(), tags, category);
        viewpager.setAdapter(pageAdapter);
        tablayout.setupWithViewPager(viewpager);
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                refreshFragment();
            }
        });
    }

    private void refreshFragment(){
        if(pageAdapter != null){
            pageAdapter.onTabReselected(tablayout.getSelectedTabPosition());
        }
    }

    private void getTagData(){
        AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.MeinvCategory.MeinvCategory);
        query.whereEqualTo(AVOUtil.MeinvCategory.isValid,"1");
        query.whereEqualTo(AVOUtil.MeinvCategory.app,"meixiu");
        query.findInBackground().subscribe(ObserverBuilder.buildSingleObserver(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list != null && list.size() > 0){
                    setPageAdapter(list.get(0));
                }
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more:
                toMoreActivity();
                break;
            case R.id.action_search:
                toSearchActivity();
                break;
        }
        return true;
    }

    private void toSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(KeyUtil.Category,category);
        startActivity(intent);
    }

    private void toMoreActivity() {
        Intent intent = new Intent(this, MoreActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            refreshFragment();
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
                MeixiuActivityPermissionsDispatcher.showPermissionWithPermissionCheck(MeixiuActivity.this);
            }
        }, 1 * 1000);
    }

    @NeedsPermission({android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION})
    void showPermission() {
        LogUtil.DefalutLog("showPermission");
    }

    @OnShowRationale({android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION})
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

    @OnPermissionDenied({android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION})
    void onPerDenied() {
        ToastUtil.diaplayMesShort(this,"没有授权，部分功能将无法使用！");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MeixiuActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
