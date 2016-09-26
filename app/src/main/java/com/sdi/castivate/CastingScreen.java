package com.sdi.castivate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.sdi.castivate.adapter.CastingsListAdapter;
import com.sdi.castivate.adapter.EthnicityFilterAdapter;
import com.sdi.castivate.adapter.ImageListAdapter;
import com.sdi.castivate.adapter.LocationSearchAdapter;
import com.sdi.castivate.help.Help;
import com.sdi.castivate.location.GPSTracker;
import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.model.CastingImagesModel;
import com.sdi.castivate.model.EthnicityModel;
import com.sdi.castivate.utils.CastingsLinkMovementMethod;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.GCMInterface;
import com.sdi.castivate.utils.GridInterface;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.ListExpandable;
import com.sdi.castivate.utils.Network;

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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.sdi.castivate.utils.Library.SENDER_ID;

/**
 * Created by Balachandar on 16-Apr-15.
 */

@SuppressWarnings("deprecation")
@SuppressLint({ "ResourceAsColor", "InlinedApi", "ShowToast", "UseSparseArrays" })
public class CastingScreen extends Activity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, View.OnTouchListener, GCMInterface, GridInterface/*
																																									 * ,
																																									 * ListInterface
																																									 */{

	SharedPreferences sharedpreferences;
	Editor editor;
	static String androidUserID;
	static String remainingDays;

	GridView grid_view_photos;

	LinearLayout lay_submission_detail_no_image;
	static final String EXTRA_MESSAGE = "message";
	double dlatitude, dlongitude;
	public static double dclatitude, dclongitude;

	GPSTracker gps;
	File filePath;
	String provider = "", android_unique_user_id = "";

	String strState = "", strCity = "";
	LinearLayout castivityScreen;
	ImageView gear_icon, gear_icon_mirror, outbox_icon, casting_image, help_overlay;
	public static String favCount = "", roleFav;

	public static String favImgCount = "", imageFav;

	ToggleButton toggleButton;
	TextView birthYear, currentLocation, txtEthnicity, txtSubmitCasting, txtReset, text_help_over_lay, favCountText, select_icon, imgGrid, txtName, local_talent;
	RelativeLayout castingViewNoImage, rel_cast_view, rel_cast, rel_upload_screen, rel_cast_outer, rel_info_ListView, home_screen, rel_home_screen, rel_image_screen;
	CastingsListAdapter castListAdapter;
	LocationSearchAdapter searchAdapter = null;
	ArrayList<String> stringList;
	ListView locationListView;
	ProgressBar progressView;
	ArrayList<String> years = new ArrayList<String>();

	ArrayList<EthnicityModel> ethnicityList;
	public static RelativeLayout tap_current_location;
	ArrayList<String> selchkboxlist;

	FrameLayout frlay;
	boolean castImageViewStatus, listViewStatus, gridViewStatus;
	// ProgressDialog pDialog;
	// casting filter screen
	public CheckBox chkActor, chkModel, chkSinger, chkDancer, chkMale, chkFemale, chkUnion, chkNonUnion, fav_iconNoImage;
	public static int sequenceOrder = -1;

	Context context;
	View view;
	File picFile;
	int rateThisAppFlag = 1;

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

	boolean helpOverlayFlag/* ,noImageCasting */, notifFlag = false;
	public static String strPerformanceType, strGender, strUnionType, strBithYear, strEthnicity, strSelectedLocation = "", strCurrentLocation;
	public static boolean notification, UnionType = false, ratePopFlag;

	// casting main screen
	public TextView textCastingTitleNoImage, textCastingTypeNoImage, textPaidStatusNoImage, textUnionStatusNoImage, textUnionTypeNoImage, textSynopsisNoImage, textAgeRangeNoImage,
			textRoleForGenderNoImage, textRoleForEthnicityNoImage, txtRoleDescriptionNoImage, textSubmissionDetailNoImage, textCastivateNoImage, textDistanceNoImage,
			textCastivate, txtUploadYourImage, txtSwipeCastings;
	CheckBox imgFav;
	EditText editTextLocation;
	String getFrom = "cast";
	int initialPositionValue = 0;
	Activity activity;

	ImageView clear, birthClear, castingsListIcon /* ,castingsListIconChange */;
	EthnicityFilterAdapter adapterEthnicityList;

	String[] ethnicity = { "Caucasian", "African American", "Hispanic", "Asian", "Native American", "Middle Eastern", "Other" };

	public ListView ethnicityListView, castingsList;
	// Swipe left and right

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	// Sugumaran Changes (25th May 16)
	// private GestureDetector imgGestureDetector;
	// View.OnTouchListener imgGestureListener;

	// public static String defaultCurrentLocation;
	public static String userTokenId = "", favriteFlag;

	int count = 0;
	// to retrieve the Google JSON Map services for US states

	DownloadTask placesDownloadTask;

	String totalCasting;

	public static int rateCount = 0;

	static final String TAG = "Register Activity";
	MixpanelAPI mixpanel;
	Animation animBlink;
	public static boolean isFavScreen;

	// /////////////

	String build = android.os.Build.MODEL;
	String os = android.os.Build.VERSION.RELEASE;

	// 17-Dec-2015 : New Concept
	private SwipeRefreshLayout swipeRefreshLayout;

	// Sugumaran Changes (25th May 16)
	ImageView casting_image_only;

	//10-08-2016
	Button btn_apply;

	private ArrayList<CastingDetailsModel> selectedCastingDetailsModels = new ArrayList<CastingDetailsModel>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		context = this;
		activity = this;

		Intent intent = getIntent();
		if (intent.hasExtra("CalledBy")) {
			checkRate();
		} else if (intent.hasExtra("from")) {
			position = 0;
		}




		// New Concept
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setVisibility(View.GONE);

		swipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
				imageGrid();
			}
		});

		// final Animation animScale = AnimationUtils.loadAnimation(this,
		// R.anim.scale_up);

		// bottmLay =(RelativeLayout)findViewById(R.id.bottmLay);
		imgFav = (CheckBox) findViewById(R.id.imgFav);

		animBlink = AnimationUtils.loadAnimation(this, R.anim.blink);
		txtSwipeCastings = (TextView) findViewById(R.id.swipe_text);
		txtSwipeCastings.setVisibility(View.GONE);
		txtSwipeCastings.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				txtSwipeCastings.setVisibility(View.GONE);
				return false;
			}
		});

		// Initialize the library with your
		// Mixpanel project token, MIXPANEL_TOKEN, and a reference
		// to your application context.

		if (TextUtils.isEmpty(regId)) {
			regId = registerGCM();
			Log.d("RegisterActivity", "GCM RegId: " + regId);
		} else {
			// Toast.makeText(getApplicationContext(),
			// "Already Registered with GCM Server!",
			// Toast.LENGTH_LONG).show();
		}

		swpe = true;

		myList = new ArrayList<CastingDetailsModel>();
		// bList = new ArrayList<CastingDetailsModel>();
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = thisYear; i >= 1900; i--) {
			years.add(Integer.toString(i));
		}

		userTokenId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		DebugReportOnLocat.ln("Android ID:" + userTokenId);
		sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
		androidUserID = sharedpreferences.getString(Library.CUSTOMER_ID, "");
		remainingDays = sharedpreferences.getString(Library.REMAINING_DAYS, "");
		strPerformanceType = sharedpreferences.getString(Library.PERFORMANCE_TYPE, "");
		strUnionType = sharedpreferences.getString(Library.UNION, "");
		strCurrentLocation = sharedpreferences.getString(Library.CURRENT_LOCATION, "");
		strSelectedLocation = sharedpreferences.getString(Library.SELECTED_LOCATION, "");
		strGender = sharedpreferences.getString(Library.GENDER, "");
		strEthnicity = sharedpreferences.getString(Library.ETHNICITY, "");
		dlatitude = sharedpreferences.getFloat(Library.LAT, 0);
		dlongitude = sharedpreferences.getFloat(Library.LNG, 0);
		strBithYear = sharedpreferences.getString(Library.BIRTH, "");
		rateThisAppFlag = sharedpreferences.getInt(Library.RATEIT, 0);
		UnionType = sharedpreferences.getBoolean(Library.NON_UNION, false);
		notification = sharedpreferences.getBoolean(Library.NOTIFICATION, false);
		helpOverlayFlag = sharedpreferences.getBoolean(Library.HELP_OVERLAY, false);
		DebugReportOnLocat.ln("user id::" + androidUserID);
		Library.androidUserID = androidUserID;
		DebugReportOnLocat.ln("Android ID:" + Library.androidUserID);

		findElements();
		clickMenuEvents();

		mixpanel = MixpanelAPI.getInstance(context, Library.MIXPANEL_TOKEN);

		try{
			postUserTokenIdSync = new PostUserTokenIdSync();
			postUserTokenIdSync.execute();
		}
		catch (Exception e)
		{
			System.out.println("Exception :"+e.getMessage());
		}

		//10-08-2016 add applay button

		btn_apply=(Button) findViewById(R.id.btn_apply);
		/*btn_apply=(Button) findViewById(R.id.btn_apply);

		btn_apply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				System.out.println("REMAINING DAYS ---------------> "+Library.remainingDays);

				//String rdays="0";

				if(Library.remainingDays.equals("0"))
				{
					Intent intent = new Intent(CastingScreen.this, CastingPlan.class);
					//overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
					startActivity(intent);
				}
				else {

					Intent intent = new Intent(CastingScreen.this, CastingLogin.class);
					//overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
					startActivity(intent);
				}


			}
		});*/

		castingsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

				// TODO Auto-generated method stub

				listViewStatus = false;
				String getTitle = "";
				if (castingsListArr != null && castingsListArr.size() != 0) {
					getTitle = castingsListArr.get(pos).castingTitle.toString();
					System.out.println(getTitle);
				}
				//
				// int myPoss = pos % 20;
				// int myPag = pos % 20;
				// int myPo = castingsListArr.size() / 20;
				// int totalList = (myPo * 20) + 20;
				//
				// if (castingsListArr.size() < totalList) {
				// ArrayList<CastingDetailsModel> list = null;
				// for (int m = 0; m < totalList; m++) {
				// list = new ArrayList<CastingDetailsModel>();
				// list.add(castingsListArr.get(m));
				// }
				//
				// page = myPag;
				// myList = new ArrayList<CastingDetailsModel>(list);
				// setAllData(myList, myPoss);
				// } else {
				// //int myPo = castingsListArr.size() / 20;
				// ArrayList<CastingDetailsModel> list = null;
				// for (int m = (myPo*20); m < totalList; m++) {
				// list = new ArrayList<CastingDetailsModel>();
				// list.add(castingsListArr.get(m));
				// }
				//
				// page = myPo;
				// myList = new ArrayList<CastingDetailsModel>(list);
				// setAllData(myList, myPoss);
				//
				// }

				// page = myPage;
				//
				// myList = new ArrayList<CastingDetailsModel>(tempList);
				// setAllData(myList, myPoss);

				int padge = page;
				int scrollC = scrollCount;

				int size = CastivateApplication.getInstance().hashMap.size();

				for (int i = 0; i < (size + 1); i++) {

					if (CastivateApplication.getInstance().hashMap.containsKey(i)) {
						ArrayList<CastingDetailsModel> tempList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(i));

						for (int j = 0; j < tempList.size(); j++) {
							String newTitle = tempList.get(j).castingTitle.toString();
							if (newTitle.equals(getTitle)) {
								int getPoss = j;

								head = 0;
								tail = tempList.size();
								page = i;
								myList = new ArrayList<CastingDetailsModel>(tempList);
								setAllData(myList, getPoss);

								break;
							}
						}
					}
				}

			}
		});

		castingsList.setOnScrollListener(new OnScrollListener() {
			private int currentVisibleItemCount;
			private int currentScrollState;
			private int currentFirstVisibleItem;
			private int totalItem;
			private LinearLayout lBelow;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				this.currentScrollState = scrollState;
				this.isScrollCompleted();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				this.currentFirstVisibleItem = firstVisibleItem;
				this.currentVisibleItemCount = visibleItemCount;
				this.totalItem = totalItemCount;

			}

			private void isScrollCompleted() {
				if (totalItem - currentFirstVisibleItem == currentVisibleItemCount && this.currentScrollState == SCROLL_STATE_IDLE) {
					/** To do code here */

					int totalRecord = Integer.parseInt(myList.get(0).totalCasting);
					// if (totalRecord > totalItem) {
					if (totalRecord > totalItem) {

						scrollCount++;
						int pages = page;
						int totalpage = totalPages;

						if (page < totalPages) {
							page = page + 1;
							int newPage = page;

							handlerCastingSync.sendEmptyMessage(0);

							// for (int i = 0; i < totalPages; i++) {
							// if (!hashMap.containsKey(i)) {
							// page = i;
							// handlerCastingSync.sendEmptyMessage(0);
							// break;
							// }
							// }

						}
						// else {
						// Toast.makeText(context, "Castings List ended",
						// Toast.LENGTH_SHORT).show();
						// }

						castingsList.setAdapter(null);

						ArrayList<CastingDetailsModel> castingsListArrList = new ArrayList<CastingDetailsModel>(castingsListArr);

						refreshYourAdapter(castingsListArrList);
						castingsList.setAdapter(castListAdapter);
						castListAdapter.notifyDataSetChanged();
						int getpos = castingsListArr.size();
						System.out.println("getPos " + getpos);
						castingsList.setSelection(getpos - 19);

					}

				}
			}
		});

		if (helpOverlayFlag == false) {
			// castivityScreen.setVisibility(View.GONE);
			help_overlay.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

			help_overlay.setVisibility(View.VISIBLE);
			sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
			editor = sharedpreferences.edit();
			editor.putBoolean(Library.HELP_OVERLAY, true);
			editor.commit();
			// Library.helpOverlayView=false;
		}

		if (Network.isNetworkConnected(CastingScreen.this)) {
			if (!androidUserID.equals("") &&!remainingDays.equals("")) {
				String get;
				if (savedInstanceState == null && isFavScreen == true) {
					Bundle extras = getIntent().getExtras();
					if (extras == null) {
						get = null;
					} else {

						// My New Code

						if (getIntent().hasExtra("fromImage")) {
							getFrom = extras.getString("fromImage");
						}

						if (getFrom.equalsIgnoreCase("Image")) {
							handlerCastingSync.sendEmptyMessage(0);
						} else {
							if (getIntent().hasExtra("rollID")) {
								get = extras.getString("rollID");
								PushRollID = get;
								getPushNotificationCasting();
								isFavScreen = false;
							}
						}
					}
				} else {

					handlerCastingSync.sendEmptyMessage(0);
				}

				// Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
				// Uri.fromParts(
				// "mailto","bala@sdi.la", null));
				// emailIntent.putExtra(Intent.EXTRA_SUBJECT,
				// "castivate user id");
				// emailIntent.putExtra(Intent.EXTRA_TEXT,"user id:" +
				// androidUserID +" token id:    "+userTokenId);
				// startActivity(Intent.createChooser(emailIntent,
				// "Send email..."));

			} else {

				postUserTokenIdSync = new PostUserTokenIdSync();
				postUserTokenIdSync.execute();

				// {"userId":"152","userNotificationFlag":"No"}

			}
		} else {
			Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();
			finishAffinity();
		}
		editTextLocation.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				tap_current_location.setVisibility(View.VISIBLE);
				return false;

			}
		});
		// Adding textchange listener
		editTextLocation.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Creating a DownloadTask to download Google Places matching
				// "s"
				if (Network.isNetworkConnected(context)) {

					if (s.toString().trim().contains(strCurrentLocation)) {
						tap_current_location.setVisibility(View.GONE);
						hideSoftKeyboard();
						// KeyboardUtility.hideSoftKeyboard(activity);
					} else if (s.toString().trim().contains(strSelectedLocation)) {
						tap_current_location.setVisibility(View.VISIBLE);
						// hideSoftKeyboard();
						// KeyboardUtility.hideSoftKeyboard(activity);
					} else {
						tap_current_location.setVisibility(View.VISIBLE);
					}
					if (s.length() == 0) {
						tap_current_location.setVisibility(View.GONE);
					}

					placesDownloadTask = new DownloadTask();

					// Getting url to the Google Places Autocomplete api
					String url = getAutoCompleteUrl(s.toString().trim());

					// Start downloading Google Places
					// This causes to execute doInBackground() of DownloadTask
					// class
					placesDownloadTask.execute(url);
				} else {
					Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		// Setting an item click listener for the AutoCompleteTextView dropdown
		// list
		locationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {
				// TODO Auto-generated method stub
				// hideSoftKeyboard();
				// tap_current_location.setText(stringList.get(position).toString());

				editTextLocation.setText(stringList.get(index).toString());

				strSelectedLocation = stringList.get(index).toString();

				hideSoftKeyboard();

				// Creating a DownloadTask to download Places details of
				// the selected place
				try {
					// Geocoder geocoders = new Geocoder(CastingScreen.this);
					List<Address> addr;
					try {
						addr = geocoder.getFromLocationName(stringList.get(index).toString(), 1);
						if (addr.size() > 0) {
							dlatitude = addr.get(0).getLatitude();
							dlongitude = addr.get(0).getLongitude();

							// Toast.makeText(CastingScreen.this, "Lat "+
							// dlatitude+"\n Long "+ dlongitude,
							// Toast.LENGTH_SHORT).show();

						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (NullPointerException e) {
					// TODO: handle exception
					Toast.makeText(context, "Please enable the GPS to see filter casting on location based.", Toast.LENGTH_LONG).show();
				}

				tap_current_location.setVisibility(View.GONE);

			}
		});
		// if (ratePopFlag == false) {
		// if (rateThisAppFlag >= 3) {
		// rateThisAppAlert();
		// }
		// }

		if (strPerformanceType.contains("actor")) {
			chkActor.setChecked(true);
			chkActor.setTextColor(context.getResources().getColor(R.color.white));
		}

		if (strPerformanceType.contains("model")) {
			chkModel.setChecked(true);
			chkModel.setTextColor(context.getResources().getColor(R.color.white));
		}
		if (strPerformanceType.contains("singer")) {
			chkSinger.setChecked(true);
			chkSinger.setTextColor(context.getResources().getColor(R.color.white));
		}
		if (strPerformanceType.contains("dancer")) {
			chkDancer.setChecked(true);
			chkDancer.setTextColor(context.getResources().getColor(R.color.white));
		}

		if (strGender.contains("female")) {
			chkFemale.setChecked(true);
			chkFemale.setTextColor(context.getResources().getColor(R.color.white));
		} else if (strGender.equals("male")) {
			chkMale.setChecked(true);
			chkMale.setTextColor(context.getResources().getColor(R.color.white));
		}
		if (UnionType == true) {

			chkUnion.setChecked(true);
			chkUnion.setTextColor(context.getResources().getColor(R.color.white));
		}
		if (strUnionType.contains("non-union")) {

			chkNonUnion.setChecked(true);
			chkNonUnion.setTextColor(context.getResources().getColor(R.color.white));
		}
		if (strBithYear != null) {
			birthYear.setText(strBithYear);

		}

		if (strEthnicity != null) {
			txtEthnicity.setText(strEthnicity);
		}

		favCountText.setOnClickListener(this);
		txtReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
				hideSoftKeyboard();

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

				alertDialogBuilder

				.setMessage("            Reset Filter            ").setCancelable(false).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int id) {
						if (rel_image_screen.getVisibility() == View.VISIBLE) {
							rel_image_screen.setVisibility(View.GONE);
							rel_upload_screen.setVisibility(View.GONE);
						}
						castImageViewStatus = false;
						gear_icon.setVisibility(View.VISIBLE);
						outbox_icon.setVisibility(View.VISIBLE);
						// if (listViewStatus)
						// castingsListIconChange.setVisibility(View.VISIBLE);
						// else
						// castingsListIcon.setVisibility(View.VISIBLE);

						gear_icon_mirror.setVisibility(View.INVISIBLE);
						chkActor.setTextColor(context.getResources().getColor(R.color.dark_black));
						chkActor.setChecked(false);
						chkModel.setTextColor(context.getResources().getColor(R.color.dark_black));
						chkModel.setChecked(false);
						chkSinger.setTextColor(context.getResources().getColor(R.color.dark_black));
						chkSinger.setChecked(false);
						chkDancer.setTextColor(context.getResources().getColor(R.color.dark_black));
						chkDancer.setChecked(false);
						chkMale.setTextColor(context.getResources().getColor(R.color.dark_black));
						chkMale.setChecked(false);
						chkFemale.setTextColor(context.getResources().getColor(R.color.dark_black));
						chkFemale.setChecked(false);
						chkUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
						chkUnion.setChecked(false);
						chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
						chkNonUnion.setChecked(false);
						birthYear.setText("");
						dlatitude = dclatitude;
						dlongitude = dclongitude;
						editTextLocation.setText(strCurrentLocation);
						txtEthnicity.setText("");
						if (selchkboxlist != null) {
							selchkboxlist.clear();
						}
						CastivateApplication.getInstance().hashMap.clear();
						bs = false;
						strPerformanceType = "";
						strGender = "";
						strBithYear = "";
						strEthnicity = "";
						strUnionType = "";

						page = 0;
						totalPages = 0;
						castViewStatus = false;
						castViewStatusreak = false;
						swipe = "";
						bSwipe = "";
						swpe = true;
						position = 0;
						bs = false;
						if (castivityScreen.getVisibility() == View.VISIBLE) {
							castivityScreen.setVisibility(View.GONE);
						}
						castingViewNoImage.setVisibility(View.VISIBLE);
						castingsList.setVisibility(View.GONE);
						dlongitude = dclongitude;
						dlatitude = dclatitude;
						grid_view_photos.setVisibility(View.GONE);
						swipeRefreshLayout.setVisibility(View.GONE);
						myList = new ArrayList<CastingDetailsModel>();
						castingsListArr = new ArrayList<CastingDetailsModel>();
						handlerCastingSync.sendEmptyMessage(0);
					}
				})

				.setPositiveButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

			}
		});

		rel_cast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rel_upload_screen.setEnabled(true);

				// KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
				hideSoftKeyboard();
				castingViewNoImage.setVisibility(View.VISIBLE);
				castingsList.setVisibility(View.GONE);
				if (rel_image_screen.getVisibility() == View.VISIBLE) {
					rel_image_screen.setVisibility(View.GONE);
					rel_upload_screen.setVisibility(View.GONE);
					castImageViewStatus = false;
				}
				// if (listViewStatus)
				// castingsListIconChange.setVisibility(View.VISIBLE);
				// else
				// castingsListIcon.setVisibility(View.VISIBLE);
				gear_icon.setVisibility(View.VISIBLE);
				grid_view_photos.setVisibility(View.GONE);
				swipeRefreshLayout.setVisibility(View.GONE);
				tap_current_location.setVisibility(View.GONE);
				outbox_icon.setVisibility(View.VISIBLE);
				gear_icon_mirror.setVisibility(View.INVISIBLE);
				castivityScreen.setVisibility(View.VISIBLE);
				if (castivityScreen.getVisibility() == View.VISIBLE) {
					castivityScreen.setVisibility(View.GONE);
				}
				castingViewNoImage.setVisibility(View.VISIBLE);
				castingsList.setVisibility(View.GONE);
				// filterValue=1;

				myList = new ArrayList<CastingDetailsModel>();
				castingsListArr = new ArrayList<CastingDetailsModel>();
				page = 0;
				CastivateApplication.getInstance().hashMap.clear();
				position = 0;

				castViewStatus = false;
				castViewStatusreak = false;
				swipe = "";
				bSwipe = "";
				swpe = true;
				bs = false;
				totalPages = 0;

				handlerCastingSync.sendEmptyMessage(0);
			}
		});
		select_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
				hideSoftKeyboard();

				rel_upload_screen.setEnabled(true);
				try {
					currentLocation();
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				// v.startAnimation(animScale);

				// Handler handler = null;
				// handler = new Handler();
				// handler.postDelayed(new Runnable() {
				// public void run() {
				overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
				gear_icon.setVisibility(View.VISIBLE);
				// if (listViewStatus)
				// castingsListIconChange.setVisibility(View.VISIBLE);
				// else
				// castingsListIcon.setVisibility(View.VISIBLE);
				outbox_icon.setVisibility(View.VISIBLE);
				gear_icon_mirror.setVisibility(View.INVISIBLE);
				grid_view_photos.setVisibility(View.GONE);
				swipeRefreshLayout.setVisibility(View.GONE);
				if (castivityScreen.getVisibility() == View.VISIBLE) {
					castivityScreen.setVisibility(View.GONE);
				}
				// filterValue=1;
				// list = new ArrayList<CastingDetailsModel>();
				myList = new ArrayList<CastingDetailsModel>();
				castingsListArr = new ArrayList<CastingDetailsModel>();
				myList.clear();
				page = 0;
				bs = false;
				position = 0;

				castViewStatus = false;
				castViewStatusreak = false;
				swipe = "";
				bSwipe = "";
				swpe = true;

				totalPages = 0;

				CastivateApplication.getInstance().hashMap.clear();
				tap_current_location.setVisibility(View.GONE);
				castingViewNoImage.setVisibility(View.VISIBLE);
				castingsList.setVisibility(View.GONE);
				if (rel_image_screen.getVisibility() == View.VISIBLE) {
					rel_image_screen.setVisibility(View.GONE);
					rel_upload_screen.setVisibility(View.GONE);
					castImageViewStatus = false;
				}
				myList = new ArrayList<CastingDetailsModel>();
				JSONObject props = new JSONObject();
				try {
					props.put("User ID", androidUserID);
					props.put("Location", strSelectedLocation);
					props.put("Gender", strGender);
					props.put("Birth Year", strBithYear);
					props.put("Ethnicity", strEthnicity);
					props.put("Performance Type", strPerformanceType);
					props.put("Union Type", strUnionType);
					props.put("Android Version", os);
					// props.put("Castivate Version", )

					props.put("UserID", androidUserID);
					mixpanel.track("Filter Criteria", props);
					props.put("Plan", "Premium");
					mixpanel.getPeople().identify(androidUserID);

				} catch (JSONException e) {
					// TODO: handle exception
				}

				handlerCastingSync.sendEmptyMessage(0);

			}
			// }, 1500);
			//
			// }
		});

		clickEvents();

		// DebugReportOnLocat.ln("Register Id:"+registerID);
		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		rel_cast_view.setOnTouchListener(gestureListener);
		lay_submission_detail_no_image.setOnTouchListener(gestureListener);
		casting_image.setOnTouchListener(gestureListener);
		rel_cast_outer.setOnTouchListener(gestureListener);

		// Sugumaran Changes (25th May 16)
		// imgGestureDetector = new GestureDetector(this, new
		// ImageGestureDetector());
		// imgGestureListener = new View.OnTouchListener() {
		// public boolean onTouch(View v, MotionEvent event) {
		// return imgGestureDetector.onTouchEvent(event);
		// }
		// };
		// // Sugumaran Changes (25th May 16)
		// casting_image_only.setOnTouchListener(imgGestureListener);

		casting_image_only.setOnTouchListener(gestureListener);

		// grid_view_photos.setOnTouchListener(gestureListener);
		try {
			currentLocation();

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		// home_screen
		// rel_upload_screen

		// System.out.println("TopBar width * Height: "+home_screen.getWidth()+" x "+home_screen.getHeight());
		// System.out.println("BottomBar width * Height: "+rel_upload_screen.getWidth()+" x "+rel_upload_screen.getHeight());

		mixpanel.getPeople().identify(androidUserID);
		mixpanel.getPeople().set("Plan", "Premium");
	}

	@SuppressLint("InlinedApi")
	private void findElements() {

		castingsList = (ListView) findViewById(R.id.rel_info_list);
		imgGrid = (TextView) findViewById(R.id.imgGrid);
		local_talent = (TextView) findViewById(R.id.local_talent);
		txtName = (TextView) findViewById(R.id.txtName);

		grid_view_photos = (GridView) findViewById(R.id.grid_view_photos);

		rel_info_ListView = (RelativeLayout) findViewById(R.id.rel_info_ListView);

		// grid_view_photos.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// // TODO Auto-generated method stub
		//
		// String getName =
		// myListImage.get(position).userName.toString().trim();
		// txtName.setVisibility(View.VISIBLE);
		// if (getName.equals("")) {
		// txtName.setVisibility(View.GONE);
		// } else {
		// txtName.setText(getName);
		// }
		//
		// imgGrid.setVisibility(View.VISIBLE);
		// local_talent.setVisibility(View.VISIBLE);
		// rel_cast_view.setVisibility(View.VISIBLE);
		// grid_view_photos.setVisibility(View.GONE);
		// gridViewStatus = false;
		// casting_image.setVisibility(View.VISIBLE);
		// rel_upload_screen.setVisibility(View.VISIBLE);
		// Picasso.with(context).load(myListImage.get(position).imageRole).into(casting_image);
		//
		// }
		// });

		imgGrid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// castingsListIcon.setVisibility(View.GONE);

				// grid_view_photos.removeAllViews();

				swipeRefreshLayout.setVisibility(View.VISIBLE);
				imgFav.setVisibility(View.GONE);

				rel_image_screen.setVisibility(View.VISIBLE);
				imgGrid.setVisibility(View.GONE);
				local_talent.setVisibility(View.GONE);
				// rel_cast_view.setVisibility(View.GONE);
				casting_image.setVisibility(View.GONE);
				txtName.setVisibility(View.GONE);

				grid_view_photos.setVisibility(View.VISIBLE);
				swipeRefreshLayout.setVisibility(View.VISIBLE);
				gridViewStatus = true;
				imageGrid();

				int oldPage = page;
				page = 0;

				// Sugumaran Changes (25th May 16)
				casting_image_only.setVisibility(View.GONE);

			}
		});
		progressView = (ProgressBar) findViewById(R.id.progress_view);
		castingsListIcon = (ImageView) findViewById(R.id.castings_list_icon);
		castingsListIcon.setOnClickListener(this);
		// castingsListIconChange = (ImageView)
		// findViewById(R.id.castings_list_icon_change);
		// castingsListIconChange.setOnClickListener(this);

		lay_submission_detail_no_image = (LinearLayout) findViewById(R.id.lay_submission_detail_no_image);

		rel_home_screen = (RelativeLayout) findViewById(R.id.rel_home_screen);
		rel_image_screen = (RelativeLayout) findViewById(R.id.rel_image_screen);

		help_overlay = (ImageView) findViewById(R.id.help_overlay);
		help_overlay.setOnTouchListener(this);

		gear_icon = (ImageView) findViewById(R.id.gear_icon);
		editTextLocation = (EditText) findViewById(R.id.current_location);
		editTextLocation.setOnClickListener(this);
		locationListView = (ListView) findViewById(R.id.lView);

		outbox_icon = (ImageView) findViewById(R.id.outbox_icon);
		outbox_icon.setVisibility(View.VISIBLE);
		gear_icon_mirror = (ImageView) findViewById(R.id.gear_icon_mirror);
		home_screen = (RelativeLayout) findViewById(R.id.top_bar);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
		if (notification == true) {
			toggleButton.setChecked(true);
		} else {
			toggleButton.setChecked(false);
		}
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				JSONObject props = new JSONObject();
				if (arg1 == true) {
					userNotificationFlag = "Yes";
					notification = true;
					sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
					editor = sharedpreferences.edit();
					editor.putBoolean(Library.NOTIFICATION, notification);
					editor.commit();
					toggleButton.setChecked(true);

					try {
						props.put("User ID", androidUserID);
						props.put("Notification status", "On");
						mixpanel.track("Notification", props);
					} catch (JSONException e) {

					}
					postSetNotifSync = new PostSetNotifSync();
					postSetNotifSync.execute();
				} else {
					userNotificationFlag = "No";
					toggleButton.setChecked(false);
					notification = false;
					sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
					editor = sharedpreferences.edit();
					editor.putBoolean(Library.NOTIFICATION, notification);
					editor.commit();

					try {
						props.put("User ID", androidUserID);
						props.put("Notification status", "Off");
						mixpanel.track("Notification", props);
					} catch (JSONException e) {
						// TODO: handle exception
					}

					postSetNotifSync = new PostSetNotifSync();
					postSetNotifSync.execute();
				}

			}
		});

		rel_cast_view = (RelativeLayout) findViewById(R.id.rel_cast_view);

		tap_current_location = (RelativeLayout) findViewById(R.id.tap_current_location);
		tap_current_location.setOnClickListener(this);
		if (tap_current_location.getVisibility() == View.VISIBLE)
			tap_current_location.setVisibility(View.GONE);
		frlay = (FrameLayout) findViewById(R.id.frlay);

		rel_cast = (RelativeLayout) findViewById(R.id.rel_cast);

		castivityScreen = (LinearLayout) findViewById(R.id.castivity_screen);

		casting_image = (ImageView) findViewById(R.id.casting_image);

		// Sugumaran Changes (25th May 16)
		casting_image_only = (ImageView) findViewById(R.id.casting_image_only);

		select_icon = (TextView) findViewById(R.id.select_icon);
		clear = (ImageView) findViewById(R.id.clear);
		clear.setOnClickListener(this);

		birthClear = (ImageView) findViewById(R.id.birth_clear);
		birthClear.setOnClickListener(this);
		castingViewNoImage = (RelativeLayout) findViewById(R.id.casting_view_no_image);

		home_screen.setVisibility(View.VISIBLE);
		rel_upload_screen = (RelativeLayout) findViewById(R.id.rel_upload_screen);

		rel_upload_screen.setOnClickListener(this);

		rel_cast_outer = (RelativeLayout) findViewById(R.id.rel_cast_outer);
		chkActor = (CheckBox) findViewById(R.id.checkbox_actor);
		chkModel = (CheckBox) findViewById(R.id.txt_model);
		chkSinger = (CheckBox) findViewById(R.id.txt_singer);
		chkDancer = (CheckBox) findViewById(R.id.txt_dancer);

		chkMale = (CheckBox) findViewById(R.id.txt_male);
		chkFemale = (CheckBox) findViewById(R.id.txt_female);

		chkUnion = (CheckBox) findViewById(R.id.chk_union);
		chkNonUnion = (CheckBox) findViewById(R.id.chk_non_union);

		birthYear = (TextView) findViewById(R.id.birth_year);

		txtEthnicity = (TextView) findViewById(R.id.txt_ethnicity);

		txtEthnicity.setOnClickListener(this);
		txtReset = (TextView) findViewById(R.id.text_reset);
		text_help_over_lay = (TextView) findViewById(R.id.text_help_over_lay);
		text_help_over_lay.setOnClickListener(this);
		txtSubmitCasting = (TextView) findViewById(R.id.txt_submit_casting);
		txtSubmitCasting.setOnClickListener(this);
		textCastivate = (TextView) findViewById(R.id.text_castivate);

		// new design to view casting no image
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
		fav_iconNoImage = (CheckBox) findViewById(R.id.fav_icon_select_no_image);
		favCountText = (TextView) findViewById(R.id.fav_count);
		favCountText.setVisibility(View.INVISIBLE);
		textDistanceNoImage = (TextView) findViewById(R.id.distance_no_image);

	}

	private void ethnicityDialog() {

		final Dialog dialog = new Dialog(CastingScreen.this, android.R.style.Theme_DeviceDefault_Dialog);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.ethnicity);
		dialog.setCancelable(false);

		ethnicityListView = (ListView) dialog.findViewById(R.id.ethnicity_list);

		ethnicityList = new ArrayList<EthnicityModel>();

		ImageView backBtn = (ImageView) dialog.findViewById(R.id.back_arrow);

		ImageView selectBtn = (ImageView) dialog.findViewById(R.id.select_icon);

		for (int i = 0; i < ethnicity.length; i++) {

			EthnicityModel ethnicityModel = new EthnicityModel(ethnicity[i], false);
			ethnicityList.add(ethnicityModel);
		}
		if (strEthnicity != null) {
			// split the string using separator, in this case it is ","
			String[] items = strEthnicity.split(", ");
			selchkboxlist = new ArrayList<String>();
			for (String item : items) {
				selchkboxlist.add(item);
			}

			DebugReportOnLocat.ln("Java String converted to ArrayList: " + selchkboxlist);
		}

		for (int i = 0; i < ethnicityList.size(); i++) {
			if (selchkboxlist != null)
				for (int j = 0; j < selchkboxlist.size(); j++) {

					if (ethnicityList.get(i).name.equals(selchkboxlist.get(j))) {

						ethnicityList.get(i).checked = true;
					}
				}
		}
		ethnicityListView.setAdapter(new EthnicityFilterAdapter(CastingScreen.this, ethnicityList));

		selectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ethnicityFilter();

				dialog.dismiss();
			}

		});
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ethnicityFilter();
				dialog.dismiss();
			}
		});
		// Create and populate ethnicity.
		// private String[] ethnicity = {"Caucasian",
		// "African American", "Hispanic", "Asian", "Mixed",
		// "Native American","Middle Eastern", "Other"};

		dialog.show();
		// =====================

	}

	private void ethnicityFilter() {
		// TODO Auto-generated method stub
		int count = ethnicityListView.getAdapter().getCount();
		selchkboxlist = new ArrayList<String>();
		strEthnicity = "";
		for (int i = 0; i < count; i++) {
			RelativeLayout itemLayout = (RelativeLayout) ethnicityListView.getChildAt(i); // Find
																							// by
																							// under
																							// LinearLayout
			CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.ethnicity_checkbox);

			if (checkbox.isChecked()) {
				Log.d("Item " + String.valueOf(i), checkbox.getTag().toString());
				selchkboxlist.add(checkbox.getTag().toString());
				// String str2 =
				// Arrays.asList(selchkboxlist).toString();

				strEthnicity = selchkboxlist.toString();

				DebugReportOnLocat.ln("items selected:::" + selchkboxlist);
				// Toast.makeText(Activities.this,checkbox.getTag().toString()
				// ,Toast.LENGTH_LONG).show();

			}

		}

		strEthnicity = strEthnicity.replace("[", "").replace("]", "");
		if (strEthnicity.equals("")) {
			txtEthnicity.setText("Ethnicity");
		} else {
			txtEthnicity.setText(strEthnicity);
		}

	}

	TextView curLoc;
	LinearLayout auto_complete_view;
	View autoCompleteView;

	private void rateThisAppAlert() {
		// TODO Auto-generated method stub

		final Dialog dialog = new Dialog(context);
		// Set Dialog Title
		dialog.setTitle("Rate Castivate");
		dialog.setCancelable(false);
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(context);
		tv.setText("If you enjoy using Castivate App, would you mind taking a moment to rate it? It won't take more than a minute. Thanks for your support!");
		tv.setTextSize(18);
		tv.setGravity(Gravity.CENTER);
		// tv.setLayoutParams(new LayoutParams(500, 500));
		tv.setPadding(10, 10, 10, 10);

		ll.addView(tv);

		// First Button
		Button b1 = new Button(context);
		b1.setText("Rate this App");
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					context.startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(context, "Couldn't launch the Play Store.", Toast.LENGTH_LONG);
				}

				SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
				Editor editor = prefs.edit();
				editor.putBoolean(Library.RATEIT_FLAG, true);
				editor.commit();

				// sharedpreferences =
				// getSharedPreferences(Library.MyPREFERENCES,
				// Context.MODE_PRIVATE);
				// ratePopFlag = true;
				// editor.putBoolean(Library.RATEIT_FLAG, ratePopFlag);
				// editor.commit();
				dialog.dismiss();

			}
		});
		ll.addView(b1);

		// Second Button
		Button b2 = new Button(context);
		b2.setText("Remind me later");
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();

				//
				// sharedpreferences =
				// getSharedPreferences(Library.MyPREFERENCES,
				// Context.MODE_PRIVATE);
				// if (sharedpreferences.contains(Library.RATEIT)) {
				// SharedPreferences.Editor editor = sharedpreferences.edit();
				// rateCount = -1;
				// editor.putInt(Library.RATEIT, rateCount);
				// editor.commit();
				// }

			}
		});
		ll.addView(b2);

		// Third Button
		Button b3 = new Button(context);
		b3.setText("No, Thanks");
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
				Editor editor = prefs.edit();
				editor.putBoolean(Library.RATEIT_FLAG, true);
				editor.commit();

				// sharedpreferences =
				// getSharedPreferences(Library.MyPREFERENCES,
				// Context.MODE_PRIVATE);
				//
				// ratePopFlag = true;
				// editor.putBoolean(Library.RATEIT_FLAG, ratePopFlag);
				// editor.commit();
				dialog.dismiss();
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);

		// Show Dialog
		dialog.show();

	}

	Geocoder geocoder;
	List<Address> addresses;
	Address returnAddress;

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

			// \n is for new line
			// Toast.makeText(getApplicationContext(),
			// "Your Location is - \nLat: " + latitude + "\nLong: " +
			// longitude, Toast.LENGTH_LONG).show();
			try {
				geocoder = new Geocoder(context, Locale.ENGLISH);
				addresses = geocoder.getFromLocation(dclatitude, dclongitude, 1);
				StringBuilder str = new StringBuilder();
				if (geocoder.isPresent()) {
					// Toast.makeText(getApplicationContext(),
					// "geocoder present", Toast.LENGTH_SHORT).show();

					if (strSelectedLocation.equals("")) {
						dlatitude = dclatitude;
						dlongitude = dclongitude;

						// curlatitude=dlatitude;
						// curlongitude=dlongitude;
						try {
							if (addresses != null)
								returnAddress = addresses.get(0);
							String localityString = returnAddress.getLocality();
							String country = returnAddress.getCountryName();

							String region_code = returnAddress.getCountryCode();
							// String zipcode =
							// returnAddress.getPostalCode();

							str.append(localityString + " ");
							str.append(country + ", " + region_code + "");
							// str.append(zipcode + "");

							// if (strSelectedLocation.equals("")) {
							if (str != null) {
								editTextLocation.setText(str);
								strSelectedLocation = str.toString();
								strCurrentLocation = str.toString();

							}

							sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
							editor = sharedpreferences.edit();

							editor.putString(Library.CURRENT_LOCATION, strCurrentLocation);
							editor.commit();
						} catch (IndexOutOfBoundsException e) {

							e.printStackTrace();
						}

					} else {

						editTextLocation.setText(strSelectedLocation);
					}

				} else {
					Toast.makeText(getApplicationContext(), "geocoder not present", Toast.LENGTH_SHORT).show();
				}

			} catch (IOException e) {

				Log.e("tag", e.getMessage());
			}

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			showSettingsAlert();
		}
	}

	private Handler handlerCastingSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(CastingScreen.this)) {
				rel_upload_screen.setVisibility(View.GONE);
				castingsList.setEnabled(false);
				new GetDatas().execute();

			} else {
				Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
				finishAffinity();
			}
		}
	};

	private Handler handlerCastingImagesSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(CastingScreen.this)) {
				rel_upload_screen.setVisibility(View.GONE);
				new GetImages().execute();
			} else {
				Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
				finishAffinity();
			}
		}
	};

	private void clickMenuEvents() {
		gear_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				rel_upload_screen.setEnabled(false);

				if (castivityScreen.getVisibility() == View.VISIBLE) {
					castivityScreen.setVisibility(View.GONE);
					outbox_icon.setVisibility(View.VISIBLE);
					gear_icon_mirror.setVisibility(View.GONE);
					gear_icon.setVisibility(View.VISIBLE);
					castingsListIcon.setVisibility(View.VISIBLE);

					// ///

					// if (listViewStatus)
					// castingsListIconChange.setVisibility(View.VISIBLE);
					// else

				} else {
					// castingsListIconChange.setVisibility(View.INVISIBLE);
					castingsListIcon.setVisibility(View.INVISIBLE);

					gear_icon.setVisibility(View.INVISIBLE);
					castivityScreen.setVisibility(View.VISIBLE);
					outbox_icon.setVisibility(View.GONE);
					gear_icon_mirror.setVisibility(View.VISIBLE);
					castivityScreen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));
					overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

				}
			}

		});
		gear_icon_mirror.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyboard();

				if (castivityScreen.getVisibility() == View.VISIBLE) {
					// if (listViewStatus)
					// castingsListIconChange.setVisibility(View.VISIBLE);
					// else
					// castingsListIcon.setVisibility(View.VISIBLE);

					rel_upload_screen.setEnabled(true);

					castingsListIcon.setVisibility(View.VISIBLE);

					castivityScreen.setVisibility(View.GONE);
					outbox_icon.setVisibility(View.VISIBLE);
					gear_icon_mirror.setVisibility(View.GONE);
					tap_current_location.setVisibility(View.GONE);
					gear_icon.setVisibility(View.VISIBLE);
					overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
				}
			}

		});

		outbox_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				view = findViewById(R.id.rel_home_screen);// your layout id
				view.getRootView();
				String state = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					File picDir = new File(Environment.getExternalStorageDirectory() + "/Castivate");
					if (!picDir.exists()) {
						picDir.mkdir();
					}
					view.setDrawingCacheEnabled(true);
					view.buildDrawingCache(true);
					Bitmap bitmap = view.getDrawingCache();
					// Date date = new Date();
					String fileName = "castivate" + ".jpeg";
					picFile = new File(picDir + "/" + fileName);
					try {
						picFile.createNewFile();
						FileOutputStream picOut = new FileOutputStream(picFile);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) (bitmap.getHeight()));
						boolean saved = bitmap.compress(CompressFormat.JPEG, 100, picOut);
						if (saved) {
							// Toast.makeText(getApplicationContext(),
							// "Image saved to your device Pictures "+
							// "directory!", Toast.LENGTH_SHORT).show();
						} else {
							// Error
						}
						picOut.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					view.destroyDrawingCache();
				} else {
					// Error

				}

				Intent chooser = new Intent(Intent.ACTION_SEND);

				chooser.setType("image/*");
				chooser.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picFile));
				if (rel_image_screen.getVisibility() == View.VISIBLE) {
					chooser.putExtra(Intent.EXTRA_TEXT, "Hey, Check out this awesome talent on Castivate! Click the URL below:  http://www.castivate.com/get");
					chooser.putExtra(Intent.EXTRA_SUBJECT, "A local talent from Castivate!");

				} else {
					chooser.putExtra(Intent.EXTRA_TEXT, "Hey, Check out this awesome casting on Castivate! Click the URL below:  http://www.castivate.com/get");
					chooser.putExtra(Intent.EXTRA_SUBJECT, "A casting from Castivate!");

				}

				chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

				startActivity(chooser);
				JSONObject props = new JSONObject();

				try {
					props.put("User ID", androidUserID);
					props.put("Casting Title", myList.get(position).castingTitle.toString().trim());
					props.put("Casting ID", myList.get(position).roleId.toString().trim());
				}

				catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}

				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mixpanel.track("Share Social Network", props);

			}
		});

	}

	@SuppressLint("InflateParams")
	public void alert(ArrayList<String> year_values, final TextView text) {
		// TODO Auto-generated method stub
		final Dialog alertDialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
		// alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setTitle("Birth Year");

		LayoutInflater inflater = getLayoutInflater();
		View convertView = (View) inflater.inflate(R.layout.alert_spinner, null);
		alertDialog.setContentView(convertView);
		final ListView lv = (ListView) convertView.findViewById(R.id.list_spinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, year_values);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String selectedFromList = (String) (lv.getItemAtPosition(position));
				text.setText(selectedFromList);
				text.setTextColor(Color.BLACK);
				strBithYear = selectedFromList;
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
	}

	@Override
	public void onPause() {

		super.onPause();

		DebugReportOnLocat.ln("onpause:");

	}

	@Override
	protected void onStart() {

		super.onStart();
		// KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
		hideSoftKeyboard();
		// FlurryAgent.onStartSession(this, "7XR67SX3652ZF6VCSDPS");
		DebugReportOnLocat.ln("onstart:");
	}

	@Override
	protected void onStop() {

		super.onStop();
		// FlurryAgent.onEndSession(this);
		DebugReportOnLocat.ln("onstop:");
	}

	@Override
	protected void onResume() {
		if (progressBar != null && progressBar.isShowing()) {
			progressBar.dismiss();
		}
		DebugReportOnLocat.ln("onresume:");
		super.onResume();
		// KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
		hideSoftKeyboard();
		((CastingsLinkMovementMethod) CastingsLinkMovementMethod.getInstance()).setContext(context, false);

		// results = new ArrayList<CastingListDetailsModel>();
		// _itemListAdapter = new ItemListBaseAdapter(this, image_details);
		// refreshYourAdapter(GetSearchResults());
		// lv1.setAdapter(_itemListAdapter);

		// refreshYourAdapter(castingsListArr);
		// castingsList.setAdapter(castListAdapter);
		// castListAdapter.notifyDataSetChanged();

	}

	private void refreshYourAdapter(final ArrayList<CastingDetailsModel> items) {
		// this is what I meant. The error clearly states you are not updating
		// the adapter on the UI thread
		runOnUiThread(new Runnable() {
			public void run() {
				if (items != null && items.size() != 0) {
					castListAdapter.refreshAdapter(items);
				}
			}
		});
	}

	private String getAutoCompleteUrl(String place) {

		// Obtain browser key from https://code.google.com/apis/console
		// String key = "key=AIzaSyAq-4noVLUBMP-kbkeJoQeAjkYb8sPuseI";
		String key = "key=AIzaSyDG78fc_ZTNzO7OqoVaL-o_Q_6aJ9mLwas";

		// place to be be searched
		place = place.replaceAll(" ", "%20");
		String input = "input=" + place.trim();
		// reference of countries
		String country = "components=country:us";
		// place type to be searched
		// String types = "types=(cities)";
		String types = "types=(regions)";
		// String radius = "radius=" + PROXIMITY_RADIUS;
		// Sensor enabledo
		String sensor = "sensor=false";
		// (cities)&language=pt_BR
		// Building the parameters to the web service
		String parameters = input + "&" + country + "&" + types + "&"
		/* + language+ "&" */+ sensor + "&" + key;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

		return url;
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

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();
			DebugReportOnLocat.ln("data " + data);
			br.close();

		} catch (Exception e) {
			//Log.d("Exception while downloading url", e.toString());
			System.out.println("Exception while downloading url"+ e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	String[] from = null;
	int[] to = null;
	JSONObject jObject;

	// List<HashMap<String, String>> listLocation = new
	// ArrayList<HashMap<String, String>>();

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
				DebugReportOnLocat.ln("data " + data);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// tap_current_location.setVisibility(View.GONE);

			try {

				jObject = new JSONObject(result);
				JSONArray jsonArray = jObject.getJSONArray("predictions");
				// PlaceJSONParser placeJsonParser = new PlaceJSONParser();
				// list = placeJsonParser.parse(jObject);
				from = new String[jsonArray.length()];
				to = new int[jsonArray.length()];

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					from[i] = object.get("description").toString();
					to[i] = android.R.id.text1;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				if (from.length > 0) {

					// StringBuilder sb = new StringBuilder();
					// for (int i = 0; i < from.length; i++) {
					// sb.append(from[i] + "\n");
					// HashMap<String, String> h = new HashMap<String,
					// String>();
					// h.put(from[i], from[i]);
					// listLocation.add(h);
					// }
					try {
						// String ar[] = new String[3];
						// ar[0] = from[0];
						// ar[1] = from[1];
						// ar[2] = from[2];

						stringList = new ArrayList<String>(Arrays.asList(from));

						if (!stringList.isEmpty()) {
							try {
								// tap_current_location
								// .setVisibility(View.VISIBLE);
								// frlay.setVisibility(View.VISIBLE);
								searchAdapter = new LocationSearchAdapter(CastingScreen.this, stringList);

								locationListView.setAdapter(searchAdapter);

								searchAdapter.notifyDataSetChanged();

								ListExpandable.getListViewSize(locationListView);
								FrameLayout frlay = (FrameLayout) findViewById(R.id.frlay);
								frlay.bringToFront();
							} catch (IllegalStateException e) {
								e.printStackTrace();
							}

						}
					} catch (IndexOutOfBoundsException e) {
						// TODO: handle exception
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	private PostUserTokenIdSync postUserTokenIdSync;

	public class PostUserTokenIdSync extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog = null;

		@Override
		public void onPreExecute() {
			pDialog = new ProgressDialog(CastingScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			postJSONData();
			return null;
		}

		@Override
		public void onPostExecute(Void params) {
			if (pDialog != null) {
				if (pDialog.isShowing())
					pDialog.dismiss();
			}
			notifFlag = true;
			handlerCastingSync.sendEmptyMessage(0);

		}

	}

	private PostSetNotifSync postSetNotifSync;

	public class PostSetNotifSync extends AsyncTask<String, Void, Void> {

		// @Override
		// public void onPreExecute() {
		// pDialog = new ProgressDialog(CastingScreen.this,
		// AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		// pDialog.setMessage("Please wait...");
		// pDialog.setCancelable(false);
		// pDialog.show();
		// }

		@Override
		protected Void doInBackground(String... params) {

			postJSONNotifData();
			return null;
		}

		@Override
		public void onPostExecute(Void params) {
			// if (pDialog != null) {
			// if (pDialog.isShowing())
			// pDialog.dismiss();
			notifFlag = false;
			// }

			// handlerCastingSync.sendEmptyMessage(0);

		}

	}

	InputStream is = null;
	String json = "";
	JSONObject jObj = null;
	JSONArray jArr = null;
	StringBuilder sb;

	String Status;

	public void postJSONData() {

		System.out.println("set token service --------------------->");
		try {
			HttpPost request = new HttpPost(HttpUri.SETTOKEN);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			JSONStringer item = null;

			try {
				// {"userTokenId":"3b10484355df66599afc32d6652383ed4f4b8ba6429f7821480b505ebe1299djjjjj4111","userDeviceId":"aaabbb"}
				item = new JSONStringer().object().key("userTokenId").value(userTokenId).key("userDeviceId").value(regId).endObject();
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

						System.out.println("JSON Result ----------------> "+json);

						try {

							JSONObject oneObject = new JSONObject(json);

							System.out.println("User ID ------------------------------> "+oneObject.getString("userId"));

							System.out.println("Remaining Days ------------------------------> "+oneObject.getString("remainingdays"));

							//jArr = new JSONArray(json);
							//DebugReportOnLocat.ln("json response for submit::" + jArr);

							//JSONObject oneObject = jArr.getJSONObject(0); // Pulling
							// items
							// from
							// the
							// array
							//System.out.println("User id : ---------------------> " + oneObject.getString("userId"));

							Library.androidUserID = oneObject.getString("userId");
							androidUserID = Library.androidUserID;
							Library.remainingDays=oneObject.getString("remainingdays");
							remainingDays=Library.remainingDays;
						}
						catch (JSONException e) {
							System.out.println("Exception : "+e.getMessage());
							// TODO: handle exception
						}
						JSONObject props = new JSONObject();

						try {
							props.put("User ID", androidUserID);
							props.put("Gender", strGender);
							props.put("Birth Year", strBithYear);
							props.put("Ethnicity", strEthnicity);
						} catch (JSONException e) {
							// TODO: handle exception
						}

						mixpanel.track("New Downloads", props);

						// mixpanel.registerSuperProperties(oneObject);
						DebugReportOnLocat.ln("json response for submit::" + Library.androidUserID);
						sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
						editor = sharedpreferences.edit();
						editor.putString(Library.CUSTOMER_ID, Library.androidUserID);
						editor.putString(Library.REMAINING_DAYS, Library.remainingDays);
						editor.commit();
						// Toast.makeText(context,
						// "Your device registered to the server. user id: " +
						// json,
						// Toast.LENGTH_SHORT).show();
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

			e.printStackTrace();
		}

	}

	public void postJSONNotifData() {
		try {
			HttpPost request = new HttpPost(HttpUri.SETNOTIFICATION);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			JSONStringer item = null;

			try {
				// {"userTokenId":"3b10484355df66599afc32d6652383ed4f4b8ba6429f7821480b505ebe1299djjjjj4111","userDeviceId":"aaabbb"}
				item = new JSONStringer().object().key("userId").value(androidUserID).key("userNotificationFlag").value(userNotificationFlag).endObject();
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
						if (json.contains("1")) {
							// Library.showToast(context, "success");
							DebugReportOnLocat.ln("json response for notif::" + json);
						}
						// jArr = new JSONArray(json);

						// JSONObject oneObject = jArr.getJSONObject(0); //
						// Pulling
						// items
						// from
						// the
						// array

						// Toast.makeText(context,
						// "Your device registered to the server. user id: " +
						// json,
						// Toast.LENGTH_SHORT).show();
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

	public void clickEvents() {

		fav_iconNoImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (((CheckBox) v).isChecked()) {
					fav_iconNoImage.setEnabled(false);
					favriteFlag = "1";

					// if (casting_image.getVisibility() == View.VISIBLE) {
					// handlerImgFavoriteSync.sendEmptyMessage(0);
					// } else {
					handlerFavoriteSync.sendEmptyMessage(0);
					// }

				} else {
					fav_iconNoImage.setEnabled(false);
					favriteFlag = "0";

					// if (casting_image.getVisibility() == View.VISIBLE) {
					// handlerImgFavoriteSync.sendEmptyMessage(0);
					// } else {
					handlerFavoriteSync.sendEmptyMessage(0);
					// }
					// handlerFavoriteSync.sendEmptyMessage(0);
				}
			}
		});

		imgFav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View i) {
				// TODO Auto-generated method stub

				if (((CheckBox) i).isChecked()) {
					imgFav.setEnabled(false);
					favriteFlag = "1";
					handlerImgFavoriteSync.sendEmptyMessage(0);

				} else {
					imgFav.setEnabled(false);
					favriteFlag = "0";
					handlerImgFavoriteSync.sendEmptyMessage(0);
				}
			}
		});

		chkActor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					chkActor.setTextColor(context.getResources().getColor(R.color.white));

					if (strPerformanceType != null)
						strPerformanceType = strPerformanceType + "," + "actor";
					else
						strPerformanceType = "actor";
				} else {
					strPerformanceType = "";
					chkActor.setTextColor(context.getResources().getColor(R.color.dark_black));
				}
			}
		});
		chkModel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					chkModel.setTextColor(context.getResources().getColor(R.color.white));

					if (strPerformanceType != null)
						strPerformanceType = strPerformanceType + "," + "model";
					else
						strPerformanceType = "model";
				} else {
					strPerformanceType = "";
					chkModel.setTextColor(context.getResources().getColor(R.color.dark_black));
				}
			}
		});
		chkSinger.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					chkSinger.setTextColor(context.getResources().getColor(R.color.white));

					if (strPerformanceType != null)
						strPerformanceType = strPerformanceType + "," + "singer";
					else
						strPerformanceType = "singer";

				} else {
					strPerformanceType = "";
					chkSinger.setTextColor(context.getResources().getColor(R.color.dark_black));
				}
			}
		});
		chkDancer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					chkDancer.setTextColor(context.getResources().getColor(R.color.white));

					if (strPerformanceType != null)
						strPerformanceType = strPerformanceType + "," + "dancer";
					else
						strPerformanceType = "dancer";
				} else {
					strPerformanceType = "";
					chkDancer.setTextColor(context.getResources().getColor(R.color.dark_black));
				}
			}
		});
		chkMale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					chkMale.setTextColor(context.getResources().getColor(R.color.white));
					chkFemale.setChecked(false);
					chkFemale.setTextColor(context.getResources().getColor(R.color.dark_black));
					strGender = "male";
				} else {
					strGender = "";
					chkMale.setTextColor(context.getResources().getColor(R.color.dark_black));

				}
			}
		});
		chkFemale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					chkFemale.setTextColor(context.getResources().getColor(R.color.white));
					chkMale.setChecked(false);
					chkMale.setTextColor(context.getResources().getColor(R.color.dark_black));
					strGender = "female";
				} else {
					strGender = "";
					chkFemale.setTextColor(context.getResources().getColor(R.color.dark_black));

				}
			}
		});
		chkUnion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// is chkIos checked?

				if (((CheckBox) v).isChecked()) {
					chkUnion.setTextColor(context.getResources().getColor(R.color.white));
					chkNonUnion.setChecked(false);
					chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
					strUnionType = "union";
					UnionType = true;
				} else {
					strUnionType = "";
					UnionType = false;
					chkUnion.setTextColor(context.getResources().getColor(R.color.dark_black));

				}

				// if (((CheckBox) v).isChecked()) {
				// chkUnion.setTextColor(context.getResources().getColor(
				// R.color.white));
				// if (!strUnionType.equals("")) {
				// strUnionType = strUnionType + "," + "Union";
				// }
				//
				// else {
				// strUnionType = "union";
				// }
				// UnionType = true;
				//
				// } else {
				// strUnionType = "";
				// if (chkNonUnion.isChecked() == true) {
				// strUnionType = "non-union";
				// }
				// UnionType = false;
				// chkUnion.setTextColor(context.getResources().getColor(
				// R.color.dark_black));
				//
				// }
			}
		});
		chkNonUnion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// is chkIos checked?

				if (((CheckBox) v).isChecked()) {
					chkNonUnion.setTextColor(context.getResources().getColor(R.color.white));
					chkUnion.setChecked(false);
					chkUnion.setTextColor(context.getResources().getColor(R.color.dark_black));
					strUnionType = "non-union";
					UnionType = false;
				} else {
					strUnionType = "";
					UnionType = true;
					chkNonUnion.setTextColor(context.getResources().getColor(R.color.dark_black));

				}

				// if (((CheckBox) v).isChecked()) {
				// chkNonUnion.setTextColor(context.getResources().getColor(
				// R.color.white));
				//
				// if (!strUnionType.equals(""))
				// strUnionType = strUnionType + "," + "non-union";
				// else
				// strUnionType = "non-union";
				// } else {
				// strUnionType = "";
				// if (chkUnion.isChecked() == true) {
				// strUnionType = "union";
				// }
				//
				// chkNonUnion.setTextColor(context.getResources().getColor(
				// R.color.dark_black));
				//
				// }
			}
		});
		birthYear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert(years, birthYear);

			}
		});
	}

	private Handler handlerFavoriteSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(CastingScreen.this)) {
				postFavoriteCastingSync = new PostFavoriteCastingSync();
				postFavoriteCastingSync.execute();
			} else {
				Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();

			}
		}
	};

	// Sugumaran Code
	private Handler handlerImgFavoriteSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(CastingScreen.this)) {
				// postFavoriteCastingSync = new PostFavoriteCastingSync();
				// postFavoriteCastingSync.execute();

				postFavoriteImageSync = new PostFavoriteImageSync();
				postFavoriteImageSync.execute();

			} else {
				Toast.makeText(CastingScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();

			}
		}
	};
	private PostFavoriteCastingSync postFavoriteCastingSync;

	// Sugumaran
	private PostFavoriteImageSync postFavoriteImageSync;

	@SuppressLint("InlinedApi")
	public class PostFavoriteCastingSync extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog = null;

		@Override
		public void onPreExecute() {
			pDialog = new ProgressDialog(CastingScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			postFavorite(favriteFlag);
			return null;
		}

		@Override
		public void onPostExecute(Void params) {
			if (pDialog.isShowing())
				pDialog.dismiss();
			fav_iconNoImage.setEnabled(true);
			if (!favCount.equals("0")) {
				// fav_icon.setVisibility(View.INVISIBLE);

				favCountText.setVisibility(View.VISIBLE);
				favCountText.setText(favCount);
			} else {
				// fav_icon.setVisibility(View.VISIBLE);
				favCountText.setVisibility(View.INVISIBLE);
			}
			try {
				JSONObject props = new JSONObject();
				if (roleFav != null && roleFav.equals("1")) {
					myList.get(position).favCasting = "1";
					fav_iconNoImage.setSelected(true);

					try {
						props.put("User ID", androidUserID);
						props.put("Casting ID", myList.get(position).roleId.toString().trim());
						props.put("Casting Title", myList.get(position).castingTitle.toString().trim());
						props.put("Action", "Added");
						mixpanel.track("Favorites", props);
					} catch (JSONException e) {

					}

				} else {
					myList.get(position).favCasting = "0";
					fav_iconNoImage.setSelected(false);
					try {
						props.put("User ID", androidUserID);
						props.put("Casting ID", myList.get(position).roleId.toString().trim());
						props.put("Casting Title", myList.get(position).castingTitle.toString().trim());
						props.put("Action", "Removed");
						mixpanel.track("Favorites", props);
					} catch (JSONException e) {

					}
				}

			} catch (IndexOutOfBoundsException e) {

			}

		}

	}

	// Sugumaran

	@SuppressLint("InlinedApi")
	private class PostFavoriteImageSync extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog = null;

		@Override
		public void onPreExecute() {
			pDialog = new ProgressDialog(CastingScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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

			// int getTotalfav = Integer.parseInt(favCount) +
			// Integer.parseInt(favImgCount);
			// favCount = "" + getTotalfav;

			if (!favCount.equals("0")) {
				// fav_icon.setVisibility(View.INVISIBLE);

				favCountText.setVisibility(View.VISIBLE);
				favCountText.setText(favCount);
			} else {
				// fav_icon.setVisibility(View.VISIBLE);
				favCountText.setVisibility(View.INVISIBLE);
			}

			// Sugumaran Changes (25th May 16)
			if (casting_image_only.getVisibility() == View.VISIBLE) {

				if (myListImage.get((imagePos)).favImg) {
					myListImage.get((imagePos)).favImg = false;
				} else {
					myListImage.get((imagePos)).favImg = true;
				}

			} else {
				if (myListImage.get((sequenceOrder)).favImg) {
					myListImage.get((sequenceOrder)).favImg = false;
				} else {
					myListImage.get((sequenceOrder)).favImg = true;
				}
				// .get((sequenceOrder)).favImg = favriteFlag;
			}

			// try {
			// JSONObject props = new JSONObject();
			// if (imageFav != null && imageFav.equals("1")) {
			// myList.get(position).favCasting = "1";
			// myListImage.get(3);
			//
			// fav_iconNoImage.setSelected(true);
			//
			// try {
			// props.put("User ID", androidUserID);
			// props.put("Casting ID",
			// myList.get(position).roleId.toString().trim());
			// props.put("Casting Title",
			// myList.get(position).castingTitle.toString().trim());
			// props.put("Action", "Added");
			// mixpanel.track("Favorites", props);
			// } catch (JSONException e) {
			//
			// }
			//
			// } else {
			// myList.get(position).favCasting = "0";
			// fav_iconNoImage.setSelected(false);
			// try {
			// props.put("User ID", androidUserID);
			// props.put("Casting ID",
			// myList.get(position).roleId.toString().trim());
			// props.put("Casting Title",
			// myList.get(position).castingTitle.toString().trim());
			// props.put("Action", "Removed");
			// mixpanel.track("Favorites", props);
			// } catch (JSONException e) {
			//
			// }
			// }
			//
			// } catch (IndexOutOfBoundsException e) {
			//
			// }

		}

	}

	public void postFavorite(String favoriteValue) {
		try {
			HttpPost request = new HttpPost(HttpUri.SET_FAV);
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
				if (myList.size() == 10) {
					if (position == 10)
						position = 9;
				}

				item = new JSONStringer().object().key("userId").value(Library.androidUserID).key("roleId").value(myList.get(position).roleId).key("castingFav")
						.value(favoriteValue).key("roleEnthicity").value(myList.get(position).roleForEthnicity).key("roleAgeRange").value(myList.get(position).ageRange)
						.key("roleFor").value(myList.get(position).roleForGender).endObject();
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

						JSONObject oneObject = jArr.getJSONObject(0); // Pulling
																		// items
																		// from
																		// the
																		// array

						favCount = oneObject.getString("fav_count");
						roleFav = oneObject.getString("roleFav");

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

	// Sugumaran

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
				if (position == 10)
					position = 9;

				// int get = sequenceOrder - 1;

				// Sugumaran Changes (25th May 16)
				// this below if condition made for the new changes(Image only
				// shows). Otherwise else content is remain
				if (casting_image_only.getVisibility() == View.VISIBLE) {

					if (imagePos >= 0 && imagePos < myListImage.size()) {
						item = new JSONStringer().object().key("userId").value(Library.androidUserID).key("imageId").value(myListImage.get((imagePos)).imgId).key("imageFav")
								.value(favoriteValue).endObject();
					}
				} else {
					if (sequenceOrder == -1) {
						// get = 0;
						sequenceOrder = 0;
					}
					item = new JSONStringer().object().key("userId").value(Library.androidUserID).key("imageId").value(myListImage.get((sequenceOrder)).imgId).key("imageFav")
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
						favCount = oneObject.getString("fav_count");

						imageFav = oneObject.getString("imageFav");

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

	// to get the general casting from web service
	public String getJSONData() {
		String result = "";
		try {

			HttpClient httpclient = new DefaultHttpClient();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			if (strPerformanceType != null) {
				strPerformanceType = strPerformanceType.startsWith(",") ? strPerformanceType.substring(1) : strPerformanceType;
			}

			dlatitude = Math.round(dlatitude * 100.0) / 100.0;
			dlongitude = Math.round(dlongitude * 100.0) / 100.0;
			if (dlatitude == 0.0 && dlongitude == 0.0) {
				// http://casting.sdiphp.com/castingNew/public/casting?lat=37.28&lang=-122.00&userId=152&birthyear=&ethnicity=&gender=&performancetype=&page=0&uniontype=
				dlatitude = 37.28; // latitude::37.3382082
				dlongitude = -122.00; // longitude:: -121.8863286
			}
			nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(dlatitude)));
			nameValuePairs.add(new BasicNameValuePair("lang", String.valueOf(dlongitude)));
			nameValuePairs.add(new BasicNameValuePair("userId", Library.androidUserID));

			mixpanel.identify(Library.androidUserID);

			if (!strBithYear.equals(""))
				nameValuePairs.add(new BasicNameValuePair("birthyear", strBithYear));
			if (!strEthnicity.equals(""))
				nameValuePairs.add(new BasicNameValuePair("ethnicity", strEthnicity));
			if (!strGender.equals(""))
				nameValuePairs.add(new BasicNameValuePair("gender", strGender));
			if (!strPerformanceType.equals(""))
				nameValuePairs.add(new BasicNameValuePair("performancetype", strPerformanceType));
			if (!strUnionType.equals(""))
				nameValuePairs.add(new BasicNameValuePair("uniontype", strUnionType));

			nameValuePairs.add(new BasicNameValuePair("page", String.valueOf(page)));
			String Urls = HttpUri.CASTING + URLEncodedUtils.format(nameValuePairs, "utf-8");
			System.out.println("Casting URL Link :: " + Urls);
			HttpGet httpget = new HttpGet(Urls);
			DebugReportOnLocat.ln("filter casting values:" + URLEncodedUtils.format(nameValuePairs, "utf-8"));
			HttpResponse response = httpclient.execute(httpget);
			// Execute HTTP Get Request
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				DebugReportOnLocat.ln("result:::" + result);

				sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
				editor = sharedpreferences.edit();
				editor.putFloat(Library.LAT, (float) dlatitude);
				editor.putFloat(Library.LNG, (float) dlongitude);
				editor.putString(Library.ETHNICITY, strEthnicity);
				editor.putString(Library.PERFORMANCE_TYPE, strPerformanceType);
				editor.putString(Library.UNION, strUnionType);
				editor.putString(Library.GENDER, strGender);
				editor.putString(Library.BIRTH, strBithYear);
				editor.putString(Library.SELECTED_LOCATION, strSelectedLocation);
				editor.putBoolean(Library.NON_UNION, UnionType);

				editor.commit();

			} else {

				return "";

			}
		} catch (Exception e) {
			e.printStackTrace();
			DebugReportOnLocat.ln("Error : " + e.getMessage());
		}

		return result;
	}

	// to get the general casting from web service
	public String getJSONImages() {
		String result = "";
		// try {
		//
		// HttpClient httpclient = new DefaultHttpClient();
		//
		// HttpGet httpget = new HttpGet(HttpUri.IMAGE_URL);
		//
		// HttpResponse response = httpclient.execute(httpget);.
		double latitude = gps.getLatitude();
		double longitude = gps.getLongitude();

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
			
			System.out.println("Url "+Url);

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

	public static int position = 0;/*
									 * ,
									 * saveImagePostionleft=4,saveImagePostionRight
									 * =5
									 */;
	int head, tail;
	int page = 0;
	public static int scrollCount = 0;

	// int oldPage = 0;
	int getTotalCastings;
	int lastPage;
	int totalPages;

	boolean bs = false;

	ArrayList<CastingDetailsModel> myList;
	ArrayList<CastingDetailsModel> castingsListArr = new ArrayList<CastingDetailsModel>();;
	ArrayList<CastingImagesModel> myListImage;

	// HashMap<Integer, ArrayList<CastingDetailsModel>> hashMap = new
	// HashMap<Integer, ArrayList<CastingDetailsModel>>();

	void CastingLeftSwipeView() {

		position--;
		DebugReportOnLocat.ln("left pos" + position);
		int pos = position;

		if (position >= head && position <= tail) {

			setAllData(myList, position);

		} else {

			page = page - 1;

			if (page >= 0 && page <= totalPages) {

				if (CastivateApplication.getInstance().hashMap.containsKey(page)) {
					myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
					castViewStatus = false;
					position = 19;
					head = 0;
					tail = myList.size();

					setAllData(myList, position);

				} else {
					position = 19;

					handlerCastingSync.sendEmptyMessage(0);
				}
			} else {

				page = lastPage;

				if (getTotalCastings <= 20) {
					page = 0;

					myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
					castViewStatus = false;

					position = myList.size() - 1;

					head = 0;
					tail = myList.size();

					if ((page == 0 && position == 0) || (page == totalPages && position == myList.size() - 1)) {

						textCastivate.setVisibility(View.VISIBLE);
						castingViewNoImage.setVisibility(View.GONE);

						castingsList.setVisibility(View.GONE);
						bSwipe = "left";
						DebugReportOnLocat.ln("" + pos);

					} else {

						setAllData(myList, position);

					}

				} else {
					if (CastivateApplication.getInstance().hashMap.containsKey(page)) {

						myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
						castViewStatus = false;

						position = myList.size() - 1;

						head = 0;
						tail = myList.size();

						if ((page == 0 && position == 0) || (page == totalPages && position == myList.size() - 1)) {

							textCastivate.setVisibility(View.VISIBLE);
							castingViewNoImage.setVisibility(View.GONE);

							castingsList.setVisibility(View.GONE);
							bSwipe = "left";

							DebugReportOnLocat.ln("" + pos);
						} else {

							setAllData(myList, position);

						}

					} else {
						swpe = false;
						handlerCastingSync.sendEmptyMessage(0);

					}
				}

			}

		}

	}

	boolean testB = false;
	boolean swpe = false;

	void CastingRightSwipeView() {

		position++;

		if (position >= head && position < tail) {

			setAllData(myList, position);
		} else {

			page = page + 1;

			if (CastivateApplication.getInstance().hashMap.containsKey(page)) {

				myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
				castViewStatus = false;

				head = 0;
				position = 0;
				tail = myList.size();

				setAllData(myList, position);

			} else {

				position = -1;

				if (page > totalPages) {

					page = 0;

					if (CastivateApplication.getInstance().hashMap.containsKey(page)) {

						myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
						castViewStatus = false;
						position = 0;
						head = 0;
						tail = myList.size();

						if ((page == 0 && position == 0) || (page == totalPages && position == myList.size() - 1)) {

							testB = true;
							bSwipe = "right";
							setAllData(myList, position);
						} else {
							setAllData(myList, position);
						}

					} else {
						position = 0;

						handlerCastingSync.sendEmptyMessage(0);
					}
				} else {

					handlerCastingSync.sendEmptyMessage(0);

				}

			}
		}

	}

	String cityAndStateName = "";

	public void setAllData(final ArrayList<CastingDetailsModel> myList, final int currentPos) {
		try {

			System.out.println("Swipe------------------------------->");

			if (myList.get(currentPos).favCasting.equals("0")) {
				fav_iconNoImage.setChecked(false);
			} else {
				fav_iconNoImage.setChecked(true);
			}
			if (castingViewNoImage.getVisibility() == View.GONE) {
				castingViewNoImage.setVisibility(View.VISIBLE);
			}
			if (listViewStatus == true) {
				// scrollCount = 0;
				// castingsListIconChange.setVisibility(View.VISIBLE);
				castingsListIcon.setVisibility(View.GONE);
				castingsList.setVisibility(View.VISIBLE);
				rel_cast_view.setVisibility(View.GONE);
				castingViewNoImage.setVisibility(View.GONE);
			}

			else {
				castingsListIcon.setVisibility(View.VISIBLE);
				// castingsListIconChange.setVisibility(View.GONE);
				rel_cast_view.setVisibility(View.VISIBLE);
				castingViewNoImage.setVisibility(View.VISIBLE);
				castingsList.setVisibility(View.GONE);
			}

			textCastingTitleNoImage.setText(Html.fromHtml(myList.get(currentPos).castingTitle.toString().trim()));
			textCastingTypeNoImage.setText(Html.fromHtml(myList.get(currentPos).castingType.toString().trim()));
			textPaidStatusNoImage.setText(myList.get(currentPos).castingPaidStatus.toString().trim());
			strState = myList.get(currentPos).state.toString().trim();
			strCity = myList.get(currentPos).country.toString().trim();

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

			String html = cityAndStateName + " - " + myList.get(currentPos).castingSubmissionDetail.toString().trim();
			Spannable WordtoSpan = new SpannableString(html);
			WordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, cityAndStateName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			WordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.NORMAL), cityAndStateName.length(), html.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			textSubmissionDetailNoImage.setText(WordtoSpan);

			Linkify.addLinks(textSubmissionDetailNoImage, Linkify.ALL);

			((CastingsLinkMovementMethod) CastingsLinkMovementMethod.getInstance()).setContext(context, false);
			textSubmissionDetailNoImage.setMovementMethod(CastingsLinkMovementMethod.getInstance());

			textAgeRangeNoImage.setText("Age " + myList.get(currentPos).ageRange.toString().trim());
			textSynopsisNoImage.setText(Html.fromHtml(myList.get(currentPos).castingSynopsis.toString().trim()));
			textRoleForEthnicityNoImage.setText(Html.fromHtml(myList.get(currentPos).roleForEthnicity.toString().trim()));
			textUnionStatusNoImage.setText(myList.get(currentPos).castingUnionStatus.toString().trim());
			textRoleForGenderNoImage.setText(myList.get(currentPos).roleForGender.toString().trim());
			txtRoleDescriptionNoImage.setText(myList.get(currentPos).roleDescription.toString().trim());


			if(myList.get(currentPos).castingEmail.isEmpty())
			{
				btn_apply.setVisibility(View.GONE);
				System.out.println("castingEmail Empty");
			}
			else {

				System.out.println("applyFlag : "+myList.get(currentPos).applyFlag);

				if(myList.get(currentPos).applyFlag.equals("1"))
				{
					//submitted
					btn_apply.setVisibility(View.VISIBLE);
					//btn_apply.setText("Submitted");
					//btn_apply.setBackgroundColor(R.color.green);
				}
				else {
					System.out.println("castingEmail------------>"+myList.get(currentPos).castingEmail);
					btn_apply.setVisibility(View.VISIBLE);
					//btn_apply.setBackgroundColor(R.color.red);
					btn_apply.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							selectedCastingDetailsModels.clear();
							selectedCastingDetailsModels.add(myList.get(currentPos));

							if(Library.remainingDays.equals("0"))
							{
								Intent intent = new Intent(CastingScreen.this, CastingPlan.class);
								intent.putExtra("selectedCastingDetailsModels",selectedCastingDetailsModels);
								startActivity(intent);
							}
							else {
								Intent intent = new Intent(CastingScreen.this, CastingFileUpload.class);
								intent.putExtra("selectedCastingDetailsModels",selectedCastingDetailsModels);
								startActivity(intent);
							}
						}
					});
				}
			}




			if (testB == true) {

				textCastivate.setVisibility(View.VISIBLE);
				castingViewNoImage.setVisibility(View.GONE);
				castingsList.setVisibility(View.GONE);
				testB = false;
				int po = currentPos;
				DebugReportOnLocat.ln("new position" + po);
			}
			position = currentPos;
			if (position == -1) {
				position = 19;
			}
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			System.out.println("ee " + e);
		}
	}

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

			swipeRefreshLayout.setRefreshing(false);

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
								json_data.getInt("image_id"), flag,"");
						myListImage.add(detailsModel);

					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

			if (notifFlag == true) {
				userNotificationFlag = "Yes";
				notification = true;
				sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
				editor = sharedpreferences.edit();
				editor.putBoolean(Library.NOTIFICATION, notification);
				editor.commit();
				toggleButton.setChecked(true);
				postSetNotifSync = new PostSetNotifSync();
				postSetNotifSync.execute();
			}

		}

	}

	JSONObject json_data = null;

	public class GetDatas extends AsyncTask<String, Void, Void> {

		String getAll = "";
		ProgressDialog pDialog = null;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			// if (pDialog != null && pDialog.isShowing()) {
			// pDialog.dismiss();
			pDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Loading Castings..");
			pDialog.show();
			pDialog.setCancelable(false);
			// }
		}

		@Override
		protected Void doInBackground(String... params) {

			getAll = getJSONData();

			return null;
		}

		@Override
		protected void onPostExecute(Void params) {

			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				castingsList.setEnabled(true);

				if (getAll != null && !getAll.equals("[]") && !getAll.equals("")) {
					if (listViewStatus == true) {
						// castingsListIconChange.setVisibility(View.VISIBLE);
						castingsList.setVisibility(View.VISIBLE);
						rel_cast_view.setVisibility(View.GONE);
						castingViewNoImage.setVisibility(View.GONE);

					}

					else {
						castingsListIcon.setVisibility(View.VISIBLE);
						rel_cast_view.setVisibility(View.VISIBLE);
						castingViewNoImage.setVisibility(View.VISIBLE);
						castingsList.setVisibility(View.GONE);
					}
					textCastivate.setVisibility(View.GONE);
					myList = new ArrayList<CastingDetailsModel>();

					castViewStatus = false;
					try {
						JSONArray jArray = new JSONArray(getAll);

						CastingDetailsModel detailsModel;
						// CastingDetailsModel listDetailsModel;
						for (int i = 0; i < jArray.length(); i++) {
							json_data = jArray.getJSONObject(i);

							detailsModel = new CastingDetailsModel(json_data.getString("IdRole"), json_data.getString("casting_title"), json_data.getString("casting_type"),
									json_data.getString("casting_paid_status"), json_data.getString("casting_union_status"), json_data.getString("casting_union_type"),
									json_data.getString("casting_submission_detail"), json_data.getString("casting_synopsis"), json_data.getString("casting_image"),
									json_data.getString("role_desc"), json_data.getString("ageRange"), json_data.getString("role_for"), json_data.getString("role_ethnicity"),
									json_data.getString("fav_flag"), json_data.getString("fav_count"), "", json_data.getString("castingsTotal"),
									json_data.getString("casting_state"), json_data.getString("casting_city"),json_data.getString("casting_email"),json_data.getString("apply_flag"));
							// listDetailsModel = new
							// CastingListDetailsModel(json_data.getString("IdRole"),
							// json_data.getString("casting_title"),
							// json_data.getString("casting_type"),
							// json_data.getString("casting_paid_status"));

							getTotalCastings = Integer.parseInt(json_data.getString("castingsTotal"));
							favCount = json_data.getString("fav_count");
							DebugReportOnLocat.ln("total::" + getTotalCastings);

							if (!favCount.equals("0")) {

								favCountText.setVisibility(View.VISIBLE);
								favCountText.setText(favCount);
							} else {

								favCountText.setVisibility(View.INVISIBLE);
							}

							myList.add(detailsModel);
							castingsListArr.add(detailsModel);
						}

						if (castingsList.getVisibility() == View.VISIBLE) {
							castingsList.setAdapter(null);
							ArrayList<CastingDetailsModel> castingsListArrList = new ArrayList<CastingDetailsModel>(castingsListArr);
							refreshYourAdapter(castingsListArrList);
							castingsList.setAdapter(castListAdapter);
							castListAdapter.notifyDataSetChanged();

							// Sugumaran Changes(31 May 2016)
							int getpos = castingsListArr.size();
							System.out.println("getPos " + getpos);
							castingsList.setSelection(getpos - 19);
						}

						// My New
						if (pDialog.isShowing()) {
							pDialog.dismiss();
						}
						CastivateApplication.getInstance().hashMap.put(page, myList);
						if (getTotalCastings > 20) {
							totalPages = getTotalCastings / 20;

							DebugReportOnLocat.ln("totalPages::" + totalPages);
						}
						if (isPush) {
							getPushNotificationCasting();
						} else {

							DebugReportOnLocat.ln("total::" + getTotalCastings);

							if (bs == false) {
								lastPage = totalPages;
								bs = true;
							}

							DebugReportOnLocat.ln("page " + page);

							DebugReportOnLocat.ln("list is " + myList);
							if (getTotalCastings > 20) {
								if (page == lastPage) {
									position = myList.size() - 1;
								}
							}

							head = 0;
							tail = myList.size();

							if (position == -1) {
								position = 0;
							}

							// if ((page == 0 && position == 0) || (page ==
							// totalPages && position == myList.size() - 1)) {
							if ((page == totalPages && position == myList.size() - 1)) {

								if (swpe == true) {
									head = 0;
									tail = myList.size();

									position = 0;
									setAllData(myList, position);
									swpe = false;
								} else {

									textCastivate.setVisibility(View.VISIBLE);
									castingViewNoImage.setVisibility(View.GONE);
									castingsList.setVisibility(View.GONE);
									bSwipe = "left";

									int pos = position;
									DebugReportOnLocat.ln("" + pos);
								}
							} else {

								setAllData(myList, position);

							}

						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {

					rel_cast_view.setVisibility(View.GONE);
					castivityScreen.setVisibility(View.INVISIBLE);
					outbox_icon.setVisibility(View.VISIBLE);
					gear_icon_mirror.setVisibility(View.INVISIBLE);
					gear_icon.setVisibility(View.VISIBLE);
					// TODO Auto-generated method stub
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

					alertDialogBuilder.setMessage("There are no castings that match your filter selection").setCancelable(false)
							.setNeutralButton("Try undoing some filters", new DialogInterface.OnClickListener() {
								@SuppressLint("NewApi")
								public void onClick(DialogInterface dialog, int id) {

									if (castivityScreen.getVisibility() == View.VISIBLE) {
										castivityScreen.setVisibility(View.GONE);
										outbox_icon.setVisibility(View.VISIBLE);
										gear_icon_mirror.setVisibility(View.GONE);
										// if (listViewStatus)
										// castingsListIconChange.setVisibility(View.VISIBLE);
										// else
										// castingsListIcon.setVisibility(View.VISIBLE);

									} else {
										castivityScreen.setVisibility(View.VISIBLE);
										outbox_icon.setVisibility(View.GONE);
										gear_icon_mirror.setVisibility(View.VISIBLE);

										// castingsListIconChange.setVisibility(View.GONE);

										castingsListIcon.setVisibility(View.GONE);
										castivityScreen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));
										overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
										dlongitude = dclongitude;
										dlatitude = dclatitude;
									}

								}
							});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();

					// Toast.makeText(CastingScreen.this, "Empty Casting ",
					// 0).show();

					// Toast.makeText(CastingScreen.this, "Empty Casting ",
					// 0).show();

					if (rel_info_ListView.getVisibility() == View.VISIBLE) {
						rel_info_ListView.setFocusable(false);

						textCastivate.setVisibility(View.GONE);
						castingViewNoImage.setVisibility(View.GONE);
						rel_image_screen.setVisibility(View.GONE);
						rel_upload_screen.setVisibility(View.GONE);
						castingsList.setVisibility(View.VISIBLE);

						castingsListIcon.setVisibility(View.GONE);
						// castingsListIconChange.setVisibility(View.GONE);// My
						rel_info_ListView.setVisibility(View.VISIBLE);
						// listViewStatus = true;

						grid_view_photos.setVisibility(View.GONE);
						swipeRefreshLayout.setVisibility(View.GONE);
					} else {
						page = 0;
					}
					if (CastivateApplication.getInstance().hashMap.containsKey(page)) {

						myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
						castViewStatus = false;
						position = 0;
						head = 0;
						tail = myList.size();
						int TAILs = myList.size();

						if ((page == 0 && position == 0) || (page == totalPages && position == myList.size() - 1)) {

							textCastivate.setVisibility(View.VISIBLE);
							castingViewNoImage.setVisibility(View.GONE);
							castingsList.setVisibility(View.GONE);
						} else {
							setAllData(myList, position);
						}

					} /*
					 * else { position = 0;
					 * 
					 * handlerCastingSync.sendEmptyMessage(0); }
					 */

				}
			}
			// castingsListIconChange.setVisibility(View.GONE);//My
			// if(rel_info_ListView.getVisibility() == View.VISIBLE){
			// rel_info_ListView.setFocusable(false);
			//
			// textCastivate.setVisibility(View.GONE);
			// castingViewNoImage.setVisibility(View.GONE);
			// rel_image_screen.setVisibility(View.GONE);
			// rel_upload_screen.setVisibility(View.GONE);
			// castingsList.setVisibility(View.VISIBLE);
			//
			// castingsListIcon.setVisibility(View.GONE);
			// // castingsListIconChange.setVisibility(View.GONE);// My
			// rel_info_ListView.setVisibility(View.VISIBLE);
			// // listViewStatus = true;
			//
			// grid_view_photos.setVisibility(View.GONE);
			// }
			if (listViewStatus == false) {
				handlerCastingImagesSync.sendEmptyMessage(0);
			}

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if (pDialog != null && pDialog.isShowing())
				pDialog.dismiss();

			ProgressDialog myDialog = new ProgressDialog(context);
			if (myDialog.isShowing()) {
				myDialog.cancel();
			}
		}

	}

	boolean castViewStatus = false, lastImageStatus;
	boolean castViewStatusreak = false;
	String swipe = "";
	String bSwipe = "";
	String imgCasting = "", userName;

	private ProgressDialog progressBar;

	class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					if (casting_image_only.getVisibility() == View.VISIBLE) {

						overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_right);
						casting_image_only.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));

						imagePos++;
						if (imagePos >= 0 && myListImage.size() > imagePos) {

						} else {
							imagePos = 0;
						}

						imgFav.setVisibility(View.VISIBLE);
						CastingImagesModel model = myListImage.get(imagePos);
						String getName = model.userName.toString().trim();
						imgFav.setChecked(model.favImg);
						imgFav.setEnabled(true);
						txtName.setVisibility(View.VISIBLE);
						if (getName.equals("")) {
							txtName.setVisibility(View.GONE);
						} else {
							txtName.setText(getName);
						}
						if (Network.isNetworkConnected(context)) {
							
							progressView.setVisibility(View.VISIBLE);
//							Picasso.with(context).load(myListImage.get(imagePos).imageRole).noFade().into(casting_image_only, new Callback() {
//								@Override
//								public void onSuccess() {
//									progressView.setVisibility(View.GONE);
//								}
//
//								@Override
//								public void onError() {
//									Library.showToast(context, "Image load error.");
//								}
//							});
							
							
							UrlImageViewHelper.setUrlDrawable(casting_image_only, myListImage.get(imagePos).imageRole,new UrlImageViewCallback() {
								
								@Override
								public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
									// TODO Auto-generated method stub
									progressView.setVisibility(View.GONE);
								}
							});

						} else {
							Library.showToast(context, "Please check your network connection.");
						}

					} else {

						progressView.setVisibility(View.GONE);
						rel_upload_screen.setVisibility(View.GONE);
						grid_view_photos.setVisibility(View.GONE);
						swipeRefreshLayout.setVisibility(View.GONE);

						overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_right);
						rel_cast_view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));
						if (grid_view_photos.getVisibility() == View.VISIBLE) {
							rel_upload_screen.setVisibility(View.GONE);
						}

						if (textCastivate.getVisibility() == View.VISIBLE) {
							castingViewNoImage.setVisibility(View.VISIBLE);
							textCastivate.setVisibility(View.GONE);
							castingsList.setVisibility(View.GONE);
							rel_upload_screen.setVisibility(View.GONE);
							if (bSwipe.equals("left")) {
								position++;
							}
							if (bSwipe.equals("right")) {

							}
							bSwipe = "";

							if (position < myList.size()) {

							} else {

								page = 0;

								myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
								castViewStatus = false;
								position = 0;
								head = 0;
								tail = myList.size();

							}
							setAllData(myList, position);
						}

						else {

							castViewStatusreak = false;
							if (rel_image_screen.getVisibility() == View.VISIBLE) {
								rel_image_screen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));
								castingViewNoImage.setVisibility(View.VISIBLE);
								rel_image_screen.setVisibility(View.GONE);
								rel_upload_screen.setVisibility(View.GONE);
								castingsList.setVisibility(View.GONE);
								castImageViewStatus = false;
								gridViewStatus = false;
								if (position == 5 || position == 10 || position == 15) {
									position--;
								}

								if (bSwipe.equals("left")) {
									position++;
								}

								if (position <= myList.size() - 1) {

									if (position > 19 && lastImageStatus == false) {
										castingViewNoImage.setVisibility(View.GONE);
										castingsList.setVisibility(View.GONE);
										CastingRightSwipeView();
										lastImageStatus = true;
									} else {

										if (position <= myList.size() - 1) {
											setAllData(myList, position);
										} else {

											CastingRightSwipeView();
										}
									}

								} else {

									CastingRightSwipeView();
								}

								swipe = "";
								castViewStatus = false;
							} else if (position == 4 || position == 9 || position == 14 || position == 18 && castViewStatus == false) {
								progressView.setVisibility(View.GONE);
								castingViewNoImage.setVisibility(View.GONE);
								castingsList.setVisibility(View.GONE);
								rel_image_screen.setVisibility(View.VISIBLE);
								rel_upload_screen.setVisibility(View.VISIBLE);
								castImageViewStatus = true;
								gridViewStatus = false;
								bSwipe = "left";
								// if(position==saveImagePostionleft){
								// saveImagePostionleft= saveImagePostionleft+5;
								if (sequenceOrder == myListImage.size() - 1) {
									sequenceOrder = 0;
								} else {
									sequenceOrder++;

								}
								// }else{
								// sequenceOrder=saveImagePostionRight;
								// }

								if (myListImage.get(sequenceOrder).imageRole.toString().trim() != null) {

									String imgCasting = myListImage.get(sequenceOrder).imageRole.toString().trim();
									userName = myListImage.get(sequenceOrder).userName.toString().trim();
									imgFav.setChecked(myListImage.get(sequenceOrder).favImg);
									if (!userName.equals("")) {

										txtName.setVisibility(View.VISIBLE);
										txtName.setText(userName);

									} else {
										txtName.setVisibility(View.GONE);
									}
									imgCasting = imgCasting.replace("\"", "");
									DebugReportOnLocat.ln("image url::" + imgCasting);

									if (myListImage.get(sequenceOrder).favImg == true)
										fav_iconNoImage.setChecked(true);
									else
										fav_iconNoImage.setChecked(false);

									rel_upload_screen.setVisibility(View.VISIBLE);
									imgGrid.setVisibility(View.VISIBLE);
									local_talent.setVisibility(View.VISIBLE);
									casting_image.setVisibility(View.VISIBLE);
									grid_view_photos.setVisibility(View.GONE);
									swipeRefreshLayout.setVisibility(View.GONE);
									if (Network.isNetworkConnected(context)) {

										progressView.setVisibility(View.VISIBLE);
//										// fav_iconNoImage.setEnabled(false);
//										// imgFav.setEnabled(false);
//										Picasso.with(context).load(imgCasting.replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
//											@Override
//											public void onSuccess() {
//												progressView.setVisibility(View.GONE);
//												fav_iconNoImage.setEnabled(true);
//												imgFav.setEnabled(true);
//											}
//
//											@Override
//											public void onError() {
//												// TODO
//												// Auto-generated
//												// method stub
//
//											}
//										});

										UrlImageViewHelper.setUrlDrawable(casting_image, imgCasting,new UrlImageViewCallback() {
											
											@Override
											public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
												// TODO Auto-generated method stub
												progressView.setVisibility(View.GONE);
											}
										});
										
									} else {
										Library.showToast(context, "Please check your network connection.");

									}

								}

								castViewStatus = true;

								swipe = "right";

								int pos = position;
								DebugReportOnLocat.ln("pos " + pos);

							} else {
								castViewStatus = false;

								swpe = true;

								CastingRightSwipeView();
							}
						}
					}

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					if (casting_image_only.getVisibility() == View.VISIBLE) {
						overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
						casting_image_only.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));

						imagePos--;
						if (imagePos >= 0 && myListImage.size() > imagePos) {

						} else {
							imagePos = myListImage.size() - 1;
						}
						imgFav.setVisibility(View.VISIBLE);
						CastingImagesModel model = myListImage.get(imagePos);
						String getName = model.userName.toString().trim();
						imgFav.setChecked(model.favImg);
						imgFav.setEnabled(true);
						txtName.setVisibility(View.VISIBLE);
						if (getName.equals("")) {
							txtName.setVisibility(View.GONE);
						} else {
							txtName.setText(getName);
						}
						if (Network.isNetworkConnected(context)) {
							progressView.setVisibility(View.VISIBLE);
//							Picasso.with(context).load(myListImage.get(imagePos).imageRole).noFade().into(casting_image_only, new Callback() {
//								@Override
//								public void onSuccess() {
//									progressView.setVisibility(View.GONE);
//								}
//
//								@Override
//								public void onError() {
//									Library.showToast(context, "Image load error.");
//								}
//							});
							
							UrlImageViewHelper.setUrlDrawable(casting_image_only, myListImage.get(imagePos).imageRole,new UrlImageViewCallback() {
								
								@Override
								public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
									// TODO Auto-generated method stub
									progressView.setVisibility(View.GONE);
								}
							});

						} else {
							Library.showToast(context, "Please check your network connection.");
						}

					} else {

						overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
						rel_cast_view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));

						rel_upload_screen.setVisibility(View.GONE);
						grid_view_photos.setVisibility(View.GONE);
						swipeRefreshLayout.setVisibility(View.GONE);

						if (grid_view_photos.getVisibility() == View.VISIBLE) {
							rel_upload_screen.setVisibility(View.GONE);
						}

						if (castivityScreen.getVisibility() == View.VISIBLE) {
							rel_image_screen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));
							gear_icon.setVisibility(View.VISIBLE);
							outbox_icon.setVisibility(View.VISIBLE);
							gear_icon_mirror.setVisibility(View.INVISIBLE);
							// if (listViewStatus)
							// castingsListIconChange.setVisibility(View.VISIBLE);
							// else
							// castingsListIcon.setVisibility(View.VISIBLE);
							if (castivityScreen.getVisibility() == View.VISIBLE) {
								castivityScreen.setVisibility(View.GONE);
							}
							// filterValue=1;
							// list = new ArrayList<CastingDetailsModel>();
							myList = new ArrayList<CastingDetailsModel>();

							myList.clear();
							page = 0;
							bs = false;
							position = 0;
							castViewStatus = false;
							castViewStatusreak = false;
							swipe = "";
							bSwipe = "";
							swpe = true;

							totalPages = 0;

							CastivateApplication.getInstance().hashMap.clear();
							tap_current_location.setVisibility(View.GONE);
							castingViewNoImage.setVisibility(View.VISIBLE);
							castingsList.setVisibility(View.GONE);
							if (rel_image_screen.getVisibility() == View.VISIBLE) {
								rel_image_screen.setVisibility(View.GONE);
								rel_upload_screen.setVisibility(View.GONE);
								castImageViewStatus = false;
								gridViewStatus = false;
							}
							myList = new ArrayList<CastingDetailsModel>();
							handlerCastingSync.sendEmptyMessage(0);
						} else {
							if (textCastivate.getVisibility() == View.VISIBLE) {
								castingViewNoImage.setVisibility(View.VISIBLE);
								textCastivate.setVisibility(View.GONE);
								castingsList.setVisibility(View.GONE);

								rel_upload_screen.setVisibility(View.GONE);

								if (bSwipe.equals("right")) {
									position--;
								}
								bSwipe = "";
								int pos = position;

								if (pos == -1) {
									if (page == 0) {
										page = totalPages;
										myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
										castViewStatus = false;
										position = myList.size() - 1;
										head = 0;
										tail = myList.size();

									} else {

										page = 0;

										myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(page));
										castViewStatus = false;
										position = 0;
										head = 0;
										tail = myList.size();

									}
								}

								setAllData(myList, position);

							} else {
								if (rel_image_screen.getVisibility() == View.VISIBLE) {
									castingViewNoImage.setVisibility(View.VISIBLE);
									castingsList.setVisibility(View.GONE);
									rel_image_screen.setVisibility(View.GONE);
									rel_upload_screen.setVisibility(View.GONE);

									if (swipe.equals("left")) {
										position--;
									}
									if (swipe.equals("right")) {

									}
									castImageViewStatus = false;

									castViewStatus = false;
									setAllData(myList, position);

								} else if (position == 5 || position == 10 || position == 15 || position == 19 && castViewStatus == false) {

									// if(position==saveImagePostionRight){
									// saveImagePostionRight=
									// saveImagePostionRight+5;
									if (sequenceOrder == myListImage.size() - 1) {
										sequenceOrder = 0;
									} else {

										sequenceOrder++;

									}
									// }else{
									// sequenceOrder=saveImagePostionleft;
									//
									// }

									castingViewNoImage.setVisibility(View.GONE);
									castingsList.setVisibility(View.GONE);
									rel_image_screen.setVisibility(View.VISIBLE);
									rel_upload_screen.setVisibility(View.VISIBLE);
									castImageViewStatus = true;
									gridViewStatus = false;
									if (myListImage.get(sequenceOrder).imageRole.toString().trim() != null) {

										imgCasting = myListImage.get(sequenceOrder).imageRole.toString().trim();
										imgCasting = imgCasting.replace("\"", "");
										userName = myListImage.get(sequenceOrder).userName.toString().trim();
										imgFav.setChecked(myListImage.get(sequenceOrder).favImg);
										if (!userName.equals("")) {

											txtName.setVisibility(View.VISIBLE);
											txtName.setText(userName);

										} else {
											txtName.setVisibility(View.GONE);
										}
										if (myListImage.get(sequenceOrder).favImg == true)
											fav_iconNoImage.setChecked(true);
										else
											fav_iconNoImage.setChecked(false);

										DebugReportOnLocat.ln("image url::" + imgCasting);
										rel_upload_screen.setVisibility(View.VISIBLE);
										imgGrid.setVisibility(View.VISIBLE);
										local_talent.setVisibility(View.VISIBLE);
										casting_image.setVisibility(View.VISIBLE);
										grid_view_photos.setVisibility(View.GONE);
										swipeRefreshLayout.setVisibility(View.GONE);

										// download and display image from url
										if (Network.isNetworkConnected(context)) {

											progressView.setVisibility(View.VISIBLE);
//											// fav_iconNoImage.setEnabled(false);
//											// imgFav.setEnabled(false);
//											Picasso.with(context).load(imgCasting.replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
//												@Override
//												public void onSuccess() {
//													progressView.setVisibility(View.GONE);
//													fav_iconNoImage.setEnabled(true);
//													imgFav.setEnabled(true);
//												}
//
//												@Override
//												public void onError() {
//													// Library.showToast(context,
//													// "Something went wrong!");
//
//												}
//											});
											
											
											UrlImageViewHelper.setUrlDrawable(casting_image, imgCasting,new UrlImageViewCallback() {
												
												@Override
												public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
													// TODO Auto-generated method stub
													progressView.setVisibility(View.GONE);
												}
											});
											
										} else {
											Library.showToast(context, "Please check your internet connection");
										}

									}

									swipe = "left";

									castViewStatus = true;
									int pos = position;
									DebugReportOnLocat.ln("pos " + pos);

								} else {

									castViewStatus = false;

									CastingLeftSwipeView();

									overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				// nothing
				System.out.println("ee " + e);
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

	// Sugumaran Changes (25th May 16)
	class ImageGestureDetector extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_right);
					casting_image_only.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));

					imagePos++;
					if (imagePos >= 0 && myListImage.size() > imagePos) {

					} else {
						imagePos = 0;
					}
					if (Network.isNetworkConnected(context)) {
						progressView.setVisibility(View.VISIBLE);
//						Picasso.with(context).load(myListImage.get(imagePos).imageRole).noFade().into(casting_image_only, new Callback() {
//							@Override
//							public void onSuccess() {
//								progressView.setVisibility(View.GONE);
//							}
//
//							@Override
//							public void onError() {
//								Library.showToast(context, "Image load error.");
//							}
//						});

						UrlImageViewHelper.setUrlDrawable(casting_image_only, myListImage.get(imagePos).imageRole,new UrlImageViewCallback() {
							
							@Override
							public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
								// TODO Auto-generated method stub
								progressView.setVisibility(View.GONE);
							}
						});
						
					} else {
						Library.showToast(context, "Please check your network connection.");
					}

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
					casting_image_only.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));

					imagePos--;
					if (imagePos >= 0 && myListImage.size() > imagePos) {

					} else {
						imagePos = myListImage.size() - 1;
					}

					// casting_image.setVisibility(View.GONE);
					// casting_image_only.setVisibility(View.VISIBLE);
					if (Network.isNetworkConnected(context)) {
						progressView.setVisibility(View.VISIBLE);
//						Picasso.with(context).load(myListImage.get(imagePos).imageRole).noFade().into(casting_image_only, new Callback() {
//							@Override
//							public void onSuccess() {
//								progressView.setVisibility(View.GONE);
//							}
//
//							@Override
//							public void onError() {
//								Library.showToast(context, "Image load error.");
//							}
//						});

						UrlImageViewHelper.setUrlDrawable(casting_image_only, myListImage.get(imagePos).imageRole,new UrlImageViewCallback() {
							
							@Override
							public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
								// TODO Auto-generated method stub
								progressView.setVisibility(View.GONE);
							}
						});
						
					} else {
						Library.showToast(context, "Please check your network connection.");
					}

				}
			} catch (Exception e) {
				System.out.println("ee " + e);
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

	// Sugumaran Changes (25th May 16)

	public static Bitmap getBitmapFromResources(Resources resources, int resImage) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inSampleSize = 1;
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		return BitmapFactory.decodeResource(resources, resImage, options);
	}

	@Override
	public void onBackPressed() {

		if (castivityScreen.getVisibility() == View.GONE /*
														 * ||thankYouLayout.
														 * getVisibility() ==
														 * View
														 * .GONE||grid_view_photos
														 * .getVisibility() ==
														 * View.GONE
														 */) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

			alertDialogBuilder.setMessage("Do you want to Exit from this Application?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int id) {
					CastivateApplication.getInstance().hashMap.clear();
					bs = false;

					page = 0;
					totalPages = 0;
					castViewStatus = false;
					castViewStatusreak = false;
					swipe = "";
					bSwipe = "";
					swpe = true;
					position = 0;
					bs = false;

					myList = new ArrayList<CastingDetailsModel>();
					sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
					editor = sharedpreferences.edit();
					rateCount++;
					editor.putInt(Library.RATEIT, rateCount);
					editor.commit();
					finishAffinity();

				}
			})

			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					dialog.cancel();
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		} else {
			if (castivityScreen.getVisibility() == View.VISIBLE) {
				rel_image_screen.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));
				gear_icon.setVisibility(View.VISIBLE);
				outbox_icon.setVisibility(View.VISIBLE);
				gear_icon_mirror.setVisibility(View.INVISIBLE);

				if (castivityScreen.getVisibility() == View.VISIBLE) {
					castivityScreen.setVisibility(View.GONE);
				}
				// filterValue=1;
				// list = new ArrayList<CastingDetailsModel>();
				myList = new ArrayList<CastingDetailsModel>();

				myList.clear();
				page = 0;
				bs = false;
				position = 0;
				castViewStatus = false;
				castViewStatusreak = false;
				swipe = "";
				bSwipe = "";
				swpe = true;

				totalPages = 0;

				CastivateApplication.getInstance().hashMap.clear();
				tap_current_location.setVisibility(View.GONE);
				castingViewNoImage.setVisibility(View.VISIBLE);
				castingsList.setVisibility(View.GONE);
				if (rel_image_screen.getVisibility() == View.VISIBLE) {
					rel_image_screen.setVisibility(View.GONE);
					rel_upload_screen.setVisibility(View.GONE);
					castImageViewStatus = false;
				}
				myList = new ArrayList<CastingDetailsModel>();
				handlerCastingSync.sendEmptyMessage(0);
			}
		}

	}

	public Bitmap screenShot(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return bitmap;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
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

	@Override
	protected void onDestroy() {

		mixpanel.flush();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		// ImageView Click

		case R.id.castings_list_icon:
			textCastivate.setVisibility(View.GONE);
			castingViewNoImage.setVisibility(View.GONE);
			rel_image_screen.setVisibility(View.GONE);
			rel_upload_screen.setVisibility(View.GONE);
			castingsList.setVisibility(View.VISIBLE);

			castingsListIcon.setVisibility(View.GONE);
			// castingsListIconChange.setVisibility(View.GONE);// My
			rel_info_ListView.setVisibility(View.VISIBLE);
			listViewStatus = true;

			grid_view_photos.setVisibility(View.GONE);
			swipeRefreshLayout.setVisibility(View.GONE);

			castListAdapter = new CastingsListAdapter(CastingScreen.this, castingsListArr/*
																						 * ,
																						 * this
																						 */);
			refreshYourAdapter(castingsListArr);
			castingsList.setAdapter(castListAdapter);
			castListAdapter.notifyDataSetChanged();

			page = 0;

			// Sugumaran Changes (25th May 16)
			casting_image_only.setVisibility(View.GONE);

			break;
		// case R.id.castings_list_icon_change:
		// // castingsListIconChange.setVisibility(View.GONE);
		// // castingsListIcon.setVisibility(View.GONE);
		//
		// castingsList.setVisibility(View.GONE);
		// listViewStatus = false;
		//
		// rel_info_ListView.setVisibility(View.GONE);
		// if (gridViewStatus) {
		// progressView.setVisibility(View.GONE);
		// grid_view_photos.setVisibility(View.VISIBLE);
		// // rel_cast_view.setVisibility(View.GONE);
		// castingViewNoImage.setVisibility(View.GONE);
		// } else {
		// rel_cast_view.setVisibility(View.VISIBLE);
		// castingViewNoImage.setVisibility(View.VISIBLE);
		// grid_view_photos.setVisibility(View.GONE);
		// }
		// if (castImageViewStatus) {
		// rel_image_screen.setVisibility(View.VISIBLE);
		// rel_upload_screen.setVisibility(View.VISIBLE);
		// // rel_cast_view.setVisibility(View.GONE);
		// castingViewNoImage.setVisibility(View.GONE);
		// } else {
		// rel_image_screen.setVisibility(View.GONE);
		// rel_upload_screen.setVisibility(View.GONE);
		// rel_cast_view.setVisibility(View.VISIBLE);
		// castingViewNoImage.setVisibility(View.VISIBLE);
		//
		// }
		// break;

		case R.id.current_location:
			tap_current_location.setVisibility(View.VISIBLE);
			editTextLocation.requestFocus();
			findViewById(R.id.frlay).setVisibility(View.VISIBLE);
			break;
		case R.id.birth_year:

			alert(years, birthYear);
			break;

		case R.id.txt_ethnicity:
			ethnicityDialog();
			break;
		case R.id.txt_submit_casting:

			startActivity(new Intent(CastingScreen.this, PostCastivity.class));

			break;

		case R.id.text_help_over_lay:

			startActivity(new Intent(context, Help.class));

			break;
		case R.id.clear:

			editTextLocation.setText("");
			tap_current_location.setVisibility(View.GONE);
			strSelectedLocation = "";
			hideSoftKeyboard();
			break;
		case R.id.birth_clear:

			birthYear.setText("");
			strBithYear = "";

			break;

		case R.id.rel_upload_screen:
			// rel_info_GirdView.setVisibility(View.GONE);
			// grid_view_photos.setVisibility(View.GONE);

			uploadImageToBackEnd();
			break;
		case R.id.tap_current_location:
			if (tap_current_location.getVisibility() == View.VISIBLE)
				tap_current_location.setVisibility(View.GONE);
			// KeyboardUtility.hideSoftKeyboard(CastingScreen.this);
			hideSoftKeyboard();
			dlatitude = dclatitude;
			dlongitude = dclongitude;
			editTextLocation.setText(strCurrentLocation);
			hideSoftKeyboard();
			break;

		case R.id.fav_count:

			Intent createBinderIntent = new Intent(context, FavoriteScreen.class);
			startActivity(createBinderIntent);
			isFavScreen = false;
			position = 0;
			sequenceOrder = -1;

			sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
			if (sharedpreferences.contains(Library.RATEIT)) {
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.remove(Library.RATEIT);
				editor.commit();
			}

			break;

		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.help_overlay:
			help_overlay.setVisibility(View.GONE);
			txtSwipeCastings.setVisibility(View.VISIBLE);
			txtSwipeCastings.startAnimation(animBlink);

			Handler handler = null;
			handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					txtSwipeCastings.setVisibility(View.GONE);

				}
			}, 5000);
			break;
		}

		return false;
	}

	// to receive push notifications
	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity", "registerGCM - successfully registered with GCM server - regId: " + regId);
		} else {
			// Toast.makeText(getApplicationContext(),
			// "RegId already available. RegId: " + regId,
			// Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(CastingScreen.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			DebugReportOnLocat.ln("Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity", "I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	GoogleCloudMessaging gcm;
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";
	String regId;
	String userNotificationFlag = "Yes";

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(SENDER_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: " + regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				// Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// Toast.makeText(getApplicationContext(),
				// "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
				// .show();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(CastingScreen.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
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

					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();
					
					System.out.println("latitude "+latitude);
					System.out.println("longitude "+longitude);
					

					String lat = Double.toString(latitude);
					String lang = Double.toString(longitude);

					Intent intent = new Intent(context, ThankYouActivity.class);
					intent.putExtra("Latitude", lat);
					intent.putExtra("Longitude", lang);
					intent.putExtra("Image", "CastingScreen");
					startActivity(intent);

				}
			}
		}
	}

	/**
	 * Hides the soft keyboard
	 */
	public void hideSoftKeyboard() {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	// private Camera mCamera;
	// int cameraId;
	// private boolean cameraFront = false;
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

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			String lat = Double.toString(latitude);
			String lang = Double.toString(longitude);

			Intent intent = new Intent(context, ThankYouActivity.class);
			intent.putExtra("Latitude", lat);
			intent.putExtra("Longitude", lang);
			intent.putExtra("Image", "CastingScreen");
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

	public boolean isPush;

	public boolean forChk;

	int checkPages = -1;

	@Override
	public void getPushNotificationCasting() {

		// rollId614#Female#18-30#Caucasian#Actor,Model
		boolean chkPsh = false;
		int pos = 0;
		// String pushData = "605Male18-30Caucasian";
		// String pushData = "605";//Male18-30Caucasian";

		if (forChk == false) {
			for (int pages = 0; pages <= totalPages; pages++) {

				if (CastivateApplication.getInstance().hashMap.containsKey(pages)) {
					myList = new ArrayList<CastingDetailsModel>(CastivateApplication.getInstance().hashMap.get(pages));

					for (int i = 0; i < myList.size(); i++) {
						String getPushCheck = myList.get(i).roleId;

						if (!PushRollID.equals("")) {
							if (PushRollID.equalsIgnoreCase(getPushCheck)) {
								pos = i;
								chkPsh = true;
								break;
							}
						}
					}
				} else {
					checkPages = -1;
					forChk = true;
					break;
				}

			}

		}

		if (chkPsh == false) {
			if (checkPages != totalPages) {
				checkPages++;
			} else {
				checkPages = 0;
			}

			for (int i = 0; i < myList.size(); i++) {
				String getPushCheck = myList.get(i).roleId;

				if (!PushRollID.equals("")) {
					if (PushRollID.equalsIgnoreCase(getPushCheck)) {
						pos = i;
						chkPsh = true;
						break;
					}
				}
			}
		}

		if (chkPsh == true) {
			chkPsh = false;
			isPush = false;
			setAllData(myList, pos);
		} else {
			page = checkPages;
			isPush = true;

			new GetDatasNew().execute();
		}

	}

	public String PushRollID = "";

	/*
	 * private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) {
	 * 
	 * String getRollIDs = intent.getStringExtra("rollID");
	 * 
	 * System.out.println("CastingScreen GetRollID >> " + getRollIDs);
	 * 
	 * PushRollID = getRollIDs; getPushNotificationCasting();
	 * 
	 * } };
	 */

	public class GetDatasNew extends AsyncTask<String, Void, Void> {

		String getAll = "";

		// ProgressDialog dd = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressBar = new ProgressDialog(context);
			progressBar.setMessage("Loading Castings...");
			progressBar.show();
			progressBar.setCancelable(false);
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			getAll = getJSONData();

			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			// TODO Auto-generated method stub
			// super.onPostExecute(dd);

			// if (dd != null)
			if (progressBar != null && progressBar.isShowing()) {
				progressBar.dismiss();

				if (getAll != null && !getAll.equals("[]") && !getAll.equals("")) {
					rel_cast_view.setVisibility(View.VISIBLE);
					castingViewNoImage.setVisibility(View.VISIBLE);
					castingsList.setVisibility(View.GONE);
					textCastivate.setVisibility(View.GONE);
					myList = new ArrayList<CastingDetailsModel>();
					castViewStatus = false;
					try {
						JSONArray jArray = new JSONArray(getAll);

						CastingDetailsModel detailsModel;

						for (int i = 0; i < jArray.length(); i++) {
							JSONObject json_data = jArray.getJSONObject(i);

							detailsModel = new CastingDetailsModel(json_data.getString("IdRole"), json_data.getString("casting_title"), json_data.getString("casting_type"),
									json_data.getString("casting_paid_status"), json_data.getString("casting_union_status"), json_data.getString("casting_union_type"),
									json_data.getString("casting_submission_detail"), json_data.getString("casting_synopsis"), json_data.getString("casting_image"),
									json_data.getString("role_desc"), json_data.getString("ageRange"), json_data.getString("role_for"), json_data.getString("role_ethnicity"),
									json_data.getString("fav_flag"), json_data.getString("fav_count"), "", json_data.getString("castingsTotal"),
									json_data.getString("casting_state"), json_data.getString("casting_city"),json_data.getString("casting_email"),json_data.getString("apply_flag"));

							getTotalCastings = Integer.parseInt(json_data.getString("castingsTotal"));
							favCount = json_data.getString("fav_count");
							DebugReportOnLocat.ln("total::" + getTotalCastings);
							if (getTotalCastings > 20) {
								totalPages = getTotalCastings / 20;
								// totalPages--;

								DebugReportOnLocat.ln("totalPages::" + totalPages);
							}
							if (!favCount.equals("0")) {
								// fav_icon.setVisibility(View.INVISIBLE);

								favCountText.setVisibility(View.VISIBLE);
								favCountText.setText(favCount);
							} else {
								// fav_icon.setVisibility(View.VISIBLE);
								favCountText.setVisibility(View.INVISIBLE);
							}

							myList.add(detailsModel);

						}

						if (isPush == true) {
							getPushNotificationCasting();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					}

				} else {
					DebugReportOnLocat.ln("[]");
				}
			}
		}
	}

	public void imageGrid() {

		// rel_cast_view.setVisibility(View.GONE);

		// if (myListImage != null && myListImage.size() == 0) {
		progressView.setVisibility(View.GONE);

		progressBar = new ProgressDialog(context);
		progressBar.setMessage("Loading image...");
		// progressBar.show();
		progressBar.setCancelable(false);

		grid_view_photos.setAdapter(null);
		if (Network.isNetworkConnected(context)) {
			new GetImages().execute();
		} else {
			Library.showToast(context, "Please check your network connection.");
		}

		if (myListImage != null && myListImage.size() > 0) {
			
			
//			CastivateGridAdapter adapter = new CastivateGridAdapter(context, myListImage, this);
//			grid_view_photos.setAdapter(adapter);
//			
			
			
			ImageListAdapter imageListAdapter = new ImageListAdapter(context, myListImage,CastingScreen.this);
			grid_view_photos.setAdapter(imageListAdapter);
			
			
			
		}
	}

	// Sugumaran Changes (25th May 16)
	int imagePos = -1;

	@Override 
	public void get(int pos) {
		// Sugumaran Changes (25th May 16)
		imagePos = pos;

		imgFav.setVisibility(View.VISIBLE);
		CastingImagesModel model = myListImage.get(pos);
		String getName = model.userName.toString().trim();
		imgFav.setChecked(model.favImg);
		imgFav.setEnabled(true);
		txtName.setVisibility(View.VISIBLE);
		if (getName.equals("")) {
			txtName.setVisibility(View.GONE);
		} else {
			txtName.setText(getName);
		}
		sequenceOrder = pos;
		imgGrid.setVisibility(View.VISIBLE);
		local_talent.setVisibility(View.VISIBLE);
		rel_cast_view.setVisibility(View.VISIBLE);
		grid_view_photos.setVisibility(View.GONE);
		swipeRefreshLayout.setVisibility(View.GONE);
		gridViewStatus = false;
		casting_image.setVisibility(View.VISIBLE);
		rel_upload_screen.setVisibility(View.VISIBLE);

		if (Network.isNetworkConnected(context)) {
			progressView.setVisibility(View.VISIBLE);
//			Picasso.with(context).load(model.imageRole).noFade().into(casting_image, new Callback() {
//				@Override
//				public void onSuccess() {
//					progressView.setVisibility(View.GONE);
//				}
//
//				@Override
//				public void onError() {
//				}
//			});
			
			UrlImageViewHelper.setUrlDrawable(casting_image, model.imageRole,new UrlImageViewCallback() {
				
				@Override
				public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
					// TODO Auto-generated method stub
					progressView.setVisibility(View.GONE);
				}
			});

		} else {
			Library.showToast(context, "Please check your network connection.");
		}

		// Sugumaran Changes (25th May 16)
		casting_image.setVisibility(View.GONE);
		casting_image_only.setVisibility(View.VISIBLE);
		if (Network.isNetworkConnected(context)) {
//			progressView.setVisibility(View.VISIBLE);
//			Picasso.with(context).load(model.imageRole).noFade().into(casting_image_only, new Callback() {
//				@Override
//				public void onSuccess() {
//					progressView.setVisibility(View.GONE);
//				}
//
//				@Override
//				public void onError() {
//					Library.showToast(context, "Image load error.");
//				}
//			});
			
			progressView.setVisibility(View.VISIBLE);
			UrlImageViewHelper.setUrlDrawable(casting_image_only, model.imageRole,new UrlImageViewCallback() {
				
				@Override
				public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
					// TODO Auto-generated method stub
					progressView.setVisibility(View.GONE);
				}
			});

		} else {
			Library.showToast(context, "Please check your network connection.");
		}

	};

	public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

		private String url;
		private ImageView imageView;

		// ProgressDialog pd;

		public ImageLoadTask(String url, ImageView imageView) {
			this.url = url;
			this.imageView = imageView;
			this.imageView.setImageBitmap(null);
		}

		@Override
		public void onPreExecute() {
			// pd = new ProgressDialog(context);
			// pd.setMessage("Loading...");
			// pd.setCancelable(false);
			// pd.show();
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				URL urlConnection = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);

			// if (pd.isShowing()) {
			// pd.dismiss();
			// }
			imageView.setImageBitmap(result);
		}

	}

	int Ratecount;
	int nwdate;

	public void checkRate() {

		SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);

		Ratecount = prefs.getInt("Ratecount", 0);
		nwdate = prefs.getInt("ndate", 0);
		boolean getRateFlag = prefs.getBoolean(Library.RATEIT_FLAG, false);
		int dateCount = prefs.getInt("dateCount", 0);
		//
		// Ratecount++;
		// SharedPreferences.Editor editor = getSharedPreferences("my_pref",
		// MODE_PRIVATE).edit();
		// editor.putInt("Ratecount", Ratecount);
		// editor.commit();
		//
		// String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
		// .replace("-", "");
		// int datei = Integer.parseInt(date);
		//
		// if (datei != nwdate) {
		//
		// dateCount++;
		// editor.putInt("dateCount ", dateCount);
		// editor.putInt("ndate", datei);
		// editor.commit();
		//
		// }

		if ((Ratecount == 5 || dateCount == 3) && getRateFlag == false) {
			rateThisAppAlert();
			// int myDays = 3;
			//
			// final Calendar c = Calendar.getInstance();
			// c.add(Calendar.DATE, myDays); // number of days to add
			// int newDate = (c.get(Calendar.YEAR) * 10000) +
			// ((c.get(Calendar.MONTH) + 1) * 100) +
			// (c.get(Calendar.DAY_OF_MONTH));
			SharedPreferences.Editor editor = getSharedPreferences("my_pref", MODE_PRIVATE).edit();
			editor.putInt("Ratecount", 0);
			editor.putInt("dateCount ", 0);
			editor.putInt("ndate", 0);
			editor.commit();
		}

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

		System.out.println("DS "+DS); 
		return DS;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		imageGrid();
	}

	// @Override
	// public void onWindowFocusChanged(boolean hasFocus) {
	// // TODO Auto-generated method stub
	// super.onWindowFocusChanged(hasFocus);
	//
	// DisplayMetrics displaymetrics = new DisplayMetrics();
	// getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	// int height = displaymetrics.heightPixels;
	// int width = displaymetrics.widthPixels;
	// System.out.println("Screen Width :"+width);
	// System.out.println("Screen Height :"+height);
	//
	// System.out.println("TopBar width * Height: "+home_screen.getWidth()+" x "+home_screen.getHeight());
	// System.out.println("BottomBar width * Height: "+rel_upload_screen.getWidth()+" x "+rel_upload_screen.getHeight());
	// //System.out.println("casting_image width * Height: "+casting_image.getWidth()+" x "+casting_image.getHeight());
	//
	//
	// }

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);

		if (grid_view_photos.getVisibility() == View.VISIBLE) {
			return false;
		} else {
			return gestureDetector.onTouchEvent(ev);
		}
	}

}