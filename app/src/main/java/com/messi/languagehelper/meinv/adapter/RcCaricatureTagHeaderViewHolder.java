package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.leancloud.AVObject;
import com.google.android.flexbox.FlexboxLayout;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.impl.AdapterStringListener;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.ScreenUtil;

import java.util.List;


/**
 * Created by luli on 10/23/16.
 */

public class RcCaricatureTagHeaderViewHolder extends RecyclerView.ViewHolder {

    public View headerView;
    private Context context;
    private FlexboxLayout auto_wrap_layout;
    private List<AVObject> list;
    private AdapterStringListener listener;

    public RcCaricatureTagHeaderViewHolder(View itemView, AdapterStringListener listener) {
        super(itemView);
        context = itemView.getContext();
        this.listener = listener;
        auto_wrap_layout = (FlexboxLayout) itemView.findViewById(R.id.auto_wrap_layout);
        if(list != null){
            setData(list);
        }
    }

    public void setData(List<AVObject> tags){
        this.list = tags;
        auto_wrap_layout.removeAllViews();
        for (AVObject tag : tags){
            auto_wrap_layout.addView(createNewFlexItemTextView(tag));
        }
    }

    private TextView createNewFlexItemTextView(final AVObject book) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setText(book.getString(AVOUtil.SearchHot.name));
        textView.setTextSize(15);
        textView.setTextColor(context.getResources().getColor(R.color.text_black));
        if(book.getString("selected").equals("1")){
            textView.setBackgroundResource(R.drawable.bg_btn_orange);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                book.put("selected","1");
                if(listener != null){
                    listener.OnItemClick(book.getString(AVOUtil.SearchHot.name));
                }
            }
        });
        int padding = ScreenUtil.dip2px(context,5);
        int paddingLeftAndRight = ScreenUtil.dip2px(context,9);
        ViewCompat.setPaddingRelative(textView, paddingLeftAndRight, padding, paddingLeftAndRight, padding);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = ScreenUtil.dip2px(context,5);
        int marginTop = ScreenUtil.dip2px(context,5);
        layoutParams.setMargins(margin, marginTop, margin, 0);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private void reset(){
        for (AVObject tag : list){
            tag.put("selected","0");
        }
    }
}
