package com.messi.languagehelper.meinv;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.messi.languagehelper.meinv.util.AppUpdateUtil;
import com.messi.languagehelper.meinv.util.Setings;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaricatureMainActivity extends AppCompatActivity {

    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    private Fragment engFragment;
    private Fragment dashboardFragment;
    private Fragment radioHomeFragment;
    private long exitTime = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    hideAllFragment();
                    getSupportFragmentManager().beginTransaction().show(engFragment).commit();
                    engFragment.setUserVisibleHint(true);
                    return true;
                case R.id.navigation_dashboard:
                    hideAllFragment();
                    getSupportFragmentManager().beginTransaction().show(dashboardFragment).commit();
                    dashboardFragment.setUserVisibleHint(true);
                    return true;
                case R.id.navigation_history:
                    hideAllFragment();
                    getSupportFragmentManager().beginTransaction().show(radioHomeFragment).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caricature_main);
        ButterKnife.bind(this);
        initFragment();
        initSDKAndPermission();
        AppUpdateUtil.runCheckUpdateTask(this);
    }

    private void initFragment(){
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        engFragment = CaricatureHomeFragment.newInstance();
        dashboardFragment = CaricatureWebListFragment.newInstance();
        radioHomeFragment = CaricatureBHFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, engFragment)
                .add(R.id.content, dashboardFragment)
                .add(R.id.content, radioHomeFragment)
                .commit();
        hideAllFragment();
        getSupportFragmentManager()
                .beginTransaction().show(engFragment).commit();
    }

    private void hideAllFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(dashboardFragment)
                .hide(radioHomeFragment)
                .hide(engFragment)
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
