package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.WebViewWithCollectedActivity;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * Created by luli on 10/23/16.
 */

public class RcNovelListItemViewHolder extends RecyclerView.ViewHolder {

    private final View cover;
    private final TextView name;
    private final TextView des;
    private final SimpleDraweeView img;
    private final LinearLayout ad_layout;
    private final LinearLayout content;
    private final LinearLayout delete_layout;
    private final ImageView delete_img;
    private Context context;

    public RcNovelListItemViewHolder(View convertView) {
        super(convertView);
        this.context = convertView.getContext();
        cover = (View) convertView.findViewById(R.id.layout_cover);
        name = (TextView) convertView.findViewById(R.id.name);
        des = (TextView) convertView.findViewById(R.id.des);
        img = (SimpleDraweeView) convertView.findViewById(R.id.img);
        ad_layout = (LinearLayout) convertView.findViewById(R.id.ad_layout);
        content = (LinearLayout) convertView.findViewById(R.id.content);
        delete_img = (ImageView) convertView.findViewById(R.id.delete_img);
        delete_layout = (LinearLayout) convertView.findViewById(R.id.delete_layout);
    }

    public void render(final CNWBean mAVObject) {
        delete_layout.setVisibility(View.GONE);
        delete_img.setImageResource(R.drawable.ic_check_white);
        ad_layout.setVisibility(View.GONE);
        img.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        final NativeADDataRef mNativeADDataRef = mAVObject.getmNativeADDataRef();
        if(mNativeADDataRef == null){
            NativeExpressADView mADView = mAVObject.getmTXADView();
            if(mADView != null){
                ad_layout.setVisibility(View.VISIBLE);
                ad_layout.removeAllViews();
                if (mADView.getParent() != null) {
                    ((ViewGroup) mADView.getParent()).removeView(mADView);
                }
                ad_layout.addView(mADView);
                mADView.render();
            }else {
                content.setVisibility(View.VISIBLE);
                name.setText( mAVObject.getTitle());
                des.setText(mAVObject.getSub_title());
                cover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onItemClick(mAVObject);
                    }
                });
                if(!TextUtils.isEmpty(mAVObject.getDelete_model())){
                    if (mAVObject.getDelete_model().equals("1")) {
                        delete_layout.setVisibility(View.VISIBLE);
                    }
                }
                if(!TextUtils.isEmpty(mAVObject.getIs_need_delete())){
                    if (mAVObject.getIs_need_delete().equals("1")) {
                        delete_img.setImageResource(R.drawable.ic_done);
                    }
                }
                delete_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!TextUtils.isEmpty(mAVObject.getIs_need_delete())){
                            if(!"1".equals(mAVObject.getIs_need_delete())){
                                mAVObject.setIs_need_delete("1");
                                delete_img.setImageResource(R.drawable.ic_done);
                            }else {
                                mAVObject.setIs_need_delete("0");
                                delete_img.setImageResource(R.drawable.ic_check_white);
                            }
                        }else {
                            mAVObject.setIs_need_delete("1");
                            delete_img.setImageResource(R.drawable.ic_done);
                        }
                    }
                });
            }
        }else{
            content.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            name.setText(mNativeADDataRef.getTitle());
            des.setText(mNativeADDataRef.getSubTitle()+"  广告");
            img.setAspectRatio((float) 0.75);
            img.setImageURI(Uri.parse(mNativeADDataRef.getImage()));
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean onClicked = mNativeADDataRef.onClicked(v);
                    LogUtil.DefalutLog("onClicked:"+onClicked);
                }
            });
        }
    }

    private void onItemClick(CNWBean mAVObject){
        Intent intent = new Intent(context, WebViewWithCollectedActivity.class);
        intent.putExtra(KeyUtil.URL, mAVObject.getRead_url());
        intent.putExtra(KeyUtil.ObjectKey, mAVObject);
        intent.putExtra(KeyUtil.FilterName, mAVObject.getSource_name());
        intent.putExtra(KeyUtil.IsNeedGetFilter, true);
        intent.putExtra(KeyUtil.IsHideToolbar, true);
        intent.putExtra(KeyUtil.isHideMic,true);
        intent.putExtra(KeyUtil.IsReedPullDownRefresh, false);
        intent.putExtra(KeyUtil.IsShowCollectedBtn, false);
        context.startActivity(intent);
    }

}
