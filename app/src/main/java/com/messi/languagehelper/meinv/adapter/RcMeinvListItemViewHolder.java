package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.WebViewActivity;
import com.messi.languagehelper.meinv.bean.SougoItem;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * Created by luli on 10/23/16.
 */

public class RcMeinvListItemViewHolder extends RecyclerView.ViewHolder {

    private TextView des;
    private SimpleDraweeView list_item_img;
    private FrameLayout ad_layout;
    private Context context;

    public RcMeinvListItemViewHolder(View convertView) {
        super(convertView);
        this.context = convertView.getContext();
        des = (TextView) convertView.findViewById(R.id.des);
        list_item_img = (SimpleDraweeView) convertView.findViewById(R.id.list_item_img);
        ad_layout = (FrameLayout) convertView.findViewById(R.id.ad_layout);
    }

    public void render(final SougoItem mAVObject) {
        list_item_img.setVisibility(View.GONE);
        ad_layout.setVisibility(View.GONE);
        final NativeADDataRef mNativeADDataRef = mAVObject.getmNativeADDataRef();
        if(mNativeADDataRef == null){
            NativeExpressADView mADView = mAVObject.getmADView();
            if(mADView != null){
                ad_layout.setVisibility(View.VISIBLE);
                ad_layout.removeAllViews();
                if (mADView.getParent() != null) {
                    ((ViewGroup) mADView.getParent()).removeView(mADView);
                }
                ad_layout.addView(mADView);
                mADView.render();
            }else {
                des.setText(mAVObject.getTitle());
                list_item_img.setAspectRatio((float)mAVObject.getThumb_width() / mAVObject.getThumb_height());
                list_item_img.setVisibility(View.VISIBLE);
                list_item_img.setImageURI(mAVObject.getThumbUrl());
                list_item_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick(mAVObject);
                    }
                });
            }
        }else{
            des.setText(mNativeADDataRef.getTitle()+"  广告");
            list_item_img.setAspectRatio((float) 1.5);
            list_item_img.setVisibility(View.VISIBLE);
            list_item_img.setImageURI(Uri.parse(mNativeADDataRef.getImage()));
            list_item_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean onClicked = mNativeADDataRef.onClicked(v);
                    LogUtil.DefalutLog("onClicked:"+onClicked);
                }
            });
        }
    }

    private void onItemClick(SougoItem mAVObject) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KeyUtil.ActionbarTitle, " ");
        if(!TextUtils.isEmpty(mAVObject.getPic_url())){
            intent.putExtra(KeyUtil.URL, mAVObject.getPic_url());
        }else {
            intent.putExtra(KeyUtil.URL, mAVObject.getThumbUrl());
        }
        context.startActivity(intent);
    }

}
