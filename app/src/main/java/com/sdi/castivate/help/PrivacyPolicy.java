package com.sdi.castivate.help;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.sdi.castivate.R;


public class PrivacyPolicy extends Activity{
	WebView privacyPolicy;
	ImageView backArrow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacy_policy);
		  backArrow=(ImageView)findViewById(R.id.back_arrow);
	        backArrow.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                finish();
	            }
	        });
		privacyPolicy = (WebView) findViewById(R.id.webView);
		privacyPolicy.loadUrl("file:///android_asset/Privacy_ Policy.html"); 
	}

}
