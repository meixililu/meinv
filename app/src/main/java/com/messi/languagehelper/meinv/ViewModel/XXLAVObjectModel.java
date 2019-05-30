package com.messi.languagehelper.meinv.ViewModel;

import android.app.Activity;

import com.avos.avoscloud.AVObject;
import com.baidu.mobads.AdView;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.NumberUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.SystemUtil;
import com.qq.e.ads.nativ.NativeExpressADView;

public class XXLAVObjectModel extends XXLRootModel{

    private AVObject mADObject;

    public XXLAVObjectModel(Activity mContext) {
        super(mContext);
    }

    @Override
    public void addXFAD(NativeADDataRef nad) {
        mADObject = new AVObject();
        mADObject.put(KeyUtil.ADKey, nad);
        mADObject.put(KeyUtil.ADIsShowKey, false);
    }

    @Override
    public void addTXAD(NativeExpressADView mADView) {
        mADObject = new AVObject();
        mADObject.put(KeyUtil.TXADView, mADView);
    }

    @Override
    public void addBDAD(AdView adView) {
        int height = (int)(SystemUtil.SCREEN_WIDTH / 2);
        mADObject = new AVObject();
        mADObject.put(KeyUtil.BDADView, adView);
        mADObject.put(KeyUtil.BDADViewHeigh, height);
    }

    @Override
    public void addCSJAD(TTFeedAd ad) {
        mADObject = new AVObject();
        mADObject.put(KeyUtil.CSJADView, ad);
    }

    @Override
    public boolean addAD() {
        if (mADObject != null && avObjects != null && avObjects.size() > 0) {
            int index = avObjects.size() - Setings.page_size + NumberUtil.randomNumberRange(1, 2);
            if (index < 0) {
                index = 1;
            }
            avObjects.add(index, mADObject);
            mAdapter.notifyDataSetChanged();
            mADObject = null;
            return false;
        } else {
            return true;
        }
    }


}
