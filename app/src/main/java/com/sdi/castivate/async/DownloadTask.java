package com.sdi.castivate.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Void, String> {

	private int downloadType = 0;
	
	final int PLACES = 0;

	// Constructor
	public DownloadTask(int type) {
		this.downloadType = type;
	}

	@Override
	protected String doInBackground(String... url) {

		// For storing data from web service
		String data = "";

		try {
			// Fetching the data from web service
			data = downloadUrl(url[0]);
			System.out.println("data " + data);
		} catch (Exception e) {
			Log.d("Background Task", e.toString());
		}
		return data;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(mListener!=null){
			
			
			mListener.onResponse(result,downloadType);
		}
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();
			System.out.println("data " + data);
			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	
	OnActionDownloadPostCompleted mListener;

	public interface OnActionDownloadPostCompleted {
		public abstract void onResponse(String response, int code);

		
	}

	public void setOnActionPostCompleted(OnActionDownloadPostCompleted listener) {
		mListener = listener;
	}

	
}