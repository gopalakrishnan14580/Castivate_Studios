package com.sdi.castivate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.sdi.castivate.utils.CastivateWebClient;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;

/**
 * Created by Balachandar on 17-Apr-15.
 */

public class CastingsWebActivity extends Activity {
	Context context;
	private WebView mWebview; 
	ImageView btnBack;
	

	String getUlr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_show);
		context = this;

		if (getIntent() != null && getIntent().hasExtra("getUlr")) {
			getUlr = getIntent().getStringExtra("getUlr");
		}

		mWebview = (WebView) findViewById(R.id.webShow);
		btnBack = (ImageView)findViewById(R.id.btnBack);

		// mWebview = new WebView(this);
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				finish();								
			}
		});

		mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

		// final Activity activity = this;

		mWebview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
			}
		});

		//mWebview.loadUrl(getUlr);
		
		  if (Network.isNetworkConnected(context)) {
			  mWebview.setWebViewClient(new CastivateWebClient(context));
			  mWebview.getSettings().setJavaScriptEnabled(true);
			  mWebview.loadUrl(getUlr);
	        } else {
	            Toast.makeText(context, "Please Turnon Internet to load this page", Toast.LENGTH_LONG).show();
	        }
		// setContentView(mWebview );

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		
	}
	
}
