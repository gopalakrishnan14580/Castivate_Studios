package com.sdi.castivate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.sdi.castivate.adapter.EthnicityFilterAdapter;
import com.sdi.castivate.adapter.LocationSearchAdapter;
import com.sdi.castivate.location.GPSTracker;
import com.sdi.castivate.model.EthnicityModel;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.KeyboardUtility;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.ListExpandable;
import com.sdi.castivate.utils.Network;

/**
 * Created by Balachandar on 17-Apr-15.
 */
@SuppressWarnings("deprecation")
@SuppressLint("ResourceAsColor")
public class PostCastivity extends Activity implements View.OnClickListener {
	LocationSearchAdapter searchAdapter = null;
	ArrayList<String> stringList;
	ListView lView;
	ImageView backBtn;
	RelativeLayout relDetails, tap_current_location;
	LinearLayout lnrInfo;
	TextView emailUs, info, details, nextLayout, ageRange, txtEthnicity,
			productionType, jobType;
	String strEmailUs = "<font color='#000000'>Please fill this form or email us at</font><br> \n"
			+ "<font color='#D00000'><a href='mailto:team@castivate.com'>team@castivate.com</a></font>";
	String detailsText = "<u>Details</u>", infoText = "<u>Info</u>";
	EditText titleCompanyName, emailID, submitToEmail, submitByDate;
	EditText editCastivity, roleDescription, submissionDetails, synopsis;
	// ArrayList<String> age;
	ArrayList<String> productionTypeArray;
	// ArrayList<String> ageRangeArray;
	String strCompanyName, strEmailID, strProductionType, strJobType,
			strSubmitToEmail, strSubmitByDate, strCastingCallLocation;
	String strPaidStatus, strGender, strUnionStatus, strCastivityType,
			strEthnicityType, strAgeRange, strRoleDescription,
			strSubmissionDetails, strSynopsis;
	String strCurrentLocation;
	// private SimpleDateFormat dateFormatter;
	RadioGroup paidGroup, unionGroup, genderGroup;
	RadioButton radioPaid, radioNonPaid, radioUnion, radioTbd, radioBothUnion,
			radioNonUnion, radioFemale, radioMale, radioMaleFemale;
	Context context;
	Activity activity;
	ProgressDialog pDialog;
	int paidPos, unionPos,genderPos;
	TextView btnSubmitCasting;
	ArrayAdapter<EthnicityModel> listAdapter;
	EditText autoCompleteTextViewCountry;
	EthnicityFilterAdapter adapterEthnicityList;
	ArrayList<String> selchkboxlist = new ArrayList<String>();
	ArrayList<EthnicityModel> ethnicityList =new ArrayList<EthnicityModel>();
	String[] ethnicity = { "Caucasian", "African American", "Hispanic",
			"Asian",
			// "Mixed",
			"Native American", "Middle Eastern", "Other" };
	String[] jobTypeArray = { "Actor", "Model", "Singer", "Dancer" };
	String[] ageRangeArray = { "0-2", "2-6", "6-12", "12-18", "18-30", "30-40",
			"40-50", "50-60", "60+" };

	ListView ethnicityListView;
	TextView textViewJobType;
	// to retrieve the Google JSON Map services for US states
	MixpanelAPI mixpanel;
	DownloadTask placesDownloadTask;

	
	String customDate = "";
	Calendar myCalendar = Calendar.getInstance();
	int hour = myCalendar.get(Calendar.HOUR_OF_DAY), min = myCalendar
			.get(Calendar.MINUTE);
	private int year = myCalendar.get(Calendar.YEAR), month = myCalendar
			.get(Calendar.MONTH), day = myCalendar.get(Calendar.DAY_OF_MONTH);
	@SuppressWarnings("unused")
	private int dayy, yearr, monthh;
	String ddate, dmonth, dyear;
	FrameLayout frlay;
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
	//	 FlurryAgent.onEndSession(this);
		DebugReportOnLocat.ln("onstop:");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_casting);
		context = this;
		activity = this;
		mixpanel = MixpanelAPI.getInstance(context, Library.MIXPANEL_TOKEN);
		CastingScreen.isFavScreen=false;
		Library.helpOverlayView = false;
		// dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		findElements();
		// setSubmitByDateField();
		tap_current_location = (RelativeLayout) findViewById(R.id.tap_current_location);
		frlay = (FrameLayout) findViewById(R.id.frlay);

		tap_current_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (tap_current_location.getVisibility() == View.VISIBLE)
					tap_current_location.setVisibility(View.GONE);

				autoCompleteTextViewCountry.setText(strCurrentLocation);
			}
		});
		autoCompleteTextViewCountry = (EditText) findViewById(R.id.casting_call_location);

		// autoCompleteTextViewCountry.setThreshold(1);
		currentLocation();
		// Adding textchange listener

		autoCompleteTextViewCountry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tap_current_location.setVisibility(View.VISIBLE);
				autoCompleteTextViewCountry.requestFocus();
				findViewById(R.id.frlay).setVisibility(View.VISIBLE);

			}
		});

		// Adding textchange listener
		autoCompleteTextViewCountry.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Creating a DownloadTask to download Google Places matching
				// "s"
				if (Network.isNetworkConnected(context)) {
					if (s.toString().trim().contains(strCurrentLocation)) {
						tap_current_location.setVisibility(View.GONE);
					} else {
						tap_current_location.setVisibility(View.VISIBLE);
					}

					if (s.length() == 0) {
						tap_current_location.setVisibility(View.GONE);
					}

					placesDownloadTask = new DownloadTask();

					// Getting url to the Google Places Autocomplete api
					String url = getAutoCompleteUrl(s.toString());

					// Start downloading Google Places
					// This causes to execute doInBackground() of DownloadTask
					// class
					placesDownloadTask.execute(url);
				} else {
					Toast.makeText(context,
							"Please check your internet connection.",
							Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		lView = (ListView) findViewById(R.id.lView);
		// Setting an item click listener for the AutoCompleteTextView dropdown
		// list
		lView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unused")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long id) {
				// TODO Auto-generated method stub
				autoCompleteTextViewCountry.setText(stringList.get(index)
						.toString());
				strCurrentLocation = stringList.get(index).toString();

				// Creating a DownloadTask to download Places details of
				// the selected place
				try {
					Geocoder geocoders = new Geocoder(PostCastivity.this);
					List<Address> addr;
					try {
						addr = geocoder.getFromLocationName(
								stringList.get(index).toString(), 1);
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
					Toast.makeText(
							context,
							"Please enable the GPS to see location filter casting",
							Toast.LENGTH_SHORT).show();
				}

				// stringList= new ArrayList<String>();

				tap_current_location.setVisibility(View.GONE);

			}
		});

		emailID.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)
						|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
					ProductionTypeAlert(productionTypeArray, productionType);
				}
				return false;
			}
		});
		productionType
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
								|| (actionId == EditorInfo.IME_ACTION_DONE)
								|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
							ethnicityList.clear();
							JobTypealert();
						}
						return false;
					}
				});
		submitToEmail
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
								|| (actionId == EditorInfo.IME_ACTION_DONE)
								|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
							dates();
						}
						return false;
					}
				});
		editCastivity
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
								|| (actionId == EditorInfo.IME_ACTION_DONE)
								|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
							ethnicityList.clear();
							ethnicityDialog();
						}
						return false;
					}
				});
		txtEthnicity
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
								|| (actionId == EditorInfo.IME_ACTION_DONE)
								|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
							AgeRangeAlert();
						}
						return false;
					}
				});

	}

	Geocoder geocoder;
	List<Address> addresses;
	Address returnAddress;
	double dlatitude, dlongitude;

	// GPSTracker class
	GPSTracker gps;

	@SuppressWarnings("static-access")
	public void currentLocation() {

		// create class object
		gps = new GPSTracker(context);

		// check if GPS enabled
		if (gps.canGetLocation()) {

			dlatitude = gps.getLatitude();
			dlongitude = gps.getLongitude();

			// \n is for new line
			// Toast.makeText(getApplicationContext(),
			// "Your Location is - \nLat: " + latitude + "\nLong: " +
			// longitude, Toast.LENGTH_LONG).show();
			try {
				geocoder = new Geocoder(context, Locale.ENGLISH);
				addresses = geocoder.getFromLocation(dlatitude, dlongitude, 1);
				StringBuilder str = new StringBuilder();
				if (geocoder.isPresent()) {
					// Toast.makeText(getApplicationContext(),
					// "geocoder present", Toast.LENGTH_SHORT).show();
					try {
						returnAddress = addresses.get(0);
						String localityString = returnAddress.getLocality();
						String country = returnAddress.getCountryName();

						String region_code = returnAddress.getCountryCode();
						// String zipcode =
						// returnAddress.getPostalCode();

						str.append(localityString + " ");
						str.append(country + ", " + region_code + "");
						// str.append(zipcode + "");
						strCurrentLocation = str.toString();
						autoCompleteTextViewCountry.setText(str);
					} catch (IndexOutOfBoundsException e) {
						// TODO: handle exception
						e.printStackTrace();
					}

					// Toast.makeText(getApplicationContext(), str,
					// Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(getApplicationContext(),
							"geocoder not present", Toast.LENGTH_SHORT).show();
				}

				// } else {
				// Toast.makeText(getApplicationContext(),
				// "address not available", Toast.LENGTH_SHORT).show();
				// }
			} catch (IOException e) {
				// TODO Auto-generated catch block

				Log.e("tag", e.getMessage());
			}

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			showSettingsAlert();
		}
	}

	public void findElements() {
		backBtn = (ImageView) findViewById(R.id.back_arrow);
		backBtn.setOnClickListener(this);
		relDetails = (RelativeLayout) findViewById(R.id.details_view);
		relDetails.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				KeyboardUtility.hideSoftKeyboard(activity);
				return false;
			}
		});
		lnrInfo = (LinearLayout) findViewById(R.id.info_layout);
		lnrInfo.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				KeyboardUtility.hideSoftKeyboard(activity);
				return false;
			}
		});

		emailUs = (TextView) findViewById(R.id.email_us);
		info = (TextView) findViewById(R.id.info);
		info.setText(Html.fromHtml(infoText));
		info.setOnClickListener(this);

		nextLayout = (TextView) findViewById(R.id.next);
		nextLayout.setOnClickListener(this);

		details = (TextView) findViewById(R.id.details);
		details.setOnClickListener(this);

		emailUs.setText(Html.fromHtml(strEmailUs), BufferType.SPANNABLE);
		// stripUnderlines(emailUs);
		emailUs.setMovementMethod(LinkMovementMethod.getInstance());

		// info screen
		titleCompanyName = (EditText) findViewById(R.id.title_company_name);
		emailID = (EditText) findViewById(R.id.txt_email_address);

		productionType = (TextView) findViewById(R.id.production_type);
		productionType.setOnClickListener(this);

		jobType = (TextView) findViewById(R.id.job_type);
		jobType.setOnClickListener(this);

		submitToEmail = (EditText) findViewById(R.id.submitToEmail);
		submitByDate = (EditText) findViewById(R.id.submitByDate);
		submitByDate.setOnClickListener(this);
		// castingCallLocation=(EditText)findViewById(R.id.casting_call_location);

		// details screen
		paidGroup = (RadioGroup) findViewById(R.id.radioGroup);
		unionGroup = (RadioGroup) findViewById(R.id.radiogroup_union);
		genderGroup = (RadioGroup) findViewById(R.id.radio_gender);

		radioPaid = (RadioButton) findViewById(R.id.radio_paid);
		radioPaid.setChecked(true);
		strPaidStatus = "paid";
		radioNonPaid = (RadioButton) findViewById(R.id.radio_non_paid);
		radioUnion = (RadioButton) findViewById(R.id.radio_union);
		radioNonUnion = (RadioButton) findViewById(R.id.radio_non_union);
		radioNonUnion.setChecked(true);

		radioMaleFemale = (RadioButton) findViewById(R.id.radio_both);
		radioFemale = (RadioButton) findViewById(R.id.radio_female);
		radioMale = (RadioButton) findViewById(R.id.radio_male);
		radioMale.setChecked(true);
		strGender = "Male";

		radioTbd = (RadioButton) findViewById(R.id.radio_tbd);
		radioBothUnion = (RadioButton) findViewById(R.id.radio_both_union);

		strUnionStatus = "Non-Union";
		// age = new ArrayList<String>();
		// for (int i = 1; i <= 90; i++) {
		// age.add(Integer.toString(i));
		// }
		productionTypeArray = new ArrayList<String>();
		productionTypeArray.add("Feature Film");
		productionTypeArray.add("Scripted TV");
		productionTypeArray.add("Reality TV");
		productionTypeArray.add("Musical");
		productionTypeArray.add("Commercial");
		productionTypeArray.add("Short Film");
		productionTypeArray.add("Student Film");
		productionTypeArray.add("Events");
		productionTypeArray.add("Other");

	
		ageRange = (TextView) findViewById(R.id.age_range);
		ageRange.setOnClickListener(this);

		paidGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub

						// Method 1 For Getting Index of RadioButton
						paidPos = paidGroup
								.indexOfChild(findViewById(checkedId));

						strPaidStatus = String.valueOf(paidPos);
						DebugReportOnLocat.ln("strPaidStatus" + strPaidStatus);
						switch (paidPos) {
						case 0:
							strPaidStatus = "Paid";
							break;

						case 2:
							strPaidStatus = "Non-Paid";
							break;
						case 4:
							strPaidStatus = "Pay-TBD";
							break;
						}
					}
				});
		unionGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						unionPos = unionGroup
								.indexOfChild(findViewById(checkedId));

						strUnionStatus = String.valueOf(unionPos);
						DebugReportOnLocat
								.ln("strUnionStatus" + strUnionStatus);

						switch (unionPos) {
						case 0:
							strUnionStatus = "Union";

							break;

						case 2:
							strUnionStatus = "Non-Union";

							break;
						case 4:
							strUnionStatus = "Union/Non-Union";

							break;
						}
					}
				});
		genderGroup
		.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				genderPos = genderGroup
						.indexOfChild(findViewById(checkedId));

				strGender = String.valueOf(genderPos);
				DebugReportOnLocat
						.ln("strUnionStatus" + strUnionStatus);

				switch (genderPos) {
				case 0:
					strGender = "Male";

					break;

				case 2:
					strGender = "Female";

					break;
				case 4:
					strGender = "Male/Female";

					break;
				}
			}
		});
		editCastivity = (EditText) findViewById(R.id.castivity_type);
		txtEthnicity = (TextView) findViewById(R.id.ethnicity_type);

		txtEthnicity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ethnicityList.clear();
				ethnicityDialog();

			}
		});

		roleDescription = (EditText) findViewById(R.id.role_description);
		submissionDetails = (EditText) findViewById(R.id.submission_details);
		synopsis = (EditText) findViewById(R.id.synosis);

		btnSubmitCasting = (TextView) findViewById(R.id.btn_submit_casting);
		btnSubmitCasting.setOnClickListener(this);
	}

	protected void ethnicityDialog() {

		final Dialog dialog = new Dialog(PostCastivity.this,
				android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.ethnicity);
		dialog.setCancelable(false);

		// Set our custom array adapter as the ListView's adapter.

		ethnicityListView = (ListView) dialog.findViewById(R.id.ethnicity_list);

		for (int i = 0; i < ethnicity.length; i++) {

			EthnicityModel ethnicityModel = new EthnicityModel(ethnicity[i],
					false);
			ethnicityList.add(ethnicityModel);
		}
		if (strEthnicityType != null) {
			// split the string using separator, in this case it is ","
			String[] items = strEthnicityType.split(", ");
			 selchkboxlist = new ArrayList<String>();
			for (String item : items) {
				selchkboxlist.add(item);
			}

			DebugReportOnLocat.ln("Java String converted to ArrayList: "
					+ selchkboxlist);
		}

		for (int i = 0; i < ethnicityList.size(); i++) {
			if (selchkboxlist != null)
				for (int j = 0; j < selchkboxlist.size(); j++) {

					if (ethnicityList.get(i).name.equals(selchkboxlist.get(j))) {

						ethnicityList.get(i).checked = true;
					}
				}
		}
		ethnicityListView.setAdapter(new EthnicityFilterAdapter(PostCastivity.this,
				ethnicityList));

		ImageView backBtn = (ImageView) dialog.findViewById(R.id.back_arrow);
		ImageView selectBtn = (ImageView) dialog.findViewById(R.id.select_icon);
		selectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int count = ethnicityListView.getAdapter().getCount();
				selchkboxlist = new ArrayList<String>();
				strEthnicityType="";
				for (int i = 0; i < count; i++) {
					RelativeLayout itemLayout = (RelativeLayout) ethnicityListView
							.getChildAt(i); // Find by under
											// LinearLayout
					CheckBox checkbox = (CheckBox) itemLayout
							.findViewById(R.id.ethnicity_checkbox);

					if (checkbox.isChecked()) {
						Log.d("Item " + String.valueOf(i), checkbox.getTag()
								.toString());
						selchkboxlist.add(checkbox.getTag().toString());

						strEthnicityType = selchkboxlist.toString();

						DebugReportOnLocat.ln("items selected:::"
								+ selchkboxlist);
						// Toast.makeText(Activities.this,checkbox.getTag().toString()
						// ,Toast.LENGTH_LONG).show();
					}
				}
				strEthnicityType = strEthnicityType.replace("[", "").replace(
						"]", "");
			//	txtEthnicity.setText(strEthnicityType);
				if (strEthnicityType.equals("")) {
					txtEthnicity.setText("");
				} else {
					txtEthnicity.setText(strEthnicityType);
				}
				dialog.dismiss();
			}
		});
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

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

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.info:
			lnrInfo.setVisibility(View.VISIBLE);
			relDetails.setVisibility(View.GONE);
			info.setText(Html.fromHtml(infoText));
			details.setText("Details");
			break;
		case R.id.details:
			submitInfo();
			break;
		case R.id.next:
			submitInfo();
			break;
		case R.id.production_type:
			ProductionTypeAlert(productionTypeArray, productionType);
			break;
		case R.id.age_range:
			ethnicityList.clear();
			AgeRangeAlert();
			break;
		case R.id.job_type:
			ethnicityList.clear();
			JobTypealert();

			break;
		case R.id.submitByDate:
			dates();
			break;

		case R.id.btn_submit_casting:
			KeyboardUtility.hideSoftKeyboard(activity);
			submitDetails();
			break;

		case R.id.back_arrow:
			
			
			finish();
			break;

		}
	}

	// private void ageRangeAlert(ArrayList<String> age_values, final TextView
	// text) {
	// // TODO Auto-generated method stub
	// final Dialog alertDialog = new Dialog(context,
	// android.R.style.Theme_Holo_Light_Dialog_MinWidth);
	// // alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// alertDialog.setTitle("Age Range");
	//
	// LayoutInflater inflater = getLayoutInflater();
	// View convertView = (View) inflater
	// .inflate(R.layout.alert_spinner, null);
	// alertDialog.setContentView(convertView);
	// final ListView lv = (ListView) convertView
	// .findViewById(R.id.list_spinner);
	//
	// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	// android.R.layout.simple_dropdown_item_1line, age_values);
	// lv.setAdapter(adapter);
	// lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id) {
	// // TODO Auto-generated method stub
	// String selectedFromList = (String) (lv
	// .getItemAtPosition(position));
	// text.setText(selectedFromList);
	// text.setTextColor(Color.BLACK);
	// alertDialog.dismiss();
	// }
	// });
	//
	// // WindowManager.LayoutParams wmlp =
	// // alertDialog.getWindow().getAttributes();
	// //
	// // wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
	// // wmlp.x = 200; //x position
	// // wmlp.y = 200; //y position
	// alertDialog.show();
	// }

	private void ProductionTypeAlert(ArrayList<String> age_values,
			final TextView text) {
		// TODO Auto-generated method stub
		final Dialog alertDialog = new Dialog(context,
				android.R.style.Theme_Holo_Light_Dialog_MinWidth);
		// alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setTitle("Production Type");

		LayoutInflater inflater = getLayoutInflater();
		View convertView = (View) inflater
				.inflate(R.layout.alert_spinner, null);
		alertDialog.setContentView(convertView);
		final ListView lv = (ListView) convertView
				.findViewById(R.id.list_spinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, age_values);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String selectedFromList = (String) (lv
						.getItemAtPosition(position));
				text.setText(selectedFromList);

				text.setTextColor(Color.BLACK);
				alertDialog.dismiss();

			}
		});

		// WindowManager.LayoutParams wmlp =
		// alertDialog.getWindow().getAttributes();
		//
		// wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
		// wmlp.x = 200; //x position
		// wmlp.y = 200; //y position

		alertDialog.show();
	}

	private String getAutoCompleteUrl(String place) {

		// Obtain browser key from https://code.google.com/apis/console
	//	String key = "key=AIzaSyAq-4noVLUBMP-kbkeJoQeAjkYb8sPuseI";
		String key = "key=AIzaSyDG78fc_ZTNzO7OqoVaL-o_Q_6aJ9mLwas";
		
		place = place.replaceAll(" ", "%20");
		// place to be be searched
		String input = "input=" + place;
		
		// place type to be searched
		String types = "types=(regions)";
		
		// reference of countries
		String country = "components=country:us";
	
		// String radius = "radius=" + PROXIMITY_RADIUS;
	//	String language = "language=" + "en";
		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = input + "&" + types + "&" + country + "&"
				/*+ language + "&"*/ + sensor + "&" + key;
		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
				+ output + "?" + parameters;

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

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();
			DebugReportOnLocat.ln("data " + data);
			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	String[] from = null;
	int[] to = null;
	JSONObject jObject;
	
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


								try {
							
								
								
									stringList = new ArrayList<String>(
											Arrays.asList(from));
                          
									if (!stringList.isEmpty()) {
										try {
										
											searchAdapter = new LocationSearchAdapter(
													PostCastivity.this,
													stringList);

											lView.setAdapter(searchAdapter);
											
											searchAdapter.notifyDataSetChanged();

											ListExpandable
													.getListViewSize(lView);
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

	public void submitInfo() {
		if (Network.isNetworkConnected(context)) {
			// Internet connection available
			// Validate inputs.

			strCompanyName = titleCompanyName.getText().toString().trim();
			strEmailID = emailID.getText().toString().trim();
			strProductionType = productionType.getText().toString().trim();
			strJobType = jobType.getText().toString().trim();
			strSubmitToEmail = submitToEmail.getText().toString().trim();
			strSubmitByDate = submitByDate.getText().toString().trim();
			strCastingCallLocation = autoCompleteTextViewCountry.getText()
					.toString().trim();
			strCastivityType = editCastivity.getText().toString().trim();
			strEthnicityType = txtEthnicity.getText().toString().trim();
			// Toast.makeText(context,
			// "OnClickListener : " +
			// "\nSpinner 1 : " +
			// String.valueOf(productionType.getSelectedItem()) +
			// "\nSpinner 2 : " + String.valueOf(jobType.getSelectedItem()),
			// Toast.LENGTH_SHORT).show();
			if (strCompanyName.length() > 0 && strEmailID.length() > 0
					&& strProductionType.length() > 0
					&& strJobType.length() > 0 && strSubmitToEmail.length() > 0
					&& strSubmitByDate.length() > 0
					&& strCastingCallLocation.length() > 0) {
				// validate email

				String emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
				if (strEmailID.matches(emailPattern)) {
					// Email Validation success.
					lnrInfo.setVisibility(View.GONE);
					relDetails.setVisibility(View.VISIBLE);
					details.setText(Html.fromHtml(detailsText));
					info.setText("Info");
				} else {
					Library.showToast(context,
							"Please enter your valid Email id");
					emailID.requestFocus();
				}

			} else {
				if (strCompanyName.length() == 0) {
					Library.showToast(context, "Enter the Company Name");
				} else if (strEmailID.length() == 0) {
					Library.showToast(context, "Enter your Email ID");
				} else if (strProductionType.length() == 0) {
					Library.showToast(context, "Enter the Production Type");
				} else if (strJobType.length() == 0) {
					Library.showToast(context, "Enter the Job Type");
				} else if (strSubmitToEmail.length() == 0) {
					Library.showToast(context, "Enter the Submit To Email");
				} else if (strSubmitByDate.length() == 0) {
					Library.showToast(context, "Choose the Submit By Date");
				} else if (strCastingCallLocation.length() == 0) {
					Library.showToast(context,
							"Select the Casting Call Location");
				}
			}
		} else {
			// Internet connection not availabe
			Library.showToast(context,
					getResources().getString(R.string.internet_not_available));
		}
	}

	
	public void dates() {

		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);



		String[] split = null;
		if (!customDate.equalsIgnoreCase("")) {
			if (customDate.contains("/")) {
				split = customDate.split("/");
			}
			if (customDate.contains("-")) {
				split = customDate.split("-");
			}
			if (customDate.contains(" ")) {
				split = customDate.split(" ");
			}

			ddate = split[2];
			dmonth = split[1];
			dyear = split[0];
		} else {
			ddate = String.valueOf(day);
			dmonth = String.valueOf(month + 1);
			dyear = String.valueOf(year);
		}

		yearr = Integer.parseInt(dyear);
		dayy = Integer.parseInt(ddate);
		monthh = Integer.parseInt(dmonth);

		showDialog(999);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		// set date picker as current date
		DatePickerDialog _date = new DatePickerDialog(this, datePickerListener,
				yearr, month, dayy) {

			@Override
			public void onDateChanged(DatePicker view, int myear,
					int mmonthOfYear, int mdayOfMonth) {
				if (myear < year)
					view.updateDate(year, month, day);

				if (mmonthOfYear < month && myear == year)
					view.updateDate(year, month, day);

				if (mdayOfMonth < day && myear == year && mmonthOfYear == month)
					view.updateDate(year, month, day);
			}
		};
		return _date;

	}

	/* Dialog for Date picker */
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

		
			myCalendar.set(Calendar.YEAR, selectedYear);
			myCalendar.set(Calendar.MONTH, selectedMonth);
			myCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);

			String myFormat = "yyyy-MM-dd";
			String customFormat = "MMM dd, yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

			SimpleDateFormat sdf2 = new SimpleDateFormat(customFormat,
					Locale.US);
			customDate = sdf.format(myCalendar.getTime());
			submitByDate.setText(sdf2.format(myCalendar.getTime()));

			dayy = selectedDay;
			monthh = selectedMonth;
			yearr = selectedYear;

		}
	};

	public void submitDetails() {
		if (Network.isNetworkConnected(context)) {
			// Internet connection available
			// Validate inputs.

			strCastivityType = editCastivity.getText().toString().trim();
			strEthnicityType = txtEthnicity.getText().toString().trim();
			strRoleDescription = roleDescription.getText().toString().trim();
			strSubmissionDetails = submissionDetails.getText().toString()
					.trim();
			strSynopsis = synopsis.getText().toString().trim();
			strAgeRange = ageRange.getText().toString().trim();

			if (strCastivityType.length() > 0 && strEthnicityType.length() > 0
					&& strAgeRange.length() > 0

					&& strRoleDescription.length() > 0
					&& strSubmissionDetails.length() > 0
					&& strSynopsis.length() > 0) {
				// validate email
				handlerSubmitCastingSync.sendEmptyMessage(0);

			} else {
				if (strCastivityType.length() == 0) {
					Library.showToast(context, "Enter the Production Title");
				} else if (strEthnicityType.length() == 0) {
					Library.showToast(context, "Choose the Ethnicity Type");
				} else if (strAgeRange.length() == 0) {
					Library.showToast(context, "Choose the Age Range");
				} else if (strRoleDescription.length() == 0) {
					Library.showToast(context, "Enter the Role Description");
				} else if (strSubmissionDetails.length() == 0) {
					Library.showToast(context, "Enter the Submission Details");
				} else if (strSynopsis.length() == 0) {
					Library.showToast(context, "Enter the Synopsis");
				}
			}
		} else {
			// Internet connection not availabe
			Library.showToast(context,
					getResources().getString(R.string.internet_not_available));
		}
	}

	public void alert(ArrayList<String> age_values, final TextView text) {
		// TODO Auto-generated method stub
		final Dialog alertDialog = new Dialog(context);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		LayoutInflater inflater = getLayoutInflater();
		View convertView = (View) inflater
				.inflate(R.layout.alert_spinner, null);
		alertDialog.setContentView(convertView);
		final ListView lv = (ListView) convertView
				.findViewById(R.id.list_spinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, age_values);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String selectedFromList = (String) (lv
						.getItemAtPosition(position));
				text.setText(selectedFromList);
				text.setTextColor(Color.BLACK);
				alertDialog.dismiss();
			}
		});

		// WindowManager.LayoutParams wmlp =
		// alertDialog.getWindow().getAttributes();
		//
		// wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
		// wmlp.x = 200; //x position
		// wmlp.y = 200; //y position
		alertDialog.show();
	}

	public void AgeRangeAlert() {

		final Dialog dialog = new Dialog(PostCastivity.this,
				android.R.style.Theme_Holo_Light_Dialog_MinWidth);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.ethnicity);
		dialog.setCancelable(false);

		// Set our custom array adapter as the ListView's adapter.
		textViewJobType = (TextView) dialog.findViewById(R.id.textView);
		textViewJobType.setText("Age Range");

		ethnicityListView = (ListView) dialog.findViewById(R.id.ethnicity_list);

		for (int i = 0; i < ageRangeArray.length; i++) {

			EthnicityModel ethnicityModel = new EthnicityModel(ageRangeArray[i],
					false);
			ethnicityList.add(ethnicityModel);
		}
		if (strAgeRange != null) {
			// split the string using separator, in this case it is ","
			String[] items = strAgeRange.split(", ");
			selchkboxlist = new ArrayList<String>();
			for (String item : items) {
				selchkboxlist.add(item);
			}

			DebugReportOnLocat.ln("Java String converted to ArrayList: "
					+ selchkboxlist);
		}

		for (int i = 0; i < ethnicityList.size(); i++) {
			if (selchkboxlist != null)
				for (int j = 0; j < selchkboxlist.size(); j++) {

					if (ethnicityList.get(i).name.equals(selchkboxlist.get(j))) {

						ethnicityList.get(i).checked = true;
					}
				}
		}
		ethnicityListView.setAdapter(new EthnicityFilterAdapter(PostCastivity.this,
				ethnicityList));
		
//		ethnicityListView.setAdapter(new EthnicityAdapter(PostCastivity.this,
//				ageRangeArray));

		ImageView backBtn = (ImageView) dialog.findViewById(R.id.back_arrow);
		ImageView selectBtn = (ImageView) dialog.findViewById(R.id.select_icon);
		selectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int count = ethnicityListView.getAdapter().getCount();
			selchkboxlist = new ArrayList<String>();
			strAgeRange="";
				for (int i = 0; i < count; i++) {
					RelativeLayout itemLayout = (RelativeLayout) ethnicityListView
							.getChildAt(i); // Find by under LinearLayout
					CheckBox checkbox = (CheckBox) itemLayout
							.findViewById(R.id.ethnicity_checkbox);

					if (checkbox.isChecked()) {
						Log.d("Item " + String.valueOf(i), checkbox.getTag()
								.toString());
						selchkboxlist.add(checkbox.getTag().toString());

						strAgeRange = selchkboxlist.toString();

						DebugReportOnLocat.ln("items selected:::"
								+ selchkboxlist);
						// Toast.makeText(Activities.this,checkbox.getTag().toString()
						// ,Toast.LENGTH_LONG).show();
					}
				}
				strAgeRange = strAgeRange.replace("[", "").replace("]", "");
				if (strAgeRange.equals("")) {
					ageRange.setText("");
				} else {
					ageRange.setText(strAgeRange);
				}

				

				dialog.dismiss();
			}
		});
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				
				
				dialog.dismiss();
			}
		});
		// Create and populate ethnicity.
		// private String[] ethnicity = {"Caucasian", "African American",
		// "Hispanic", "Asian", "Mixed", "Native American","Middle Eastern",
		// "Other"};

		dialog.show();
		// =====================

	}

	public void JobTypealert() {

		final Dialog dialog = new Dialog(PostCastivity.this,
				android.R.style.Theme_Holo_Light_Dialog_MinWidth);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.ethnicity);
		dialog.setCancelable(false);

		// Set our custom array adapter as the ListView's adapter.
		textViewJobType = (TextView) dialog.findViewById(R.id.textView);
		textViewJobType.setText("Job Type");

		ethnicityListView = (ListView) dialog.findViewById(R.id.ethnicity_list);
		

		for (int i = 0; i < jobTypeArray.length; i++) {

			EthnicityModel ethnicityModel = new EthnicityModel(jobTypeArray[i],
					false);
			ethnicityList.add(ethnicityModel);
		}
		if (strJobType != null) {
			// split the string using separator, in this case it is ","
			String[] items = strJobType.split(", ");
		selchkboxlist = new ArrayList<String>();
			for (String item : items) {
				selchkboxlist.add(item);
			}

			DebugReportOnLocat.ln("Java String converted to ArrayList: "
					+ selchkboxlist);
		}

		for (int i = 0; i < ethnicityList.size(); i++) {
			if (selchkboxlist != null)
				for (int j = 0; j < selchkboxlist.size(); j++) {

					if (ethnicityList.get(i).name.equals(selchkboxlist.get(j))) {

						ethnicityList.get(i).checked = true;
					}
				}
		}
		ethnicityListView.setAdapter(new EthnicityFilterAdapter(PostCastivity.this,
				ethnicityList));
		
	

		ImageView backBtn = (ImageView) dialog.findViewById(R.id.back_arrow);
		ImageView selectBtn = (ImageView) dialog.findViewById(R.id.select_icon);
		selectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int count = ethnicityListView.getAdapter().getCount();
				selchkboxlist = new ArrayList<String>();
				strJobType="";
				for (int i = 0; i < count; i++) {
					RelativeLayout itemLayout = (RelativeLayout) ethnicityListView
							.getChildAt(i); // Find by under LinearLayout
					CheckBox checkbox = (CheckBox) itemLayout
							.findViewById(R.id.ethnicity_checkbox);

					if (checkbox.isChecked()) {
						Log.d("Item " + String.valueOf(i), checkbox.getTag()
								.toString());
						selchkboxlist.add(checkbox.getTag().toString());

						strJobType = selchkboxlist.toString();

						DebugReportOnLocat.ln("items selected:::"
								+ selchkboxlist);
						// Toast.makeText(Activities.this,checkbox.getTag().toString()
						// ,Toast.LENGTH_LONG).show();
					}
				}
				strJobType = strJobType.replace("[", "").replace("]", "");
				if (strJobType.equals("")) {
					jobType.setText("");
				} else {
					jobType.setText(strJobType);
				}
			

				dialog.dismiss();
			}
		});
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Sugumaran Changes (26th May 16)
				int count = ethnicityListView.getAdapter().getCount();
				selchkboxlist = new ArrayList<String>();
				strJobType="";
				for (int i = 0; i < count; i++) {
					RelativeLayout itemLayout = (RelativeLayout) ethnicityListView
							.getChildAt(i); // Find by under LinearLayout
					CheckBox checkbox = (CheckBox) itemLayout
							.findViewById(R.id.ethnicity_checkbox);

					if (checkbox.isChecked()) {
						Log.d("Item " + String.valueOf(i), checkbox.getTag()
								.toString());
						selchkboxlist.add(checkbox.getTag().toString());

						strJobType = selchkboxlist.toString();

						DebugReportOnLocat.ln("items selected:::"
								+ selchkboxlist);
						// Toast.makeText(Activities.this,checkbox.getTag().toString()
						// ,Toast.LENGTH_LONG).show();
					}
				}
				strJobType = strJobType.replace("[", "").replace("]", "");
				if (strJobType.equals("")) {
					jobType.setText("");
				} else {
					jobType.setText(strJobType);
				}
				//Sugumaran Changes (26th May 16)
				
				dialog.dismiss();
			}
		});
		// Create and populate ethnicity.
		// private String[] ethnicity = {"Caucasian", "African American",
		// "Hispanic", "Asian", "Mixed", "Native American","Middle Eastern",
		// "Other"};

		dialog.show();
		// =====================

	}

	private Handler handlerSubmitCastingSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(context)) {
				postSubmitCastingASync = new PostSubmitCastingASync();
				postSubmitCastingASync.execute();
			} else {
				Toast.makeText(context, "Internet connection is not available",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	private PostSubmitCastingASync postSubmitCastingASync;

	public class PostSubmitCastingASync extends AsyncTask<String, Void, Void> {

		@Override
		public void onPreExecute() {
			pDialog = new ProgressDialog(PostCastivity.this,
					AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("submitting casting...");
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			postSubmitCasting();
			return null;
		}

		@Override
		public void onPostExecute(Void params) {
			if (pDialog.isShowing())
				pDialog.dismiss();
			if (json != null) {
				alert(context, json);
				JSONObject props = new JSONObject();
				try {
					props.put("User ID",Library.androidUserID );
					props.put("Posted", "Yes");
					mixpanel.track("Casting Posted", props);	
				} catch (JSONException e) {
					// TODO: handle exception
				}
			
			
			}else{
				alert(context, "Casting not posted. Please try again.");
				JSONObject props = new JSONObject();
				try {
					props.put("User ID",Library.androidUserID );
					props.put("Posted", "No");
					mixpanel.track("Casting Posted", props);	
				} catch (JSONException e) {
					// TODO: handle exception
				}
			}

		}

	}

	InputStream is = null;
	String json = "";
//	JSONObject jObj = null;
	StringBuilder sb;

	String Status;

	public void postSubmitCasting() {
		try {
			HttpPost request = new HttpPost(HttpUri.SUBMITCASTING);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type",
					"application/x-www-form-urlencoded");

			JSONStringer item = null;
			// {"userId":"0","castingTitle":"TestTitle","castingLocation":"New York",
			// "castingSubmission":"Test Castivate","castingFor":"TestSubmit@gmail.com",
			// "castingFromAge":"30","castingToAge":"50","userEmail":"Testes@gmail.com",
			// "castingType":"TestProductionType","castingJob":"TestJobType","castingPaidStatus":"nonpaid",
			// "castingUnionStatus":"union","castingSynosis":"synosis","castingDate":"2020-05-17",
			// "castingEthnicity":"Caucasian,Hispanic","castingDesc":"Test Role Description"}

			try {

				item = new JSONStringer()
						.object()
						.key("userId")
						.value(Library.androidUserID)
						.key("castingTitle")
						.value(strCastivityType)
						.key("castingLocation")
						.value(strCastingCallLocation)
						.key("companyName")
						.value(strCompanyName)
						.key("castingSubmission")
						.value(strSubmissionDetails)
						.key("castingFor")
						.value(strEmailID)
						.key("castingFromAge")
						.value(strAgeRange)
						.key("gender").value(strGender)
						.key("userEmail").value(strSubmitToEmail)
						.key("castingType").value(strProductionType)
						.key("castingJob").value(strJobType)
						.key("castingPaidStatus").value(strPaidStatus)
						.key("castingUnionStatus").value(strUnionStatus)
						.key("castingSynosis").value(strSynopsis)
						.key("castingDate").value(strSubmitByDate)
						.key("castingEthnicity").value(strEthnicityType)
						.key("castingDesc").value(strRoleDescription)
						.endObject();
				Log.d("sumit data", item.toString());
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
						BufferedReader reader2 = new BufferedReader(
								new InputStreamReader(is, "iso-8859-1"), 8);
						sb = new StringBuilder();
						String line = null;
						while ((line = reader2.readLine()) != null) {
							sb.append(line + "\n");
						}
						is.close();
						json = sb.toString();
					
					
			//			mixpanel.registerSuperProperties(props);
						DebugReportOnLocat.ln("json response for submit::"
								+ json);
						// Toast.makeText(context,"Casting submitted successfully: "+json,Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.e("Buffer Error",
								"Error converting result " + e.toString());
					}

					// try {
					// jObj = new JSONObject(json);
					// //Status = jObj.getBoolean("Authentication");
					//
					// String Token, UserID, UserType;
					//
					//
					// String result = new String(sb);
					// Log.d("Result", result);
					//
					// } catch (JSONException e) {
					// Log.e("JSON Parser",
					// "Error parsing data " + e.toString());
					// }

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

	private void alert(Context context2, String string) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity, android.R.style.Theme_DeviceDefault_Dialog);

		alertDialogBuilder.setTitle("Post Casting").setMessage(string)
				.setCancelable(false)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	// public void postJSONData() {
	// AsyncHttpClient client = new AsyncHttpClient();
	// RequestParams params = new RequestParams();
	//
	// //
	// userId,castingTitle,castingLocation,castingSubmission,castingFor,castingFromAge,castingToAge,userEmail,
	// // castingType,castingJob,
	// //
	// //
	// castingPaidStatus,castingUnionStatus,castingSynosis,castingDate,castingEthnicity,castingDesc
	// params.put("userId", Library.androidUserID);
	// params.put("castingTitle", strCompanyName);
	// params.put("castingLocation", strCastingCallLocation);
	// // params.put("castingSubmission", strRoleDescription);
	// // params.put("castingFor", st);
	// params.put("castingFromAge", strFromAge);
	// params.put("castingToAge", strToAge);
	// params.put("userEmail", strSubmitToEmail);
	// params.put("castingType",strCastivityType );
	// params.put("castingJob", strJobType);
	// params.put("castingPaidStatus", strPaidStatus);
	// params.put("castingUnionStatus", strUnionStatus);
	// // params.put("castingSynosis", st);
	// params.put("castingDate", strSubmitByDate);
	// params.put("castingEthnicity", strEthnicityType);
	// params.put("castingDesc", strRoleDescription);
	//
	//
	//
	//
	//
	// if (params != null) {
	// DebugReportOnLocat.ln(params.toString());
	// }
	//
	// client.setTimeout(50000);
	// client.post(HttpUri.SUBMITCASTING, params, new JsonHttpResponseHandler()
	// {
	//
	// ProgressDialog pd;
	//
	// @Override
	// public void onStart() {
	//
	// pd = new ProgressDialog(context, AlertDialog.THEME_TRADITIONAL);
	// pd.setInverseBackgroundForced(false);
	// pd.setTitle("Submitting casting...");
	// pd.show();
	// }
	//
	// @Override
	// public void onFinish() {
	// if (pd.isShowing()) {
	// pd.hide();
	// }
	// pd.dismiss();
	// }
	// public void onSuccess(JSONObject response) {
	// pd.hide();
	// // Do something with the response
	// pd.dismiss();
	// try {
	// Log.d("Comments: ", "Comment" + response.toString());
	// if (response.getString("Result").equalsIgnoreCase("1")) {
	//
	//
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// @Override
	// public void onFailure(Throwable error, String content) {
	// pd.hide();
	// System.out.println("AsyncHttpClient Failure:" + content);
	// Toast.makeText(context, "Message could not be added",
	// Toast.LENGTH_SHORT).show();
	// error.printStackTrace();
	// pd.dismiss();
	// }
	// });
	// }

	// private void setSubmitByDateField() {
	// submitByDate.setOnClickListener(this);
	//
	//
	// Calendar newCalendar = Calendar.getInstance();
	// submitByDateDatePickerDialog = new DatePickerDialog(this, new
	// DatePickerDialog.OnDateSetListener() {
	//
	// public void onDateSet(DatePicker view, int year, int monthOfYear, int
	// dayOfMonth) {
	// Calendar newDate = Calendar.getInstance();
	// newDate.set(year, monthOfYear, dayOfMonth);
	// submitByDate.setText(dateFormatter.format(newDate.getTime()));
	// }
	//
	// },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
	// newCalendar.get(Calendar.DAY_OF_MONTH));
	//
	//
	// }
	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,
				android.R.style.Theme_DeviceDefault_Dialog);

		// Setting Dialog Title
		// alertDialog.setTitle("Cast");

		// Setting Dialog Message
		alertDialog.setMessage("Please enable GPS to continue inside the app.");

		// On pressing Settings button
		alertDialog.setPositiveButton("Enable",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						context.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Close App",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finishAffinity();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
