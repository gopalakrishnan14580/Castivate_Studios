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
import com.sdi.castivate.model.TotalFavModel;
import com.sdi.castivate.utils.CastingsLinkMovementMethod;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.GridInterface;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint({ "InlinedApi", "UseSparseArrays" })
@SuppressWarnings("deprecation")
public class FavoriteScreen extends Activity implements GridInterface {
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
	// ArrayList<CastingDetailsModel> favoriteCastings;

	// Sugumaran
	ArrayList<FavImagesModel> tempImagesList = null;
	ArrayList<FavImagesModel> mainImagesList = null;

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

	HashMap<Integer, ArrayList<TotalFavModel>> mapNew = new HashMap<Integer, ArrayList<TotalFavModel>>();

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

		// try {
		// favoriteCastings = new ArrayList<CastingDetailsModel>();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

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
				callCastingSCreen();
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
		progressView.setOnTouchListener(gestureListener);

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
		// new GetImages().execute();

		if (tempImagesList != null && tempImagesList.size() > 0) {
			FavoriteGridAdapter adapter = new FavoriteGridAdapter(context, tempImagesList, this);
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

	boolean isFirstCall = false;
	private Handler handlerFavCastingSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(context)) {

				downloadedPages.add(page);
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
			if (Network.isNetworkConnected(FavoriteScreen.this)) {
				postFavoriteCastingSync = new PostFavoriteCastingSync();
				postFavoriteCastingSync.execute();
			} else {
				Toast.makeText(FavoriteScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private PostFavoriteCastingSync postFavoriteCastingSync;

	public class PostFavoriteCastingSync extends AsyncTask<String, Void, Void> {

		@SuppressLint("InlinedApi")
		@Override
		public void onPreExecute() {
			pDialog = new ProgressDialog(FavoriteScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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

			int get = fPosition;
			list.remove(fPosition);

			fav_iconNoImage.setChecked(true);

			if (list.size() == fPosition) {
				if (list.size() != 0) {
					rel_image_screen.setVisibility(View.GONE);
					rel_upload_screen.setVisibility(View.GONE);
					rel_cast_view_new.setVisibility(View.GONE);
					textCastivate.setVisibility(View.VISIBLE);
				} else {
					callCastingSCreen();
				}
			} else if (list.size() == 0) {
				callCastingSCreen();
			} else if (list.size() < 0) {
				rel_image_screen.setVisibility(View.GONE);
				rel_upload_screen.setVisibility(View.GONE);
				rel_cast_view_new.setVisibility(View.GONE);
				textCastivate.setVisibility(View.VISIBLE);
			} else {
				for (int i = get; i < list.size(); i++) {

					if (i < list.size()) {

						if (list.get(i).imageShow == true) {
							fPosition = i;

							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);

							imgFav.setChecked(true);
							
							if(list.get(i).imagesModel.userName!=null&&!list.get(i).imagesModel.userName.equals("")){
								txtName.setVisibility(View.VISIBLE);
								txtName.setText(list.get(i).imagesModel.userName);
							}
							
							progressView.setVisibility(View.VISIBLE);
							Picasso.with(context).load((list.get(i).imagesModel.casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
								@Override
								public void onSuccess() {
									progressView.setVisibility(View.GONE);
								}

								@Override
								public void onError() {
								}
							});
							break;

						} else if (list.get(i).castingShow == true) {
							rel_image_screen.setVisibility(View.GONE);
							rel_upload_screen.setVisibility(View.GONE);
							rel_cast_view_new.setVisibility(View.VISIBLE);
							textCastivate.setVisibility(View.GONE);

							fPosition = i;
							setAllDataNew(list, fPosition);
							break;
						} else {
							rel_image_screen.setVisibility(View.GONE);
							rel_upload_screen.setVisibility(View.GONE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.VISIBLE);
						}
					} else {
						System.out.println("Again check");
					}

				}
			}
		}

	}

	public void postFavorite() {
		try {
			HttpPost request = new HttpPost(HttpUri.SET_FAV);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			JSONStringer item = null;

			try {
				// int fPos = fPosition;
				// if (fPosition == -1 && favoriteCastings.size() == 1) {
				// fPosition = 0;
				// }
				// {"userId":"68","roleId":"77","castingFav":"1","roleEnthicity":"Caucasian","roleAgeRange":"40-50","roleFor":"Female"}

				// Old
				// item = new
				// JSONStringer().object().key("userId").value(Library.androidUserID).key("roleId").value(favoriteCastings.get(fPosition).roleId).key("castingFav")
				// .value("0").key("roleEnthicity").value(favoriteCastings.get(fPosition).roleForEthnicity).key("roleAgeRange")
				// .value(favoriteCastings.get(fPosition).ageRange).key("roleFor").value(favoriteCastings.get(fPosition).roleForGender).endObject();

				// New
				CastingDetailsModel model = list.get(fPosition).castingModel;

				item = new JSONStringer().object().key("userId").value(Library.androidUserID).key("roleId").value(model.roleId).key("castingFav").value("0").key("roleEnthicity")
						.value(model.roleForEthnicity).key("roleAgeRange").value(model.ageRange).key("roleFor").value(model.roleForGender).endObject();

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
						// CastingScreen.favCount =
						// oneObject.getString("fav_count");
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
	//
	// void CastingLeftSwipeView() {
	//
	// fPosition--;
	// if (fPosition >= head && fPosition < tail) {
	// setAllData(favoriteCastings, fPosition);
	// } else {
	//
	// // if(favPos<favImagesList.size()){
	// //
	// // rel_image_screen.setVisibility(View.VISIBLE);
	// // rel_upload_screen.setVisibility(View.VISIBLE);
	// // rel_cast_view_new.setVisibility(View.GONE);
	// // textCastivate.setVisibility(View.GONE);
	// // showImage();
	// // }else{
	// //
	// // }
	//
	// page = page - 1;
	// if (page >= 0 && page <= lastPage) {
	//
	// if (hashMap.containsKey(page)) {
	// favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
	// fPosition = favoriteCastings.size() - 1;
	// head = 0;
	// tail = favoriteCastings.size();
	// setAllData(favoriteCastings, fPosition);
	// } else {
	// if (page < totalPages) {
	// from_swipe = "left";
	// handlerFavCastingSync.sendEmptyMessage(0);
	// } else {
	//
	// favPos = 0;
	// page = lastPage;
	// favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
	// fPosition = favoriteCastings.size() - 1;
	// head = 0;
	// tail = favoriteCastings.size();
	// if ((fPosition == favoriteCastings.size() - 1 && page == lastPage) ||
	// (page == 0 && fPosition == 0)) {
	// showText = true;
	// from_swipe = "left";
	// textCastivate.setVisibility(View.VISIBLE);
	// rel_cast_view_new.setVisibility(View.GONE);
	// rel_image_screen.setVisibility(View.GONE);
	// rel_upload_screen.setVisibility(View.GONE);
	// // setAllData(favoriteCastings, fPosition);
	// } else {
	// setAllData(favoriteCastings, fPosition);
	// }
	//
	// }
	//
	// }
	// } else {
	//
	// // if (page == 0 && (favoriteCastings == null ||
	// // favoriteCastings.size() == 0) && favImagesList.size() > 0 &&
	// // favImagesList != null) {
	// // rel_image_screen.setVisibility(View.VISIBLE);
	// // rel_upload_screen.setVisibility(View.VISIBLE);
	// // rel_cast_view_new.setVisibility(View.GONE);
	// // textCastivate.setVisibility(View.GONE);
	// // favPos = 0;
	// // showImage();
	// //
	// // }else{
	//
	// from_swipe = "left";
	// page = lastPage;
	// favPos = 0;
	//
	// if (hashMap.containsKey(page)) {
	//
	// // if(favPos<favImagesList.size()){
	// //
	// // rel_image_screen.setVisibility(View.VISIBLE);
	// // rel_upload_screen.setVisibility(View.VISIBLE);
	// // rel_cast_view_new.setVisibility(View.GONE);
	// // textCastivate.setVisibility(View.GONE);
	// // showImage();
	// // }else{
	//
	// favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
	// fPosition = favoriteCastings.size() - 1;
	// head = 0;
	// tail = favoriteCastings.size();
	// if ((fPosition == favoriteCastings.size() - 1 && page == lastPage) ||
	// (page == 0 && fPosition == 0)) {
	// showText = true;
	// from_swipe = "left";
	// textCastivate.setVisibility(View.VISIBLE);
	// rel_cast_view_new.setVisibility(View.GONE);
	// rel_image_screen.setVisibility(View.GONE);
	// rel_upload_screen.setVisibility(View.GONE);
	// // setAllData(favoriteCastings, fPosition);
	// } else {
	// setAllData(favoriteCastings, fPosition);
	// }
	// // }
	// } else {
	// handlerFavCastingSync.sendEmptyMessage(0);
	// }
	// // }
	// }
	// // }
	// }
	//
	// //
	// // fPosition--;
	// //
	// // int pos = fPosition;
	// //
	// // if (fPosition >= head && fPosition < tail) {
	// // if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
	// // == favoriteCastings.size() - 1)) {
	// //
	// // if (totalPages == 0 && bss == true) {
	// // textCastivate.setVisibility(View.VISIBLE);
	// // rel_cast_view_new.setVisibility(View.GONE);
	// // bSwipe = "left";
	// //
	// // DebugReportOnLocat.ln("" + pos);
	// //
	// // } else {
	// // setAllData(favoriteCastings, fPosition);
	// // bss = true;
	// // }
	// // } else {
	// // setAllData(favoriteCastings, fPosition);
	// // }
	// //
	// // } else {
	// //
	// // page = page - 1;
	// //
	// // if (page >= 0 && page <= totalPages) {
	// //
	// // if (hashMap.containsKey(page)) {
	// // favoriteCastings = new
	// // ArrayList<CastingDetailsModel>(hashMap.get(page));
	// // myB = false;
	// // // fPosition = 9;
	// // fPosition = favoriteCastings.size() - 1;
	// //
	// // head = 0;
	// // tail = favoriteCastings.size();
	// //
	// // if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
	// // == favoriteCastings.size() - 1)) {
	// //
	// // textCastivate.setVisibility(View.VISIBLE);
	// // rel_cast_view_new.setVisibility(View.GONE);
	// // bSwipe = "left";
	// //
	// // DebugReportOnLocat.ln("" + pos);
	// // } else {
	// //
	// // setAllData(favoriteCastings, fPosition);
	// //
	// // }
	// //
	// // } else {
	// // fPosition = 9;
	// //
	// // handlerFavCastingSync.sendEmptyMessage(0);
	// // }
	// // } else {
	// //
	// // page = lastPage;
	// //
	// // if (getTotalCastings < 10) {
	// // page = 0;
	// //
	// // favoriteCastings = new
	// // ArrayList<CastingDetailsModel>(hashMap.get(page));
	// // myB = false;
	// //
	// // fPosition = favoriteCastings.size() - 1;
	// //
	// // head = 0;
	// // tail = favoriteCastings.size();
	// //
	// // if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
	// // == favoriteCastings.size() - 1)) {
	// //
	// // textCastivate.setVisibility(View.VISIBLE);
	// // rel_cast_view_new.setVisibility(View.GONE);
	// // bSwipe = "left";
	// //
	// // DebugReportOnLocat.ln("" + pos);
	// // } else {
	// //
	// // setAllData(favoriteCastings, fPosition);
	// //
	// // }
	// //
	// // } else {
	// // if (hashMap.containsKey(page)) {
	// //
	// // favoriteCastings = new
	// // ArrayList<CastingDetailsModel>(hashMap.get(page));
	// // myB = false;
	// //
	// // fPosition = favoriteCastings.size() - 1;
	// //
	// // head = 0;
	// // tail = favoriteCastings.size();
	// //
	// // if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
	// // == favoriteCastings.size() - 1)) {
	// //
	// // textCastivate.setVisibility(View.VISIBLE);
	// // rel_cast_view_new.setVisibility(View.GONE);
	// // bSwipe = "left";
	// //
	// // DebugReportOnLocat.ln("" + pos);
	// // } else {
	// //
	// // setAllData(favoriteCastings, fPosition);
	// //
	// // }
	// //
	// // } else {
	// //
	// // handlerFavCastingSync.sendEmptyMessage(0);
	// //
	// // }
	// // }
	// //
	// // }
	// //
	// // }
	//
	// }

	//
	// boolean testB = false;
	//
	// boolean showImage = false;

	String from_swipe = "";
	boolean showText = false;

	// void CastingRightSwipeView() {
	//
	// fPosition++;
	// if (fPosition >= head && fPosition < tail) {
	// setAllData(favoriteCastings, fPosition);
	// } else {
	//
	// // if(favPos<favImagesList.size()){
	// //
	// // rel_image_screen.setVisibility(View.VISIBLE);
	// // rel_upload_screen.setVisibility(View.VISIBLE);
	// // rel_cast_view_new.setVisibility(View.GONE);
	// // textCastivate.setVisibility(View.GONE);
	// // showImage();
	// //
	// //
	// // }else{
	//
	// page = page + 1;
	// if (hashMap.containsKey(page)) {
	// favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
	// head = 0;
	// fPosition = 0;
	// tail = favoriteCastings.size();
	// setAllData(favoriteCastings, fPosition);
	// } else {
	// if (page < totalPages) {
	// from_swipe = "right";
	// handlerFavCastingSync.sendEmptyMessage(0);
	// } else {
	// page = 0;
	// favPos = 0;
	// favoriteCastings = new ArrayList<CastingDetailsModel>(hashMap.get(page));
	// head = 0;
	// fPosition = 0;
	// tail = favoriteCastings.size();
	//
	// // if ((page == 0 && position == 0) || (page == totalPages
	// // && position == myList.size() - 1)) {
	// if ((fPosition == favoriteCastings.size() - 1 && page == lastPage) ||
	// (page == 0 && fPosition == 0)) {
	// showText = true;
	// from_swipe = "right";
	// textCastivate.setVisibility(View.VISIBLE);
	// rel_cast_view_new.setVisibility(View.GONE);
	// rel_image_screen.setVisibility(View.GONE);
	// rel_upload_screen.setVisibility(View.GONE);
	// // setAllData(favoriteCastings, fPosition);
	// } else {
	// setAllData(favoriteCastings, fPosition);
	// }
	//
	// }
	//
	// }
	//
	// // }
	// }
	//
	// //
	// // fPosition++;
	// //
	// // if (fPosition >= head && fPosition < tail) {
	// //
	// // setAllData(favoriteCastings, fPosition);
	// // } else {
	// //
	// // page = page + 1;
	// //
	// // if (hashMap.containsKey(page)) {
	// //
	// // favoriteCastings = new
	// // ArrayList<CastingDetailsModel>(hashMap.get(page));
	// // myB = false;
	// //
	// // head = 0;
	// //
	// // fPosition = 0;
	// //
	// // tail = favoriteCastings.size();
	// //
	// // if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
	// // == favoriteCastings.size() - 1)) {
	// //
	// // testB = true;
	// // bSwipe = "right";
	// // setAllData(favoriteCastings, fPosition);
	// // } else {
	// // setAllData(favoriteCastings, fPosition);
	// // }
	// //
	// // } else {
	// //
	// // fPosition = -1;
	// //
	// // if (page < totalPages) {
	// //
	// // // Here
	// // // gdfgfg
	// // //page = 0;
	// //
	// // if (hashMap.containsKey(page)) {
	// //
	// // favoriteCastings = new
	// // ArrayList<CastingDetailsModel>(hashMap.get(page));
	// // myB = false;
	// // fPosition = 0;
	// // head = 0;
	// // tail = favoriteCastings.size();
	// //
	// // if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
	// // == favoriteCastings.size() - 1)) {
	// //
	// // testB = true;
	// // bSwipe = "right";
	// // setAllData(favoriteCastings, fPosition);
	// // } else {
	// // setAllData(favoriteCastings, fPosition);
	// // }
	// //
	// // } else {
	// // fPosition = 0;
	// //
	// // handlerFavCastingSync.sendEmptyMessage(0);
	// // }
	// // } else {
	// // // Sugumaran
	// // // fPosition = 0;
	// //
	// // // handlerFavCastingSync.sendEmptyMessage(0);
	// //
	// // // Here
	// // page = 0;
	// //
	// // if (hashMap.containsKey(page)) {
	// //
	// // favoriteCastings = new
	// // ArrayList<CastingDetailsModel>(hashMap.get(page));
	// // myB = false;
	// // fPosition = 0;
	// // head = 0;
	// // tail = favoriteCastings.size();
	// //
	// // if ((page == 0 && fPosition == 0) || (page == totalPages && fPosition
	// // == favoriteCastings.size() - 1)) {
	// //
	// // testB = true;
	// // bSwipe = "right";
	// // setAllData(favoriteCastings, fPosition);
	// // } else {
	// // setAllData(favoriteCastings, fPosition);
	// // }
	// //
	// // } else {
	// // fPosition = 0;
	// //
	// // handlerFavCastingSync.sendEmptyMessage(0);
	// // }
	// //
	// // }
	// //
	// // }
	// // }
	//
	// }

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
	}

	public void setAllDataNew(ArrayList<TotalFavModel> list, int currentPos) {

		CastingDetailsModel model = list.get(currentPos).castingModel;
		fav_iconNoImage.setChecked(true);

		try {
			textCastingTitleNoImage.setText(Html.fromHtml(model.castingTitle.toString().trim()));
			textCastingTypeNoImage.setText(Html.fromHtml(model.castingType.toString().trim()));
			textPaidStatusNoImage.setText(model.castingPaidStatus.toString().trim());
			strState = model.state.toString().trim();
			strCity = model.country.toString().trim();
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
			String html = cityAndStateName + " - " + model.castingSubmissionDetail.toString().trim();
			Spannable WordtoSpan = new SpannableString(html);
			WordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, cityAndStateName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			WordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.NORMAL), cityAndStateName.length(), html.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textSubmissionDetailNoImage.setText(WordtoSpan);
			Linkify.addLinks(textSubmissionDetailNoImage, Linkify.ALL);
			((CastingsLinkMovementMethod) CastingsLinkMovementMethod.getInstance()).setContext(context, false);
			textSubmissionDetailNoImage.setMovementMethod(CastingsLinkMovementMethod.getInstance());

			textAgeRangeNoImage.setText("Age " + model.ageRange.toString().trim());
			textSynopsisNoImage.setText(Html.fromHtml(model.castingSynopsis.toString().trim()));
			textRoleForEthnicityNoImage.setText(Html.fromHtml(model.roleForEthnicity.toString().trim()));
			textUnionStatusNoImage.setText(model.castingUnionStatus.toString().trim());
			textRoleForGenderNoImage.setText(model.roleForGender.toString().trim());
			txtRoleDescriptionNoImage.setText(model.roleDescription.toString().trim());

		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	ArrayList<TotalFavModel> list = null;
	int myPage = 0;
	int Totals = 0;
	int totalCasts = 0;
	String getTitle = "";

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
					ArrayList<CastingDetailsModel> favoriteCastings = new ArrayList<CastingDetailsModel>();

					try {
						JSONArray jArray = new JSONArray(getAll);

						CastingDetailsModel detailsModel;

						for (int i = 0; i < jArray.length(); i++) {
							JSONObject json_data = jArray.getJSONObject(i);

							detailsModel = new CastingDetailsModel(json_data.getString("role_id"), json_data.getString("casting_title"), json_data.getString("casting_type"),
									json_data.getString("casting_paid_status"), json_data.getString("casting_union_status"), json_data.getString("casting_union_type"),
									json_data.getString("casting_submission_detail"), json_data.getString("casting_synopsis"), "", json_data.getString("role_desc"),
									json_data.getString("ageRange"), json_data.getString("role_for"), json_data.getString("role_ethnicity"), json_data.getString("fav_flag"), "",
									"", "", json_data.getString("casting_state"), json_data.getString("casting_city"),json_data.getString("casting_email"),json_data.getString("apply_flag"));
							favoriteCastings.add(detailsModel);
						}

						int getSize = favoriteCastings.size();
						DebugReportOnLocat.ln("getSize " + getSize);

						// /////////////////////////////////////////////////

						if (isFirstCall == true) {

							isFirstCall = false;

							// Check the Total Counts from CastingSCreen
							if (CastingScreen.favCount != null) {
								if (!CastingScreen.favCount.equals("0")) {

									Totals = Integer.parseInt(CastingScreen.favCount);
									totalCasts = Totals - mainImagesList.size();

									DebugReportOnLocat.ln("Total Cast :: " + totalCasts);
									DebugReportOnLocat.ln("Total Image :: " + mainImagesList.size());

									if (totalCasts > 10) {

										int getCoz = totalCasts % 10;

										if (getCoz != 0) {
											myPage = (totalCasts / 10) + 1;
										} else {
											myPage = (totalCasts / 10);
										}

									} else {
										myPage = 1;
									}
								} else {
									callCastingSCreen();
								}
							}

							// getImgTotal is empty
							if (mainImagesList.size() == 0) {
								if (totalCasts == 0) {

									callCastingSCreen();

								} else {

									list = new ArrayList<TotalFavModel>();
									int position = 0;
									for (int i = 0; i < favoriteCastings.size(); i++) {

										if (i < favoriteCastings.size())
											list.add(new TotalFavModel(favoriteCastings.get(i), null, false, true));
									}

									int total = list.size();
									DebugReportOnLocat.ln("total list size :: " + total);
									fPosition = 0;
									setAllDataNew(list, fPosition);
								}

								// CastingTotal is empty
							} else if (totalCasts == 0) {
								if (mainImagesList.size() == 0) {
									callCastingSCreen();
								} else {
									list = new ArrayList<TotalFavModel>();
									for (int i = 0; i < mainImagesList.size(); i++) {
										if (i < mainImagesList.size())
											list.add(new TotalFavModel(null, mainImagesList.get(i), true, false));
									}

									mainImagesList.clear();
									int total = list.size();
									DebugReportOnLocat.ln("total list size :: " + total);

									fPosition = 0;
									rel_image_screen.setVisibility(View.VISIBLE);
									rel_upload_screen.setVisibility(View.VISIBLE);
									rel_cast_view_new.setVisibility(View.GONE);
									textCastivate.setVisibility(View.GONE);
									showImg();
								}
							}// getCastingTotal is big
							else if (mainImagesList.size() != 0 && totalCasts != 0 && mainImagesList.size() < totalCasts) {

								int getPos = totalCasts / mainImagesList.size();
								int incPos = getPos;
								DebugReportOnLocat.ln("getPos :: " + getPos);
								int position = 0;

								list = new ArrayList<TotalFavModel>();
								boolean chk = false;

								if (getPos == 1) {
									int total = (favoriteCastings.size() + mainImagesList.size());
									for (int i = 0; i < total; i++) {

										if (chk == false) {
											chk = true;
											if (position < favoriteCastings.size()) {
												list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
												favoriteCastings.remove(position);
											}
										} else if (chk == true) {
											chk = false;
											if (position < mainImagesList.size()) {
												list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
												mainImagesList.remove(position);
											} else if (position < favoriteCastings.size()) {
												list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
												favoriteCastings.remove(position);
											}
										}
									}

								} else {
									int total = (favoriteCastings.size() + mainImagesList.size());
									for (int i = 0; i < total; i++) {
										if (i == incPos && position < mainImagesList.size()) {
											incPos = incPos + getPos;
											if (position < mainImagesList.size()) {
												list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
												mainImagesList.remove(position);
											}
										} else {
											if (position < favoriteCastings.size()) {
												list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
												favoriteCastings.remove(position);
											}

										}
									}

								}

								mapNew.put(page, list);
								int total = list.size();
								DebugReportOnLocat.ln("total lists size :: " + total);
								fPosition = 0;
								setAllDataNew(list, fPosition);

								// getImgTotal is big
							} else if (mainImagesList.size() != 0 && totalCasts != 0 && mainImagesList.size() > totalCasts) {

								int getPos = mainImagesList.size() / favoriteCastings.size();
								// Check Here
								if (getPos == 1) {
									int incPos = getPos;
									int position = 0;
									list = new ArrayList<TotalFavModel>();
									// if(totalCasts<=10){
									int totals = favoriteCastings.size() + mainImagesList.size();
									boolean chk = false;
									for (int i = 0; i < totals; i++) {

										if (chk == false) {
											chk = true;
											if (position < favoriteCastings.size()) {
												list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
												favoriteCastings.remove(position);
											} else if (position < mainImagesList.size() && totalCasts < 10) {
												list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
												mainImagesList.remove(position);
											}
										} else if (chk == true) {
											chk = false;
											if (position < mainImagesList.size()) {
												list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
												mainImagesList.remove(position);
											}
										}
									}

								} else {
									int incPos = getPos;
									list = new ArrayList<TotalFavModel>();
									int position = 0;
									list = new ArrayList<TotalFavModel>();
									int total = (favoriteCastings.size() + mainImagesList.size());
									for (int i = 0; i < total; i++) {

										if (i == incPos && position < mainImagesList.size()) {
											incPos = incPos + getPos;
											if (position < mainImagesList.size()) {
												list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
												mainImagesList.remove(position);
											}
											// else if (position <
											// mainImagesList.size()&&totalCasts
											// < 10) {
											// list.add(new TotalFavModel(null,
											// mainImagesList.get(position),
											// true, false));
											// mainImagesList.remove(position);
											// }
										} else {
											if (position < favoriteCastings.size()) {
												list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
												favoriteCastings.remove(position);
											} else if (position < mainImagesList.size() && totalCasts < 10) {
												list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
												mainImagesList.remove(position);
											}
										}
									}
								}

								mapNew.put(page, list);
								fPosition = 0;
								setAllDataNew(list, fPosition);

							} else if (totalCasts == mainImagesList.size()) {

								int getPos = 1;
								int incPos = getPos;
								list = new ArrayList<TotalFavModel>();
								int position = 0;
								int totals = (favoriteCastings.size() + mainImagesList.size());

								boolean chk = false;
								for (int i = 0; i < totals; i++) {

									if (chk == false) {
										chk = true;
										if (position < favoriteCastings.size()) {
											list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
											favoriteCastings.remove(position);
										}
									} else if (chk == true) {
										chk = false;
										if (position < mainImagesList.size()) {
											list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
											mainImagesList.remove(position);
										}
									}
								}
								mapNew.put(page, list);
								fPosition = 0;
								setAllDataNew(list, fPosition);

							} else {
								Toast.makeText(context, "Check the Condition", 0).show();
							}

							// Next pages
						} else {

							// totalCasts ,mainImagesList.size();

							
							
								isCallSer = false;
								getTitle = favoriteCastings.get(0).castingTitle;
								
								
						
							
							int getOldPos = list.size();

							if (favoriteCastings != null && favoriteCastings.size() > 0) {
								if (mainImagesList != null && mainImagesList.size() > 0) {

									// Casting list big
									if (mainImagesList.size() < favoriteCastings.size()) {
										int getPos = 1;
										int incPos = getPos;
										int position = 0;
										int totals = (favoriteCastings.size() + mainImagesList.size());

										boolean chk = false;
										for (int i = 0; i < totals; i++) {

											if (chk == false) {
												chk = true;
												if (position < favoriteCastings.size()) {
													list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
													favoriteCastings.remove(position);
												}
											} else if (chk == true) {
												chk = false;
												if (position < mainImagesList.size()) {
													list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
													mainImagesList.remove(position);
												} else if (position < favoriteCastings.size()) {
													list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
													favoriteCastings.remove(position);
												}
											}
										}
									}

									// Image list big
									if (mainImagesList.size() > favoriteCastings.size()) {
										int getPos = 1;
										int incPos = getPos;
										int position = 0;
										int totals = (favoriteCastings.size() + mainImagesList.size());

										boolean chk = false;
										for (int i = 0; i < totals; i++) {

											if (chk == false) {
												chk = true;
												if (position < favoriteCastings.size()) {
													list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
													favoriteCastings.remove(position);
												} else if (position < mainImagesList.size()) {
													list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
													mainImagesList.remove(position);
												}
											} else if (chk == true) {
												chk = false;
												if (position < mainImagesList.size()) {
													list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
													mainImagesList.remove(position);
												}
											}
										}
									}
									

									//both are equal
									if (mainImagesList.size() == favoriteCastings.size()) {
										int getPos = 1;
										int incPos = getPos;
										int position = 0;
										int totals = (favoriteCastings.size() + mainImagesList.size());

										boolean chk = false;
										for (int i = 0; i < totals; i++) {

											if (chk == false) {
												chk = true;
												if (position < favoriteCastings.size()) {
													list.add(new TotalFavModel(favoriteCastings.get(position), null, false, true));
													favoriteCastings.remove(position);
												} else if (position < mainImagesList.size()) {
													list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
													mainImagesList.remove(position);
												}
											} else if (chk == true) {
												chk = false;
												if (position < mainImagesList.size()) {
													list.add(new TotalFavModel(null, mainImagesList.get(position), true, false));
													mainImagesList.remove(position);
												}
											}
										}
									}
									
									

								} else {

									int position = 0;
									for (int i = 0; i < favoriteCastings.size(); i++) {

										if (position < favoriteCastings.size()) {
											list.add(new TotalFavModel(favoriteCastings.get(i), null, false, true));
											// favoriteCastings.remove(position);
										}
									}

								}
							} else {

							}
							
							
							

							// If used for Left Swipe show last
							if (isCheck == true) {
								boolean showCast = false;
								if (list.get((list.size() - 1)).castingShow == true) {
									showCast = true;
								}
								if (list.get((list.size() - 1)).imageShow == true) {
									showCast = false;
								}
								fPosition = (list.size() - 1);
								DebugReportOnLocat.ln("Old fPosition " + fPosition);

								if (showCast == true) {
									setAllDataNew(list, fPosition);
									rel_image_screen.setVisibility(View.GONE);
									rel_upload_screen.setVisibility(View.GONE);
									rel_cast_view_new.setVisibility(View.VISIBLE);
									textCastivate.setVisibility(View.GONE);
									
									txtName.setVisibility(View.GONE);
									
								} else if (showCast == false) {

									rel_image_screen.setVisibility(View.VISIBLE);
									rel_upload_screen.setVisibility(View.VISIBLE);
									rel_cast_view_new.setVisibility(View.GONE);
									textCastivate.setVisibility(View.GONE);

									
									if(list.get(fPosition).imagesModel.userName!=null&&!list.get(fPosition).imagesModel.userName.equals("")){
										txtName.setVisibility(View.VISIBLE);
										txtName.setText(list.get(fPosition).imagesModel.userName);
									}
									
									progressView.setVisibility(View.VISIBLE);
									Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade()
											.into(casting_image, new Callback() {
												@Override
												public void onSuccess() {
													progressView.setVisibility(View.GONE);
												}

												@Override
												public void onError() {

												}
											});
								}

								// If used for right Swipe show next
							} else {
								
								if(list!=null)
								{
									
								
								boolean showCast = false;
								if (list.get(getOldPos).castingShow == true) {
									showCast = true;
								}
								if (list.get(getOldPos).imageShow == true) {
									showCast = false;
								}
								fPosition = getOldPos;
								DebugReportOnLocat.ln("Old fPosition " + fPosition);

								if (showCast == true) {
									setAllDataNew(list, fPosition);
									rel_image_screen.setVisibility(View.GONE);
									rel_upload_screen.setVisibility(View.GONE);
									rel_cast_view_new.setVisibility(View.VISIBLE);
									textCastivate.setVisibility(View.GONE);
								} else if (showCast == false) {

									rel_image_screen.setVisibility(View.VISIBLE);
									rel_upload_screen.setVisibility(View.VISIBLE);
									rel_cast_view_new.setVisibility(View.GONE);
									textCastivate.setVisibility(View.GONE);
									
									

									progressView.setVisibility(View.VISIBLE);
									Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade()
											.into(casting_image, new Callback() {
												@Override
												public void onSuccess() {
													progressView.setVisibility(View.GONE);
												}

												@Override
												public void onError() {

												}
											});
								}
							}
							}

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {

					if (mainImagesList.size() != 0) {
						
						if(list==null){
							list = new ArrayList<TotalFavModel>();
						}

						for (int i = 0; i < mainImagesList.size(); i++) {
							if (i < mainImagesList.size())
								list.add(new TotalFavModel(null, mainImagesList.get(i), true, false));
						}

						if (mainImagesList != null && mainImagesList.size() > 0) {
							fPosition = 0;
							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);
							showImg();
						}
					} else {
						rel_image_screen.setVisibility(View.GONE);
						rel_upload_screen.setVisibility(View.GONE);
						rel_cast_view_new.setVisibility(View.GONE);
						textCastivate.setVisibility(View.VISIBLE);
					}

				}
			}

		}

	}

	void CastingRightSwipeViewNew() {

		fPosition++;

		if (fPosition < list.size()) {

			if (list.get(fPosition).castingShow == true) {

				rel_image_screen.setVisibility(View.GONE);
				rel_upload_screen.setVisibility(View.GONE);
				rel_cast_view_new.setVisibility(View.VISIBLE);
				textCastivate.setVisibility(View.GONE);
				
				txtName.setVisibility(View.GONE);

				setAllDataNew(list, fPosition);

			} else if (list.get(fPosition).imageShow == true) {

				rel_image_screen.setVisibility(View.VISIBLE);
				rel_upload_screen.setVisibility(View.VISIBLE);
				rel_cast_view_new.setVisibility(View.GONE);
				textCastivate.setVisibility(View.GONE);
				imgFav.setChecked(true);
				progressView.setVisibility(View.VISIBLE);
				
			
				
				
				if(list.get(fPosition).imagesModel.userName!=null&&!list.get(fPosition).imagesModel.userName.equals("")){
					txtName.setVisibility(View.VISIBLE);
					txtName.setText(list.get(fPosition).imagesModel.userName);
				}
				
				
				Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
					@Override
					public void onSuccess() {
						progressView.setVisibility(View.GONE);
					}

					@Override
					public void onError() {

					}
				});

			}
		} else {
			page = page + 1;
			if (page < myPage&& !downloadedPages.contains(page)) {
				handlerFavCastingSync.sendEmptyMessage(0);
			} else {
				rel_image_screen.setVisibility(View.GONE);
				rel_upload_screen.setVisibility(View.GONE);
				rel_cast_view_new.setVisibility(View.GONE);
				textCastivate.setVisibility(View.VISIBLE);
				
				
				new Handler().postDelayed(new Runnable() {
					public void run() {
						fPosition = list.size()-2;
						CastingRightSwipeViewNew();
					}
				}, 500);
				

			}
		}

		// //Show text Castivate
		// textCastivate.setVisibility(View.VISIBLE);
		// rel_cast_view_new.setVisibility(View.GONE);
		// rel_image_screen.setVisibility(View.GONE);
		// rel_upload_screen.setVisibility(View.GONE);
		//
		// ///// to hide textcastivate
		// rel_image_screen.setVisibility(View.GONE);
		// rel_upload_screen.setVisibility(View.GONE);
		// rel_cast_view_new.setVisibility(View.VISIBLE);
		// textCastivate.setVisibility(View.GONE);
		//
		// ////////to Show images
		// rel_image_screen.setVisibility(View.VISIBLE);
		// rel_upload_screen.setVisibility(View.VISIBLE);
		// rel_cast_view_new.setVisibility(View.GONE);
		// textCastivate.setVisibility(View.GONE);

	}

	boolean isCheck = false;
	boolean isCallSer = false;

	void CastingLeftSwipeViewNew() {
		fPosition--;
		if (fPosition < list.size() && fPosition != -1 ) {
			
			if (list.get(fPosition).castingShow == true) {
				rel_image_screen.setVisibility(View.GONE);
				rel_upload_screen.setVisibility(View.GONE);
				rel_cast_view_new.setVisibility(View.VISIBLE);
				textCastivate.setVisibility(View.GONE);
				
				txtName.setVisibility(View.GONE);

				setAllDataNew(list, fPosition);

			} else if (list.get(fPosition).imageShow == true) {

				rel_image_screen.setVisibility(View.VISIBLE);
				rel_upload_screen.setVisibility(View.VISIBLE);
				rel_cast_view_new.setVisibility(View.GONE);
				textCastivate.setVisibility(View.GONE);

				imgFav.setChecked(true);
				
				if(list.get(fPosition).imagesModel.userName!=null&&!list.get(fPosition).imagesModel.userName.equals("")){
					txtName.setVisibility(View.VISIBLE);
					txtName.setText(list.get(fPosition).imagesModel.userName);
				}
				
				progressView.setVisibility(View.VISIBLE);
				Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
					@Override
					public void onSuccess() {
						progressView.setVisibility(View.GONE);
					}

					@Override
					public void onError() {

					}
				});
			}

//			if((list.get(fPosition).castingShow == true)&&getTitle.equals(list.get(fPosition).castingModel.castingTitle)){
//			
//				isCallSer = true;
//				System.out.println("getTitle Yes");
//				if (list.get(fPosition).castingShow == true) {
//					rel_image_screen.setVisibility(View.GONE);
//					rel_upload_screen.setVisibility(View.GONE);
//					rel_cast_view_new.setVisibility(View.VISIBLE);
//					textCastivate.setVisibility(View.GONE);
//
//					setAllDataNew(list, fPosition);
//
//				} else if (list.get(fPosition).imageShow == true) {
//
//					rel_image_screen.setVisibility(View.VISIBLE);
//					rel_upload_screen.setVisibility(View.VISIBLE);
//					rel_cast_view_new.setVisibility(View.GONE);
//					textCastivate.setVisibility(View.GONE);
//
//					imgFav.setChecked(true);
//					progressView.setVisibility(View.VISIBLE);
//					Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
//						@Override
//						public void onSuccess() {
//							progressView.setVisibility(View.GONE);
//						}
//
//						@Override
//						public void onError() {
//
//						}
//					});
//				}
//			}else{
//				System.out.println("getTitle NO");
//				
//				if (list.get(fPosition).castingShow == true) {
//					rel_image_screen.setVisibility(View.GONE);
//					rel_upload_screen.setVisibility(View.GONE);
//					rel_cast_view_new.setVisibility(View.VISIBLE);
//					textCastivate.setVisibility(View.GONE);
//
//					setAllDataNew(list, fPosition);
//
//				} else if (list.get(fPosition).imageShow == true) {
//
//					rel_image_screen.setVisibility(View.VISIBLE);
//					rel_upload_screen.setVisibility(View.VISIBLE);
//					rel_cast_view_new.setVisibility(View.GONE);
//					textCastivate.setVisibility(View.GONE);
//
//					imgFav.setChecked(true);
//					progressView.setVisibility(View.VISIBLE);
//					Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
//						@Override
//						public void onSuccess() {
//							progressView.setVisibility(View.GONE);
//						}
//
//						@Override
//						public void onError() {
//
//						}
//					});
//				}
//				
//				
//			}
			
			
			
			
			
			
			
			
			
		} else if (fPosition == -1) {
			
			
			rel_image_screen.setVisibility(View.GONE);
			rel_upload_screen.setVisibility(View.GONE);
			rel_cast_view_new.setVisibility(View.GONE);
			textCastivate.setVisibility(View.VISIBLE);
			
			new Handler().postDelayed(new Runnable() {
				public void run() {
					fPosition = 1;
					CastingLeftSwipeViewNew();
				}
			}, 500);
			
			
			
//			isCheck = true;
//			rel_image_screen.setVisibility(View.GONE);
//			rel_upload_screen.setVisibility(View.GONE);
//			rel_cast_view_new.setVisibility(View.GONE);
//			textCastivate.setVisibility(View.VISIBLE);
//
//		} else if (page < myPage&& !downloadedPages.contains(page)) {
//			page = myPage - 1;
//			handlerFavCastingSync.sendEmptyMessage(0);
//		} else {
//			rel_image_screen.setVisibility(View.GONE);
//			rel_upload_screen.setVisibility(View.GONE);
//			rel_cast_view_new.setVisibility(View.GONE);
//			textCastivate.setVisibility(View.VISIBLE);
		}
	}

	boolean showsImage = false;
	int getImgPos = 0;
	int incImgPos = 0;

	String castSwipe = "";
	String imgSwipe = "";

	ArrayList<Integer> downloadedPages = new ArrayList<Integer>();

	class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				
				if(grid_view_photos.getVisibility()==View.VISIBLE){
					grid_view_photos.setVisibility(View.GONE);
				}

				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_right);

					rel_cast_view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));
					
					if (textCastivate.getVisibility() == View.VISIBLE) {
						

						rel_image_screen.setVisibility(View.GONE);
						rel_upload_screen.setVisibility(View.GONE);
						rel_cast_view_new.setVisibility(View.VISIBLE);
						textCastivate.setVisibility(View.GONE);

						fPosition = 0;
						if (list.get(fPosition).castingShow == true) {
							txtName.setVisibility(View.GONE);
							setAllDataNew(list, fPosition);
						} else if (list.get(fPosition).imageShow == true) {
							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);
							
							if(list.get(fPosition).imagesModel.userName!=null&&!list.get(fPosition).imagesModel.userName.equals("")){
								txtName.setVisibility(View.VISIBLE);
								txtName.setText(list.get(fPosition).imagesModel.userName);
							}

							imgFav.setChecked(true);
							progressView.setVisibility(View.VISIBLE);
							Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
								@Override
								public void onSuccess() {
									progressView.setVisibility(View.GONE);
								}

								@Override
								public void onError() {

								}
							});
						}

					} else {
						CastingRightSwipeViewNew();
					}

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					rel_cast_view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));

					if (textCastivate.getVisibility() == View.VISIBLE) {

//						rel_image_screen.setVisibility(View.GONE);
//						rel_upload_screen.setVisibility(View.GONE);
//						rel_cast_view_new.setVisibility(View.VISIBLE);
//						textCastivate.setVisibility(View.GONE);
//						
//						
//						fPosition = list.size() - 1;
//						if (list.get(fPosition).castingShow == true) {
//							setAllDataNew(list, fPosition);
//						} else if (list.get(fPosition).imageShow == true) {
//							rel_image_screen.setVisibility(View.VISIBLE);
//							rel_upload_screen.setVisibility(View.VISIBLE);
//							rel_cast_view_new.setVisibility(View.GONE);
//							textCastivate.setVisibility(View.GONE);
//							imgFav.setChecked(true);
//							progressView.setVisibility(View.VISIBLE);
//							Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade()
//									.into(casting_image, new Callback() {
//										@Override
//										public void onSuccess() {
//											progressView.setVisibility(View.GONE);
//										}
//
//										@Override
//										public void onError() {
//
//										}
//									});
//						}

//						if (isCheck == true ) {
//
//							page = myPage - 1;
//							if (page < myPage && !downloadedPages.contains(page)) {
//
//								handlerFavCastingSync.sendEmptyMessage(0);
//
//							} else {
//
//								fPosition = list.size() - 1;
//								if (list.get(fPosition).castingShow == true) {
//									setAllDataNew(list, fPosition);
//								} else if (list.get(fPosition).imageShow == true) {
//									rel_image_screen.setVisibility(View.VISIBLE);
//									rel_upload_screen.setVisibility(View.VISIBLE);
//									rel_cast_view_new.setVisibility(View.GONE);
//									textCastivate.setVisibility(View.GONE);
//									imgFav.setChecked(true);
//									progressView.setVisibility(View.VISIBLE);
//									Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade()
//											.into(casting_image, new Callback() {
//												@Override
//												public void onSuccess() {
//													progressView.setVisibility(View.GONE);
//												}
//
//												@Override
//												public void onError() {
//
//												}
//											});
//								}
//							}
//
//						} else {
//
//							fPosition = list.size() - 1;
//							if (list.get(fPosition).castingShow == true) {
//								setAllDataNew(list, fPosition);
//							} else if (list.get(fPosition).imageShow == true) {
//								rel_image_screen.setVisibility(View.VISIBLE);
//								rel_upload_screen.setVisibility(View.VISIBLE);
//								rel_cast_view_new.setVisibility(View.GONE);
//								textCastivate.setVisibility(View.GONE);
//								imgFav.setChecked(true);
//								progressView.setVisibility(View.VISIBLE);
//								Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade()
//										.into(casting_image, new Callback() {
//											@Override
//											public void onSuccess() {
//												progressView.setVisibility(View.GONE);
//											}
//
//											@Override
//											public void onError() {
//
//											}
//										});
//							}
//						}

						
						
					} 
					else {
						CastingLeftSwipeViewNew();
//						if(isCallSer == true){
//							isCallSer = false;
//							page = page-1;
//							if(page<myPage){
//								if(!downloadedPages.contains(page))
//								handlerFavCastingSync.sendEmptyMessage(0);
//							}
//							
//						}else{
//						CastingLeftSwipeViewNew();
//						}
					}

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
		callCastingSCreen();
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
			if (Network.isNetworkConnected(FavoriteScreen.this)) {
				new GetImages().execute();
			} else {
				Toast.makeText(FavoriteScreen.this, "Internet connection is not available", Toast.LENGTH_LONG).show();
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

			isFirstCall = true;

			tempImagesList = new ArrayList<FavImagesModel>();
			mainImagesList = new ArrayList<FavImagesModel>();

			JSONArray jArray;
			try {
				jArray = new JSONArray(getImageLinks);
				FavImagesModel imagesModel;
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					imagesModel = new FavImagesModel(json_data.getString("image_id"), json_data.getString("casting_image"), json_data.getString("casting_image_thumb"), json_data.getString("user_name"));
					tempImagesList.add(imagesModel);
					mainImagesList.add(imagesModel);
				}

				handlerFavCastingSync.sendEmptyMessage(0);

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

		grid_view_photos.setVisibility(View.GONE);
		rel_image_screen.setVisibility(View.VISIBLE);
		rel_upload_screen.setVisibility(View.VISIBLE);
		rel_cast_view_new.setVisibility(View.GONE);
		textCastivate.setVisibility(View.GONE);
		
		if(tempImagesList.get(pos).userName!=null&&!tempImagesList.get(pos).userName.equals("")){
			txtName.setVisibility(View.VISIBLE);
			txtName.setText(tempImagesList.get(pos).userName);
		}

		progressView.setVisibility(View.VISIBLE);
		Picasso.with(context).load((tempImagesList.get(pos).casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
			@Override
			public void onSuccess() {
				progressView.setVisibility(View.GONE);
			}

			@Override
			public void onError() {

			}
		});

	}

	// Sugumaran Code
	private Handler handlerImgFavoriteSync = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Network.isNetworkConnected(FavoriteScreen.this)) {
				// postFavoriteCastingSync = new PostFavoriteCastingSync();
				// postFavoriteCastingSync.execute();

				postFavoriteImageSync = new PostFavoriteImageSync();
				postFavoriteImageSync.execute();

			} else {
				Toast.makeText(FavoriteScreen.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();

			}
		}
	};

	private PostFavoriteImageSync postFavoriteImageSync;

	@SuppressLint("InlinedApi")
	private class PostFavoriteImageSync extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog = null;

		@Override
		public void onPreExecute() {
			pDialog = new ProgressDialog(FavoriteScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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

			int get = fPosition;
			
			for(int i=0;i<tempImagesList.size();i++){
				if(tempImagesList.get(i).image_id.equals(list.get(fPosition).imagesModel.image_id)){
					tempImagesList.remove(i);
				}
			}
			list.remove(fPosition);

			if (list.size() == fPosition) {
				if (list.size() != 0) {
					rel_image_screen.setVisibility(View.GONE);
					rel_upload_screen.setVisibility(View.GONE);
					rel_cast_view_new.setVisibility(View.GONE);
					textCastivate.setVisibility(View.VISIBLE);
				} else {
					callCastingSCreen();
				}
			} else if (list.size() == 0) {
				callCastingSCreen();
			} else if (list.size() < 0) {
				rel_image_screen.setVisibility(View.GONE);
				rel_upload_screen.setVisibility(View.GONE);
				rel_cast_view_new.setVisibility(View.GONE);
				textCastivate.setVisibility(View.VISIBLE);
			} else {
				for (int i = get; i < list.size(); i++) {

					if (i < list.size()) {

						if (list.get(i).imageShow == true) {
							fPosition = i;

							rel_image_screen.setVisibility(View.VISIBLE);
							rel_upload_screen.setVisibility(View.VISIBLE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.GONE);

							imgFav.setChecked(true);
							
							if(list.get(fPosition).imagesModel.userName!=null&&!list.get(fPosition).imagesModel.userName.equals("")){
								txtName.setVisibility(View.VISIBLE);
								txtName.setText(list.get(fPosition).imagesModel.userName);
							}
							
							progressView.setVisibility(View.VISIBLE);
							Picasso.with(context).load((list.get(i).imagesModel.casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
								@Override
								public void onSuccess() {
									progressView.setVisibility(View.GONE);
								}

								@Override
								public void onError() {
								}
							});
							break;

						} else if (list.get(i).castingShow == true) {
							rel_image_screen.setVisibility(View.GONE);
							rel_upload_screen.setVisibility(View.GONE);
							rel_cast_view_new.setVisibility(View.VISIBLE);
							textCastivate.setVisibility(View.GONE);

							fPosition = i;
							setAllDataNew(list, fPosition);
							break;
						} else {
							rel_image_screen.setVisibility(View.GONE);
							rel_upload_screen.setVisibility(View.GONE);
							rel_cast_view_new.setVisibility(View.GONE);
							textCastivate.setVisibility(View.VISIBLE);
						}
					} else {
						System.out.println("Again check");
					}

				}
			}

			// handlerCastingImagesSync.sendEmptyMessage(0);

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

			item = new JSONStringer().object().key("userId").value(Library.androidUserID).key("imageId").value(list.get(fPosition).imagesModel.image_id).key("imageFav")
					.value(favoriteValue).endObject();

			// try {

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

			// ////////////

			// int get = favPos - 1;
			// if (get == -1) {
			// favPos = favPos + 1;
			// }
			//
			// if (favNewPos != 0) {
			// DebugReportOnLocat.ln("favnewPos :" + favNewPos);
			// item = new
			// JSONStringer().object().key("userId").value(Library.androidUserID).key("imageId").value(favImagesList.get((favNewPos)
			// - 1).image_id).key("imageFav")
			// .value(favoriteValue).endObject();
			// } else {
			//
			// item = new
			// JSONStringer().object().key("userId").value(Library.androidUserID).key("imageId").value(favImagesList.get((favPos)
			// - 1).image_id).key("imageFav")
			// .value(favoriteValue).endObject();
			//
			// }
			// System.out.println("item.toString() :: " + item.toString());
			// Log.d("Data", item.toString());

			// //////////

			// }

			// } catch (IndexOutOfBoundsException e) {
			// e.printStackTrace();
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }

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

	// public void showImage() {
	//
	// imgFav.setChecked(true);
	// if (favImagesList != null && favImagesList.size() != 0) {
	//
	// if (rel_image_screen.getVisibility() == View.VISIBLE) {
	// DebugReportOnLocat.ln("out Visible");
	// } else {
	// DebugReportOnLocat.ln("Out Gone");
	// }
	// if (Network.isNetworkConnected(context) && favImagesList != null &&
	// favImagesList.size() != 0) {
	//
	//
	//
	// if (favPos < favImagesList.size()) {
	//
	// DebugReportOnLocat.ln(" Image Url in override method :: " +
	// favImagesList.get(favPos).casting_image);
	// DebugReportOnLocat.ln("curr Img Pos :: " + favPos);
	// progressView.setVisibility(View.VISIBLE);
	// Picasso.with(context).load((favImagesList.get(favPos).casting_image).replaceAll("http:",
	// "https:")).noFade().into(casting_image, new Callback() {
	// @Override
	// public void onSuccess() {
	// progressView.setVisibility(View.GONE);
	// favPos++;
	//
	// if (rel_image_screen.getVisibility() == View.VISIBLE) {
	// DebugReportOnLocat.ln("in Visible");
	// } else {
	// DebugReportOnLocat.ln(" in Gone");
	// }
	// }
	//
	// @Override
	// public void onError() {
	//
	// }
	// });
	// } else {
	// textCastivate.setVisibility(View.VISIBLE);
	// rel_cast_view_new.setVisibility(View.GONE);
	// rel_image_screen.setVisibility(View.GONE);
	// rel_upload_screen.setVisibility(View.GONE);
	// favPos = 0;
	// }
	//
	// } else {
	// Library.showToast(context, "Please check your network connection.");
	// }
	// } else {
	// DebugReportOnLocat.ln("No Image List.");
	//
	// // rel_image_screen.setVisibility(View.GONE);
	// // rel_upload_screen.setVisibility(View.GONE);
	// // rel_cast_view_new.setVisibility(View.VISIBLE);
	// // textCastivate.setVisibility(View.VISIBLE);
	// }
	// }

	public void chekVisibility(boolean b) {

	}

	public void showImg() {
		rel_image_screen.setVisibility(View.VISIBLE);
		rel_upload_screen.setVisibility(View.VISIBLE);
		rel_cast_view_new.setVisibility(View.GONE);
		textCastivate.setVisibility(View.GONE);

		progressView.setVisibility(View.VISIBLE);
		Picasso.with(context).load((list.get(fPosition).imagesModel.casting_image).replaceAll("http:", "https:")).noFade().into(casting_image, new Callback() {
			@Override
			public void onSuccess() {
				progressView.setVisibility(View.GONE);
			}

			@Override
			public void onError() {

			}
		});
	}

	public void callCastingSCreen() {
		Intent favIntent = new Intent(context, CastingScreen.class);
		favIntent.putExtra("from", "favorite");
		startActivity(favIntent);
		finish();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
	    super.dispatchTouchEvent(ev);
	    
	    if(grid_view_photos.getVisibility()==View.VISIBLE){
	    	return false;
	    }else{
		    return gestureDetector.onTouchEvent(ev); 
	    }
	}

}