package com.messi.languagehelper.meinv.util;

import com.messi.languagehelper.meinv.box.BoxHelper;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.box.WebFilter;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.AVObject;

public class DataUtil {

    public static List<CNWBean> toCNWBeanList(List<AVObject> list){
        List<CNWBean> beans = new ArrayList<CNWBean>();
        for(AVObject mAVObject : list){
            beans.add(AVObjectToCNWBean(mAVObject));
        }
        return beans;
    }

    public static CNWBean AVObjectToCNWBean(AVObject mAVObject){
        CNWBean bean = null;
        bean = BoxHelper.findCNWBeanByItemId(mAVObject.getObjectId());
        if(bean == null){
            bean = new CNWBean();
            bean.setTable(AVOUtil.Caricature.Caricature);
        }
        bean.setRead_url(mAVObject.getString(AVOUtil.Caricature.read_url));
        bean.setTitle(mAVObject.getString(AVOUtil.Caricature.name));
        bean.setDes(mAVObject.getString(AVOUtil.Caricature.des));
        bean.setAuthor(mAVObject.getString(AVOUtil.Caricature.author));
        bean.setImg_url(mAVObject.getString(AVOUtil.Caricature.book_img_url));
        bean.setView(mAVObject.getNumber(AVOUtil.Caricature.views).longValue());
        bean.setTag(mAVObject.getString(AVOUtil.Caricature.tag));
        bean.setSource_url(mAVObject.getString(AVOUtil.Caricature.url));
        bean.setSource_name(mAVObject.getString(AVOUtil.Caricature.source_name));
        bean.setIs_free(mAVObject.getString(AVOUtil.Caricature.isFree));
        bean.setUpdate_des(mAVObject.getString(AVOUtil.Caricature.update));
        bean.setType(mAVObject.getString(AVOUtil.Caricature.type));
        bean.setCategory(mAVObject.getString(AVOUtil.Caricature.category));
        return bean;
    }

    public static List<WebFilter> toWebFilter(List<AVObject> list){
        List<WebFilter> beans = new ArrayList<WebFilter>();
        for(AVObject mAVObject : list){
            beans.add(AVObjectToWebFilter(mAVObject));
        }
        return beans;
    }

    public static WebFilter AVObjectToWebFilter(AVObject mAVObject){
        WebFilter bean = new WebFilter();
        bean.setName(mAVObject.getString(AVOUtil.AdFilter.name));
        bean.setAd_filter(mAVObject.getString(AVOUtil.AdFilter.filter));
        bean.setAd_url(mAVObject.getString(AVOUtil.AdFilter.ad_url));
        bean.setIs_show_ad(mAVObject.getString(AVOUtil.AdFilter.isShowAd));
        return bean;
    }


}
