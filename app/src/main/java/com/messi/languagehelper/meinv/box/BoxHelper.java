package com.messi.languagehelper.meinv.box;

import android.text.TextUtils;

import com.messi.languagehelper.meinv.BaseApplication;
import com.messi.languagehelper.meinv.util.LogUtil;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;

public class BoxHelper {

    public static BoxStore boxStore;

    public static void init(BaseApplication app){
        try {
            boxStore = MyObjectBox.builder().androidContext(app).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BoxStore getBoxStore(){
        if(boxStore == null){
            boxStore = MyObjectBox.builder().androidContext(BaseApplication.mInstance).build();
        }
        return boxStore;
    }

    /**CNWBean**/
    public static Box<CNWBean> getCNWBeanBox(){
        return getBoxStore().boxFor(CNWBean.class);
    }

    public static void updateCNWBean(CNWBean bean){
        if(bean != null && !TextUtils.isEmpty(bean.getItemId())){
            CNWBean oldData = findCNWBeanByItemId(bean.getItemId());
            if(oldData != null){
                bean.setId(oldData.getId());
            }
        }
        long id = getCNWBeanBox().put(bean);
        bean.setId(id);
        LogUtil.DefalutLog("updateCNWBean:"+bean.toString());
    }

    public static void updateCNWBean(List<CNWBean> list){
        for (CNWBean item : list){
            updateCNWBean(item);
        }
//        getCNWBeanBox().put(list);
    }

    public static CNWBean findCNWBeanByItemId(String  mItemId){
        if(!TextUtils.isEmpty(mItemId)){
            return getCNWBeanBox()
                    .query()
                    .equal(CNWBean_.itemId,mItemId)
                    .build()
                    .findFirst();
        }else {
            return null;
        }
    }

    public static CNWBean getNewestData(CNWBean bean){
        if(bean != null && !TextUtils.isEmpty(bean.getItemId())){
            CNWBean oldBean = findCNWBeanByItemId(bean.getItemId());
            if(oldBean != null){
                LogUtil.DefalutLog("getNewestData:"+oldBean.toString());
                return oldBean;
            }
        }
        return bean;
    }

    public static List<CNWBean> getCollectedList(String table, int offset, int psize){
        QueryBuilder<CNWBean> query = getCNWBeanBox().query();
        query.equal(CNWBean_.table,table);
        query.greater(CNWBean_.collected,100);
        query.order(CNWBean_.updateTime,QueryBuilder.DESCENDING);
        if(psize > 0){
            return query.build().find(offset,psize);
        }else {
            return query.build().find();
        }
    }

    public static void deleteAllData(String table){
        List<CNWBean> list = getCollectedList(table,0,0);
        if(list != null && list.size() > 0){
            getCNWBeanBox().remove(list);
        }
    }

    public static void deleteCNWBean(CNWBean bean){
        getCNWBeanBox().remove(bean);
    }

    /**WebFilter**/
    public static Box<WebFilter> getWebFilterBox(){
        return getBoxStore().boxFor(WebFilter.class);
    }

    public static void updateWebFilter(WebFilter bean){
        getWebFilterBox().put(bean);
    }

    public static void updateWebFilter(List<WebFilter> list){
        getWebFilterBox().removeAll();
        getWebFilterBox().put(list);
    }

    public static WebFilter findWebFilterByName(String name){
        QueryBuilder<WebFilter> query = getWebFilterBox().query();
        query.equal(WebFilter_.name,name);
        return query.build().findFirst();

    }


}
