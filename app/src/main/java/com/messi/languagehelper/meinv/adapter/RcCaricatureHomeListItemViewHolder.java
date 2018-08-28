package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.messi.languagehelper.meinv.CaricatureDetailActivity;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;

/**
 * Created by luli on 10/23/16.
 */

public class RcCaricatureHomeListItemViewHolder extends RecyclerView.ViewHolder {

    private final View cover;
    private final TextView name;
    private final SimpleDraweeView img;
    private Context context;

    public RcCaricatureHomeListItemViewHolder(View convertView) {
        super(convertView);
        this.context = convertView.getContext();
        cover = (View) convertView.findViewById(R.id.layout_cover);
        name = (TextView) convertView.findViewById(R.id.name);
        img = (SimpleDraweeView) convertView.findViewById(R.id.img);
    }

    public void render(final AVObject mAVObject) {
        img.setImageURI(mAVObject.getString(AVOUtil.Caricature.book_img_url));
        name.setText( mAVObject.getString(AVOUtil.Caricature.name));
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
