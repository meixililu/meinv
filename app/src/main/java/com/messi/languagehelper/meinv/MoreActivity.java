package com.messi.languagehelper.meinv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.messi.languagehelper.meinv.util.Setings;
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
    @BindView(R.id.qq_layout)
    FrameLayout qq_layout;
    @BindView(R.id.qq_layout_line)
    ImageView qq_layout_line;
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
        mSharedPreferences = Setings.getSharedPreferences(this);
        mzsm_layout.setOnClickListener(this);
        comments_layout.setOnClickListener(this);
        about_layout.setOnClickListener(this);
        invite_layout.setOnClickListener(this);
        qrcode_layout.setOnClickListener(this);
        if (getPackageName().equals(Setings.application_id_caricature)) {
            qq_layout.setVisibility(View.VISIBLE);
            qq_layout_line.setVisibility(View.VISIBLE);
            qq_layout.setOnClickListener(this);
        }
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
            case R.id.qq_layout:
                toQQ("36h6FS3z8vGUaFg-1UPd42IRtEjcsY0i");
                break;
            default:
                break;
        }
    }

    private void comment(){
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if(getPackageName().equals(Setings.application_id_meixiu)){
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper.meixiu"));
            } else if (getPackageName().equals(Setings.application_id_meinv)) {
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper.meinv"));
            } else if (getPackageName().equals(Setings.application_id_caricature)) {
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper.caricature"));
            } else if (getPackageName().equals(Setings.application_id_browser)) {
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper.browser"));
            } else {
                intent.setData(Uri.parse("market://details?id=com.messi.languagehelper"));
            }
            MoreActivity.this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************
     *
     * 发起添加群流程。群号：极光漫画(763770901) 的 key 为： 36h6FS3z8vGUaFg-1UPd42IRtEjcsY0i
     * 调用 joinQQGroup(36h6FS3z8vGUaFg-1UPd42IRtEjcsY0i) 即可发起手Q客户端申请加群 极光漫画(763770901)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean toQQ(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    private void invite(){
        if(getPackageName().equals(Setings.application_id_meixiu)){
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_meixiu));
        }else if (getPackageName().equals(Setings.application_id_meinv)) {
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_meinv));
        } else if (getPackageName().equals(Setings.application_id_caricature)) {
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_caricature));
        } else if (getPackageName().equals(Setings.application_id_browser)) {
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_browser));
        } else {
            ShareUtil.shareText(MoreActivity.this, MoreActivity.this.getResources().getString(R.string.invite_friends_meixiu));
        }
    }

}
