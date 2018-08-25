package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.messi.languagehelper.meinv.impl.FragmentProgressbarListener;
import com.messi.languagehelper.meinv.util.DownLoadUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.SDCardUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ImgViewActivity extends BaseActivity implements FragmentProgressbarListener {

    @BindView(R.id.item_img)
    SimpleDraweeView itemImg;
    @BindView(R.id.close_img)
    ImageView closeImg;
    @BindView(R.id.download_img)
    ImageView downloadImg;
    private String url;
    private String img_id;
    private String DownloadUrl;
    private String saveUrl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_view_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setStatusbarColor(R.color.black);
        url = getIntent().getStringExtra(KeyUtil.URL);
        DownloadUrl = getIntent().getStringExtra(KeyUtil.DownloadUrl);
        img_id = getIntent().getStringExtra(KeyUtil.Id);
        if (!TextUtils.isEmpty(url)) {
            itemImg.setImageURI(url);
        }
    }


    @OnClick({R.id.close_img, R.id.download_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close_img:
                onBackPressed();
                break;
            case R.id.download_img:
                saveImg();
                break;
        }
    }

    private void saveImg(){
        saveUrl = url;
        if(!TextUtils.isEmpty(DownloadUrl)){
            saveUrl = DownloadUrl;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownLoadUtil.downloadFile(ImgViewActivity.this,saveUrl, SDCardUtil.ImgPath,img_id+".jpg");
            }
        }).start();

        ToastUtil.diaplayMesShort(this,"图片保存在/meinv/img/文件夹");
    }
}
