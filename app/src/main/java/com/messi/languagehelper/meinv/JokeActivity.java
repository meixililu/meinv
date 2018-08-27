package com.messi.languagehelper.meinv;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.messi.languagehelper.meinv.adapter.JokePageAdapter;
import com.messi.languagehelper.meinv.impl.FragmentProgressbarListener;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.AppUpdateUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;

import java.util.List;


public class JokeActivity extends BaseActivity implements FragmentProgressbarListener {

    private TabLayout tablayout;
    private ViewPager viewpager;
    private JokePageAdapter pageAdapter;
    private long exitTime = 0;
    private SharedPreferences sp;

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
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
        sp = Setings.getSharedPreferences(this);
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void setPageAdapter(List<AVObject> list){
        pageAdapter = new JokePageAdapter(getSupportFragmentManager(),
                this,
                list);
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
                if(pageAdapter != null){
                    pageAdapter.onTabReselected(tab.getPosition());
                }
            }
        });
    }

    private void getTagData(){
        AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.Meinv.Meinv);
        query.whereEqualTo(AVOUtil.Meinv.isValid,"1");
        query.addAscendingOrder(AVOUtil.Meinv.order);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                LogUtil.DefalutLog("done"+list);
                if(list != null){
                    LogUtil.DefalutLog("list.size()"+list.size());
                    setPageAdapter(list);
                }
            }
        });
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
                toActivity(SearchActivity.class,null);
                break;
        }
        return true;
    }

    private void toMoreActivity() {
        Intent intent = new Intent(this, MoreActivity.class);
        startActivity(intent);
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

    private void initPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        Setings.verifyStoragePermissions(this, Setings.PERMISSIONS_STORAGE);
    }

    private void initSDKAndPermission(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initPermissions();
            }
        }, 1 * 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10010:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppUpdateUtil.checkUpdate(this);
                } else {
                    Uri packageURI = Uri.parse("package:"+this.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                    startActivityForResult(intent, 10086);
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10086) {
            AppUpdateUtil.checkUpdate(this);
        }
    }
}
