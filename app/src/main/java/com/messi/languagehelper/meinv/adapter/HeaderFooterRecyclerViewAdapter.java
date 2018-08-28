package com.messi.languagehelper.meinv.adapter;

import android.support.v7.widget.RecyclerView;

import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter;

/**
 * Created by luli on 10/09/2017.
 */

public abstract class HeaderFooterRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, H, T, F>
        extends HeaderRecyclerViewAdapter<VH, H, T, F> {

    private boolean showHeader = true;

    public void showHeader(){
        this.showHeader = true;
        notifyDataSetChanged();
    }

    public void hideHeader(){
        this.showHeader = false;
        notifyDataSetChanged();
    }

    @Override
    protected boolean hasHeader() {
        return getHeader() != null && showHeader;
    }
}