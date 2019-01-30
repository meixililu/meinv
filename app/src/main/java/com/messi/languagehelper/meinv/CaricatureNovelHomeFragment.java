package com.messi.languagehelper.meinv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.messi.languagehelper.meinv.util.AVAnalytics;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.Setings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class CaricatureNovelHomeFragment extends BaseFragment {


    @BindView(R.id.search_btn)
    FrameLayout searchBtn;
    @BindView(R.id.my_awesome_toolbar)
    Toolbar myAwesomeToolbar;
    @BindView(R.id.layout_free_novel)
    FrameLayout layoutFreeNovel;
    @BindView(R.id.layout_rank_novel)
    FrameLayout layoutRankNovel;
    @BindView(R.id.layout_short_novel)
    FrameLayout layoutShortNovel;
    @BindView(R.id.layout_novel_collected)
    FrameLayout layout_novel_collected;
    @BindView(R.id.main_content)
    LinearLayout mainContent;
    Unbinder unbinder;
    private SharedPreferences sp;

    public static CaricatureNovelHomeFragment getInstance() {
        return new CaricatureNovelHomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.caricature_novel_home_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        sp = Setings.getSharedPreferences(getActivity());
        myAwesomeToolbar.setTitle("小说");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.search_btn, R.id.layout_free_novel, R.id.layout_rank_novel,
            R.id.layout_short_novel, R.id.layout_novel_collected})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                toKSearch();
                break;
            case R.id.layout_free_novel:
                toNovelActivity();
                break;
            case R.id.layout_rank_novel:
                toNovelActivity(Setings.NovelRank,"找小说",false,false);
                break;
            case R.id.layout_short_novel:
                toNovelActivity(Setings.NovelShort,"找小说",true,true);
                break;
            case R.id.layout_novel_collected:
                toNovelCollected();
                break;
        }
    }

    private void toNovelActivity() {
        Intent intent = new Intent(getContext(), WebViewForNovelActivity.class);
        intent.putExtra(KeyUtil.URL, getNovelUrl());
        intent.putExtra(KeyUtil.FilterName, "小米小说");
        getContext().startActivity(intent);
        AVAnalytics.onEvent(getActivity(), "caricature_to_free_novel");
    }

    private void toNovelActivity(String url,String filter,boolean isShowCollectedBtn,boolean isNeedWebViewGoback) {
        Intent intent = new Intent(getContext(), WebViewWithCollectedActivity.class);
        intent.putExtra(KeyUtil.URL, url);
        intent.putExtra(KeyUtil.FilterName, filter);
        intent.putExtra(KeyUtil.IsNeedGetFilter, true);
        intent.putExtra(KeyUtil.IsHideToolbar, true);
        intent.putExtra(KeyUtil.isHideMic,true);
        intent.putExtra(KeyUtil.IsReedPullDownRefresh, false);
        intent.putExtra(KeyUtil.IsShowCollectedBtn, isShowCollectedBtn);
        intent.putExtra(KeyUtil.IsNeedWebViewGoback, isNeedWebViewGoback);
        getContext().startActivity(intent);
        AVAnalytics.onEvent(getActivity(), "caricature_to_free_novel");
    }

    private void toKSearch(){
        Intent intent = new Intent(getContext(),CNSearchActivity.class);
        intent.putExtra(KeyUtil.PositionKey,1);
        startActivity(intent);
    }

    private void toNovelCollected(){
        Intent intent = new Intent(getContext(),NovelCollectedActivity.class);
        intent.putExtra(KeyUtil.ActionbarTitle,this.getString(R.string.collect));
        startActivity(intent);
    }

    private String getNovelUrl(){
        return sp.getString(KeyUtil.Lei_Novel,Setings.XMNovel);
    }
}
