package com.messi.languagehelper.meinv.box;

import com.messi.languagehelper.meinv.BaseApplication;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;

public class BoxHelper {

    public static BoxStore boxStore;

    public static void init(BaseApplication app){
        boxStore = MyObjectBox.builder().androidContext(app).build();
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
        getCNWBeanBox().put(bean);
    }

    public static void updateCNWBean(List<CNWBean> list){
        getCNWBeanBox().put(list);
    }

    public static CNWBean findCNWBeanByItemId(String  mItemId){
        return getCNWBeanBox()
                .query()
                .equal(CNWBean_.itemId,mItemId)
                .build()
                .findFirst();
    }

    public static List<CNWBean> getCaricatureList(String table,int offset,int psize,
                                                  boolean isHistory, boolean isCollected){
        QueryBuilder<CNWBean> query = getCNWBeanBox().query();
        query.equal(CNWBean_.table,table);
        if(isHistory){
            query.greater(CNWBean_.history,100);
        }
        if(isCollected){
            query.greater(CNWBean_.collected,100);
        }
        if(psize > 0){
            return query.build().find(offset,psize);
        }else {
            return query.build().find();
        }
    }

    public static void deleteAllData(String table,boolean isHistory, boolean isCollected){
        List<CNWBean> list = getCaricatureList(table,0,0,isHistory,isCollected);
        for(CNWBean bean : list){
            if(isHistory){
                bean.setHistory(0);
            }
            if(isCollected){
                bean.setCollected(0);
            }
        }
        updateCNWBean(list);
    }

    /**WebFilter**/
    public static Box<WebFilter> getWebFilterBox(){
        return getBoxStore().boxFor(WebFilter.class);
    }

    public static void updateWebFilter(WebFilter bean){
        getWebFilterBox().put(bean);
    }

    public static void updateWebFilter(List<WebFilter> list){
        getWebFilterBox().put(list);
    }

    public static WebFilter findWebFilterByName(String name){
        QueryBuilder<WebFilter> query = getWebFilterBox().query();
        query.equal(WebFilter_.name,name);
        return query.build().findFirst();

    }


}
