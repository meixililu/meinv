package com.messi.languagehelper.meinv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.messi.languagehelper.meinv.box.BoxHelper;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.event.CaricatureEventAddBookshelf;
import com.messi.languagehelper.meinv.http.LanguagehelperHttpClient;
import com.messi.languagehelper.meinv.http.UICallback;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.DownLoadUtil;
import com.messi.languagehelper.meinv.util.HtmlParseUtil;
import com.messi.languagehelper.meinv.util.ImgUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.SDCardUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.XFYSAD;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MiaosouDetailActivity extends BaseActivity {

    @BindView(R.id.item_img_bg)
    SimpleDraweeView itemImgBg;
    @BindView(R.id.item_img)
    SimpleDraweeView itemImg;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.tags)
    TextView tags;
    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.views)
    TextView views;
    @BindView(R.id.item_layout)
    LinearLayout itemLayout;
    @BindView(R.id.add_bookshelf)
    TextView addBookshelf;
    @BindView(R.id.to_read)
    TextView toRead;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.ad_img)
    SimpleDraweeView adImg;
    @BindView(R.id.ad_sign)
    TextView adSign;
    @BindView(R.id.xx_ad_layout)
    FrameLayout xxAdLayout;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.share_img)
    ImageView shareImg;
    private CNWBean mAVObject;
    private XFYSAD mXFYSAD;

    private String sharePath;
    private String imgUrl;
    private String shareImgName = "share_img.jpg";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    sharePath = SDCardUtil.getDownloadPath(SDCardUtil.ImgPath) + shareImgName;
                    ImgUtil.toBitmap(MiaosouDetailActivity.this, sharePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusbar();
        setContentView(R.layout.miaosou_detail_activity);
        ButterKnife.bind(this);
        setStatusbarColor(R.color.none);
        init();
        loadAD();
        showData();
    }

    private void init() {
        mAVObject = getIntent().getParcelableExtra(KeyUtil.ObjectKey);
        mAVObject = BoxHelper.getNewestData(mAVObject);
        imgUrl = mAVObject.getImg_url();
    }

    private void loadAD() {
        mXFYSAD = new XFYSAD(this, xxAdLayout, ADUtil.MRYJYSNRLAd);
        mXFYSAD.showAd();
    }

    private void showData(){
        LogUtil.DefalutLog(mAVObject.toString());
        if(mAVObject.getSource_url().equals(mAVObject.getRead_url()) || TextUtils.isEmpty(mAVObject.getRead_url())){
            getData();
        }else {
            setData();
        }
    }

    private void initButton(){
        if(mAVObject.getCollected() > 100){
            addBookshelf.setText(getString(R.string.add_bookshelf_already));
        }else {
            addBookshelf.setText(getString(R.string.add_bookshelf));
        }
    }

    private void getData() {
        showProgressbar();
        LanguagehelperHttpClient.get(mAVObject.getSource_url(), new UICallback(this) {
            @Override
            public void onFailured() {
            }

            @Override
            public void onFinished() {
                hideProgressbar();
            }

            @Override
            public void onResponsed(String responseString) {
                if (!TextUtils.isEmpty(responseString)) {
                    LogUtil.DefalutLog("responseString:" + responseString);
                    HtmlParseUtil.parseMiaosouDetailHtml(responseString, mAVObject);
                    setData();
                }
            }
        });
    }

    private void setData() {
        try {
            if (mAVObject != null) {
                imgUrl = mAVObject.getImg_url();
                itemImgBg.setImageURI(mAVObject.getImg_url());
                itemImg.setImageURI(mAVObject.getImg_url());
                name.setText(mAVObject.getTitle());
                tags.setText(mAVObject.getTag());
                author.setText(mAVObject.getAuthor());
                des.setText(mAVObject.getDes());
                source.setText("来源：喵搜");
                views.setText(mAVObject.getUpdate_des());
                initButton();
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void shareImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownLoadUtil.downloadFile(MiaosouDetailActivity.this,
                        imgUrl, SDCardUtil.ImgPath, shareImgName, mHandler);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXFYSAD != null) {
            mXFYSAD.showAd();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.add_bookshelf, R.id.to_read, R.id.back_btn, R.id.share_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_bookshelf:
                addToBookShelf();
                break;
            case R.id.to_read:
                onItemClick();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.share_img:
                shareImg();
                break;
        }
    }

    private void onItemClick(){
        Intent intent = new Intent(this, WebViewForMiaosouActivity.class);
        intent.putExtra(KeyUtil.ObjectKey, mAVObject);
        intent.putExtra(KeyUtil.isHideMic,true);
        intent.putExtra(KeyUtil.FilterName,mAVObject.getSource_name());
        intent.putExtra(KeyUtil.IsNeedGetFilter, true);
        intent.putExtra(KeyUtil.SearchUrl, Setings.CaricatureSearchUrl);
        intent.putExtra(KeyUtil.ImgUrl, mAVObject.getImg_url());
        intent.putExtra(KeyUtil.IsHideToolbar, true);
        intent.putExtra(KeyUtil.IsReedPullDownRefresh, false);
        startActivityForResult(intent,10001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10001){
            mAVObject = BoxHelper.getNewestData(mAVObject);
        }
    }

    private void addToBookShelf(){
        if(mAVObject.getCollected() > 100){
            mAVObject.setCollected(0);
        }else {
            mAVObject.setCollected(System.currentTimeMillis());
        }
        mAVObject.setUpdateTime(System.currentTimeMillis());
        initButton();
        BoxHelper.updateCNWBean(mAVObject);
        EventBus.getDefault().post(new CaricatureEventAddBookshelf());
    }
}
