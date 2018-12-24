package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.leancloud.AVObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.messi.languagehelper.meinv.CaricatureDetailActivity;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;

/**
 * Created by luli on 10/23/16.
 */

public class RcCaricatureBookshelfItemViewHolder extends RecyclerView.ViewHolder {

    private final View cover;
    private final TextView name;
    private final SimpleDraweeView img;
    private final LinearLayout delete_layout;
    private final ImageView delete_img;
    private Context context;

    public RcCaricatureBookshelfItemViewHolder(View convertView) {
        super(convertView);
        this.context = convertView.getContext();
        cover = (View) convertView.findViewById(R.id.layout_cover);
        name = (TextView) convertView.findViewById(R.id.name);
        img = (SimpleDraweeView) convertView.findViewById(R.id.img);
        delete_layout = (LinearLayout) convertView.findViewById(R.id.delete_layout);
        delete_img = (ImageView) convertView.findViewById(R.id.delete_img);
    }

    public void render(final AVObject mAVObject) {
        delete_layout.setVisibility(View.GONE);
        delete_img.setImageResource(R.drawable.ic_check_white);
        img.setImageURI(mAVObject.getString(AVOUtil.Caricature.book_img_url));
        name.setText( mAVObject.getString(AVOUtil.Caricature.name));
        if(!TextUtils.isEmpty(mAVObject.getString(KeyUtil.DeleteModel))){
            if (mAVObject.getString(KeyUtil.DeleteModel).equals("1")) {
                delete_layout.setVisibility(View.VISIBLE);
            }
        }
        if(!TextUtils.isEmpty(mAVObject.getString(KeyUtil.isNeedDelete))){
            if (mAVObject.getString(KeyUtil.isNeedDelete).equals("1")) {
                delete_img.setImageResource(R.drawable.ic_done);
            }
        }
        delete_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(mAVObject.getString(KeyUtil.isNeedDelete))){
                    if(!"1".equals(mAVObject.getString(KeyUtil.isNeedDelete))){
                        mAVObject.put(KeyUtil.isNeedDelete,"1");
                        delete_img.setImageResource(R.drawable.ic_done);
                    }else {
                        mAVObject.put(KeyUtil.isNeedDelete,"0");
                        delete_img.setImageResource(R.drawable.ic_check_white);
                    }
                }else {
                    mAVObject.put(KeyUtil.isNeedDelete,"1");
                    delete_img.setImageResource(R.drawable.ic_done);
                }
            }
        });
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onItemClick(mAVObject);
            }
        });
    }

    private void onItemClick(AVObject mAVObject){
        Intent intent = new Intent(context, CaricatureDetailActivity.class);
        intent.putExtra(KeyUtil.AVObjectKey, mAVObject.toString());
        intent.putExtra(KeyUtil.ActionbarTitle, mAVObject.getString(AVOUtil.Caricature.name));
        context.startActivity(intent);
    }

}
