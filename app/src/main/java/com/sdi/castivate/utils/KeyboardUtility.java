package com.sdi.castivate.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtility
{
	public static void hideSoftKeyboard(Activity activity) {
		
		try {
			if(activity==null)return;

		    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		
		} catch (Exception e) {
			DebugReportOnLocat.e(e);
		}
	}

   
}
