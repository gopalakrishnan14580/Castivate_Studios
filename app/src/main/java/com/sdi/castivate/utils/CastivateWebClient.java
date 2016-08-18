package com.sdi.castivate.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class CastivateWebClient extends WebViewClient {
    ProgressDialog pd;
    Context mContext;

    public CastivateWebClient(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        /*pd = new ProgressDialog(mContext);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        
        if(!((Activity)mContext).isDestroyed()){
        	pd.show();
        }
        */
        
        
        
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        view.loadUrl(url);
        return true;

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
       /* if (pd != null) {
            if (pd.isShowing()) {
                pd.cancel();
            }
            }
       */ 
        view.loadUrl("javascript:(function() { "
                + "window.HTMLOUT.setHtml('<html>'+"
                + "document.getElementsByTagName('html')[0].innerHTML+'</html>');})();");

    }
}
