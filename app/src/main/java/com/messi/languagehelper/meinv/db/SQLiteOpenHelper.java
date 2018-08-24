package com.messi.languagehelper.meinv.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.messi.languagehelper.meinv.dao.AvobjectDao;
import com.messi.languagehelper.meinv.dao.DaoMaster;

import org.greenrobot.greendao.database.StandardDatabase;

/**
 * Created by luli on 12/05/2017.
 */

public class SQLiteOpenHelper extends DaoMaster.OpenHelper {

    public SQLiteOpenHelper(Context context, String name) {
        super(context, name);
    }

    public SQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            MigrationHelper.migrate(db, AvobjectDao.class);
        } catch (Exception e) {
            DaoMaster.dropAllTables(new StandardDatabase(db),true);
            DaoMaster.createAllTables(new StandardDatabase(db),true);
            e.printStackTrace();
        }
    }

}
