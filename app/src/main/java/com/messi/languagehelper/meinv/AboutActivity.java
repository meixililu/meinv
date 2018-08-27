package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.widget.TextView;

import com.messi.languagehelper.meinv.util.Setings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity{

	@BindView(R.id.email_layout) TextView email_layout;
	@BindView(R.id.app_version) TextView app_version;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		ButterKnife.bind(this);
		init();
	}

	private void init() {
		setActionBarTitle(getResources().getString(R.string.title_about));
		app_version.setText(Setings.getVersionName(this));
	}

	@OnClick(R.id.email_layout)
	public void onClick() {
		Setings.contantUs(AboutActivity.this);
	}
	

	
}
