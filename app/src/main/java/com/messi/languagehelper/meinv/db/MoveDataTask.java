package com.messi.languagehelper.meinv.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.messi.languagehelper.meinv.box.BoxHelper;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;

import java.util.List;

public class MoveDataTask {

    public static void moveCaricatureData(final Context mContext){
        SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        if(!sp.getBoolean(KeyUtil.HasMoveCaricatureData,false)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<CNWBean> dataList = DataBaseUtil.getInstance().getAllAVObjectData();
                    LogUtil.DefalutLog("moveCaricatureData---dataList:"+dataList.size());
                    if(dataList.size() > 0){
                        BoxHelper.updateCNWBean(dataList);
                        DataBaseUtil.getInstance().clearAvobject();
                    }
                    LogUtil.DefalutLog("moveCaricatureData finish");
                }
            }).start();

        }
    }
}
