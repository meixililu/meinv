package com.messi.languagehelper.meinv.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVObject;
import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter;
import com.messi.languagehelper.meinv.R;

/**
 * Created by luli on 10/23/16.
 */

public class RcJokeListAdapter extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder, Object, AVObject, Object> {

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater(parent);
        View characterView = inflater.inflate(R.layout.joke_bdj_list_item, parent, false);
        return new RcJokeListItemViewHolder(characterView);
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        AVObject mAVObject = getItem(position);
        RcJokeListItemViewHolder itemViewHolder = (RcJokeListItemViewHolder)holder;
        itemViewHolder.render(mAVObject);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater(parent);
        View footerView = inflater.inflate(R.layout.footerview, parent, false);
        return new RcLmFooterViewHolder(footerView);
    }

    @Override
    protected void onBindFooterViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindFooterViewHolder(holder, position);
    }

    private LayoutInflater getLayoutInflater(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }

}
