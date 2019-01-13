package com.messi.languagehelper.meinv;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVAnalytics;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.JsonParser;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.ShareUtil;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.messi.languagehelper.meinv.util.XFUtil;
import com.messi.languagehelper.meinv.view.VideoEnabledWebChromeClient;
import com.messi.languagehelper.meinv.view.VideoEnabledWebView;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.List;

import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.callback.FindCallback;
import cn.leancloud.convertor.ObserverBuilder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class WebViewWithMicActivity extends BaseActivity implements OnClickListener{

	private final String STATE_RESUME_WINDOW = "resumeWindow";
	private final String STATE_RESUME_POSITION = "resumePosition";
	private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

	private ProgressBar progressdeterminate;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private VideoEnabledWebView mWebView;
	private VideoEnabledWebChromeClient webChromeClient;
	private TextView tap_to_reload;
	private RelativeLayout nonVideoLayout;
	private RelativeLayout videoLayout;
	private LinearLayout speak_round_layout;
	private LinearLayout record_layout;
	private ImageView record_anim_img;
	private Button voice_btn;
    private TextView ad_go_on;
    private RelativeLayout ad_layout;
    private LinearLayout ad_content;

	private SpeechRecognizer recognizer;
	private SharedPreferences mSharedPreferences;

    private String Url;
    private String title;
    private String ShareUrlMsg;
    private int ToolbarBackgroundColor;
    private boolean isReedPullDownRefresh;
    private boolean isHideToolbar;
    private long lastClick;
	private String adFilte;
	private boolean is_need_get_filter;
	private String filter_source_name;

	private boolean mExoPlayerFullscreen = false;
	private boolean isHideMic = false;
	private int mResumeWindow;
	private long mResumePosition;
	private StringBuilder sb;
	private String SearchUrl;
    private String ad_url;
    private NativeExpressADView mTXADView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view_with_mic_activity);
		setStatusbarColor(R.color.white);
		changeStatusBarTextColor(true);
		if (savedInstanceState != null) {
			mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
			mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
			mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
		}
		initData();
		initViews();
	}

	private void initData(){
		sb = new StringBuilder();
		Url = getIntent().getStringExtra(KeyUtil.URL);
        SearchUrl = getIntent().getStringExtra(KeyUtil.SearchUrl);
		title = getIntent().getStringExtra(KeyUtil.ActionbarTitle);
		ShareUrlMsg = getIntent().getStringExtra(KeyUtil.ShareUrlMsg);
		isHideMic = getIntent().getBooleanExtra(KeyUtil.isHideMic,false);
		adFilte = getIntent().getStringExtra(KeyUtil.AdFilter);
		filter_source_name = getIntent().getStringExtra(KeyUtil.FilterName);
		is_need_get_filter = getIntent().getBooleanExtra(KeyUtil.IsNeedGetFilter,false);
		ToolbarBackgroundColor = getIntent().getIntExtra(KeyUtil.ToolbarBackgroundColorKey,0);
		isReedPullDownRefresh = getIntent().getBooleanExtra(KeyUtil.IsReedPullDownRefresh, true);
		isHideToolbar = getIntent().getBooleanExtra(KeyUtil.IsHideToolbar, false);
		LogUtil.DefalutLog("ToolbarBackgroundColor:"+ToolbarBackgroundColor);
		if(ToolbarBackgroundColor != 0){
			setToolbarBackground(ToolbarBackgroundColor);
		}
		if(!TextUtils.isEmpty(title)){
			getSupportActionBar().setTitle(title);
		}
		if (isHideToolbar) {
			getSupportActionBar().hide();
		}
		if(is_need_get_filter){
			getFilter();
		}
	}

	private void initViews(){
		mSharedPreferences = Setings.getSharedPreferences(this);
		recognizer = SpeechRecognizer.createRecognizer(this, null);
		speak_round_layout = (LinearLayout) findViewById(R.id.speak_round_layout);
		record_layout = (LinearLayout) findViewById(R.id.record_layout);
		record_anim_img = (ImageView) findViewById(R.id.record_anim_img);
		voice_btn = (Button) findViewById(R.id.voice_btn);
		nonVideoLayout = (RelativeLayout) findViewById(R.id.nonVideoLayout);
		videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		progressdeterminate = (ProgressBar) findViewById(R.id.progressdeterminate);
		mWebView = (VideoEnabledWebView) findViewById(R.id.refreshable_webview);
		tap_to_reload = (TextView) findViewById(R.id.tap_to_reload);
        ad_go_on = (TextView) findViewById(R.id.ad_go_on);
        ad_layout = (RelativeLayout) findViewById(R.id.ad_layout);
        ad_content = (LinearLayout) findViewById(R.id.ad_content);
		View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);
		setScrollable(mSwipeRefreshLayout);
		mWebView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		mWebView.getSettings().setJavaScriptEnabled(true);//如果访问的页面中有Javascript，则webview必须设置支持Javascript。
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setBlockNetworkImage(false);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
			mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		if(isHideMic){
			speak_round_layout.setVisibility(View.GONE);
		}
        tap_to_reload.setOnClickListener(this);
        ad_go_on.setOnClickListener(this);
		speak_round_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				WebViewWithMicActivityPermissionsDispatcher.showIatDialogWithPermissionCheck(WebViewWithMicActivity.this);
				AVAnalytics.onEvent(WebViewWithMicActivity.this, "WebViewWithMic_speak_btn");
			}
		});

		//当前页面加载
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				LogUtil.DefalutLog("WebViewClient:onPageStarted");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				LogUtil.DefalutLog("failingUrl:"+failingUrl);
				view.loadUrl("");
				if(System.currentTimeMillis() - lastClick < 500){
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							tap_to_reload.setVisibility(View.VISIBLE);
						}
					}, 600);
				}else{
					tap_to_reload.setVisibility(View.VISIBLE);
				}
				LogUtil.DefalutLog("WebViewClient:onReceivedError---"+errorCode);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mSwipeRefreshLayout.setRefreshing(false);
				hideAd(view);
				LogUtil.DefalutLog("WebViewClient:onPageFinished");
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.DefalutLog(url);
				if(url.contains("openapp.jdmobile") || url.contains("taobao")){
					Uri uri = Uri.parse(url);
					view.loadUrl("");
					ADUtil.toAdActivity(WebViewWithMicActivity.this,uri);
					WebViewWithMicActivity.this.finish();
					return true;
				}else if(url.contains("bilibili:")){
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                LogUtil.DefalutLog("ad_url:"+ad_url);
                LogUtil.DefalutLog(url);
                int action = getAdAction(url);
                if(action == 1){
                    return new WebResourceResponse(null, null, null);
                }else if(action == 2){
                    showAD();
                    return new WebResourceResponse(null, null, null);
                }
                return super.shouldInterceptRequest(view, url);
            }

			@Override
			public void onReceivedSslError(WebView view,final SslErrorHandler handler, SslError error) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewWithMicActivity.this);
				String message = "SSL Certificate error.";
				switch (error.getPrimaryError()) {
					case SslError.SSL_UNTRUSTED:
						message = "The certificate authority is not trusted.";
						break;
					case SslError.SSL_EXPIRED:
						message = "The certificate has expired.";
						break;
					case SslError.SSL_IDMISMATCH:
						message = "The certificate Hostname mismatch.";
						break;
					case SslError.SSL_NOTYETVALID:
						message = "The certificate is not yet valid.";
						break;
				}
				message += " Do you want to continue anyway?";

				builder.setTitle("SSL Certificate Error");
				builder.setMessage(message);
				builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.proceed();
					}
				});
				builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.cancel();
					}
				});
				final AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, mWebView) // See all available constructors...
		{
			// Subscribe to standard events, such as onProgressChanged()...
			@Override
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 100) {
					progressdeterminate.setVisibility(View.GONE);
					mSwipeRefreshLayout.setRefreshing(false);
					LogUtil.DefalutLog("WebViewClient:newProgress == 100");
				} else {
					if (progressdeterminate.getVisibility() == View.GONE)
						progressdeterminate.setVisibility(View.VISIBLE);
					progressdeterminate.setProgress(progress);
				}
				super.onProgressChanged(view, progress);
			}
		};
		webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
		   @Override
		   public void toggledFullscreen(boolean fullscreen) {
			   // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
			   if (fullscreen) {
				   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				   WindowManager.LayoutParams attrs = getWindow().getAttributes();
				   attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
				   attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
				   getWindow().setAttributes(attrs);
				   if (Build.VERSION.SDK_INT >= 14) {
					   //noinspection all
					   getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
				   }
			   } else {
				   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				   WindowManager.LayoutParams attrs = getWindow().getAttributes();
				   attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
				   attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
				   getWindow().setAttributes(attrs);
				   if (Build.VERSION.SDK_INT >= 14) {
					   //noinspection all
					   getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
				   }
			   }

		   }});
		mWebView.setWebChromeClient(webChromeClient);
		
		mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, 
	            R.color.holo_green_light, 
	            R.color.holo_orange_light, 
	            R.color.holo_red_light);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mWebView.reload();
			}
		});
		if(!isReedPullDownRefresh){
			mSwipeRefreshLayout.setEnabled(false);
		}
		mWebView.loadUrl(Url);
	}

    private void showAD(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadTXAD();
            }
        });
    }

    private void loadTXAD() {
        ad_layout.setVisibility(View.VISIBLE);
        ad_content.removeAllViews();
        TXADUtil.showCDTZX(this, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {
                LogUtil.DefalutLog(adError.getErrorMsg());
                ad_layout.setVisibility(View.GONE);
            }

            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                LogUtil.DefalutLog("onADLoaded");
                if (list != null && list.size() > 0) {
                    ad_layout.setVisibility(View.VISIBLE);
                    if (mTXADView != null) {
                        mTXADView.destroy();
                    }
                    ad_content.removeAllViews();
                    mTXADView = list.get(0);
                    ad_content.addView(mTXADView);
                    mTXADView.render();
                }

            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                nativeExpressADView.render();
                LogUtil.DefalutLog("onRenderFail");
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onRenderSuccess");
            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADExposure");
            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADClicked");
            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADClosed");
            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADLeftApplication");
            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADOpenOverlay");
            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADCloseOverlay");
            }
        });
    }

	private void getFilter(){
		if(!TextUtils.isEmpty(filter_source_name)){
			try {
				AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.AdFilter.AdFilter);
				query.whereEqualTo(AVOUtil.AdFilter.name, filter_source_name);
				query.findInBackground().subscribe(ObserverBuilder.buildSingleObserver(new FindCallback<AVObject>() {
					@Override
					public void done(List<AVObject> list, AVException e) {
						if(list != null && list.size() > 0){
							AVObject mAVObject = list.get(0);
							if(mAVObject != null){
								adFilte = mAVObject.getString(AVOUtil.AdFilter.filter);
                                ad_url = mAVObject.getString(AVOUtil.AdFilter.ad_url);
                                LogUtil.DefalutLog("adFilte:"+adFilte+"--"+"ad_url:"+ad_url);
								hideAd(mWebView);
							}
						}
					}
				}));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	RecognizerListener recognizerListener = new RecognizerListener() {
		@Override
		public void onBeginOfSpeech() {
			LogUtil.DefalutLog("onBeginOfSpeech");
		}

		@Override
		public void onError(SpeechError err) {
			LogUtil.DefalutLog("onError:" + err.getErrorDescription());
			finishRecord();
			ToastUtil.diaplayMesShort(WebViewWithMicActivity.this, err.getErrorDescription());
		}

		@Override
		public void onEndOfSpeech() {
			LogUtil.DefalutLog("onEndOfSpeech");
			finishRecord();
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			LogUtil.DefalutLog("onResult---getResultString:" + results.getResultString());
			String text = JsonParser.parseIatResult(results.getResultString());
			sb.append(text.toLowerCase());
			if (isLast) {
				finishRecord();
				showResult();
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
		}

		@Override
		public void onVolumeChanged(int volume, byte[] arg1) {
			if (volume < 4) {
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_1);
			} else if (volume < 8) {
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_2);
			} else if (volume < 12) {
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_3);
			} else if (volume < 16) {
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_4);
			} else if (volume < 20) {
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_5);
			} else if (volume < 24) {
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_6);
			} else if (volume < 31) {
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_7);
			}
		}
	};

	private void showResult(){
		String question = sb.toString().trim();
		if(!TextUtils.isEmpty(question) && question.length() > 0){
			String last = question.substring(question.length() - 1);
			if (",.!;:'，。！‘；：".contains(last)) {
				question = question.substring(0, question.length() - 1);
			}
			String url = SearchUrl.replace("{0}",question);
			mWebView.loadUrl(url);
		}
	}

	@NeedsPermission(Manifest.permission.RECORD_AUDIO)
	public void showIatDialog() {
		if (recognizer != null) {
			if (!recognizer.isListening()) {
				record_layout.setVisibility(View.VISIBLE);
				sb.setLength(0);
				voice_btn.setBackgroundColor(this.getResources().getColor(R.color.none));
				voice_btn.setText(this.getResources().getString(R.string.finish));
				speak_round_layout.setBackgroundResource(R.drawable.round_light_blue_bgl);
				XFUtil.showSpeechRecognizer(this, mSharedPreferences, recognizer,
						recognizerListener, XFUtil.VoiceEngineMD);
			} else {
				recognizer.stopListening();
				finishRecord();
			}
		}
	}

	/**
	 * finish record
	 */
	private void finishRecord() {
		record_layout.setVisibility(View.GONE);
		record_anim_img.setBackgroundResource(R.drawable.speak_voice_1);
		voice_btn.setText("");
		voice_btn.setBackgroundResource(R.drawable.ic_voice_padded_normal);
		speak_round_layout.setBackgroundResource(R.drawable.round_gray_bgl_old);
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
		outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
		outState.putLong(STATE_RESUME_POSITION, mResumePosition);
		outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
		super.onSaveInstanceState(outState, outPersistentState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		try {
			menu.add(0,0,0,this.getResources().getString(R.string.menu_share)).setIcon(R.drawable.ic_share_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if(!TextUtils.isEmpty(ShareUrlMsg)){
				ShareUtil.shareText(WebViewWithMicActivity.this, ShareUrlMsg);
			}else {
				ShareUtil.shareText(WebViewWithMicActivity.this,mWebView.getTitle() + " (share from:中英互译) " + Url);
			}
			AVAnalytics.onEvent(this, "webview_share_link");
			break;
		}
       return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mWebView != null){
			mWebView.destroy();
		}
		if (recognizer != null) {
			recognizer.destroy();
			recognizer = null;
		}
	}

	private void hideAd(final WebView view){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!TextUtils.isEmpty(adFilte)){
					String[] filters = adFilte.split("#");
					if(filters != null && filters.length > 0){
						for(final String items : filters){
							if(!TextUtils.isEmpty(items)){
								if(items.startsWith("id:") || items.startsWith("class:") ){
									String[] item = items.split(":");
									if(item != null && item.length > 1){
										if(item[0].equals("id")){
											view.loadUrl(
													"javascript:(function() { " +
															"var element = document.getElementById('"+item[1]+"');"
															+ "element.parentNode.removeChild(element);" + "})()");
										}else if(item[0].equals("class")){
											view.loadUrl(
													"javascript:(function() { " +
															"var element = document.getElementsByClassName('"+item[1]+"')[0];"
															+ "element.parentNode.removeChild(element);" + "})()");
										}
									}
								}else {
									view.loadUrl(items);
								}

							}
						}
					}
				}
			}
		},60);
	}

    private int getAdAction(String url){
        int action = 0;
        if(!TextUtils.isEmpty(ad_url)){
            String[] filters = ad_url.split("#");
            if(filters != null && filters.length > 0){
                for(final String item : filters) {
                    String[] fls = item.split(":");
                    if(fls != null && fls.length == 2){
                        if(url.contains(fls[0])){
                            action = Integer.parseInt(fls[1]);
                            break;
                        }
                    }
                }
            }
        }
        return action;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tap_to_reload){
            mWebView.loadUrl(Url);
            tap_to_reload.setVisibility(View.GONE);
            lastClick = System.currentTimeMillis();
        }else if(view.getId() == R.id.ad_go_on){
            ad_layout.setVisibility(View.GONE);
        }
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		WebViewWithMicActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
	}

	@OnShowRationale(Manifest.permission.RECORD_AUDIO)
	void onShowRationale(final PermissionRequest request) {
		new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
				.setTitle("温馨提示")
				.setMessage("需要授权才能使用。")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						request.proceed();
					}
				}).show();
	}

	@OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
	void onPerDenied() {
		ToastUtil.diaplayMesShort(this,"拒绝录音权限，无法使用语音功能！");
	}

}
