package com.messi.languagehelper.meinv.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.XFYSAD;

;

/**
 * Created by luli on 10/23/16.
 */

public class RcAdHeaderViewHolder extends RecyclerView.ViewHolder {

    private XFYSAD mXFYSAD;
    public View headerView;

    public RcAdHeaderViewHolder(View itemView, XFYSAD mXFYSAD) {
        super(itemView);
        LogUtil.DefalutLog("RcAdHeaderViewHolder---init");
        this.headerView = itemView;
        this.mXFYSAD = mXFYSAD;
        mXFYSAD.setParentView(headerView);
        if(mXFYSAD != null){
            mXFYSAD.showAD();
        }
    }

}
