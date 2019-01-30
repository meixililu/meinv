package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.CaricatureDetailActivity;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * Created by luli on 10/23/16.
 */

public class RcCaricatureHomeListItemViewHolder extends RecyclerView.ViewHolder {

    private final View cover;
    private final TextView name;
    private final LinearLayout ad_layout;
    private final SimpleDraweeView img;
    private Context context;

    public RcCaricatureHomeListItemViewHolder(View convertView) {
        super(convertView);
        this.context = convertView.getContext();
        cover = (View) convertView.findViewById(R.id.layout_cover);
        name = (TextView) convertView.findViewById(R.id.name);
        img = (SimpleDraweeView) convertView.findViewById(R.id.img);
        ad_layout = (LinearLayout) convertView.findViewById(R.id.ad_layout);
    }

    public void render(final CNWBean mAVObject) {
        ad_layout.setVisibility(View.GONE);
        img.setVisibility(View.VISIBLE);
        final NativeADDataRef mNativeADDataRef = mAVObject.getmNativeADDataRef();
        if(mNativeADDataRef == null){
            NativeExpressADView mADView = mAVObject.getmTXADView();
            if(mADView != null){
                img.setVisibility(View.GONE);
                ad_layout.setVisibility(View.VISIBLE);
                ad_layout.removeAllViews();
                if (mADView.getParent() != null) {
                    ((ViewGroup) mADView.getParent()).removeView(mADView);
                }
                ad_layout.addView(mADView);
                mADView.render();
            }else {
                img.setImageURI(mAVObject.getImg_url());
                name.setText( mAVObject.getTitle());
                cover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onItemClick(mAVObject);
                    }
                });
            }
        }else{
            name.setText(mNativeADDataRef.getTitle()+"  广告");
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
        Intent intent = new Intent(context, CaricatureDetailActivity.class);
        intent.putExtra(KeyUtil.ObjectKey, mAVObject);
        intent.putExtra(KeyUtil.ActionbarTitle, mAVObject.getTitle());
        context.startActivity(intent);
    }

}
