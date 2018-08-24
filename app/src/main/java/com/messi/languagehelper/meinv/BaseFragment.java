package com.messi.languagehelper.meinv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messi.languagehelper.meinv.impl.FragmentProgressbarListener;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.umeng.analytics.MobclickAgent;


public class BaseFragment extends Fragment {
	
	public FragmentProgressbarListener mProgressbarListener;
	public SwipeRefreshLayout mSwipeRefreshLayout;
	public boolean isHasLoadData;
	public boolean misVisibleToUser;
	public boolean isRegisterBus;

	BroadcastReceiver activityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent != null){
				String action = intent.getAction();
				if(!TextUtils.isEmpty(action)){
					if(BaseActivity.UpdateMusicUIToStop.equals(action)){
						updateUI(intent.getStringExtra(KeyUtil.MusicAction));
					}
				}
			}
		}
	};

	public BaseFragment(){}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if(getUserVisibleHint() && !isHasLoadData){
			isHasLoadData = true;
			loadDataOnStart();
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		misVisibleToUser = isVisibleToUser;
		if(getActivity() != null && !isHasLoadData){
			isHasLoadData = true;
			loadDataOnStart();
		}
	}

	public void loadDataOnStart(){
		LogUtil.DefalutLog("loadDataOnStart");
	}

	public void registerBroadcast(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BaseActivity.UpdateMusicUIToStop);
		getActivity().registerReceiver(activityReceiver, intentFilter);
	}

	public void unregisterBroadcast(){
		getActivity().unregisterReceiver(activityReceiver);
	}

	public void updateUI(String music_action){}

	@Override
	public void onStart() {
		super.onStart();
//		if(isRegisterBus){
//			NYBus.get().register(this);
//		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(""); //统计页面，"MainScreen"为页面名称，可自定义
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("");
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		if (isRegisterBus) {
//			NYBus.get().unregister(this);
//		}
	}

	/**
	 * need init beford use
	 */
	protected void initSwipeRefresh(View view){
//		if(mSwipeRefreshLayout == null){
//			mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mswiperefreshlayout);
//			mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright,
//		            R.color.holo_green_light,
//		            R.color.holo_orange_light,
//		            R.color.holo_red_light);
//			mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//				@Override
//				public void onRefresh() {
//					onSwipeRefreshLayoutRefresh();
//				}
//			});
//		}
	}
	
	public void onSwipeRefreshLayoutFinish(){
		if(mSwipeRefreshLayout != null){
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	public void onSwipeRefreshLayoutStart(){
		if(mSwipeRefreshLayout != null){
			mSwipeRefreshLayout.setRefreshing(true);
		}
	}
	
	public void onSwipeRefreshLayoutRefresh(){
	}
	
	
	public void showProgressbar(){
		if(mProgressbarListener != null){
			mProgressbarListener.showProgressbar();
		}
	}
	
	public void hideProgressbar(){
		if(mProgressbarListener != null){
			mProgressbarListener.hideProgressbar();
		}
	}

	public void setmProgressbarListener(FragmentProgressbarListener listener){
		mProgressbarListener = listener;
	}

	protected void toActivity(Class mClass, Bundle bundle) {
		Intent intent = new Intent(getContext(), mClass);
		if (bundle != null) {
			intent.putExtra(KeyUtil.BundleKey, bundle);
		}
		startActivity(intent);
	}

}
