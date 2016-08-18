package com.sdi.castivate;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.sdi.castivate.async.UploadImage;
import com.sdi.castivate.async.UploadImage.OnActionUploadImagePostCompleted;
import com.sdi.castivate.utils.KeyboardUtility;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ThankYouActivity extends Activity implements OnClickListener {
	// casting thank you image submit
	TextView noThanks, submitAddress;
	ImageView back_icon;
	String strNameOptional, strEmailOptional;
	EditText nameOptional, emailIdAddress;
	Context context;
	Activity activity;
	MixpanelAPI mixpanel;
	String latitude, longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = this;
		context = this;
		setContentView(R.layout.thank_you_photo_submit);

		// Coded By Nivetha
		Bundle b = getIntent().getExtras();
		latitude = b.getString("Latitude");
		longitude = b.getString("Longitude");
		encodedImage = b.getString("Image");

		mixpanel = MixpanelAPI.getInstance(context, Library.MIXPANEL_TOKEN);

		nameOptional = (EditText) findViewById(R.id.edit_name);
		nameOptional.requestFocus();
		(new Handler()).postDelayed(new Runnable() {
			//
			public void run() {

				nameOptional.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
				nameOptional.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));

			}
		}, 200);

		emailIdAddress = (EditText) findViewById(R.id.edit_email_address);
		noThanks = (TextView) findViewById(R.id.no_thanks);
		back_icon = (ImageView) findViewById(R.id.back_icon);
		noThanks.setOnClickListener(this);
		back_icon.setOnClickListener(this);
		submitAddress = (TextView) findViewById(R.id.submit_photo_details);
		submitAddress.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_icon:
			finish();
			break;

		case R.id.submit_photo_details:

			String email = emailIdAddress.getText().toString();

			Boolean vaildEmail = Library.emailValidation(emailIdAddress.getText().toString());

			if (!vaildEmail && !email.equals("")) {
				emailIdAddress.setError("Enter the valid email address.");
			} else {

				KeyboardUtility.hideSoftKeyboard(activity);

				strNameOptional = nameOptional.getText().toString().trim();
				strEmailOptional = emailIdAddress.getText().toString().trim();
				if (Network.isNetworkConnected(context)) {
					imageUpdateHandler.sendEmptyMessage(0);
				} else {
					Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
				}
			}

			break;
		case R.id.no_thanks:

			strNameOptional = "";
			strEmailOptional = "";
			KeyboardUtility.hideSoftKeyboard(activity);

			if (Network.isNetworkConnected(context)) {
				imageUpdateHandler.sendEmptyMessage(0);
			} else {
				Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	String image ;
	String encodedImage;

	/* Handler Class for Profile Image Update Service */
	private Handler imageUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// UploadImage uploadImage=new UploadImage(context,
			// CastingScreen.androidUserID, strNameOptional,
			// strEmailOptional,CastingScreen.encodedImage, mixpanel);
			// Coded by Nivetha

			
			if(encodedImage.equals("FavoriteScreen")){
				image = FavoriteScreen.encodedImage;
			}else{
				image = CastingScreen.encodedImage;
			}

			UploadImage uploadImage = new UploadImage(context, CastingScreen.androidUserID, strNameOptional, strEmailOptional, image, mixpanel, latitude,
					longitude);
			
			
//			UploadImage uploadImage = new UploadImage(context, CastingScreen.androidUserID, strNameOptional, strEmailOptional, CastingScreen.encodedImage, mixpanel, latitude,
//					longitude);

			uploadImage.execute();

			uploadImage.setOnUploadActionPostCompleted(new OnActionUploadImagePostCompleted() {

				@Override
				public void onResponse() {

					finish();
					// Toast.makeText(context,"Your picture has been uploaded successfully",
					// 0).show();

				}
			});

		}
	};

	public void onBackPressed() {

	};
}
