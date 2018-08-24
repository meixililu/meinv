package com.messi.languagehelper.meinv.bean;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.avos.avoscloud.AVObject;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by luli on 14/12/2017.
 */

public class NativeADDataRefForZYHY extends NativeADDataRef {

    private Context context;
    private String title;
    private String sub_title;
    private String type;
    private String url;
    private String img;
    private String adObjectId;
    private ArrayList<String> imgs;

    public NativeADDataRefForZYHY(Context context,
                                  String title,
                                  String sub_title,
                                  String type,
                                  String url,
                                  String img,
                                  ArrayList<String> imgs,
                                  String adObjectId){
        this.context = context;
        this.title = title;
        this.sub_title = sub_title;
        this.type = type;
        this.url = url;
        this.img = img;
        this.imgs = imgs;
        this.adObjectId = adObjectId;
    }

    public static NativeADDataRefForZYHY build(Context context, AVObject adObject){
        return NativeADDataRefForZYHY.create()
                .setContext(context)
                .setTitile(adObject.getString(AVOUtil.AdList.title))
                .setSub_title(adObject.getString(AVOUtil.AdList.sub_title))
                .setType(adObject.getString(AVOUtil.AdList.type))
                .setUrl(adObject.getString(AVOUtil.AdList.url))
                .setImg(adObject.getString(AVOUtil.AdList.img))
                .setImgs((ArrayList<String>)adObject.get(AVOUtil.AdList.imgs))
                .setAVObjectId(adObject.getObjectId())
                .build();
    }

    @Override
    public String getAdtype() {
        return "";
    }

    @Override
    public String getAdSourceMark() {
        return "广告";
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubTitle() {
        return sub_title;
    }

    @Override
    public String getImage() {
        return img;
    }

    @Override
    public String getIcon() {
        return "";
    }

    @Override
    public ArrayList<String> getImgUrls() {
        return imgs;
    }

    @Override
    public boolean onExposured(View view) {
        LogUtil.DefalutLog("NativeADDataRefForZYHY---onExposured");
        return true;
    }

    @Override
    public boolean onClicked(View view) {
        LogUtil.DefalutLog("NativeADDataRefForZYHY---onClicked");
        ADUtil.toAdView(context,type,url);
        if(!TextUtils.isEmpty(adObjectId)){
            updateDownloadTime();
            return true;
        }
        return false;
    }
    @Override
    public boolean isExposured() {
        LogUtil.DefalutLog("NativeADDataRefForZYHY---isExposured");
        return true;
    }

    private void updateDownloadTime(){
        AVObject post = AVObject.createWithoutData(AVOUtil.AdList.AdList, adObjectId);
        post.increment(AVOUtil.AdList.click_time);
        post.saveInBackground();
    }

    public static ADBuilder create(){
        return new ADBuilder();
    }


    public static class ADBuilder{
        private Context context;
        private String title;
        private String sub_title;
        private String type;
        private String url;
        private String img;
        private ArrayList<String> imgs;
        private String adObjectId;

        public ADBuilder setContext(Context context){
            this.context = context;
            return this;
        }
        public ADBuilder setTitile(String title){
            this.title = title;
            return this;
        }
        public ADBuilder setSub_title(String sub_title){
            this.sub_title = sub_title;
            return this;
        }
        public ADBuilder setType(String type){
            this.type = type;
            return this;
        }
        public ADBuilder setUrl(String url){
            this.url = url;
            return this;
        }
        public ADBuilder setImg(String img){
            this.img = img;
            return this;
        }
        public ADBuilder setImgs(ArrayList<String> imgs){
            this.imgs = imgs;
            return this;
        }
        public ADBuilder setAVObjectId(String adObjectId){
            this.adObjectId = adObjectId;
            return this;
        }
        public NativeADDataRefForZYHY build(){
            return new NativeADDataRefForZYHY(context,title,sub_title,type,url,img,imgs,adObjectId);
        }
    }

}
