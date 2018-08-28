package com.messi.languagehelper.meinv.db;

import android.content.Context;

import com.avos.avoscloud.AVObject;
import com.messi.languagehelper.meinv.BaseApplication;
import com.messi.languagehelper.meinv.dao.Avobject;
import com.messi.languagehelper.meinv.dao.AvobjectDao;
import com.messi.languagehelper.meinv.dao.DaoSession;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

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


    public long insert(String tableName,AVObject mItem,String key,long collected,long history){
        Avobject entity = new Avobject();
        entity.setTableName(tableName);
        entity.setItemId(mItem.getObjectId());
        entity.setSerializedString(mItem.toString());
        entity.setKey(key);
        entity.setUpdateTime(System.currentTimeMillis());
        entity.setHistory(history);
        entity.setCollected(collected);
        entity.setCeateTime(System.currentTimeMillis());
        return mAvobjectDao.insert(entity);
    }
    public void update(Avobject entity){
        mAvobjectDao.update(entity);
    }

    public void updateOrInsertAVObject(String tableName,AVObject object,String key){
        Avobject mAvobject = findById(tableName,object.getObjectId());
        if(mAvobject != null){
            mAvobject.setSerializedString(object.toString());
            mAvobject.setUpdateTime(System.currentTimeMillis());
            update(mAvobject);
        }else {
            insert(tableName,object,key,0,System.currentTimeMillis());
        }
    }

    public void updateOrInsertAVObject(String tableName,AVObject object,String key,long collected){
        Avobject mAvobject = findById(tableName,object.getObjectId());
        if(mAvobject != null){
            mAvobject.setKey(key);
            mAvobject.setCollected(collected);
            mAvobject.setSerializedString(object.toString());
            mAvobject.setUpdateTime(System.currentTimeMillis());
            update(mAvobject);
        }else {
            insert(tableName,object,key,collected,0);
        }
    }

    public void updateAVObjectCollected(String tableName,AVObject object,long collected){
        Avobject mAvobject = findById(tableName,object.getObjectId());
        if(mAvobject != null){
            mAvobject.setCollected(collected);
            mAvobject.setSerializedString(object.toString());
            update(mAvobject);
        }
    }

    public void updateAVObjectHistory(String tableName,AVObject object,long history){
        Avobject mAvobject = findById(tableName,object.getObjectId());
        if(mAvobject != null){
            mAvobject.setHistory(history);
            mAvobject.setSerializedString(object.toString());
            update(mAvobject);
        }
    }

    public Avobject findById(String tableName, String objectId){
        List<Avobject> history = mAvobjectDao
                .queryBuilder()
                .where(AvobjectDao.Properties.TableName.eq(tableName))
                .where(AvobjectDao.Properties.ItemId.eq(objectId))
                .orderDesc(AvobjectDao.Properties.Id)
                .list();
        if(history != null && history.size() > 0){
            return history.get(0);
        }else {
            return null;
        }
    }

    public Avobject findByKey(String tableName, String key){
        List<Avobject> history = mAvobjectDao
                .queryBuilder()
                .where(AvobjectDao.Properties.TableName.eq(tableName))
                .where(AvobjectDao.Properties.Key.eq(key))
                .orderDesc(AvobjectDao.Properties.Id)
                .list();
        if(history != null && history.size() > 0){
            return history.get(0);
        }else {
            return null;
        }
    }

    public AVObject findByItemId(String tableName, String objectId){
        try {
            Avobject mAvobject = findById(tableName,objectId);
            if (mAvobject != null) {
                return AVObject.parseAVObject(mAvobject.getSerializedString());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AVObject findByItemKey(String tableName, String key){
        try {
            Avobject mAvobject = findByKey(tableName,key);
            if (mAvobject != null) {
                return AVObject.parseAVObject(mAvobject.getSerializedString());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Avobject> getCaricatureList(String tableName,int page,int page_size,
                                            boolean isHistory, boolean isCollected) {
        QueryBuilder<Avobject> qb = mAvobjectDao.queryBuilder();
        qb.where(AvobjectDao.Properties.TableName.eq(tableName));
        if(isCollected){
            qb.where(AvobjectDao.Properties.Collected.ge(100));
        }
        if(isHistory){
            qb.where(AvobjectDao.Properties.History.ge(100));
        }
        qb.offset(page);
        qb.limit(page_size);
        qb.orderDesc(AvobjectDao.Properties.UpdateTime);
        return qb.list();
    }

    public List<AVObject> getCaricaturesList(String tableName,int page,int page_size,
                                             boolean isHistory, boolean isCollected){
        List<AVObject> dataList = new ArrayList<AVObject>();
        try {
            List<Avobject> list = getCaricatureList(tableName,page,page_size,isHistory,isCollected);
            if(list != null && list.size() > 0){
                for(Avobject item : list){
                    AVObject object = AVObject.parseAVObject(item.getSerializedString());
                    if(object != null){
                        dataList.add(object);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public void clearAvobjectCollected(String tableName) {
        QueryBuilder<Avobject> qb = mAvobjectDao.queryBuilder();
        DeleteQuery<Avobject> bd = qb
                .where(AvobjectDao.Properties.TableName.eq(tableName))
                .where(AvobjectDao.Properties.Collected.ge(100))
                .buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    public void clearAvobjectHistory(String tableName) {
        QueryBuilder<Avobject> qb = mAvobjectDao.queryBuilder();
        DeleteQuery<Avobject> bd = qb
                .where(AvobjectDao.Properties.TableName.eq(tableName))
                .where(AvobjectDao.Properties.Collected.le(100))
                .where(AvobjectDao.Properties.History.ge(100))
                .buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
}
