package com.messi.languagehelper.meinv;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.google.android.flexbox.FlexboxLayout;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.messi.languagehelper.meinv.util.AVAnalytics;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.JsonParser;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.ScreenUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.messi.languagehelper.meinv.util.XFUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CNSearchActivity extends BaseActivity {

    @BindView(R.id.input_et)
    EditText input_et;
    @BindView(R.id.voice_btn)
    Button voice_btn;
    @BindView(R.id.input_layout)
    LinearLayout input_layout;
    @BindView(R.id.speak_round_layout)
    LinearLayout speak_round_layout;
    @BindView(R.id.record_anim_img)
    ImageView record_anim_img;
    @BindView(R.id.search_novel)
    TextView search_novel;
    @BindView(R.id.search_internet)
    TextView search_internet;
    @BindView(R.id.search_caricature)
    TextView search_caricature;
    @BindView(R.id.record_layout)
    LinearLayout record_layout;
    @BindView(R.id.search_btn)
    FrameLayout search_btn;
    @BindView(R.id.auto_wrap_layout)
    FlexboxLayout auto_wrap_layout;
    @BindView(R.id.hot_wrap_layout)
    FlexboxLayout hot_wrap_layout;
    @BindView(R.id.clear_history)
    FrameLayout clearHistory;

    private SpeechRecognizer recognizer;
    private SharedPreferences sp;
    private String SearchUrl;
    private int position;
    private List<AVObject> caricatureList;
    private List<AVObject> novelList;
    private List<AVObject> searchList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cnsearch_activity);
        ButterKnife.bind(this);
        changeStatusBarTextColor(true);
        setStatusbarColor(R.color.white);
        initData();
    }

    private void initData() {
        caricatureList = new ArrayList<AVObject>();
        novelList = new ArrayList<AVObject>();
        searchList = new ArrayList<AVObject>();
        sp = Setings.getSharedPreferences(this);
        recognizer = SpeechRecognizer.createRecognizer(this, null);
        position = getIntent().getIntExtra(KeyUtil.PositionKey,0);
        setSelectedItem(position);
        input_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    showResult();
                }
                return false;
            }
        });
    }

    private void setSelectedItem(int pos) {
        position = pos;
        resetItems();
        if (position == 2) {
            if(searchList.size() == 0){
                new QueryTask().execute();
            }else {
                setHotData();
            }
            SearchUrl = Setings.UCSearchUrl;
            search_internet.setBackgroundResource(R.drawable.bg_btn_orange_circle);
        } else if (position == 1) {
            if(novelList.size() == 0){
                new QueryTask().execute();
            }else {
                setHotData();
            }
            SearchUrl = Setings.NovelSearchUrl;
            search_novel.setBackgroundResource(R.drawable.bg_btn_orange_circle);
        } else {
            if(caricatureList.size() == 0){
                new QueryTask().execute();
            }else {
                setHotData();
            }
            SearchUrl = Setings.CaricatureSearchUrl;
            search_caricature.setBackgroundResource(R.drawable.bg_btn_orange_circle);
        }
    }

    private void resetItems() {
        search_internet.setBackgroundResource(R.drawable.bg_btn_gray_circle);
        search_novel.setBackgroundResource(R.drawable.bg_btn_gray_circle);
        search_caricature.setBackgroundResource(R.drawable.bg_btn_gray_circle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addHistory();
    }

    private void addHistory(){
        String history_str = Setings.getSharedPreferences(this).getString(KeyUtil.CaricatureSearchHistory,"");
        if (!TextUtils.isEmpty(history_str)) {
            String[] hiss = history_str.split(",");
            if(hiss.length > 0){
                List<AVObject> avObjects = new ArrayList<AVObject>();
                for (int i=0; i<hiss.length; i++){
                    if(!TextUtils.isEmpty(hiss[i])){
                        AVObject avobj = new AVObject();
                        avobj.put(AVOUtil.XmlySearchHot.name, hiss[i]);
                        avObjects.add(avobj);
                    }
                }
                setData(avObjects);
            }
        }
    }

    public void setData(List<AVObject> tags){
        auto_wrap_layout.removeAllViews();
        for (AVObject tag : tags){
            auto_wrap_layout.addView(createNewFlexItemTextView(tag));
        }
    }

    private class QueryTask extends AsyncTask<Void, Void,  List<AVObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<AVObject> doInBackground(Void... params) {
            AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.CaricatureSearchHot.CaricatureSearchHot);
            if (position == 2) {
                query.whereEqualTo(AVOUtil.CaricatureSearchHot.type,"search");
            }else if(position == 1){
                query.whereEqualTo(AVOUtil.CaricatureSearchHot.type,"novel");
            }else {
                query.whereEqualTo(AVOUtil.CaricatureSearchHot.type,"caricature");
            }
            query.orderByAscending(AVOUtil.CaricatureSearchHot.createdAt);
            query.orderByDescending(AVOUtil.CaricatureSearchHot.click_time);
            try {
                return query.find();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<AVObject> avObject) {
            super.onPostExecute(avObject);
            if(avObject != null){
                if (position == 2) {
                    searchList.clear();
                    searchList.addAll(avObject);
                }else if(position == 1){
                    novelList.clear();
                    novelList.addAll(avObject);
                }else {
                    caricatureList.clear();
                    caricatureList.addAll(avObject);
                }
                setHotData();
            }
        }
    }

    public void setHotData(){
        List<AVObject> tags = null;
        if (position == 2) {
            tags = searchList;
        }else if(position == 1){
            tags = novelList;
        }else {
            tags = caricatureList;
        }
        hot_wrap_layout.removeAllViews();
        for (AVObject tag : tags){
            hot_wrap_layout.addView(createNewFlexItemTextView(tag));
        }
    }

    private TextView createNewFlexItemTextView(final AVObject book) {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(book.getString(AVOUtil.XmlySearchHot.name));
        textView.setTextSize(16);
        textView.setTextColor(this.getResources().getColor(R.color.text_black));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnItemClick(book.getString(AVOUtil.XmlySearchHot.name));
            }
        });
        int padding = ScreenUtil.dip2px(this,5);
        int paddingLeftAndRight = ScreenUtil.dip2px(this,9);
        ViewCompat.setPaddingRelative(textView, paddingLeftAndRight, padding, paddingLeftAndRight, padding);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = ScreenUtil.dip2px(this,5);
        int marginTop = ScreenUtil.dip2px(this,5);
        layoutParams.setMargins(margin, marginTop, margin, 0);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public void OnItemClick(String question) {
        String url = SearchUrl.replace("{0}",question);
        toResult(url,question);
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
            ToastUtil.diaplayMesShort(CNSearchActivity.this, err.getErrorDescription());
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
            input_et.append(text.toLowerCase());
            input_et.setSelection(input_et.length());
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

    private void showResult() {
        String question = input_et.getText().toString().trim();
        if (!TextUtils.isEmpty(question) && question.length() > 0) {
            String last = question.substring(question.length() - 1);
            if (",.!;:'，。！‘；：".contains(last)) {
                question = question.substring(0, question.length() - 1);
            }
            input_et.setText(question);
            String url = SearchUrl.replace("{0}",question);
            toResult(url,question);
        }
    }

    private void searchCaricature(String url,String quest) {
        if (!TextUtils.isEmpty(quest)) {
            Intent intent = new Intent(this, CaricatureSearchResultActivity.class);
            intent.putExtra(KeyUtil.ActionbarTitle, quest);
            intent.putExtra(KeyUtil.SearchUrl, url);
            intent.putExtra(KeyUtil.SearchKey, quest);
            startActivity(intent);
        }
    }

    private void toResult(String url,String question) {
        if (position == 2) {
            Intent intent = new Intent(this, WebViewWithCollectedActivity.class);
            intent.putExtra(KeyUtil.URL, url);
            intent.putExtra(KeyUtil.SearchUrl, Setings.UCSearchUrl);
            intent.putExtra(KeyUtil.isHideMic,true);
            intent.putExtra(KeyUtil.IsHideToolbar, true);
            intent.putExtra(KeyUtil.IsReedPullDownRefresh, false);
            intent.putExtra(KeyUtil.IsShowCollectedBtn, false);
            startActivity(intent);
        } else if (position == 1) {
            Intent intent = new Intent(this, NovelResultListActivity.class);
            intent.putExtra(KeyUtil.ActionbarTitle, question);
            intent.putExtra(KeyUtil.SearchKey, question);
            intent.putExtra(KeyUtil.URL, url);
            startActivity(intent);
        } else {
            searchCaricature(url,question);
        }
        saveHistory(question);
    }

    private void saveHistory(String quest){
        StringBuilder sb = new StringBuilder();
        sb.append(quest);
        String history_str = Setings.getSharedPreferences(this).getString(KeyUtil.CaricatureSearchHistory,"");
        if (!TextUtils.isEmpty(history_str)) {
            String[] hiss = history_str.split(",");
            if(hiss.length > 0){
                for (int i=0; i<6; i++){
                    if(i < hiss.length){
                        if(!quest.equals(hiss[i]) && !TextUtils.isEmpty(hiss[i])){
                            sb.append(",");
                            sb.append(hiss[i]);
                        }
                    }
                }
            }
        }
        Setings.saveSharedPreferences(Setings.getSharedPreferences(this),
                KeyUtil.CaricatureSearchHistory,
                sb.toString());
        saveHistoryToServer(quest);
    }

    private void saveHistoryToServer(final String quest){
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkAndSaveData(quest);
            }
        }).start();
    }

    private void checkAndSaveData(final String quest){
        AVQuery<AVObject> query = new AVQuery<>(AVOUtil.CaricatureSearchHot.CaricatureSearchHot);
        if (position == 2) {
            query.whereEqualTo(AVOUtil.CaricatureSearchHot.type,"search");
        }else if(position == 1){
            query.whereEqualTo(AVOUtil.CaricatureSearchHot.type,"novel");
        }else {
            query.whereEqualTo(AVOUtil.CaricatureSearchHot.type,"caricature");
        }
        query.whereEqualTo(AVOUtil.CaricatureSearchHot.name, quest);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size() > 0) {
                    AVObject mAVObject = list.get(0);
                    int times = mAVObject.getInt(AVOUtil.CaricatureSearchHot.click_time);
                    mAVObject.put(AVOUtil.CaricatureSearchHot.click_time,times+1);
                    mAVObject.saveInBackground();
                } else {
                    AVObject object = new AVObject(AVOUtil.CaricatureSearchHot.CaricatureSearchHot);
                    object.put(AVOUtil.CaricatureSearchHot.name, quest);
                    if (position == 2) {
                        object.put(AVOUtil.CaricatureSearchHot.type,"search");
                    }else if(position == 1){
                        object.put(AVOUtil.CaricatureSearchHot.type,"novel");
                    }else {
                        object.put(AVOUtil.CaricatureSearchHot.type,"caricature");
                    }
                    object.saveInBackground();
                    LogUtil.DefalutLog("saveInBackground:"+quest);
                }
            }
        });
    }

    /**
     * 显示转写对话框.
     */
    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    public void showIatDialog() {
        if (recognizer != null) {
            if (!recognizer.isListening()) {
                record_layout.setVisibility(View.VISIBLE);
                input_et.setText("");
                voice_btn.setBackgroundColor(this.getResources().getColor(R.color.none));
                voice_btn.setText(this.getResources().getString(R.string.finish));
                speak_round_layout.setBackgroundResource(R.drawable.round_light_blue_bgl);
                XFUtil.showSpeechRecognizer(this, sp, recognizer,
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
    public void onDestroy() {
        super.onDestroy();
        if (recognizer != null) {
            recognizer.destroy();
            recognizer = null;
        }
    }

    @OnClick({R.id.search_novel, R.id.search_internet, R.id.search_caricature,
            R.id.speak_round_layout,R.id.search_btn,R.id.clear_history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_novel:
                setSelectedItem(1);
                AVAnalytics.onEvent(CNSearchActivity.this, "ksearch_novel");
                break;
            case R.id.search_internet:
                setSelectedItem(2);
                AVAnalytics.onEvent(CNSearchActivity.this, "ksearch_internet");
                break;
            case R.id.search_caricature:
                setSelectedItem(0);
                AVAnalytics.onEvent(CNSearchActivity.this, "ksearch_caricature");
                break;
            case R.id.speak_round_layout:
                CNSearchActivityPermissionsDispatcher.showIatDialogWithPermissionCheck(this);
                AVAnalytics.onEvent(CNSearchActivity.this, "ksearch_speak_btn");
                break;
            case R.id.search_btn:
                showResult();
                AVAnalytics.onEvent(CNSearchActivity.this, "ksearch_search_btn");
                break;
            case R.id.clear_history:
                clearHistory();
                break;
        }
    }

    private void clearHistory(){
        Setings.saveSharedPreferences(Setings.getSharedPreferences(this),
                KeyUtil.CaricatureSearchHistory,
                "");
        auto_wrap_layout.removeAllViews();
        new QueryTask().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CNSearchActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
