package com.messi.languagehelper.meinv.box;


import android.os.Parcel;
import android.os.Parcelable;

import com.iflytek.voiceads.NativeADDataRef;
import com.qq.e.ads.nativ.NativeExpressADView;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;

/**
 * Created by luli on 04/05/2017.
 */
@Entity
public class CNWBean implements Parcelable {

    @Id
    private long id;
    @Index
    private String itemId;
    private String table;
    private String category;
    private String type;
    private String title;
    private String author;
    private String tag;
    private String des;
    private String is_free;
    private String sub_title;
    private String update_des;
    private String img_url;
    private String imgs_url;
    private String read_url;
    private String last_read_url;
    private String source_url;
    private String source_name;
    private String json_str;
    private String key;
    private long collected;
    private long history;
    private long view;
    private long ceateTime;
    private long updateTime;
    private String backup1;
    private String backup2;
    private String backup3;

    @Transient
    private String delete_model;
    @Transient
    private String is_need_delete;
    @Transient
    private boolean isAd;
    @Transient
    private boolean isAdShow;
    @Transient
    private NativeADDataRef mNativeADDataRef;
    @Transient
    private NativeExpressADView mTXADView;

    public String getLast_read_url() {
        return last_read_url;
    }

    public void setLast_read_url(String last_read_url) {
        this.last_read_url = last_read_url;
    }

    public String getDelete_model() {
        return delete_model;
    }

    public void setDelete_model(String delete_model) {
        this.delete_model = delete_model;
    }

    public String getIs_need_delete() {
        return is_need_delete;
    }

    public void setIs_need_delete(String is_need_delete) {
        this.is_need_delete = is_need_delete;
    }

    public String getImgs_url() {
        return imgs_url;
    }

    public void setImgs_url(String imgs_url) {
        this.imgs_url = imgs_url;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getIs_free() {
        return is_free;
    }

    public void setIs_free(String is_free) {
        this.is_free = is_free;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getUpdate_des() {
        return update_des;
    }

    public void setUpdate_des(String update_des) {
        this.update_des = update_des;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getRead_url() {
        return read_url;
    }

    public void setRead_url(String read_url) {
        this.read_url = read_url;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getJson_str() {
        return json_str;
    }

    public void setJson_str(String json_str) {
        this.json_str = json_str;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCollected() {
        return collected;
    }

    public void setCollected(long collected) {
        this.collected = collected;
    }

    public long getHistory() {
        return history;
    }

    public void setHistory(long history) {
        this.history = history;
    }

    public long getView() {
        return view;
    }

    public void setView(long view) {
        this.view = view;
    }

    public long getCeateTime() {
        return ceateTime;
    }

    public void setCeateTime(long ceateTime) {
        this.ceateTime = ceateTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getBackup1() {
        return backup1;
    }

    public void setBackup1(String backup1) {
        this.backup1 = backup1;
    }

    public String getBackup2() {
        return backup2;
    }

    public void setBackup2(String backup2) {
        this.backup2 = backup2;
    }

    public String getBackup3() {
        return backup3;
    }

    public void setBackup3(String backup3) {
        this.backup3 = backup3;
    }

    public boolean isAd() {
        return isAd;
    }

    public void setAd(boolean ad) {
        isAd = ad;
    }

    public boolean isAdShow() {
        return isAdShow;
    }

    public void setAdShow(boolean adShow) {
        isAdShow = adShow;
    }

    public NativeADDataRef getmNativeADDataRef() {
        return mNativeADDataRef;
    }

    public void setmNativeADDataRef(NativeADDataRef mNativeADDataRef) {
        this.mNativeADDataRef = mNativeADDataRef;
    }

    public NativeExpressADView getmTXADView() {
        return mTXADView;
    }

    public void setmTXADView(NativeExpressADView mTXADView) {
        this.mTXADView = mTXADView;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.itemId);
        dest.writeString(this.table);
        dest.writeString(this.category);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.tag);
        dest.writeString(this.des);
        dest.writeString(this.is_free);
        dest.writeString(this.sub_title);
        dest.writeString(this.update_des);
        dest.writeString(this.img_url);
        dest.writeString(this.imgs_url);
        dest.writeString(this.read_url);
        dest.writeString(this.source_url);
        dest.writeString(this.source_name);
        dest.writeString(this.json_str);
        dest.writeString(this.key);
        dest.writeLong(this.collected);
        dest.writeLong(this.history);
        dest.writeLong(this.view);
        dest.writeLong(this.ceateTime);
        dest.writeLong(this.updateTime);
        dest.writeString(this.backup1);
        dest.writeString(this.backup2);
        dest.writeString(this.backup3);
        dest.writeString(this.delete_model);
        dest.writeString(this.is_need_delete);
        dest.writeByte(this.isAd ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAdShow ? (byte) 1 : (byte) 0);
    }

    public CNWBean() {
    }

    protected CNWBean(Parcel in) {
        this.id = in.readLong();
        this.itemId = in.readString();
        this.table = in.readString();
        this.category = in.readString();
        this.type = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.tag = in.readString();
        this.des = in.readString();
        this.is_free = in.readString();
        this.sub_title = in.readString();
        this.update_des = in.readString();
        this.img_url = in.readString();
        this.imgs_url = in.readString();
        this.read_url = in.readString();
        this.source_url = in.readString();
        this.source_name = in.readString();
        this.json_str = in.readString();
        this.key = in.readString();
        this.collected = in.readLong();
        this.history = in.readLong();
        this.view = in.readLong();
        this.ceateTime = in.readLong();
        this.updateTime = in.readLong();
        this.backup1 = in.readString();
        this.backup2 = in.readString();
        this.backup3 = in.readString();
        this.delete_model = in.readString();
        this.is_need_delete = in.readString();
        this.isAd = in.readByte() != 0;
        this.isAdShow = in.readByte() != 0;
    }

    public static final Parcelable.Creator<CNWBean> CREATOR = new Parcelable.Creator<CNWBean>() {
        @Override
        public CNWBean createFromParcel(Parcel source) {
            return new CNWBean(source);
        }

        @Override
        public CNWBean[] newArray(int size) {
            return new CNWBean[size];
        }
    };

    @Override
    public String toString() {
        return "CNWBean{" +
                "id=" + id +
                ", itemId='" + itemId + '\'' +
                ", table='" + table + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", tag='" + tag + '\'' +
                ", des='" + des + '\'' +
                ", is_free='" + is_free + '\'' +
                ", sub_title='" + sub_title + '\'' +
                ", update_des='" + update_des + '\'' +
                ", img_url='" + img_url + '\'' +
                ", imgs_url='" + imgs_url + '\'' +
                ", read_url='" + read_url + '\'' +
                ", source_url='" + source_url + '\'' +
                ", source_name='" + source_name + '\'' +
                ", json_str='" + json_str + '\'' +
                ", key='" + key + '\'' +
                ", collected=" + collected +
                ", history=" + history +
                ", view=" + view +
                ", ceateTime=" + ceateTime +
                ", updateTime=" + updateTime +
                ", backup1='" + backup1 + '\'' +
                ", backup2='" + backup2 + '\'' +
                ", backup3='" + backup3 + '\'' +
                ", delete_model='" + delete_model + '\'' +
                ", is_need_delete='" + is_need_delete + '\'' +
                ", isAd=" + isAd +
                ", isAdShow=" + isAdShow +
                ", mNativeADDataRef=" + mNativeADDataRef +
                ", mTXADView=" + mTXADView +
                '}';
    }
}
