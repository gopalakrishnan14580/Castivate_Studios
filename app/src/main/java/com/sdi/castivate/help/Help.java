package com.sdi.castivate.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.sdi.castivate.R;
import com.sdi.castivate.utils.CastingsLinkMovementMethod;


/**
 * Created by Balachandar on 17-Apr-15.
 */
public class Help extends Activity{
    ImageView backArrow, help_overlay;
    String strEmailUs=
            "<a href='mailto:team@castivate.com'>Support</a>";
    TextView support,helpOverlay, privacyPolicy, termOfUse;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        context=this;
      
        backArrow=(ImageView)findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        help_overlay = (ImageView) findViewById(R.id.help_overlay);
		help_overlay.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		
				help_overlay.setVisibility(View.GONE);
			
				return false;
			}
		});
		helpOverlay = (TextView) findViewById(R.id.facebook);
		helpOverlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				help_overlay.setAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.slide_left));
				overridePendingTransition(R.anim.pull_in_left,
						R.anim.push_out_right);
				
				help_overlay.setVisibility(View.VISIBLE);
				
			}
		});
		privacyPolicy = (TextView) findViewById(R.id.privacy_policy);
		privacyPolicy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				   startActivity(new Intent(context, PrivacyPolicy.class));
			}
		});
		termOfUse = (TextView) findViewById(R.id.terms_of_use);
		termOfUse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				startActivity(new Intent(context, TermsOfUse.class));
			}
		});
		support = (TextView) findViewById(R.id.support);
		support.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:team@castivate.com"));
    			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
    			context.startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
			}
		});
//		support.setText(Html.fromHtml(strEmailUs),BufferType.SPANNABLE);
//		stripUnderlines(support);
//		support.setTextColor(context.getResources().getColor(
//					R.color.dark_black));
//		support.setMovementMethod(CastingsLinkMovementMethod.getInstance());
		
    }
  
 
  
    @SuppressLint("ResourceAsColor")
	private void stripUnderlines(TextView text_support) {
        Spannable s = (Spannable) text_support.getText();
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        support.setText(s);
        support.setTextColor(context.getResources().getColor(
				R.color.dark_black));
    }
    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
