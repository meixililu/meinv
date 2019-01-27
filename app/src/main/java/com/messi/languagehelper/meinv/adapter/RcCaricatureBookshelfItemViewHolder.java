package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.messi.languagehelper.meinv.CaricatureDetailActivity;
import com.messi.languagehelper.meinv.MiaosouDetailActivity;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.util.ColorUtil;
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

    public void render(final CNWBean mAVObject) {
        delete_layout.setVisibility(View.GONE);
        delete_img.setImageResource(R.drawable.ic_check_white);
        if(!TextUtils.isEmpty(mAVObject.getImg_url())){
            img.setImageURI(mAVObject.getImg_url());
        }else {
            img.setImageResource(ColorUtil.getRadomColor());
        }
        name.setText( mAVObject.getTitle());
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
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onItemClick(mAVObject);
            }
        });
    }

    private void onItemClick(CNWBean mAVObject){
        if(mAVObject.getSource_name().equals("找漫画")){
            Intent intent = new Intent(context, MiaosouDetailActivity.class);
            intent.putExtra(KeyUtil.AVObjectKey, mAVObject);
            context.startActivity(intent);
        }else {
            Intent intent = new Intent(context, CaricatureDetailActivity.class);
            intent.putExtra(KeyUtil.AVObjectKey, mAVObject);
            intent.putExtra(KeyUtil.ActionbarTitle, mAVObject.getTitle());
            context.startActivity(intent);
        }
    }

}
