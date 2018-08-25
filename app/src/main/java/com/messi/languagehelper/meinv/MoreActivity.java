package com.messi.languagehelper.meinv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.messi.languagehelper.meinv.util.Settings;
import com.messi.languagehelper.meinv.util.ShareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreActivity extends BaseActivity implements OnClickListener {

    @BindView(R.id.mzsm_layout)
    FrameLayout mzsm_layout;
    @BindView(R.id.comments_layout)
    FrameLayout comments_layout;
    @BindView(R.id.about_layout)
    FrameLayout about_layout;
    @BindView(R.id.invite_layout)
    FrameLayout invite_layout;
    @BindView(R.id.qrcode_layout)
    FrameLayout qrcode_layout;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getSupportActionBar().setTitle(getResources().getString(R.string.title_more));
        mSharedPreferences = Settings.getSharedPreferences(this);
        mzsm_layout.setOnClickListener(this);
        comments_layout.setOnClickListener(this);
        about_layout.setOnClickListener(this);
        invite_layout.setOnClickListener(this);
        qrcode_layout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comments_layout:
                comment();
                break;
            case R.id.mzsm_layout:
                toActivity(HelpActivity.class, null);
                break;
            case R.id.about_layout:
                toActivity(AboutActivity.class, null);
                break;
            case R.id.invite_layout:
                invite();
                break;
            case R.id.qrcode_layout:
                toActivity(QRCodeShareActivity.class, null);
                break;
            default:
                break;
        }
    }

    private void comment(){
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if(getPackageName().equals(Settings.application_id_meixiu)){
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper.meixiu"));
            } else if (getPackageName().equals(Settings.application_id_meinv)) {
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper.meinv"));
            } else if (getPackageName().equals(Settings.application_id_caricature)) {
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper.caricature"));
            } else if (getPackageName().equals(Settings.application_id_browser)) {
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper.browser"));
            } else {
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper"));
            }
            MoreActivity.this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invite(){
        if(getPackageName().equals(Settings.application_id_meixiu)){
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_meixiu));
        }else if (getPackageName().equals(Settings.application_id_meinv)) {
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_meinv));
        } else if (getPackageName().equals(Settings.application_id_caricature)) {
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_caricature));
        } else if (getPackageName().equals(Settings.application_id_browser)) {
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_browser));
        } else {
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_meixiu));
        }
    }

}
