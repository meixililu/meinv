package com.messi.languagehelper.meinv;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.messi.languagehelper.meinv.adapter.JokePageAdapter;
import com.messi.languagehelper.meinv.impl.FragmentProgressbarListener;
import com.messi.languagehelper.meinv.util.AppUpdateUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Settings;


public class JokeActivity extends BaseActivity implements FragmentProgressbarListener {

    private TabLayout tablayout;
    private ViewPager viewpager;
    private JokePageAdapter pageAdapter;
    private long exitTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joke_activity);
        initViews();
        initSDKAndPermission();
        AppUpdateUtil.runCheckUpdateTask(this);
    }

    private void initViews() {
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);

        pageAdapter = new JokePageAdapter(getSupportFragmentManager(), this);
        viewpager.setAdapter(pageAdapter);
        viewpager.setOffscreenPageLimit(4);
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
        }
        return true;
    }

    private void toMoreActivity() {
//        Intent intent = new Intent(this, MoreActivity.class);
//        startActivity(intent);
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
        Settings.verifyStoragePermissions(this, Settings.PERMISSIONS_STORAGE);
    }

    private void initSDKAndPermission(){
        LogUtil.DefalutLog("main---initXimalayaSDK");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initPermissions();
            }
        }, 1 * 1000);
    }


}
