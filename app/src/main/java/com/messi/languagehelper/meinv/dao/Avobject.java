package com.messi.languagehelper.meinv.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by luli on 04/05/2017.
 */
@Entity
public class Avobject {

    @Id(autoincrement = true)
    private Long id;
    private String tableName;
    private String itemId;
    private String serializedString;
    private String key;
    private long collected;
    private long history;
    private long view;
    private long ceateTime;
    private long updateTime;
    private String backup1;
    private String backup2;
    private String backup3;
    private String backup4;
    private String backup5;
    private String backup6;
    private String backup7;
    private String backup8;
    private String backup9;
    private String backup10;

    @Transient
    private boolean isAd;
    @Transient
    private boolean isAdShow;
//    @Transient
//    private NativeADDataRef mNativeADDataRef;
//    @Transient
//    private NativeExpressADView mTXADView;
    @Generated(hash = 304265623)
    public Avobject(Long id, String tableName, String itemId,
                    String serializedString, String key, long collected, long history,
                    long view, long ceateTime, long updateTime, String backup1,
                    String backup2, String backup3, String backup4, String backup5,
                    String backup6, String backup7, String backup8, String backup9,
                    String backup10) {
        this.id = id;
        this.tableName = tableName;
        this.itemId = itemId;
        this.serializedString = serializedString;
        this.key = key;
        this.collected = collected;
        this.history = history;
        this.view = view;
        this.ceateTime = ceateTime;
        this.updateTime = updateTime;
        this.backup1 = backup1;
        this.backup2 = backup2;
        this.backup3 = backup3;
        this.backup4 = backup4;
        this.backup5 = backup5;
        this.backup6 = backup6;
        this.backup7 = backup7;
        this.backup8 = backup8;
        this.backup9 = backup9;
        this.backup10 = backup10;
    }
    @Generated(hash = 27116751)
    public Avobject() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTableName() {
        return this.tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getItemId() {
        return this.itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getSerializedString() {
        return this.serializedString;
    }
    public void setSerializedString(String serializedString) {
        this.serializedString = serializedString;
    }
    public long getCollected() {
        return this.collected;
    }
    public void setCollected(long collected) {
        this.collected = collected;
    }
    public long getHistory() {
        return this.history;
    }
    public void setHistory(long history) {
        this.history = history;
    }
    public long getView() {
        return this.view;
    }
    public void setView(long view) {
        this.view = view;
    }
    public long getCeateTime() {
        return this.ceateTime;
    }
    public void setCeateTime(long ceateTime) {
        this.ceateTime = ceateTime;
    }
    public long getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    public String getBackup1() {
        return this.backup1;
    }
    public void setBackup1(String backup1) {
        this.backup1 = backup1;
    }
    public String getBackup2() {
        return this.backup2;
    }
    public void setBackup2(String backup2) {
        this.backup2 = backup2;
    }
    public String getBackup3() {
        return this.backup3;
    }
    public void setBackup3(String backup3) {
        this.backup3 = backup3;
    }
    public String getBackup4() {
        return this.backup4;
    }
    public void setBackup4(String backup4) {
        this.backup4 = backup4;
    }
    public String getBackup5() {
        return this.backup5;
    }
    public void setBackup5(String backup5) {
        this.backup5 = backup5;
    }
    public String getBackup6() {
        return this.backup6;
    }
    public void setBackup6(String backup6) {
        this.backup6 = backup6;
    }
    public String getBackup7() {
        return this.backup7;
    }
    public void setBackup7(String backup7) {
        this.backup7 = backup7;
    }
    public String getBackup8() {
        return this.backup8;
    }
    public void setBackup8(String backup8) {
        this.backup8 = backup8;
    }
    public String getBackup9() {
        return this.backup9;
    }
    public void setBackup9(String backup9) {
        this.backup9 = backup9;
    }
    public String getBackup10() {
        return this.backup10;
    }
    public void setBackup10(String backup10) {
        this.backup10 = backup10;
    }
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    




}
