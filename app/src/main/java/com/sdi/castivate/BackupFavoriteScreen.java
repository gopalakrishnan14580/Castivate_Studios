package com.sdi.castivate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;





import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.adapter.FavoriteGridAdapter;
import com.sdi.castivate.location.GPSTracker;
import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.model.FavImagesModel;
import com.sdi.castivate.utils.CastingsLinkMovementMethod;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.GridInterface;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

@SuppressLint({ "InlinedApi", "UseSparseArrays" })
@SuppressWarnings("deprecation")
public class BackupFavoriteScreen extends Activity implements GridInterface {
	Context context;
	ImageView back_arrow, fav_list;
	CheckBox fav_iconNoImage;
	RelativeLayout rel_cast_view, rel_cast_view_new;
	// casting main screen
	TextView textCastingTitleNoImage, textCastingTypeNoImage, textPaidStatusNoImage, textUnionStatusNoImage, textUnionTypeNoImage, textSynopsisNoImage, textAgeRangeNoImage,
			textRoleForGenderNoImage, textRoleForEthnicityNoImage, txtRoleDescriptionNoImage, textSubmissionDetailNoImage, textCastivateNoImage, textDistanceNoImage,
			textCastivate;

	ProgressDialog pDialog;
	String strCity = "", strState = "", roleFav;
	int initialPositionValue = 0;
	// Swipe left and right
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	String userTokenId = "", favriteFlag;
	ArrayList<CastingDetailsModel> favoriteCastings;

	// Sugumaran
	ArrayList<FavImagesModel> favImagesList = null;
	int getDivider;
	int favPos = 0;
	ProgressBar progressView;
	ImageView casting_image;
	GridView grid_view_photos;
	TextView imgGrid;

	public int fPosition;
	int head, tail;
	int page = 0;
	int getTotalCastings;
	int getTotalImages;
	
	int lastPage;
	int totalPages;

	boolean myB = false;
	boolean myBreak = false;
	String swipe = "";
	String bSwipe = "";
	boolean bs = false;

	RelativeLayout rel_image_screen;
	RelativeLayout rel_upload_screen;

	TextView txtName;

	// Coded by Nivetha
	CheckBox imgFav;

	// Coded By Nivetha
	// image upload and camera
	public static byte[] b = new byte[1];
	public static String encodedImage = null;
	private static int RESULT_LOAD_IMAGE = 2000;
	final int CAMERA_CAPTURE = 1000;
	final int IMAGE_MAX_SIZE = 600;
	Button btnGallery, btnCamera, btnCancel;
	Dialog dialogCamera;
	File profileImage;
	String imagePath = "";
	Bitmap imgBitMap;
	String picPath;

	String fromImgGrid = "";

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// FlurryAgent.onStartSession(this, "7XR67SX3652ZF6VCSDPS");
		DebugReportOnLocat.ln("onstart:");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// FlurryAgent.onEndSession(this);
		DebugReportOnLocat.ln("onstop:");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_casting);
		Library.helpOverlayView = false;
		context = this;
		CastingScreen.isFavScreen = false;

		try {
			favoriteCastings = new ArrayList<CastingDetailsModel>();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Sugumaran
		casting_image = (ImageView) findViewById(R.id.casting_image);
		progressView = (ProgressBar) findViewById(R.id.progress_view);

		grid_view_photos = (GridView) findViewById(R.id.grid_view_photos);
		imgGrid = (TextView) findViewById(R.id.imgGrid);

		txtName = (TextView) findViewById(R.id.txtName);

		rel_image_screen = (RelativeLayout) findViewById(R.id.rel_image_screen);
		rel_upload_screen = (RelativeLayout) findViewById(R.id.rel_upload_screen);

		rel_cast_view = (RelativeLayout) findViewById(R.id.rel_info_fav);
		// rel_cast_view.setVisibility(View.INVISIBLE);
		back_arrow = (ImageView) findViewById(R.id.back_arrow);

		back_arrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent favIntent = new Intent(context, CastingScreen.class);
				// favIntent.putExtra("FAV_FLAG", true);
				startActivity(favIntent);
			}
		});

		fav_iconNoImage = (CheckBox) findViewById(R.id.fav_icon_select_no_image);
		fav_iconNoImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				favriteFlag = "0";
				handlerFavoriteSync.sendEmptyMessage(0);
			}
		});
		textCastivate = (TextView) findViewById(R.id.text_castivate);
		rel_cast_view_new = (RelativeLayout) findViewById(R.id.rel_cast_view_new);

		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		rel_cast_view.setOnTouchListener(gestureListener);
		casting_image.setOnTouchListener(gestureListener);

		textCastingTitleNoImage = (TextView) findViewById(R.id.cast_title_no_image);
		textCastingTypeNoImage = (TextView) findViewById(R.id.cast_type_no_image);
		txtRoleDescriptionNoImage = (TextView) findViewById(R.id.role_desc_no_image);
		textPaidStatusNoImage = (TextView) findViewById(R.id.paid_status_no_image);
		textSubmissionDetailNoImage = (TextView) findViewById(R.id.submission_detail_no_image);
		textAgeRangeNoImage = (TextView) findViewById(R.id.age_range_no_image);
		textRoleForEthnicityNoImage = (TextView) findViewById(R.id.ethnicity_no_image);
		textRoleForGenderNoImage = (TextView) findViewById(R.id.gender_no_image);
		textUnionStatusNoImage = (TextView) findViewById(R.id.union_status_no_image);
		textSynopsisNoImage = (TextView) findViewById(R.id.submission_info_no_image);

		// Nivetha

		rel_upload_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uploadImageToBackEnd();
			}
		});

		imgGrid.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progressView.setVisibility(View.GONE);
				imgFav.setVisibility(View.GONE);
				imageGrid();
				fromImgGrid = "yes";
			}
		});

		// Coded by Nivetha
		imgFav = (CheckBox) findViewById(R.id.imgFav);
		imgFav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fromImgGrid = "yes";
				imgFav.setEnabled(false);
				favriteFlag = "0";
				handlerImgFavoriteSync.sendEmptyMessage(0);
			}
		});

		// Call the Images
		handlerCastingImagesSync.sendEmptyMessage(0);
	}

	public void imageGrid() {

		grid_view_photos.setAdapter(null);
		new GetImages().execute();

		if (favImagesList != null && favImagesList.size() > 0) {
			FavoriteGridAdapter adapter = new FavoriteGridAdapter(context, favImagesList, this);
			grid_view_photos.setAdapter(adapter);

			rel_cast_view_new.setVisibility(View.GONE);
			grid_view_photos.setVisibility(View.VISIBLE);
			rel_image_screen.setVisibility(View.GONE);
			rel_upload_screen.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((CastingsLinkMovementMethod) CastingsLinkMovementMethod.getInstance()).setContext(context, false);

	}

	private Handler handlerFavCastingSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(context)) {

				new GetDatas().execute();
			} else {
				Toast.makeText(context, "Internet connection is not available", Toast.LENGTH_SHORT).show();
			}
		}
	};

	public String getFavJSONData() {
		String result = "";
		try {
			// int count = 0;

			HttpClient httpclient = new DefaultHttpClient();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			// http://casting.sdiphp.com/castingNew/public/casting?lat=0.00&lang=0.00
			// &userId=0&birthyear=1990&ethnicity=Caucasian%2CAfrican+American%2CHispanic%2CAsian&
			// gender=female&performancetype=actor%2Cmodel%2Csinger%2Cdancer&page=1

			nameValuePairs.add(new BasicNameValuePair("userId", Library.androidUserID));
			nameValuePairs.add(new BasicNameValuePair("page", String.valueOf(page)));

			String getUrl = HttpUri.GET_FAV + URLEncodedUtils.format(nameValuePairs, "utf-8");
			System.out.println("getUrl >>:  " + getUrl);
			HttpGet httpget = new HttpGet(getUrl);

			DebugReportOnLocat.ln("getFavCast URL " + HttpUri.GET_FAV + URLEncodedUtils.format(nameValuePairs, "utf-8"));

			DebugReportOnLocat.ln("filter casting values:" + URLEncodedUtils.format(nameValuePairs, "utf-8"));
			HttpResponse response = httpclient.execute(httpget);
			// Execute HTTP Get Request
			result = EntityUtils.toString(response.getEntity());

			// if (response.getStatusLine().getStatusCode() == 200) {
			// result = EntityUtils.toString(response.getEntity());
			// DebugReportOnLocat.ln("result:::" + result);
			// } else {

			// return "";
			// [{"enthicity":"Caucasian","age_range":"18-30","gender":"Male","fav_flag":"1","role_id":"696","casting_title":"Holland America Cruise Lines",
			// "casting_type":"Cruise Lines","casting_paid_status":"Paid","casting_union_status":"Non-Union",
			// "role_desc":"Lead M1 pop tenor who has a strong understanding of pop music. Low A to top A, and Bb is a plus.",
			// "casting_union_type":"","casting_submission_detail":"casting@belindaking.com\r\nPlease record and send 3 tracks. "
			// + "Send only links from Youtube or Vimeo. Tracks can be found at:
			// http:\/\/bit.ly\/1HLbKz9",
			// "casting_synopsis":"Come join the cast on the MS Rotterdam Luxury ship.","role_type":"Singer",
			// "ImageRole":"Male_18-30_Caucasian","ageRange":"18-30","role_ethnicity":"Caucasian","role_for":"Male","casting_state":"","casting_city":""}]
			// }
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Error : " + e.getMessage());
		}
		return result;
	}

	InputStream is = null;
	String json = "";
	JSONObject jObj = null;
	JSONArray jArr = null;
	StringBuilder sb;

	String Status;

	private Handler handlerFavoriteSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(BackupFavoriteScreen.this)) {
				postFavoriteCastingSync = new PostFavoriteCastingSync();
				postFavoriteCastingSync.execute();
			} else {
				Toast.makeText(BackupFavoriteScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private PostFavoriteCastingSync postFavoriteCastingSync;

	public class PostFavoriteCastingSync extends AsyncTask<String, Void, Void> {

		@SuppressLint("InlinedApi")
		@Override
		public void onPreExecute() {
			pDialog = new ProgressDialog(BackupFavoriteScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			postFavorite();
			return null;
		}

		@Override
		public void onPostExecute(Void params) {
			if (pDialog.isShowing())
				pDialog.dismiss();

			DebugReportOnLocat.ln("fPosition:::" + fPosition);

			DebugReportOnLocat.ln("list.size():::" + favoriteCastings.size());
			if (CastingScreen.favCount.equals("0")) {
				Intent favUpdate = new Intent(context, CastingScreen.class);

				startActivity(favUpdate);
			} else {
				if (Integer.parseInt(CastingScreen.favCount) < 2) {
					hashMap.clear();
				}

				// Last code here
				if (fPosition < favoriteCastings.size()) {
					favoriteCastings.remove(fPosition);
				}
				if (favoriteCastings != null && favoriteCastings.size() > 0) {

					if (favoriteCastings.size() > 0 && favoriteCastings != null) {
						DebugReportOnLocat.ln("siez " + favoriteCastings.size());
						for (int i = fPosition; i >= 0; i--) {
							int getI = i;
							if (i < favoriteCastings.size()) {
								fPosition = i;
								setAllData(favoriteCastings, fPosition);
								break;
							}
						}
					} else if (favImagesList.size() > 0 && favImagesList != null) {
						DebugReportOnLocat.ln("siez " + favImagesList.size());
						for (int i = favPos; i >= 0; i--) {
							int getI = i;
							if (i < favImagesList.size()) {
								favPos = i;
								showImage();
								break;
							}
						}
					} else {
						Intent favUpdate = new Intent(context, CastingScreen.class);
						startActivity(favUpdate);
					}

				} else if (favImagesList.size() > 0 && favImagesList != null) {
					DebugReportOnLocat.ln("siez " + favImagesList.size());
					for (int i = favPos; i >= 0; i--) {
						int getI = i;
						if (i < favImagesList.size()) {
							favPos = i;
							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);
							showImage();
							break;
						}
					}
				} else {
					Intent favUpdate = new Intent(context, CastingScreen.class);
					startActivity(favUpdate);
				}
				// try {
				// DebugReportOnLocat.ln("fav Postion::" + fPosition);
				// favoriteCastings.remove(fPosition);
				// tail = tail - 1;
				// hashMap.put(page, favoriteCastings);
				// DebugReportOnLocat.ln("tail value:" + tail);
				// DebugReportOnLocat.ln("list value:" +
				// favoriteCastings.size());
				//
				// fav_iconNoImage.setChecked(true);
				// handlerFavCastingSync.sendEmptyMessage(0);
				// textCastingTitleNoImage.setText(Html.fromHtml(favoriteCastings.get(fPosition).castingTitle.toString().trim()));
				// textCastingTypeNoImage.setText(Html.fromHtml(favoriteCastings.get(fPosition).castingType.toString().trim()));
				// textPaidStatusNoImage.setText(favoriteCastings.get(fPosition).castingPaidStatus.toString().trim());
				// strState =
				// favoriteCastings.get(fPosition).state.toString().trim();
				// strCity =
				// favoriteCastings.get(fPosition).country.toString().trim();
				// String cityAndStateName = strCity + ", " + strState;
				//
				// if (strState.equals("") || strCity.equals("")) {
				// cityAndStateName = "Nationwide";
				// }
				// if (cityAndStateName.equals(", ")) {
				// cityAndStateName = "Nationwide";
				// }
				// String html = cityAndStateName + " - " +
				// favoriteCastings.get(fPosition).castingSubmissionDetail.toString().trim();
				// Spannable WordtoSpan = new SpannableString(html);
				// WordtoSpan.setSpan(new
				// android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
				// 0, cityAndStateName.length(),
				// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// WordtoSpan.setSpan(new
				// android.text.style.StyleSpan(android.graphics.Typeface.NORMAL),
				// cityAndStateName.length(), html.length(),
				// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// textSubmissionDetailNoImage.setText(WordtoSpan);
				//
				// ((CastingsLinkMovementMethod)
				// CastingsLinkMovementMethod.getInstance()).setContext(context,
				// false);
				// textSubmissionDetailNoImage.setMovementMethod(CastingsLinkMovementMethod.getInstance());
				// Linkify.addLinks(textSubmissionDetailNoImage, Linkify.ALL);
				//
				// textAgeRangeNoImage.setText("Age " +
				// favoriteCastings.get(fPosition).ageRange.toString().trim());
				// textSynopsisNoImage.setText(favoriteCastings.get(fPosition).castingSynopsis.toString().trim());
				// textRoleForEthnicityNoImage.setText(favoriteCastings.get(fPosition).roleForEthnicity.toString().trim());
				// textUnionStatusNoImage.setText(favoriteCastings.get(fPosition).castingUnionStatus.toString().trim());
				// textRoleForGenderNoImage.setText(favoriteCastings.get(fPosition).roleForGender.toString().trim());
				// txtRoleDescriptionNoImage.setText(favoriteCastings.get(fPosition).roleDescription.toString().trim());
				//
				// // hashMap.remove(page);
				//
				// } catch (IndexOutOfBoundsException e) {
				// // TODO: handle exception
				// }

			}

			// if (CastingScreen.favCount.equals("0")) {
			// Intent favUpdate = new Intent(context, CastingScreen.class);
			//
			// startActivity(favUpdate);
			// } else {
			// try {
			// // if
			// (favoriteCastings.get(newStartValue).favCasting.equals("0")) {
			// // fav_iconNoImage.setChecked(false);
			// // } else {
			// fav_iconNoImage.setChecked(true);
			// // }
			// textCastingTitleNoImage.setText(Html.fromHtml(favoriteCastings.get(fPosition).castingTitle.toString().trim()));
			// textCastingTypeNoImage.setText(Html.fromHtml(favoriteCastings.get(fPosition).castingType.toString().trim()));
			// textPaidStatusNoImage.setText(favoriteCastings.get(fPosition).castingPaidStatus.toString().trim());
			// strState =
			// favoriteCastings.get(fPosition).state.toString().trim();
			// strCity =
			// favoriteCastings.get(fPosition).country.toString().trim();
			// String cityAndStateName = strCity + ", " + strState;
			//
			// if (strState.equals("") || strCity.equals("")) {
			// cityAndStateName = "Nationwide";
			// }
			// if (cityAndStateName.equals(", ")) {
			// cityAndStateName = "Nationwide";
			// }
			// String html = cityAndStateName + " - " +
			// favoriteCastings.get(fPosition).castingSubmissionDetail.toString().trim();
			// Spannable WordtoSpan = new SpannableString(html);
			// WordtoSpan.setSpan(new
			// android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
			// cityAndStateName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			// WordtoSpan.setSpan(new
			// android.text.style.StyleSpan(android.graphics.Typeface.NORMAL),
			// cityAndStateName.length(), html.length(),
			// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			// textSubmissionDetailNoImage.setText(WordtoSpan);
			//
			// Linkify.addLinks(textSubmissionDetailNoImage, Linkify.ALL);
			// textSubmissionDetailNoImage.setMovementMethod(LinkMovementMethod.getInstance());
			// textAgeRangeNoImage.setText("Age " +
			// favoriteCastings.get(fPosition).ageRange.toString().trim());
			// textSynopsisNoImage.setText(favoriteCastings.get(fPosition).castingSynopsis.toString().trim());
			// textRoleForEthnicityNoImage.setText(favoriteCastings.get(fPosition).roleForEthnicity.toString().trim());
			// textUnionStatusNoImage.setText(favoriteCastings.get(fPosition).castingUnionStatus.toString().trim());
			// textRoleForGenderNoImage.setText(favoriteCastings.get(fPosition).roleForGender.toString().trim());
			// txtRoleDescriptionNoImage.setText(favoriteCastings.get(fPosition).roleDescription.toString().trim());
			//
			// } catch (IndexOutOfBoundsException e) {
			// // TODO: handle exception
			// }

			// }

		}

	}

	public void postFavorite() {
		try {
			HttpPost request = new HttpPost(HttpUri.SET_FAV);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			JSONStringer item = null;

			try {
				int fPos = fPosition;
				if (fPosition == -1 && favoriteCastings.size() == 1) {
					fPosition = 0;
				}
				// {"userId":"68","roleId":"77","castingFav":"1","roleEnthicity":"Caucasian","roleAgeRange":"40-50","roleFor":"Female"}
				item = new JSONStringer().object().key("userId").value(Library.androidUserID).key("roleId").value(favoriteCastings.get(fPosition).roleId).key("castingFav")
						.value("0").key("roleEnthicity").value(favoriteCastings.get(fPosition).roleForEthnicity).key("roleAgeRange")
						.value(favoriteCastings.get(fPosition).ageRange).key("roleFor").value(favoriteCastings.get(fPosition).roleForGender).endObject();
				Log.d("Data", item.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			StringEntity entity = null;

			try {
				entity = new StringEntity(item.toString());
				request.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Send request to WCF service
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = null;

			try {
				response = httpClient.execute(request);
				if (response.getStatusLine().getStatusCode() == 200) {

					HttpEntity responseEntity = response.getEntity();

					try {
						is = responseEntity.getContent();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						BufferedReader reader2 = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
						sb = new StringBuilder();
						String line = null;
						while ((line = reader2.readLine()) != null) {
							sb.append(line + "\n");
						}
						is.close();

						json = sb.toString();
						// [{"fav_count":"9","roleFav":"1"}]
						jArr = new JSONArray(json);
						DebugReportOnLocat.ln("json response for fav::" + jArr);

						JSONObject oneObject = jArr.getJSONObject(0); // Pulling
																		// items
																		// from
																		// the
						// array
						CastingScreen.favCount = oneObject.getString("fav_count");
						roleFav = oneObject.getString("roleFav");

						DebugReportOnLocat.ln("json response for submit::" + json);

						// Toast.makeText(context,"registered to the server. user id: "
						// + json,Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.e("Buffer Error", "Error converting result " + e.toString());
					}

				} else {
					Status = null;
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	HashMap<Integer, ArrayList<CastingDetailsModel>> hashMap = new HashMap<Integer, ArrayList<CastingDetailsModel>>();
	boolean bss = false;

	// String swipePos = "";

	void CastingLeftSwipeView() {

		fPosition--;
		if (fPosition >= head && fPosition < tail) {
			setAllData(favoriteCastings, fPosition);
		} else {

			// if(favPos<favImagesList.size()){
			//
			// rel_image_screen.setVisibility(View.VISIBLE);
			// rel_upload_screen.setVisibility(View.VISIBLE);
			// rel_cast_view_new.setVisibility(View.GONE);
			// textCastivate.setVisibility(View.GONE);
			// showImage();
			// }else{
			//
			// }

			page = page - 1;
			if (page >= 0 && page <= lastPage) {

				if (hashMap.containsKey(page)) {
					favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
					fPosition = favoriteCastings.size() - 1;
					head = 0;
					tail = favoriteCastings.size();
					setAllData(favoriteCastings, fPosition);
				} else {
					if (page < totalPages) {
						from_swipe = "left";
						handlerFavCastingSync.sendEmptyMessage(0);
					} else {

						favPos = 0;
						page = lastPage;
						favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
						fPosition = favoriteCastings.size() - 1;
						head = 0;
						tail = favoriteCastings.size();
						if ((fPosition == favoriteCastings.size() - 1 && page == lastPage) || (page == 0 && fPosition == 0)) {
							showText = true;
							from_swipe = "left";
							textCastivate.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							rel_image_screen.setVisibility(View.GONE);
							rel_upload_screen.setVisibility(View.GONE);
							// setAllData(favoriteCastings, fPosition);
						} else {
							setAllData(favoriteCastings, fPosition);
						}

					}

				}
			} else {

				// if (page == 0 && (favoriteCastings == null ||
				// favoriteCastings.size() == 0) && favImagesList.size() > 0 &&
				// favImagesList != null) {
				// rel_image_screen.setVisibility(View.VISIBLE);
				// rel_upload_screen.setVisibility(View.VISIBLE);
				// rel_cast_view_new.setVisibility(View.GONE);
				// textCastivate.setVisibility(View.GONE);
				// favPos = 0;
				// showImage();
				//
				// }else{

				from_swipe = "left";
				page = lastPage;
				favPos = 0;

				if (hashMap.containsKey(page)) {

					// if(favPos<favImagesList.size()){
					//
					// rel_image_screen.setVisibility(View.VISIBLE);
					// rel_upload_screen.setVisibility(View.VISIBLE);
					// rel_cast_view_new.setVisibility(View.GONE);
					// textCastivate.setVisibility(View.GONE);
					// showImage();
					// }else{

					favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
					fPosition = favoriteCastings.size() - 1;
					head = 0;
					tail = favoriteCastings.size();
					if ((fPosition == favoriteCastings.size() - 1 && page == lastPage) || (page == 0 && fPosition == 0)) {
						showText = true;
						from_swipe = "left";
						textCastivate.setVisibility(View.VISIBLE);
						rel_cast_view_new.setVisibility(View.GONE);
						rel_image_screen.setVisibility(View.GONE);
						rel_upload_screen.setVisibility(View.GONE);
						// setAllData(favoriteCastings, fPosition);
					} else {
						setAllData(favoriteCastings, fPosition);
					}
					// }
				} else {
					handlerFavCastingSync.sendEmptyMessage(0);
				}
				// }
			}
			// }
		}

		//
		// fPosition--;
		//
		// int pos = fPosition;
		//
		// if (fPosition >= head && fPosition < tail) {
		// if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
		// == favoriteCastings.size() - 1)) {
		//
		// if (totalPages == 0 && bss == true) {
		// textCastivate.setVisibility(View.VISIBLE);
		// rel_cast_view_new.setVisibility(View.GONE);
		// bSwipe = "left";
		//
		// DebugReportOnLocat.ln("" + pos);
		//
		// } else {
		// setAllData(favoriteCastings, fPosition);
		// bss = true;
		// }
		// } else {
		// setAllData(favoriteCastings, fPosition);
		// }
		//
		// } else {
		//
		// page = page - 1;
		//
		// if (page >= 0 && page <= totalPages) {
		//
		// if (hashMap.containsKey(page)) {
		// favoriteCastings = new
		// ArrayList<CastingDetailsModel>(hashMap.get(page));
		// myB = false;
		// // fPosition = 9;
		// fPosition = favoriteCastings.size() - 1;
		//
		// head = 0;
		// tail = favoriteCastings.size();
		//
		// if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
		// == favoriteCastings.size() - 1)) {
		//
		// textCastivate.setVisibility(View.VISIBLE);
		// rel_cast_view_new.setVisibility(View.GONE);
		// bSwipe = "left";
		//
		// DebugReportOnLocat.ln("" + pos);
		// } else {
		//
		// setAllData(favoriteCastings, fPosition);
		//
		// }
		//
		// } else {
		// fPosition = 9;
		//
		// handlerFavCastingSync.sendEmptyMessage(0);
		// }
		// } else {
		//
		// page = lastPage;
		//
		// if (getTotalCastings < 10) {
		// page = 0;
		//
		// favoriteCastings = new
		// ArrayList<CastingDetailsModel>(hashMap.get(page));
		// myB = false;
		//
		// fPosition = favoriteCastings.size() - 1;
		//
		// head = 0;
		// tail = favoriteCastings.size();
		//
		// if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
		// == favoriteCastings.size() - 1)) {
		//
		// textCastivate.setVisibility(View.VISIBLE);
		// rel_cast_view_new.setVisibility(View.GONE);
		// bSwipe = "left";
		//
		// DebugReportOnLocat.ln("" + pos);
		// } else {
		//
		// setAllData(favoriteCastings, fPosition);
		//
		// }
		//
		// } else {
		// if (hashMap.containsKey(page)) {
		//
		// favoriteCastings = new
		// ArrayList<CastingDetailsModel>(hashMap.get(page));
		// myB = false;
		//
		// fPosition = favoriteCastings.size() - 1;
		//
		// head = 0;
		// tail = favoriteCastings.size();
		//
		// if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
		// == favoriteCastings.size() - 1)) {
		//
		// textCastivate.setVisibility(View.VISIBLE);
		// rel_cast_view_new.setVisibility(View.GONE);
		// bSwipe = "left";
		//
		// DebugReportOnLocat.ln("" + pos);
		// } else {
		//
		// setAllData(favoriteCastings, fPosition);
		//
		// }
		//
		// } else {
		//
		// handlerFavCastingSync.sendEmptyMessage(0);
		//
		// }
		// }
		//
		// }
		//
		// }

	}

	//
	// boolean testB = false;
	//
	// boolean showImage = false;

	String from_swipe = "";
	boolean showText = false;

	void CastingRightSwipeView() {

		fPosition++;
		if (fPosition >= head && fPosition < tail) {
			setAllData(favoriteCastings, fPosition);
		} else {

			// if(favPos<favImagesList.size()){
			//
			// rel_image_screen.setVisibility(View.VISIBLE);
			// rel_upload_screen.setVisibility(View.VISIBLE);
			// rel_cast_view_new.setVisibility(View.GONE);
			// textCastivate.setVisibility(View.GONE);
			// showImage();
			//
			//
			// }else{

			page = page + 1;
			if (hashMap.containsKey(page)) {
				favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
				head = 0;
				fPosition = 0;
				tail = favoriteCastings.size();
				setAllData(favoriteCastings, fPosition);
			} else {
				if (page < totalPages) {
					from_swipe = "right";
					handlerFavCastingSync.sendEmptyMessage(0);
				} else {
					page = 0;
					favPos = 0;
					favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
					head = 0;
					fPosition = 0;
					tail = favoriteCastings.size();

					// if ((page == 0 && position == 0) || (page == totalPages
					// && position == myList.size() - 1)) {
					if ((fPosition == favoriteCastings.size() - 1 && page == lastPage) || (page == 0 && fPosition == 0)) {
						showText = true;
						from_swipe = "right";
						textCastivate.setVisibility(View.VISIBLE);
						rel_cast_view_new.setVisibility(View.GONE);
						rel_image_screen.setVisibility(View.GONE);
						rel_upload_screen.setVisibility(View.GONE);
						// setAllData(favoriteCastings, fPosition);
					} else {
						setAllData(favoriteCastings, fPosition);
					}

				}

			}

			// }
		}

		//
		// fPosition++;
		//
		// if (fPosition >= head && fPosition < tail) {
		//
		// setAllData(favoriteCastings, fPosition);
		// } else {
		//
		// page = page + 1;
		//
		// if (hashMap.containsKey(page)) {
		//
		// favoriteCastings = new
		// ArrayList<CastingDetailsModel>(hashMap.get(page));
		// myB = false;
		//
		// head = 0;
		//
		// fPosition = 0;
		//
		// tail = favoriteCastings.size();
		//
		// if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
		// == favoriteCastings.size() - 1)) {
		//
		// testB = true;
		// bSwipe = "right";
		// setAllData(favoriteCastings, fPosition);
		// } else {
		// setAllData(favoriteCastings, fPosition);
		// }
		//
		// } else {
		//
		// fPosition = -1;
		//
		// if (page < totalPages) {
		//
		// // Here
		// // gdfgfg
		// //page = 0;
		//
		// if (hashMap.containsKey(page)) {
		//
		// favoriteCastings = new
		// ArrayList<CastingDetailsModel>(hashMap.get(page));
		// myB = false;
		// fPosition = 0;
		// head = 0;
		// tail = favoriteCastings.size();
		//
		// if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
		// == favoriteCastings.size() - 1)) {
		//
		// testB = true;
		// bSwipe = "right";
		// setAllData(favoriteCastings, fPosition);
		// } else {
		// setAllData(favoriteCastings, fPosition);
		// }
		//
		// } else {
		// fPosition = 0;
		//
		// handlerFavCastingSync.sendEmptyMessage(0);
		// }
		// } else {
		// // Sugumaran
		// // fPosition = 0;
		//
		// // handlerFavCastingSync.sendEmptyMessage(0);
		//
		// // Here
		// page = 0;
		//
		// if (hashMap.containsKey(page)) {
		//
		// favoriteCastings = new
		// ArrayList<CastingDetailsModel>(hashMap.get(page));
		// myB = false;
		// fPosition = 0;
		// head = 0;
		// tail = favoriteCastings.size();
		//
		// if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
		// == favoriteCastings.size() - 1)) {
		//
		// testB = true;
		// bSwipe = "right";
		// setAllData(favoriteCastings, fPosition);
		// } else {
		// setAllData(favoriteCastings, fPosition);
		// }
		//
		// } else {
		// fPosition = 0;
		//
		// handlerFavCastingSync.sendEmptyMessage(0);
		// }
		//
		// }
		//
		// }
		// }

	}

	boolean isImageShow;

	public void setAllData(ArrayList<CastingDetailsModel> favoriteCastings, int currentPos) {

		fav_iconNoImage.setChecked(true);

		try {
			textCastingTitleNoImage.setText(Html.fromHtml(favoriteCastings.get(currentPos).castingTitle.toString().trim()));
			textCastingTypeNoImage.setText(Html.fromHtml(favoriteCastings.get(currentPos).castingType.toString().trim()));
			textPaidStatusNoImage.setText(favoriteCastings.get(currentPos).castingPaidStatus.toString().trim());
			strState = favoriteCastings.get(currentPos).state.toString().trim();
			strCity = favoriteCastings.get(currentPos).country.toString().trim();
			String cityAndStateName = strCity + ", " + strState;
			if (strState.equals("") && strCity.equals("")) {
				cityAndStateName = "Nationwide";
			} else {
				cityAndStateName = strCity + ", " + strState;
			}
			if (cityAndStateName.equals(", ")) {
				cityAndStateName = "Nationwide";
			}

			if (strCity.equals("") && !strState.equals("")) {
				cityAndStateName = strState;
			} else if (!strCity.equals("") && strState.equals("")) {
				cityAndStateName = strCity;
			}
			String html = cityAndStateName + " - " + favoriteCastings.get(currentPos).castingSubmissionDetail.toString().trim();
			Spannable WordtoSpan = new SpannableString(html);
			WordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, cityAndStateName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			WordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.NORMAL), cityAndStateName.length(), html.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textSubmissionDetailNoImage.setText(WordtoSpan);
			Linkify.addLinks(textSubmissionDetailNoImage, Linkify.ALL);
			((CastingsLinkMovementMethod) CastingsLinkMovementMethod.getInstance()).setContext(context, false);
			textSubmissionDetailNoImage.setMovementMethod(CastingsLinkMovementMethod.getInstance());

			textAgeRangeNoImage.setText("Age " + favoriteCastings.get(currentPos).ageRange.toString().trim());
			textSynopsisNoImage.setText(Html.fromHtml(favoriteCastings.get(currentPos).castingSynopsis.toString().trim()));
			textRoleForEthnicityNoImage.setText(Html.fromHtml(favoriteCastings.get(currentPos).roleForEthnicity.toString().trim()));
			textUnionStatusNoImage.setText(favoriteCastings.get(currentPos).castingUnionStatus.toString().trim());
			textRoleForGenderNoImage.setText(favoriteCastings.get(currentPos).roleForGender.toString().trim());
			txtRoleDescriptionNoImage.setText(favoriteCastings.get(currentPos).roleDescription.toString().trim());

		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		// if (testB == true) {
		//
		// textCastivate.setVisibility(View.VISIBLE);
		// rel_cast_view_new.setVisibility(View.GONE);
		// testB = false;
		// int po = fPosition;
		// DebugReportOnLocat.ln("" + po);
		// }

		// Sugumaran
		// if(currentPos == 1 && isImageShow == false){
		// isImageShow = true;
		// fPosition--;
		// if(favImagesList!=null &&favImagesList.size()!=0){
		// casting_image.setVisibility(View.VISIBLE);
		// rel_cast_view.setVisibility(View.GONE);
		//
		//
		//
		// progressView.setVisibility(View.VISIBLE);
		// Picasso.with(context).load(favImagesList.get(0).casting_image).noFade().into(casting_image,
		// new Callback() {
		// @Override
		// public void onSuccess() {
		// progressView.setVisibility(View.GONE);
		// }
		//
		// @Override
		// public void onError() {
		// // TODO
		// // Auto-generated
		// // method stub
		//
		// }
		// });
		//
		// }
		// }

	}

	public class GetDatas extends AsyncTask<String, Void, Void> {

		String getAll = "";

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Loading Castings...");
			pDialog.show();
			pDialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(String... params) {
			getAll = getFavJSONData();

			return null;
		}

		@SuppressWarnings("unused")
		@Override
		protected void onPostExecute(Void params) {

			if (pDialog.isShowing()) {
				pDialog.dismiss();

				if (getAll != null && !getAll.equals("[]") && !getAll.equals("")) {

					rel_cast_view.setVisibility(View.VISIBLE);
					rel_cast_view_new.setVisibility(View.VISIBLE);
					textCastivate.setVisibility(View.GONE);
					favoriteCastings = new ArrayList<CastingDetailsModel>();
					myB = false;
					try {
						JSONArray jArray = new JSONArray(getAll);

						CastingDetailsModel detailsModel;

						for (int i = 0; i < jArray.length(); i++) {
							JSONObject json_data = jArray.getJSONObject(i);

							detailsModel = new CastingDetailsModel(json_data.getString("role_id"), json_data.getString("casting_title"), json_data.getString("casting_type"),
									json_data.getString("casting_paid_status"), json_data.getString("casting_union_status"), json_data.getString("casting_union_type"),
									json_data.getString("casting_submission_detail"), json_data.getString("casting_synopsis"), "", json_data.getString("role_desc"),
									json_data.getString("ageRange"), json_data.getString("role_for"), json_data.getString("role_ethnicity"), json_data.getString("fav_flag"), "",
									"", "", json_data.getString("casting_state"), json_data.getString("casting_city"));
							favoriteCastings.add(detailsModel);
						}
						hashMap.put(page, favoriteCastings);
						// Sugumaran

						if (from_swipe.equals("right")) {
							head = 0;
							tail = favoriteCastings.size();
							int TAIL = tail;
							fPosition = 0;

							if ((fPosition == favoriteCastings.size() - 1 && page == lastPage) || (page == 0 && fPosition == 0)) {
								showText = true;
								textCastivate.setVisibility(View.VISIBLE);
								rel_cast_view_new.setVisibility(View.GONE);
								rel_image_screen.setVisibility(View.GONE);
								rel_upload_screen.setVisibility(View.GONE);
								// setAllData(favoriteCastings, fPosition);
							} else {
								setAllData(favoriteCastings, fPosition);
							}
						} else if (from_swipe.equals("left")) {

							if (favoriteCastings == null || favoriteCastings.size() == 0) {

								rel_image_screen.setVisibility(View.VISIBLE);
								rel_upload_screen.setVisibility(View.VISIBLE);
								rel_cast_view_new.setVisibility(View.GONE);
								textCastivate.setVisibility(View.GONE);
								favPos = 0;
								showImage();
							} else {

								fPosition = favoriteCastings.size() - 1;
								head = 0;
								tail = favoriteCastings.size();
								if ((fPosition == favoriteCastings.size() - 1 && page == lastPage) || (page == 0 && fPosition == 0)) {
									showText = true;
									textCastivate.setVisibility(View.VISIBLE);
									rel_cast_view_new.setVisibility(View.GONE);
									rel_image_screen.setVisibility(View.GONE);
									rel_upload_screen.setVisibility(View.GONE);
									// setAllData(favoriteCastings, fPosition);
								} else {
									setAllData(favoriteCastings, fPosition);
								}
							}
						} else {

							
							
							if (!CastingScreen.favCount.equals("")) {
								getTotalCastings = Integer.parseInt(CastingScreen.favCount);
								getTotalCastings = getTotalCastings - favImagesList.size();

								if (getTotalCastings > 10) {
									totalPages = (getTotalCastings / 10) + 1;
									lastPage = getTotalCastings / 10;
									int TOTALPAGE = totalPages;

									if (favImagesList != null && favImagesList.size() > 0) {

										int getCasTotal = getTotalCastings;
										int getImgTotal = favImagesList.size();
										

										DebugReportOnLocat.ln("getCasTotal :: " + getCasTotal);
										DebugReportOnLocat.ln("getImgTotal :: " + getImgTotal);

										if (getCasTotal > getImgTotal) {

											getImgPos = getCasTotal / getImgTotal;
											getImgPos = getImgPos - 1;
											if (getImgPos == 0) {
												getImgPos = 1;
											}
											DebugReportOnLocat.ln("getImgPos :: " + getImgPos);

										}
										if (getCasTotal < getImgTotal) {

											getImgPos = getImgTotal / getCasTotal;
											getImgPos = getImgPos - 1;
											if (getImgPos == 0) {
												getImgPos = 1;
											}
											incImgPos = getImgPos;
											DebugReportOnLocat.ln("getImgPos :: " + getImgPos);

										}

									} else {
										DebugReportOnLocat.ln("getCasTotal :: " + favoriteCastings.size());
									}

								} else {
									totalPages = 1;

									int getCasTotal = getTotalCastings;
									int getImgTotal = favImagesList.size();

									DebugReportOnLocat.ln("getCasTotal :: " + getCasTotal);
									DebugReportOnLocat.ln("getImgTotal :: " + getImgTotal);

									if (favImagesList.size() == 1) {

										getImgPos = (getTotalCastings / 2) - 1;
										incImgPos = getImgPos;
										DebugReportOnLocat.ln("getImgPos :: " + getImgPos);

									}
									// else if (favoriteCastings.size() == 1) {
									//
									// getImgPos = (getTotalCastings / 2) - 1;
									// incImgPos = getImgPos;
									// DebugReportOnLocat.ln("getImgPos :: " +
									// getImgPos);
									//
									// }
									else {

										totalPages = (getTotalCastings / 10) + 1;
										lastPage = getTotalCastings / 10;
										int TOTALPAGE = totalPages;

										if (favImagesList != null && favImagesList.size() > 0) {

											if (getCasTotal > getImgTotal) {

												getImgPos = getCasTotal / getImgTotal;
												getImgPos = getImgPos - 1;
												if (getImgPos == 0) {
													getImgPos = 1;
												}
												DebugReportOnLocat.ln("getImgPos :: " + getImgPos);

											}
											if (getCasTotal < getImgTotal) {

												getImgPos = getImgTotal / getCasTotal;
												getImgPos = getImgPos - 1;
												if (getImgPos == 0) {
													getImgPos = 1;
												}
												incImgPos = getImgPos;
												DebugReportOnLocat.ln("getImgPos :: " + getImgPos);

											}

										} else {
											DebugReportOnLocat.ln("getCasTotal :: " + favoriteCastings.size());
										}

									}

								}
							} else {
								getTotalCastings = favImagesList.size();
							}

							head = 0;
							tail = favoriteCastings.size();
							int TAIL = tail;
							fPosition = 0;
							// hashMap.put(page, favoriteCastings);
							setAllData(favoriteCastings, fPosition);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

					// Here

					// int TOTALPAGE = totalPages;
					// DebugReportOnLocat.ln("total::" + getTotalCastings);
					// int TOTALPAGES = totalPages;
					// int LASTPAGE = lastPage;
					// DebugReportOnLocat.ln("page " + page);
					// int TOTAL = getTotalCastings;
					// DebugReportOnLocat.ln("list is " + favoriteCastings);
					// if (getTotalCastings > 10) {
					// if (page == lastPage) {
					// fPosition = favoriteCastings.size() - 1;
					// }
					// }
					// head = 0;
					// tail = favoriteCastings.size();
					// int TAIL = tail;
					// hashMap.put(page, favoriteCastings);
					// int HASHLENGTH = hashMap.size();
					// int PAGEs = page;
					// ArrayList<CastingDetailsModel> LIST = favoriteCastings;
					// int getPos = fPosition;
					// if (fPosition == -1) {
					// fPosition = 0;
					// }
					// int fPOS = fPosition;
					// if (getTotalCastings != 1 && (page == totalPages &&
					// fPosition == favoriteCastings.size() - 1)) {
					// textCastivate.setVisibility(View.VISIBLE);
					// rel_cast_view_new.setVisibility(View.GONE);
					// bSwipe = "left";
					// int pos = fPosition;
					// DebugReportOnLocat.ln("" + pos);
					// } else {
					// int pos = fPosition;
					// if (fPosition >= 1) {
					// fPosition = 0;
					// }
					// setAllData(favoriteCastings, fPosition);
					// }

				} else {

					// Sugumaran

					getTotalCastings = Integer.parseInt(CastingScreen.favCount);
					if (favImagesList != null && getTotalCastings != 0) {
						if (getTotalCastings > favImagesList.size()) {
							getTotalCastings = getTotalCastings - favImagesList.size();
						}
						if (getTotalCastings < favImagesList.size()) {
							getTotalCastings = favImagesList.size() - getTotalCastings;
						}

					}
					if (favImagesList != null && favImagesList.size() > 0) {
						getTotalCastings = favImagesList.size();

						if (favPos < favImagesList.size()) {
							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);
							showImage();
						} else {
							favPos = 0;
							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);
							showImage();
						}

					} else {

						page = 0;
						favPos = 0;
						if (hashMap.containsKey(page)) {

							favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
							myB = false;
							// fPosition = 9;
							fPosition = favoriteCastings.size() - 1;

							head = 0;
							tail = favoriteCastings.size();

							if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition == favoriteCastings.size() - 1)) {

								textCastivate.setVisibility(View.VISIBLE);
								rel_cast_view_new.setVisibility(View.GONE);
								bSwipe = "left";

							} else {

								setAllData(favoriteCastings, fPosition);

							}

						} else {

							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);
							showImage();

						}
					}

				}
			}

			// handlerCastingImagesSync.sendEmptyMessage(0);

		}

	}

	boolean showsImage = false;
	int getImgPos = 0;
	int incImgPos = 0;

	String castSwipe = "";
	String imgSwipe = "";
	class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_right);

					rel_cast_view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));

					castSwipe = "right";
					favNewPos = 0;
					if (textCastivate.getVisibility() == View.VISIBLE && showText == true) {
						showText = false;
						

						rel_image_screen.setVisibility(View.GONE);
						rel_upload_screen.setVisibility(View.GONE);
						rel_cast_view_new.setVisibility(View.VISIBLE);

						if (from_swipe.equals("right")) {
							// fPosition++;
							// Sugu
							incImgPos = incImgPos - getImgPos;

							DebugReportOnLocat.ln("fPosition:: " + fPosition);
							setAllData(favoriteCastings, fPosition);
						} else {

							fPosition++;
							if (fPosition >= head && fPosition < tail) {
								setAllData(favoriteCastings, fPosition);
							} else {
								page = 0;
								if (hashMap.containsKey(page)) {
									favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
									head = 0;
									fPosition = 0;
									tail = favoriteCastings.size();
									DebugReportOnLocat.ln("fPosition:: " + fPosition);
									setAllData(favoriteCastings, fPosition);
								} else {
									DebugReportOnLocat.ln("Paeg " + page + "fPosition:: " + fPosition);
								}
							}

							// CastingRightSwipeView();
						}

					} else {

						if (page == 0 && (favoriteCastings == null || favoriteCastings.size() == 0) && favImagesList.size() > 0 && favImagesList != null) {
							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);
							showImage();
						} else {

							if (getImgPos != 0/* &&fPosition<getImgPos */) {

								int get = fPosition;
								int getimgPo = getImgPos;
								int getFavPo = favPos;
								boolean shImg = showsImage;
								
						
								
								if ((fPosition % getImgPos == 0) && fPosition != 0 && showsImage == false && favPos < favImagesList.size()) {
									rel_image_screen.setVisibility(View.VISIBLE);
									rel_upload_screen.setVisibility(View.VISIBLE);
									rel_cast_view_new.setVisibility(View.GONE);
									textCastivate.setVisibility(View.GONE);
									showsImage = true;
imgSwipe = "right";
									incImgPos = incImgPos + getImgPos;
									DebugReportOnLocat.ln("getImgPos after incre :: " + incImgPos);
									showImage();
								} else {
									
									
									
									
									
									//THis if condition is new
//									if(getTotalCastings <getTotalImages&&getTotalCastings<10 && favPos<favImagesList.size()){
//										rel_image_screen.setVisibility(View.VISIBLE);
//										rel_upload_screen.setVisibility(View.VISIBLE);
//										rel_cast_view_new.setVisibility(View.GONE);
//										textCastivate.setVisibility(View.GONE);
//										showImage();
//									}else{
//										showsImage = false;
//										rel_image_screen.setVisibility(View.GONE);
//										rel_upload_screen.setVisibility(View.GONE);
//										rel_cast_view_new.setVisibility(View.VISIBLE);
//										// showImage = false;
//										CastingRightSwipeView();
//									}
//									if(imgSwipe.equals("left")){
//										imgSwipe = "";
//										showsImage = false;
//										favPos--;
//									}
									
									
//									if(imgSwipe.equals("right")){
//										fPosition++;
//									}
									
									imgSwipe = "";
									showsImage = false;
									rel_image_screen.setVisibility(View.GONE);
									rel_upload_screen.setVisibility(View.GONE);
									rel_cast_view_new.setVisibility(View.VISIBLE);
									// showImage = false;
					//New
									//setAllData(favoriteCastings, fPosition);
									//fPosition--;
									
								
									CastingRightSwipeView();
									
									
								}
							} else {
								imgSwipe = "";
								showsImage = false;
								rel_image_screen.setVisibility(View.GONE);
								rel_upload_screen.setVisibility(View.GONE);
								rel_cast_view_new.setVisibility(View.VISIBLE);
								// showImage = false;
								CastingRightSwipeView();
							}

							// if(page==0){
							//
							// if(getImgPos!=0){
							// if(getImgPos == favPos && showsImage == false){
							// rel_image_screen.setVisibility(View.VISIBLE);
							// rel_upload_screen.setVisibility(View.VISIBLE);
							// rel_cast_view_new.setVisibility(View.GONE);
							// textCastivate.setVisibility(View.GONE);
							// showsImage = true;
							// showImage(getImgPos);
							// }
							// } else {
							// showsImage = false;
							// rel_image_screen.setVisibility(View.GONE);
							// rel_upload_screen.setVisibility(View.GONE);
							// rel_cast_view_new.setVisibility(View.VISIBLE);
							// // showImage = false;
							// CastingRightSwipeView();
							// }
							//
							// }else{
							//
							//
							// }
							// int getPage = page*10;
							// getImgPos = getImgPos +getPage;
							//
							// if(getImgPos!=0){
							// int get = (fPosition+1)/getImgPos;
							// if(get == favPos && showsImage == false){
							// rel_image_screen.setVisibility(View.VISIBLE);
							// rel_upload_screen.setVisibility(View.VISIBLE);
							// rel_cast_view_new.setVisibility(View.GONE);
							// textCastivate.setVisibility(View.GONE);
							// showsImage = true;
							// showImage(get);
							// }
							// } else {
							// showsImage = false;
							// rel_image_screen.setVisibility(View.GONE);
							// rel_upload_screen.setVisibility(View.GONE);
							// rel_cast_view_new.setVisibility(View.VISIBLE);
							// // showImage = false;
							// CastingRightSwipeView();
							// }
						}
					}

					// if (textCastivate.getVisibility() == View.VISIBLE) {
					// // if(casting_image.getVisibility() == View.VISIBLE){
					// // casting_image.setVisibility(View.GONE);
					// // rel_cast_view.setVisibility(View.VISIBLE);
					// // }
					// rel_cast_view_new.setVisibility(View.VISIBLE);
					// textCastivate.setVisibility(View.GONE);
					//
					// if (bSwipe.equals("left")) {
					// fPosition++;
					// }
					//
					// bSwipe = "";
					//
					// if (fPosition < favoriteCastings.size()) {
					//
					// } else {
					//
					// page = 0;
					//
					// favoriteCastings = new
					// ArrayList<CastingDetailsModel>(hashMap.get(page));
					// myB = false;
					// fPosition = 0;
					// head = 0;
					// tail = favoriteCastings.size();
					//
					// }
					// setAllData(favoriteCastings, fPosition);
					// } else {
					//
					// myBreak = false;
					// myB = false;
					// int fpos = fPosition;
					// if ((fPosition == 3 || fPosition == 5 || fPosition == 7
					// || fPosition == 9) && showImage == false) {
					// rel_image_screen.setVisibility(View.VISIBLE);
					// rel_upload_screen.setVisibility(View.VISIBLE);
					// rel_cast_view_new.setVisibility(View.GONE);
					// // fPosition--;
					// showImage = true;
					//
					// if (Network.isNetworkConnected(context) && favImagesList
					// != null && favImagesList.size() != 0) {
					//
					// if (favPos < favImagesList.size()) {
					//
					// } else {
					// favPos = 0;
					// }
					//
					// progressView.setVisibility(View.VISIBLE);
					// Picasso.with(context).load(favImagesList.get(favPos).casting_image).noFade().into(casting_image,
					// new Callback() {
					// @Override
					// public void onSuccess() {
					// progressView.setVisibility(View.GONE);
					// favPos++;
					// }
					//
					// @Override
					// public void onError() {
					//
					// // TODO
					// // Auto-generated
					// // method stub
					//
					// }
					// });
					//
					// } else {
					// Library.showToast(context,
					// "Please check your network connection.");
					//
					// }
					//
					// } else {
					// rel_image_screen.setVisibility(View.GONE);
					// rel_upload_screen.setVisibility(View.GONE);
					// rel_cast_view_new.setVisibility(View.VISIBLE);
					// showImage = false;
					// swipw = "right";
					// CastingRightSwipeView();
					// }
					//
					// }

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					rel_cast_view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));

					castSwipe = "left";
					favNewPos = 0;
					if (textCastivate.getVisibility() == View.VISIBLE && showText == true) {
						showText = false;
						rel_image_screen.setVisibility(View.GONE);
						rel_upload_screen.setVisibility(View.GONE);
						rel_cast_view_new.setVisibility(View.VISIBLE);

						if (from_swipe.equals("left")) {
							// fPosition--;
							// CastingLeftSwipeView();
							incImgPos = incImgPos + getImgPos;

							DebugReportOnLocat.ln("fPosition :: " + fPosition);
							setAllData(favoriteCastings, fPosition);
						} else {

							// CastingLeftSwipeView();
							fPosition--;

							if (fPosition >= head && fPosition < tail) {
								setAllData(favoriteCastings, fPosition);
							} else {
								page = lastPage;
								if (hashMap.containsKey(page)) {
									favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
									fPosition = favoriteCastings.size() - 1;
									head = 0;
									tail = favoriteCastings.size();
									DebugReportOnLocat.ln("fPosition :: " + fPosition);
									setAllData(favoriteCastings, fPosition);
								} else {
									DebugReportOnLocat.ln("Paeg " + page + "fPosition:: " + fPosition);
								}
							}

						}
					} else {

						if (getImgPos != 0 /* &&fPosition<getImgPos */) {

							int get = fPosition;
							int getimgPo = getImgPos;
							int getFavPo = favPos;
							boolean shImg = showsImage;
							
						
							

							if ((fPosition % getImgPos == 0) && fPosition != 0 && showsImage == false && favPos < favImagesList.size()) {
								rel_image_screen.setVisibility(View.VISIBLE);
								rel_upload_screen.setVisibility(View.VISIBLE);
								rel_cast_view_new.setVisibility(View.GONE);
								textCastivate.setVisibility(View.GONE);
								showsImage = true;
								imgSwipe = "left";
								incImgPos = incImgPos + getImgPos;
								DebugReportOnLocat.ln("getImgPos after incre :: " + incImgPos);
								showImage();
							} else {
								
								//THis if condition is new
//								if(getTotalCastings <getTotalImages&&getTotalCastings<10 && favPos<favImagesList.size()){
//									rel_image_screen.setVisibility(View.VISIBLE);
//									rel_upload_screen.setVisibility(View.VISIBLE);
//									rel_cast_view_new.setVisibility(View.GONE);
//									textCastivate.setVisibility(View.GONE);
//									showImage();
//								}else{
//									showsImage = false;
//									rel_image_screen.setVisibility(View.GONE);
//									rel_upload_screen.setVisibility(View.GONE);
//									rel_cast_view_new.setVisibility(View.VISIBLE);
//									// showImage = false;
//									CastingLeftSwipeView();
//								}
								
//								if(imgSwipe.equals("right")){
//									imgSwipe = "";
//									showsImage = false;
//									//fPosition--;
//									favPos++;
//								}
								
//								if(imgSwipe.equals("left")){
//									fPosition++;
//								}
								
								imgSwipe = "";
								showsImage = false;
								rel_image_screen.setVisibility(View.GONE);
								rel_upload_screen.setVisibility(View.GONE);
								rel_cast_view_new.setVisibility(View.VISIBLE);
								// showImage = false;
		// New
								//setAllData(favoriteCastings, fPosition);
								//fPosition++;
								
								
								
								CastingLeftSwipeView();
							}
						} else {
//							if (getTotalCastings == favImagesList.size()) {
//								rel_image_screen.setVisibility(View.VISIBLE);
//								rel_upload_screen.setVisibility(View.VISIBLE);
//								rel_cast_view_new.setVisibility(View.GONE);
//								textCastivate.setVisibility(View.GONE);
//								showImage();
//							} else {
							imgSwipe = "";
								showsImage = false;
								rel_image_screen.setVisibility(View.GONE);
								rel_upload_screen.setVisibility(View.GONE);
								rel_cast_view_new.setVisibility(View.VISIBLE);
								// showImage = false;
								CastingLeftSwipeView();
							//}
						}

						// showsImage = false;
						// rel_image_screen.setVisibility(View.GONE);
						// rel_upload_screen.setVisibility(View.GONE);
						// rel_cast_view_new.setVisibility(View.VISIBLE);
						// // showImage = false;
						// CastingLeftSwipeView();

						// if (page == 0 && (favoriteCastings == null ||
						// favoriteCastings.size() ==
						// 0)&&favImagesList.size()>0&&favImagesList!=null) {
						// rel_image_screen.setVisibility(View.VISIBLE);
						// rel_upload_screen.setVisibility(View.VISIBLE);
						// rel_cast_view_new.setVisibility(View.GONE);
						// textCastivate.setVisibility(View.GONE);
						// showImage();
						// } else {
						//
						// // if only one casting
						// if (totalPages == 1 && favoriteCastings.size() == 1
						// && showsImage ==
						// false&&favImagesList.size()>0&&favImagesList!=null) {
						// if ((fPosition == 0) && showsImage == false) {
						// rel_image_screen.setVisibility(View.VISIBLE);
						// rel_upload_screen.setVisibility(View.VISIBLE);
						// rel_cast_view_new.setVisibility(View.GONE);
						// textCastivate.setVisibility(View.GONE);
						// showsImage = true;
						// showImage();
						// }
						// // if only two casting
						// } else if (totalPages == 1 && favoriteCastings.size()
						// == 2&& showsImage ==
						// false&&favImagesList.size()>0&&favImagesList!=null) {
						// if ((fPosition == 0 || fPosition == 1) && showsImage
						// == false) {
						// rel_image_screen.setVisibility(View.VISIBLE);
						// rel_upload_screen.setVisibility(View.VISIBLE);
						// rel_cast_view_new.setVisibility(View.GONE);
						// textCastivate.setVisibility(View.GONE);
						// showsImage = true;
						// showImage();
						// }
						// } else {
						//
						// if ((fPosition == 2 || fPosition == 4 || fPosition ==
						// 6 || fPosition == 8) && showsImage ==
						// false&&favImagesList.size()>0&&favImagesList!=null) {
						//
						// rel_image_screen.setVisibility(View.VISIBLE);
						// rel_upload_screen.setVisibility(View.VISIBLE);
						// rel_cast_view_new.setVisibility(View.GONE);
						//
						// textCastivate.setVisibility(View.GONE);
						// showsImage = true;
						// showImage();
						//
						// }
						// else {
						// showsImage = false;
						// rel_image_screen.setVisibility(View.GONE);
						// rel_upload_screen.setVisibility(View.GONE);
						// rel_cast_view_new.setVisibility(View.VISIBLE);
						// // showImage = false;
						// CastingLeftSwipeView();
						// }
						// }
						// }

					}

					// if (textCastivate.getVisibility() == View.VISIBLE) {
					// // if(casting_image.getVisibility() == View.VISIBLE){
					// // casting_image.setVisibility(View.GONE);
					// // rel_cast_view.setVisibility(View.VISIBLE);
					// // }
					// rel_cast_view_new.setVisibility(View.VISIBLE);
					// textCastivate.setVisibility(View.GONE);
					//
					// bss = false;
					// if (bSwipe.equals("right")) {
					// fPosition--;
					// }
					// bSwipe = "";
					// int pos = fPosition;
					//
					// if (pos == -1) {
					// if (page == 0) {
					// page = totalPages;
					// favoriteCastings = new
					// ArrayList<CastingDetailsModel>(hashMap.get(page));
					// myB = false;
					// fPosition = favoriteCastings.size() - 1;
					// head = 0;
					// tail = favoriteCastings.size();
					//
					// } else {
					//
					// page = 0;
					//
					// favoriteCastings = new
					// ArrayList<CastingDetailsModel>(hashMap.get(page));
					// myB = false;
					// fPosition = 0;
					// head = 0;
					// tail = favoriteCastings.size();
					//
					// }
					// }
					//
					// setAllData(favoriteCastings, fPosition);
					// } else {
					// int fpos = fPosition;
					// if (fPosition == 2 || fPosition == 4 || fPosition == 6 ||
					// fPosition == 8 && showImage == false) {
					//
					// rel_image_screen.setVisibility(View.VISIBLE);
					// rel_upload_screen.setVisibility(View.VISIBLE);
					// rel_cast_view_new.setVisibility(View.GONE);
					// showImage = true;
					// //fPosition++;
					//
					// // favImagesList.get(favPos);
					//
					// if (Network.isNetworkConnected(context) && favImagesList
					// != null && favImagesList.size() != 0) {
					//
					// if (favPos < favImagesList.size()) {
					//
					// } else {
					// favPos = 0;
					// }
					//
					// progressView.setVisibility(View.VISIBLE);
					// Picasso.with(context).load(favImagesList.get(favPos).casting_image).noFade().into(casting_image,
					// new Callback() {
					// @Override
					// public void onSuccess() {
					// progressView.setVisibility(View.GONE);
					// favPos++;
					// }
					//
					// @Override
					// public void onError() {
					// // TODO
					// // Auto-generated
					// // method stub
					//
					// }
					// });
					//
					// } else {
					// Library.showToast(context,
					// "Please check your network connection.");
					//
					// }
					//
					// } else {
					// rel_image_screen.setVisibility(View.GONE);
					// rel_upload_screen.setVisibility(View.GONE);
					// rel_cast_view_new.setVisibility(View.VISIBLE);
					// showImage = false;
					// myB = false;
					// swipw = "left";
					// CastingLeftSwipeView();
					// }
					//
					// overridePendingTransition(R.anim.pull_in_left,
					// R.anim.push_out_left);
					//
					// }

				}
			} catch (Exception e) {
				DebugReportOnLocat.ln("Exception :" + e);
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

	String swipw = "";

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		Intent favIntent = new Intent(context, CastingScreen.class);
		// favIntent.putExtra("FAV_FLAG", true);
		startActivity(favIntent);
	}

	// Sugumaran

	// private PostFavoriteImageSync postFavoriteImageSync;
	//
	// @SuppressLint("InlinedApi")
	// private class PostFavoriteImageSync extends AsyncTask<String, Void, Void>
	// {
	// ProgressDialog pDialog = null;
	//
	// @Override
	// public void onPreExecute() {
	// pDialog = new ProgressDialog(CastingScreen.this,
	// AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
	// pDialog.setMessage("Please wait...");
	// pDialog.setCancelable(false);
	// pDialog.show();
	// }
	//
	// @Override
	// protected Void doInBackground(String... params) {
	//
	// postImageFavorite(favriteFlag);
	// return null;
	// }
	//
	// @Override
	// public void onPostExecute(Void params) {
	// if (pDialog.isShowing())
	// pDialog.dismiss();
	// imgFav.setEnabled(true);
	//
	//
	// int getTotalfav
	// =Integer.parseInt(favCount)+Integer.parseInt(favImgCount);
	// favCount = ""+getTotalfav ;
	//
	// if (!favCount.equals("0")) {
	// // fav_icon.setVisibility(View.INVISIBLE);
	//
	// favCountText.setVisibility(View.VISIBLE);
	// favCountText.setText(favCount);
	// } else {
	// // fav_icon.setVisibility(View.VISIBLE);
	// favCountText.setVisibility(View.INVISIBLE);
	// }
	// // try {
	// // JSONObject props = new JSONObject();
	// // if (imageFav != null && imageFav.equals("1")) {
	// // myList.get(position).favCasting = "1";
	// // myListImage.get(3);
	// //
	// // fav_iconNoImage.setSelected(true);
	// //
	// // try {
	// // props.put("User ID", androidUserID);
	// // props.put("Casting ID",
	// myList.get(position).roleId.toString().trim());
	// // props.put("Casting Title",
	// myList.get(position).castingTitle.toString().trim());
	// // props.put("Action", "Added");
	// // mixpanel.track("Favorites", props);
	// // } catch (JSONException e) {
	// //
	// // }
	// //
	// // } else {
	// // myList.get(position).favCasting = "0";
	// // fav_iconNoImage.setSelected(false);
	// // try {
	// // props.put("User ID", androidUserID);
	// // props.put("Casting ID",
	// myList.get(position).roleId.toString().trim());
	// // props.put("Casting Title",
	// myList.get(position).castingTitle.toString().trim());
	// // props.put("Action", "Removed");
	// // mixpanel.track("Favorites", props);
	// // } catch (JSONException e) {
	// //
	// // }
	// // }
	// //
	// // } catch (IndexOutOfBoundsException e) {
	// //
	// // }
	//
	// }
	//
	// }
	// public void postImageFavorite(String favoriteValue) {
	// try {
	// HttpPost request = new HttpPost(HttpUri.SET_IMG_FAV);
	// request.setHeader("Accept", "application/json");
	// request.setHeader("Content-type", "application/x-www-form-urlencoded");
	//
	// JSONStringer item = null;
	//
	// try {
	//
	// // if(rel_image_screen.getVisibility() == View.VISIBLE){
	// //
	// // item = new
	// //
	// JSONStringer().object().key("userId").value(Library.androidUserID).key("roleId").value(myList.get(position).roleId).key("castingFav")
	// //
	// .value(favoriteValue).key("roleEnthicity").value(myList.get(position).roleForEthnicity).key("roleAgeRange").value(myList.get(position).ageRange)
	// // .key("roleFor").value(myList.get(position).roleForGender).endObject();
	// // Log.d("Data", item.toString());
	// //
	// // }else{
	//
	// //
	// {"userId":"68","roleId":"77","castingFav":"1","roleEnthicity":"Caucasian","roleAgeRange":"40-50","roleFor":"Female"}
	//
	// // userId,imageId,imageFav
	// if (position == 10)
	// position = 9;
	// item = new
	// JSONStringer().object().key("userId").value(Library.androidUserID).key("imageId").value(myListImage.get(sequenceOrder).imgId).key("imageFav")
	// .value(favoriteValue).endObject();
	//
	// System.out.println("item.toString() :: " + item.toString());
	// Log.d("Data", item.toString());
	//
	// // }
	//
	// } catch (IndexOutOfBoundsException e) {
	// e.printStackTrace();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// StringEntity entity = null;
	//
	// try {
	// entity = new StringEntity(item.toString());
	// System.out.println("entity :: " + entity.toString());
	// request.setEntity(entity);
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// // Send request to WCF service
	// DefaultHttpClient httpClient = new DefaultHttpClient();
	// HttpResponse response = null;
	//
	// try {
	// response = httpClient.execute(request);
	// if (response.getStatusLine().getStatusCode() == 200) {
	//
	// HttpEntity responseEntity = response.getEntity();
	//
	// try {
	// is = responseEntity.getContent();
	// } catch (IllegalStateException e) {
	//
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// BufferedReader reader2 = new BufferedReader(new InputStreamReader(is,
	// "iso-8859-1"), 8);
	// sb = new StringBuilder();
	// String line = null;
	// while ((line = reader2.readLine()) != null) {
	// sb.append(line + "\n");
	// }
	// is.close();
	// json = sb.toString();
	//
	// // response: [{"fav_count":"13"}]
	//
	// json = sb.toString();
	// // [{"fav_count":"9","roleFav":"1"}]
	// jArr = new JSONArray(json);
	// DebugReportOnLocat.ln("json response for fav::" + jArr);
	//
	// JSONObject oneObject = jArr.getJSONObject(0);
	//
	// favImgCount = oneObject.getString("fav_count");
	// imageFav = oneObject.getString("imageFav");
	//
	// DebugReportOnLocat.ln("json response for submit::" + json);
	//
	// } catch (Exception e) {
	// Log.e("Buffer Error", "Error converting result " + e.toString());
	// }
	//
	// } else {
	// Status = null;
	// }
	//
	// } catch (ClientProtocolException e) {
	//
	// e.printStackTrace();
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	// }
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//

	private Handler handlerCastingImagesSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(BackupFavoriteScreen.this)) {
				new GetImages().execute();
			} else {
				Toast.makeText(BackupFavoriteScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
				finishAffinity();
			}
		}
	};

	public class GetImages extends AsyncTask<String, Void, Void> {

		String getImageLinks = "";

		ProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			dialog.setCancelable(false);
			dialog.setMessage("Loading..");
			dialog.show();
		}

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
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			favImagesList = new ArrayList<FavImagesModel>();
			JSONArray jArray;
			try {
				jArray = new JSONArray(getImageLinks);

				FavImagesModel imagesModel;

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					imagesModel = new FavImagesModel(json_data.getString("image_id"), json_data.getString("casting_image"), json_data.getString("casting_image_thumb"),"");

					favImagesList.add(imagesModel);
				}

				getTotalImages = favImagesList.size();
				
				if (fromImgGrid.equals("yes")) {
					fromImgGrid = "";

					if (favImagesList.size() > 0 && favImagesList != null) {
						DebugReportOnLocat.ln("siez " + favImagesList.size());

						for (int i = favPos; i >= 0; i--) {
							int getI = i;
							if (i < favImagesList.size()) {
								favPos = i;
								showImage();
								break;
							}
						}

					} else if (favoriteCastings != null && favoriteCastings.size() > 0) {

						if (favoriteCastings.size() > 0 && favoriteCastings != null) {
							DebugReportOnLocat.ln("siez " + favoriteCastings.size());
							for (int i = fPosition; i >= 0; i--) {
								int getI = i;
								if (i < favoriteCastings.size()) {
									fPosition = i;
									setAllData(favoriteCastings, fPosition);
									break;
								}
							}
						} else if (favImagesList.size() > 0 && favImagesList != null) {
							DebugReportOnLocat.ln("siez " + favImagesList.size());
							for (int i = favPos; i >= 0; i--) {
								int getI = i;
								if (i < favImagesList.size()) {
									favPos = i;
									showImage();
									break;
								}
							}
						} else {
							Intent favUpdate = new Intent(context, CastingScreen.class);
							startActivity(favUpdate);
						}

					} else {
						Intent favIntent = new Intent(context, CastingScreen.class);
						// favIntent.putExtra("FAV_FLAG", true);
						startActivity(favIntent);
						// rel_image_screen.setVisibility(View.GONE);
						// rel_upload_screen.setVisibility(View.GONE);
						// rel_cast_view_new.setVisibility(View.VISIBLE);
						// textCastivate.setVisibility(View.VISIBLE);
					}

					// ///////////////////////
					// if (favImagesList.size() > 0 && favImagesList != null ) {
					// DebugReportOnLocat.ln("siez " + favImagesList.size());
					// if(favPos<favImagesList.size()){
					// showImage();
					// }else{
					// favPos--;
					//
					// showImage();
					// }
					//
					// } else {
					// rel_image_screen.setVisibility(View.GONE);
					// rel_upload_screen.setVisibility(View.GONE);
					// rel_cast_view_new.setVisibility(View.VISIBLE);
					// textCastivate.setVisibility(View.VISIBLE);
					// }
					// ///////////////////

				} else {
					handlerFavCastingSync.sendEmptyMessage(0);
				}

				// if (!CastingScreen.favCount.equals("")) {
				// getTotalCastings = Integer.parseInt(CastingScreen.favCount);
				// }
				//
				// getTotalCastings= getTotalCastings-favImagesList.size();
				// int getTotalfCastings = getTotalCastings;
				//
				// if (getTotalCastings > 10) {
				// lastPage = (getTotalCastings / 10);
				// totalPages = (getTotalCastings / 10)+1;
				// int TOTALPAGE = totalPages;
				//
				// }

				// int favCastSize = favoriteCastings.size();
				// int favImgSize = favImagesList.size();
				//
				// if((favCastSize%favImgSize == 0) ||(favCastSize/favImgSize ==
				// 0)){
				//
				// }else{
				// getDivider = favCastSize/favImgSize;
				//
				// }

			} catch (JSONException e) {

				e.printStackTrace();
			}

			// if (notifFlag == true) {
			// userNotificationFlag = "Yes";
			// notification = true;
			// sharedpreferences = getSharedPreferences(Library.MyPREFERENCES,
			// Context.MODE_PRIVATE);
			// editor = sharedpreferences.edit();
			// editor.putBoolean(Library.NOTIFICATION, notification);
			// editor.commit();
			// toggleButton.setChecked(true);
			// postSetNotifSync = new PostSetNotifSync();
			// postSetNotifSync.execute();
			// }

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

		// double latitude = gps.getLatitude();
		// double longitude = gps.getLongitude();

		// String lat = Double.toString(dclatitude);
		// String lang = Double.toString(dclongitude);

		try {

			// http://castivate.com/castingNew/public/getImageFav?userId=60

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			// nameValuePairs.add(new BasicNameValuePair("lat", lat));
			// nameValuePairs.add(new BasicNameValuePair("lang", lang));
			//
			// //Sugumaran
			// nameValuePairs.add(new BasicNameValuePair("ds",getDS()));
			nameValuePairs.add(new BasicNameValuePair("userId", Library.androidUserID));

			HttpClient httpclient = new DefaultHttpClient();
			String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");

			String Url = HttpUri.GET_IMG_FAV + paramsString;
			DebugReportOnLocat.ln("ImageFavUrl :: " + Url);
			HttpGet httpget = new HttpGet(Url);
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

	GPSTracker gps;
	Geocoder geocoder;
	List<Address> addresses;
	Address returnAddress;
	public static double dclatitude, dclongitude;

	@SuppressWarnings("static-access")
	public void currentLocation() {

		// create class object
		gps = new GPSTracker(context);

		// check if GPS enabled
		if (gps.canGetLocation()) {

			// dlatitude = gps.getLatitude();
			// dlongitude = gps.getLongitude();

			dclatitude = gps.getLatitude();
			dclongitude = gps.getLongitude();
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			showSettingsAlert();
		}
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog);

		// Setting Dialog Title
		// alertDialog.setTitle("Cast");

		// Setting Dialog Message
		alertDialog.setMessage("Please enable GPS to continue inside the app.");

		// On pressing Settings button
		alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finishAffinity();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	// Nivetha

	// Coded By Nivetha

	public void uploadImageToBackEnd() {
		dialogCamera = new Dialog(context);
		dialogCamera.setContentView(R.layout.dialog);
		dialogCamera.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogCamera.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		dialogCamera.show();

		btnCamera = (Button) dialogCamera.findViewById(R.id.btnCamera);
		btnGallery = (Button) dialogCamera.findViewById(R.id.btnGallery);
		btnCancel = (Button) dialogCamera.findViewById(R.id.btnCancel);

		/* Camera button click Method */
		btnCamera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialogCamera.dismiss();

				// grid_view_photos.setVisibility(View.GONE);

				captureImage();
				// mCamera = Camera.open(cameraId);

				// do we have a camera?
				// if (!getPackageManager()
				// .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
				// Library.showToast(context, "No camera on this device");
				//
				// }

			}
		});

		/* Gallery button click Method */
		btnGallery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				dialogCamera.dismiss();

				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);

			}

		});
		/* Cancel button click Method */
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				dialogCamera.dismiss();

			}
		});

	}

	public void imageDisplay() {

		Bitmap bm = decodeFile(profileImage);
		DebugReportOnLocat.ln("Profile image : " + bm);
		if (bm != null) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 25, baos);
			b = baos.toByteArray();
			encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
			DebugReportOnLocat.ln("encodedImage >> " + encodedImage);

			currentLocation();

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			String lat = Double.toString(latitude);
			String lang = Double.toString(longitude);

			Intent intent = new Intent(context, ThankYouActivity.class);
			intent.putExtra("Latitude", lat);
			intent.putExtra("Longitude", lang);
			intent.putExtra("Image", "FavoriteScreen");
			startActivity(intent);

		} else {
			// Toast.makeText(context, "Pick Images Only", Toast.LENGTH_LONG)
			// .show();
		}
	}

	private Bitmap decodeFile(File f) {

		Bitmap b = null;

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);

			BitmapFactory.decodeStream(fis, null, o);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {

			try {
				fis.close();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int scale = 1;

		if (f.length() > 8000) {

			scale = 5;

			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}
		}
		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		b = BitmapFactory.decodeStream(fis, null, o2);
		try {
			fis.close();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public void captureImage() {
		try {

			File f = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/tempCastivate.jpg");
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// camera.putExtra("android.intent.extras.CAMERA_FACING", -1);
			// camera.putExtra("android.intent.extras.CAMERA_FACING",
			// android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
			camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			startActivityForResult(camera, CAMERA_CAPTURE);
		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Oops! - your device doesn't support capturing images!";
			Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	Bitmap bm;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == CAMERA_CAPTURE) {
				imagePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/tempCastivate.jpg";
				DebugReportOnLocat.ln("imagePath  " + imagePath);
				picPath = imagePath;
				profileImage = new File(imagePath);
				imageDisplay();
			}

			if (requestCode == RESULT_LOAD_IMAGE && null != data) {
				encodedImage = "";
				Uri selectedImage = data.getData();
				DebugReportOnLocat.ln("URI : " + selectedImage);
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				picPath = picturePath;
				// bm = BitmapFactory.decodeFile(picturePath);
				Bitmap bm = decodeFile(new File(picturePath));
				if (bm != null) {

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
					bm.recycle();
					bm = null;
					b = baos.toByteArray();
					encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
					DebugReportOnLocat.ln("encodedImage >> " + encodedImage);

					currentLocation();

					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();

					String lat = Double.toString(latitude);
					String lang = Double.toString(longitude);

					Intent intent = new Intent(context, ThankYouActivity.class);
					intent.putExtra("Latitude", lat);
					intent.putExtra("Longitude", lang);
					intent.putExtra("Image", "FavoriteScreen");
					startActivity(intent);

				}
			}
		}
	}

	int favNewPos;

	@Override
	public void get(final int pos) {
		// TODO Auto-generated method stub

		imgFav.setChecked(true);
		imgFav.setVisibility(View.VISIBLE);
		imgFav.setEnabled(true);

		FavImagesModel model = favImagesList.get(pos);

		fav_iconNoImage.setChecked(true);

		// new ImageLoadTask(model.casting_image, casting_image).execute();

		grid_view_photos.setVisibility(View.GONE);
		rel_image_screen.setVisibility(View.VISIBLE);
		rel_upload_screen.setVisibility(View.VISIBLE);

		if (Network.isNetworkConnected(context) && favImagesList != null && favImagesList.size() != 0) {

			if (favPos < favImagesList.size()) {

			} else {
				favPos = 0;
			}
			DebugReportOnLocat.ln("Grid Item Url :: " + favImagesList.get(pos).casting_image);
			progressView.setVisibility(View.VISIBLE);
			Picasso.with(context).load((favImagesList.get(pos).casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
				@Override
				public void onSuccess() {
					progressView.setVisibility(View.GONE);
					// favPos = pos + 1;
					favNewPos = pos + 1;
				}

				@Override
				public void onError() {

				}
			});

		} else {
			Library.showToast(context, "Please check your network connection.");

		}

	}

	// Sugumaran Code
	private Handler handlerImgFavoriteSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(BackupFavoriteScreen.this)) {
				// postFavoriteCastingSync = new PostFavoriteCastingSync();
				// postFavoriteCastingSync.execute();

				postFavoriteImageSync = new PostFavoriteImageSync();
				postFavoriteImageSync.execute();

			} else {
				Toast.makeText(BackupFavoriteScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();

			}
		}
	};

	private PostFavoriteImageSync postFavoriteImageSync;

	@SuppressLint("InlinedApi")
	private class PostFavoriteImageSync extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog = null;

		@Override
		public void onPreExecute() {
			pDialog = new ProgressDialog(BackupFavoriteScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			postImageFavorite(favriteFlag);
			return null;
		}

		@Override
		public void onPostExecute(Void params) {
			if (pDialog.isShowing())
				pDialog.dismiss();
			imgFav.setEnabled(true);

			favNewPos = 0;

			handlerCastingImagesSync.sendEmptyMessage(0);

			// int getTotalfav = Integer.parseInt(favCount) +
			// Integer.parseInt(favImgCount);
			// favCount = "" + getTotalfav;

			// if (!favCount.equals("0")) {
			// // fav_icon.setVisibility(View.INVISIBLE);
			//
			// favCountText.setVisibility(View.VISIBLE);
			// favCountText.setText(favCount);
			// } else {
			// // fav_icon.setVisibility(View.VISIBLE);
			// favCountText.setVisibility(View.INVISIBLE);
			// }
			//
		}

	}

	public void postImageFavorite(String favoriteValue) {
		try {
			HttpPost request = new HttpPost(HttpUri.SET_IMG_FAV);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			JSONStringer item = null;

			try {

				// if(rel_image_screen.getVisibility() == View.VISIBLE){
				//
				// item = new
				// JSONStringer().object().key("userId").value(Library.androidUserID).key("roleId").value(myList.get(position).roleId).key("castingFav")
				// .value(favoriteValue).key("roleEnthicity").value(myList.get(position).roleForEthnicity).key("roleAgeRange").value(myList.get(position).ageRange)
				// .key("roleFor").value(myList.get(position).roleForGender).endObject();
				// Log.d("Data", item.toString());
				//
				// }else{

				// {"userId":"68","roleId":"77","castingFav":"1","roleEnthicity":"Caucasian","roleAgeRange":"40-50","roleFor":"Female"}

				// userId,imageId,imageFav

				// DebugReportOnLocat.ln("FavPos:: "+(favPos-1));

				int get = favPos - 1;
				if (get == -1) {
					favPos = favPos + 1;
				}

				if (favNewPos != 0) {
					DebugReportOnLocat.ln("favnewPos :" + favNewPos);
					item = new JSONStringer().object().key("userId").value(Library.androidUserID).key("imageId").value(favImagesList.get((favNewPos) - 1).image_id).key("imageFav")
							.value(favoriteValue).endObject();
				} else {

					item = new JSONStringer().object().key("userId").value(Library.androidUserID).key("imageId").value(favImagesList.get((favPos) - 1).image_id).key("imageFav")
							.value(favoriteValue).endObject();

				}
				System.out.println("item.toString() :: " + item.toString());
				Log.d("Data", item.toString());

				// }

			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			StringEntity entity = null;

			try {
				entity = new StringEntity(item.toString());
				System.out.println("entity :: " + entity.toString());
				request.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Send request to WCF service
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = null;

			try {
				response = httpClient.execute(request);
				if (response.getStatusLine().getStatusCode() == 200) {

					HttpEntity responseEntity = response.getEntity();

					try {
						is = responseEntity.getContent();
					} catch (IllegalStateException e) {

						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						BufferedReader reader2 = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
						sb = new StringBuilder();
						String line = null;
						while ((line = reader2.readLine()) != null) {
							sb.append(line + "\n");
						}
						is.close();
						json = sb.toString();

						// response: [{"fav_count":"13"}]

						json = sb.toString();
						// [{"fav_count":"9","roleFav":"1"}]
						jArr = new JSONArray(json);
						DebugReportOnLocat.ln("json response for fav::" + jArr);

						JSONObject oneObject = jArr.getJSONObject(0);

						// favImgCount = oneObject.getString("fav_count");
						String favCount = oneObject.getString("fav_count");

						String imageFav = oneObject.getString("imageFav");

						DebugReportOnLocat.ln("json response for submit::" + json);

					} catch (Exception e) {
						Log.e("Buffer Error", "Error converting result " + e.toString());
					}

				} else {
					Status = null;
				}

			} catch (ClientProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void showImage() {

		imgFav.setChecked(true);
		// int newPos = pos - 1;
		// favPos = newPos;
		if (favImagesList != null && favImagesList.size() != 0) {

			// rel_image_screen.setVisibility(View.VISIBLE);
			// rel_upload_screen.setVisibility(View.VISIBLE);
			// rel_cast_view_new.setVisibility(View.GONE);
			// textCastivate.setVisibility(View.GONE);

			if (rel_image_screen.getVisibility() == View.VISIBLE) {
				DebugReportOnLocat.ln("out Visible");
			} else {
				DebugReportOnLocat.ln("Out Gone");
			}
			if (Network.isNetworkConnected(context) && favImagesList != null && favImagesList.size() != 0) {

				// if (favPos < favImagesList.size()) {
				//
				// } else {
				//
				// // This if condition only for images when no fav.casting...
				// if (page == 0 && (favoriteCastings == null ||
				// favoriteCastings.size() == 0)) {
				// rel_image_screen.setVisibility(View.VISIBLE);
				// rel_upload_screen.setVisibility(View.VISIBLE);
				// rel_cast_view_new.setVisibility(View.GONE);
				// textCastivate.setVisibility(View.GONE);
				// favPos = 0;
				// } else
				// favPos = 0;
				// }

				if (favPos < favImagesList.size()) {

					DebugReportOnLocat.ln(" Image Url in override method :: " + favImagesList.get(favPos).casting_image);
					DebugReportOnLocat.ln("curr Img Pos :: " + favPos);
					progressView.setVisibility(View.VISIBLE);
					Picasso.with(context).load((favImagesList.get(favPos).casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
						@Override
						public void onSuccess() {
							progressView.setVisibility(View.GONE);
							favPos++;

							if (rel_image_screen.getVisibility() == View.VISIBLE) {
								DebugReportOnLocat.ln("in Visible");
							} else {
								DebugReportOnLocat.ln(" in Gone");
							}
						}

						@Override
						public void onError() {

						}
					});
				} else {
					textCastivate.setVisibility(View.VISIBLE);
					rel_cast_view_new.setVisibility(View.GONE);
					rel_image_screen.setVisibility(View.GONE);
					rel_upload_screen.setVisibility(View.GONE);
					favPos = 0;
				}

			} else {
				Library.showToast(context, "Please check your network connection.");
			}
		} else {
			DebugReportOnLocat.ln("No Image List.");

			// rel_image_screen.setVisibility(View.GONE);
			// rel_upload_screen.setVisibility(View.GONE);
			// rel_cast_view_new.setVisibility(View.VISIBLE);
			// textCastivate.setVisibility(View.VISIBLE);
		}
	}

	public void chekVisibility(boolean b) {

	}
}