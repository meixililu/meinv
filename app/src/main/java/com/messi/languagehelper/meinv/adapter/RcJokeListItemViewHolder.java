package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.WebViewActivity;
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
    private LinearLayout layout_cover;
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
                des.setText(StringUtils.fromHtml(mAVObject.getString(AVOUtil.Joke.text)));
                if(mAVObject.getString(AVOUtil.Joke.type).equals("3")){
                    list_item_img.setAspectRatio((float)mAVObject.getDouble(AVOUtil.Joke.ratio));
                    list_item_img.setVisibility(View.VISIBLE);
                    DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                            .setAutoPlayAnimations(true)
                            .setUri(Uri.parse(mAVObject.getString(AVOUtil.Joke.img)))
                            .build();
                    list_item_img.setController(mDraweeController);
                }
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

    private void onItemClick(AVObject mAVObject) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KeyUtil.ActionbarTitle, " ");
        if(!TextUtils.isEmpty(mAVObject.getString(AVOUtil.Joke.img))){
            intent.putExtra(KeyUtil.URL, mAVObject.getString(AVOUtil.Joke.img));
        }else {
            intent.putExtra(KeyUtil.URL, mAVObject.getString(AVOUtil.Joke.source_url));
        }
        context.startActivity(intent);
    }

}
