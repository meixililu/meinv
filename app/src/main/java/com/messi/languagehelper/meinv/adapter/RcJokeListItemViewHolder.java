package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.ImgViewActivity;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.StringUtils;
import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * Created by luli on 10/23/16.
 */

public class RcJokeListItemViewHolder extends RecyclerView.ViewHolder {

    private TextView des;
    private SimpleDraweeView list_item_img;
    private FrameLayout ad_layout;
    private Context context;

    public RcJokeListItemViewHolder(View convertView) {
        super(convertView);
        this.context = convertView.getContext();
        des = (TextView) convertView.findViewById(R.id.des);
        list_item_img = (SimpleDraweeView) convertView.findViewById(R.id.list_item_img);
        ad_layout = (FrameLayout) convertView.findViewById(R.id.ad_layout);
    }

    public void render(final AVObject mAVObject) {
        list_item_img.setVisibility(View.GONE);
        ad_layout.setVisibility(View.GONE);
        des.setVisibility(View.GONE);
        final NativeADDataRef mNativeADDataRef = (NativeADDataRef) mAVObject.get(KeyUtil.ADKey);
        if(mNativeADDataRef == null){
            NativeExpressADView mADView = (NativeExpressADView) mAVObject.get(KeyUtil.TXADView);
            if(mADView != null){
                ad_layout.setVisibility(View.VISIBLE);
                ad_layout.removeAllViews();
                if (mADView.getParent() != null) {
                    ((ViewGroup) mADView.getParent()).removeView(mADView);
                }
                ad_layout.addView(mADView);
                mADView.render();
            }else {
                des.setVisibility(View.VISIBLE);
                list_item_img.setVisibility(View.VISIBLE);
                des.setText(StringUtils.fromHtml(mAVObject.getString(AVOUtil.MeinvDetail.title)));
                list_item_img.setAspectRatio((float)mAVObject.getDouble(AVOUtil.MeinvDetail.ratio));
                list_item_img.setImageURI(mAVObject.getString(AVOUtil.MeinvDetail.img_url));
                list_item_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick(mAVObject);
                    }
                });
            }
        }else{
            des.setVisibility(View.VISIBLE);
            list_item_img.setVisibility(View.VISIBLE);
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

    private void onItemClick(AVObject mAVObject) {
        Intent intent = new Intent(context, ImgViewActivity.class);
        intent.putExtra(KeyUtil.URL, mAVObject.getString(AVOUtil.MeinvDetail.img_url));
        intent.putExtra(KeyUtil.Id, mAVObject.getObjectId());
        intent.putExtra(KeyUtil.Ratio, mAVObject.getDouble(AVOUtil.MeinvDetail.ratio));
        intent.putExtra(KeyUtil.DownloadUrl, mAVObject.getString(AVOUtil.MeinvDetail.img_url));
        context.startActivity(intent);
    }


}
