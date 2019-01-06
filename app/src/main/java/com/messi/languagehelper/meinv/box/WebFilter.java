package com.messi.languagehelper.meinv.box;


import android.os.Parcel;
import android.os.Parcelable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class WebFilter implements Parcelable {

    @Id
    private long id;
    @Index
    private String name;
    private String ad_filter;
    private String ad_url;
    private String is_show_ad;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAd_filter() {
        return ad_filter;
    }

    public void setAd_filter(String ad_filter) {
        this.ad_filter = ad_filter;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getIs_show_ad() {
        return is_show_ad;
    }

    public void setIs_show_ad(String is_show_ad) {
        this.is_show_ad = is_show_ad;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.ad_filter);
        dest.writeString(this.ad_url);
        dest.writeString(this.is_show_ad);
    }

    public WebFilter() {
    }

    protected WebFilter(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.ad_filter = in.readString();
        this.ad_url = in.readString();
        this.is_show_ad = in.readString();
    }

    public static final Parcelable.Creator<WebFilter> CREATOR = new Parcelable.Creator<WebFilter>() {
        @Override
        public WebFilter createFromParcel(Parcel source) {
            return new WebFilter(source);
        }

        @Override
        public WebFilter[] newArray(int size) {
            return new WebFilter[size];
        }
    };
}
