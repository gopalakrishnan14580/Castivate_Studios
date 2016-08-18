package com.sdi.castivate.help;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.sdi.castivate.R;


public class TermsOfUse extends Activity{
	WebView termsOfUse;
	ImageView backArrow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_of_use);
		  backArrow=(ImageView)findViewById(R.id.back_arrow);
	        backArrow.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                finish();
	            }
	        });
		termsOfUse = (WebView) findViewById(R.id.terms_of_use_web_view);
		termsOfUse.loadUrl("file:///android_asset/Terms_and_ Conditions.html"); 
	}
	

}

