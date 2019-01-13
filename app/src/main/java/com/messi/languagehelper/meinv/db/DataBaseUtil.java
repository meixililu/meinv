package com.messi.languagehelper.meinv.db;

import android.content.Context;

import com.messi.languagehelper.meinv.BaseApplication;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.dao.Avobject;
import com.messi.languagehelper.meinv.dao.AvobjectDao;
import com.messi.languagehelper.meinv.dao.DaoSession;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.AVObject;

public class DataBaseUtil {

    private static DataBaseUtil instance;
    private static Context appContext;
    private DaoSession mDaoSession;
    private AvobjectDao mAvobjectDao;

    public DataBaseUtil() {
    }

    public static DataBaseUtil getInstance() {
        if (instance == null) {
            instance = new DataBaseUtil();
            if (appContext == null) {
                appContext = BaseApplication.mInstance;
            }
            instance.mDaoSession = BaseApplication.getDaoSession(appContext);
            instance.mAvobjectDao = instance.mDaoSession.getAvobjectDao();
        }
        return instance;
    }

    public void clearAvobject() {
        mAvobjectDao.deleteAll();
    }

    public List<CNWBean> getAllAVObjectData(){
        List<CNWBean> dataList = new ArrayList<CNWBean>();
        List<Avobject> list = mAvobjectDao.loadAll();
        if(list != null && list.size() > 0){
            for(Avobject item : list){
                LogUtil.DefalutLog("-------------------------------");
                LogUtil.DefalutLog(item.getSerializedString());
                LogUtil.DefalutLog("-------------------------------");
                AVObject object = null;
                try{
                    object = AVObject.parseAVObject(item.getSerializedString());
                }catch (Exception e){
                    LogUtil.DefalutLog("---------------Exception----------------");
                }
                if(object != null){
                    CNWBean bean = new CNWBean();
                    bean.setTable(item.getTableName());
                    bean.setTitle(object.getString(AVOUtil.Caricature.name));
                    bean.setCategory(object.getString(AVOUtil.Caricature.category));
                    bean.setTag(object.getString(AVOUtil.Caricature.tag));
                    bean.setUpdate_des(object.getString(AVOUtil.Caricature.update));
                    bean.setAuthor(object.getString(AVOUtil.Caricature.author));
                    bean.setImg_url(object.getString(AVOUtil.Caricature.book_img_url));
                    bean.setRead_url(object.getString(AVOUtil.Caricature.read_url));
                    bean.setSource_url(object.getString(AVOUtil.Caricature.url));
                    bean.setSource_name(object.getString(AVOUtil.Caricature.source_name));
                    bean.setDes(object.getString(AVOUtil.Caricature.des));
                    bean.setType(object.getString(AVOUtil.Caricature.type));
                    bean.setCollected(item.getCollected());
                    bean.setHistory(item.getHistory());
                    bean.setItemId(item.getItemId());
                    bean.setView(item.getView());
                    bean.setUpdateTime(item.getUpdateTime());
                    dataList.add(bean);
                }
            }
        }
        return dataList;
    }
}
