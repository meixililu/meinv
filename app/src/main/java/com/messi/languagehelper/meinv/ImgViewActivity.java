package com.messi.languagehelper.meinv;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.messi.languagehelper.meinv.impl.FragmentProgressbarListener;
import com.messi.languagehelper.meinv.util.DownLoadUtil;
import com.messi.languagehelper.meinv.util.ImgUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.SDCardUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.messi.languagehelper.meinv.view.DoubleTapGestureListener;
import com.messi.languagehelper.meinv.view.ZoomableDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ImgViewActivity extends BaseActivity implements FragmentProgressbarListener {

    public static int ShareImgCode = 10020;
    @BindView(R.id.item_img)
    ZoomableDraweeView itemImg;
    @BindView(R.id.close_img)
    ImageView closeImg;
    @BindView(R.id.download_img)
    ImageView downloadImg;
    @BindView(R.id.share_img)
    ImageView shareImg;
    private String url;
    private String img_id;
    private String DownloadUrl;
    private String saveUrl;
    private String sharePath;
    private String shareImgName = "share_img.jpg";
    private float ratio;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                try {
                    sharePath = SDCardUtil.getDownloadPath(SDCardUtil.ImgPath)+shareImgName;
                    ImgUtil.toBitmap(ImgViewActivity.this,sharePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

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
        ratio = getIntent().getFloatExtra(KeyUtil.Ratio, 0);
        DownloadUrl = getIntent().getStringExtra(KeyUtil.DownloadUrl);
        img_id = getIntent().getStringExtra(KeyUtil.Id);
        shareImgName = img_id + ".jpg";
        if (!TextUtils.isEmpty(url)) {
            if (ratio > 0) {
                itemImg.setAspectRatio(ratio);
            }
            itemImg.setAllowTouchInterceptionWhileZoomed(true);
            //长按
            itemImg.setIsLongpressEnabled(false);
            //双击击放大或缩小
            itemImg.setTapListener(new DoubleTapGestureListener(itemImg));

            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(url))
                    .build();
            //加载图片
            itemImg.setController(draweeController);
        }
    }


    @OnClick({R.id.close_img, R.id.download_img,R.id.share_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close_img:
                onBackPressed();
                break;
            case R.id.share_img:
                shareImg();
                break;
            case R.id.download_img:
                saveImg();
                break;
        }
    }

    private void saveImg() {
        saveUrl = url;
        if (!TextUtils.isEmpty(DownloadUrl)) {
            saveUrl = DownloadUrl;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownLoadUtil.downloadFile(ImgViewActivity.this, saveUrl, SDCardUtil.ImgPath, img_id + ".jpg");
            }
        }).start();

        ToastUtil.diaplayMesShort(this, "图片保存在/meinv/img/文件夹");
    }

    private void shareImg() {
        saveUrl = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownLoadUtil.downloadFile(ImgViewActivity.this, saveUrl, SDCardUtil.ImgPath, shareImgName, mHandler);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            SDCardUtil.deleteFile(sharePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
