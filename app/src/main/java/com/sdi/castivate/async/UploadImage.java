package com.sdi.castivate.async;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.Library;

public class UploadImage extends AsyncTask<String, Void, Void> {

	ProgressDialog dialog;

	Context context;

	String get = "";
	MixpanelAPI mixpanel;
	String androidUserID, strNameOptional, strEmailOptional;
	String encodedImage;

	String latitude, longitude;

	public UploadImage(Context contexts, String androidUserID, String strNameOptional, String strEmailOptional, String encodedImage, MixpanelAPI mixpanel, String latitude,
			String longitude) {

		this.context = contexts;
		this.androidUserID = androidUserID;
		this.strNameOptional = strNameOptional;
		this.strEmailOptional = strEmailOptional;
		this.encodedImage = encodedImage;
		this.mixpanel = mixpanel;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();

		dialog = new ProgressDialog(context);
		dialog.show();
		dialog.setMessage("Uploading Image...");
		dialog.setCancelable(false);
	}

	@Override
	protected Void doInBackground(String... params) {

		JSONStringer item = null;

		try {
			// userId,Image
			// {"userTokenId":"3b10484355df66599afc32d6652383ed4f4b8ba6429f7821480b505ebe1299djjjjj4111","userDeviceId":"aaabbb"}
			// item = new
			// JSONStringer().object().key("userId").value(androidUserID).key("name").value(strNameOptional).key("email").value(strEmailOptional).key("Image")
			// .value(encodedImage).endObject();

			// Coded by Nivetha

			item = new JSONStringer().object().key("userId").value(androidUserID).key("name").value(strNameOptional).key("email").value(strEmailOptional).key("Image")
					.value(encodedImage).key("lat").value(latitude).key("lang").value(longitude).endObject();

			Log.d("Data", item.toString());
		} catch (JSONException e) {
			e.printStackTrace();

		}

		get = Library.senData(item);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if (dialog.isShowing()) {
			dialog.cancel();
		}
		DebugReportOnLocat.ln("res " + get);
		if (!get.equals("")) {
			if (!get.equals("")) {
				// stop the timer if the imageUpload() method returns true
				JSONObject props = new JSONObject();
				try {
					props.put("User ID", androidUserID);
					props.put("Upload", "Success");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mixpanel.track("Upload photos", props);
				// castingViewNoImage.setVisibility(View.GONE);

				// txtSwipeCastings.setVisibility(View.GONE);
				// final Toast toast = Toast.makeText(getApplicationContext(),
				// "Thank you for submitting your photo, we will post it shortly.",
				// Toast.LENGTH_LONG);
				// toast.show();
				//
				// Handler handler = new Handler();
				// handler.postDelayed(new Runnable() {
				// @Override
				// public void run() {
				// toast.cancel();
				// }
				// }, 7000);

			} else {

				// final Toast toast = Toast.makeText(getApplicationContext(),
				// "Photo upload failed.", Toast.LENGTH_LONG);
				// toast.show();
				JSONObject props = new JSONObject();
				try {
					props.put("User ID", androidUserID);
					props.put("Upload", "Failed");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mixpanel.track("Upload photos", props);
				// Handler handler = new Handler();
				// handler.postDelayed(new Runnable() {
				// @Override
				// public void run() {
				// toast.cancel();
				// }
				// }, 5000);
			}

			if (mListener != null) {

				mListener.onResponse();
			}
		}

	}

	OnActionUploadImagePostCompleted mListener;

	public interface OnActionUploadImagePostCompleted {
		public abstract void onResponse();

	}

	public void setOnUploadActionPostCompleted(OnActionUploadImagePostCompleted listener) {
		mListener = listener;
	}

}