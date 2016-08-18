package com.sdi.castivate.test_gridviews;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sdi.castivate.R;
import com.sdi.castivate.CastingScreen.GetImages;
import com.sdi.castivate.CastingScreen.PostSetNotifSync;
import com.sdi.castivate.model.CastingImagesModel;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.GridView;

public class SampleGridViewActivity extends Activity {

	Context context;
	GridView gv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_gridview_activity);

		context = this;

		gv = (GridView) findViewById(R.id.grid_view);

		if (Network.isNetworkConnected(context)) {
			new GetImages().execute();
		} else {
			Library.showToast(context, "Please check your network connection.");
		}

		
	}

	ArrayList<CastingImagesModel> myListImage;
	ArrayList<String> urls = new ArrayList<String>();
	private ProgressDialog progressBar;

	public class GetImages extends AsyncTask<String, Void, Void> {

		String getImageLinks = "";

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			getImageLinks = getJSONImages();
			System.out.println("getImageLinks:: " + getImageLinks);

			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			// TODO Auto-generated method stub
			if (progressBar != null && progressBar.isShowing())
				progressBar.dismiss();

			myListImage = new ArrayList<CastingImagesModel>();
			JSONArray jArray;
			try {
				jArray = new JSONArray(getImageLinks);
				if (jArray != null) {

					CastingImagesModel detailsModel;

					for (int i = 0; i < jArray.length(); i++) {
						JSONObject json_data = jArray.getJSONObject(i);

						boolean flag = false;
						if (json_data.getInt("fav_flag") == 0) {
							flag = false;
						} else if (json_data.getInt("fav_flag") == 1) {
							flag = true;
						}

						detailsModel = new CastingImagesModel(json_data.getString("casting_image"), json_data.getString("user_name"), json_data.getString("casting_image_thumb"),
								json_data.getInt("image_id"), flag, "");
						myListImage.add(detailsModel);

//						Library.URLS[i] = json_data.getString("casting_image_thumb");
						urls.add(json_data.getString("casting_image_thumb"));
					}

					////// Get
					if (myListImage!= null) {
						for (int i = 0; i < myListImage.size(); i++) {

							System.out.println(i + ",  " + myListImage.get(i).imgThumb);
						}
						
						
						gv.setAdapter(new SampleGridViewAdapter(context,urls));
						gv.setOnScrollListener(new SampleScrollListener(context));
					}

					
					
					
					
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		}
	}

	public String getJSONImages() {
		String result = "";
		// try {
		//
		// HttpClient httpclient = new DefaultHttpClient();
		//
		// HttpGet httpget = new HttpGet(HttpUri.IMAGE_URL);
		//
		// HttpResponse response = httpclient.execute(httpget);.
		double latitude = 11.925078;
		double longitude = 79.764805;

		// 11.925078, 79.764805

		String lat = Double.toString(latitude);
		String lang = Double.toString(longitude);

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lang", lang));

			// Sugumaran
			nameValuePairs.add(new BasicNameValuePair("ds", getDS()));
			nameValuePairs.add(new BasicNameValuePair("userId", Library.androidUserID));

			HttpClient httpclient = new DefaultHttpClient();
			String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");

			String Url = HttpUri.IMAGE_URL + "?" + paramsString;

			HttpGet httpget = new HttpGet(HttpUri.IMAGE_URL + "?" + paramsString);
			HttpResponse response = httpclient.execute(httpget);
			// Execute HTTP Get Request
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				DebugReportOnLocat.ln("result:::" + result);

			} else {

				return "";

			}
		} catch (Exception e) {
			e.printStackTrace();
			DebugReportOnLocat.ln("Error : " + e.getMessage());
		}

		return result;
	}

	public String getDS() {
		String DS = "";
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		System.out.println("Screen Width :" + width);
		System.out.println("Screen Height :" + height);

		String getWH = width + "x" + height;

		if (getWH.equalsIgnoreCase("480x800")) {
			DS = "ap1";
		} else if (getWH.equalsIgnoreCase("720x1280")) {
			DS = "ap2";
		} else if (getWH.equalsIgnoreCase("768x1280")) {
			DS = "ap3";
		} else if ((getWH.equalsIgnoreCase("1080x1920"))/*
														 * ||(getWH.equalsIgnoreCase
														 * ("1920x1080"))
														 */) {
			DS = "ap4";
		} else if (getWH.equalsIgnoreCase("1280x800")) {
			DS = "ap4";
		}

		return DS;
	}
}
