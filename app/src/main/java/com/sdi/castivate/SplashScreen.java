package com.sdi.castivate;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.sdi.castivate.utils.Library;

/**
 * Created by Balachandar on 17-Apr-15.
 */

public class SplashScreen extends Activity {
	Context context;
	SharedPreferences sharedpreferences;
	boolean initalScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		context = this;

		checkRate();

		new Handler().postDelayed(new Runnable() {
			public void run() {
				sharedpreferences = getSharedPreferences(Library.MyPREFERENCES,
						Context.MODE_PRIVATE);
				initalScreen = sharedpreferences.getBoolean(
						Library.INITIAL_SCREEN, false);
				if (initalScreen == false) {
					Library.helpOverlayView = true;
					startActivity(new Intent(SplashScreen.this,
							InitialFilterScreen.class));
				} else {
					CastingScreen.isFavScreen = false;

					Intent in = new Intent(SplashScreen.this,
							CastingScreen.class);
					in.putExtra("CalledBy", "Spalah");
					startActivity(in);
					// startActivity(new Intent(SplashScreen.this,
					// CastingScreen.class));
				}
			}
		}, 2000);
	}

	int Ratecount;
	int nwdate;

	public void checkRate() {
		SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);

		Ratecount = prefs.getInt("Ratecount", 0);
		nwdate = prefs.getInt("ndate", 0);
		boolean getRateFlag = prefs.getBoolean(Library.RATEIT_FLAG, false);
		int dateCount = prefs.getInt("dateCount", 0);

		Ratecount++;
		SharedPreferences.Editor editor = getSharedPreferences("my_pref",
				MODE_PRIVATE).edit();
		editor.putInt("Ratecount", Ratecount);
		editor.commit();

		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
				.replace("-", "");
		int datei = Integer.parseInt(date);

		if (datei != nwdate) {
			dateCount++;
			editor.putInt("dateCount", dateCount);
			editor.putInt("ndate", datei);
			editor.commit();

		}else{
			dateCount = 3;
			editor.putInt("dateCount", dateCount);
			editor.commit();
		}
	}
}
