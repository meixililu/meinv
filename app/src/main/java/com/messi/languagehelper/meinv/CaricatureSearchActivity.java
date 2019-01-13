package com.messi.languagehelper.meinv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.ScreenUtil;
import com.messi.languagehelper.meinv.util.Setings;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.callback.FindCallback;
import cn.leancloud.convertor.ObserverBuilder;

public class CaricatureSearchActivity extends BaseActivity {

    @BindView(R.id.search_et)
    EditText searchEt;
    @BindView(R.id.search_btn)
    FrameLayout searchBtn;
    @BindView(R.id.auto_wrap_layout)
    FlexboxLayout auto_wrap_layout;
    @BindView(R.id.clear_history)
    FrameLayout clearHistory;
    private long lastTime;
    private List<AVObject> words;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmly_search);
        ButterKnife.bind(this);
        init();
        addHistory();
        new QueryTask().execute();
    }

    private void init() {
        hideTitle();
        words = new ArrayList<AVObject>();
        searchEt.requestFocus();
        searchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    if (System.currentTimeMillis() - lastTime > 1000) {
                        search(searchEt.getText().toString());
                    }
                    lastTime = System.currentTimeMillis();
                }
                return false;
            }
        });
    }

    private void addHistory(){
        String history_str = Setings.getSharedPreferences(this).getString(KeyUtil.CaricatureSearchHistory,"");
        if (!TextUtils.isEmpty(history_str)) {
            String[] hiss = history_str.split(",");
            if(hiss.length > 0){
                List<AVObject> avObjects = new ArrayList<AVObject>();
                for (int i=0; i<6; i++){
                    if(i < hiss.length) {
                        if(!TextUtils.isEmpty(hiss[i])){
                            AVObject avobj = new AVObject();
                            avobj.put(AVOUtil.XmlySearchHot.name, hiss[i]);
                            avObjects.add(avobj);
                        }
                    }
                }
                setData(avObjects);
            }
        }
    }

    public void OnItemClick(String item) {
        search(item);
    }


    private class QueryTask extends AsyncTask<Void, Void,  List<AVObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<AVObject> doInBackground(Void... params) {
            AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.CaricatureSearchHot.CaricatureSearchHot);
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
                if(avObject.size() != 0){
                    List<AVObject> avObjects = new ArrayList<AVObject>();
                    for (AVObject object : avObject){
                        boolean isAdd = true;
                        for (AVObject history : words){
                            if(history.getString(AVOUtil.SearchHot.name).equals(
                                    object.getString(AVOUtil.SearchHot.name))){
                                isAdd = false;
                                break;
                            }
                        }
                        if (isAdd) {
                            AVObject avobj = new AVObject();
                            avobj.put(AVOUtil.XmlySearchHot.name, object.getString(AVOUtil.SearchHot.name));
                            avObjects.add(avobj);
                        }
                    }
                    setData(avObjects);
                }
            }
        }
    }

    public void setData(List<AVObject> tags){
        words.addAll(tags);
        for (AVObject tag : tags){
            auto_wrap_layout.addView(createNewFlexItemTextView(tag));
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

    @OnClick({R.id.search_btn, R.id.clear_history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                search(searchEt.getText().toString());
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

    private void search(String quest) {
        if (!TextUtils.isEmpty(quest)) {
            Intent intent = new Intent(this, CaricatureSearchResultActivity.class);
            intent.putExtra(KeyUtil.ActionbarTitle, quest);
            intent.putExtra(KeyUtil.SearchKey, quest);
            startActivity(intent);
            saveHistory(quest);
        }
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
        query.whereEqualTo(AVOUtil.CaricatureSearchHot.name, quest);
        query.findInBackground().subscribe(ObserverBuilder.buildSingleObserver(new FindCallback<AVObject>() {
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
                    object.saveInBackground();
                }
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideIME(searchEt);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideIME(searchEt);
    }

}
