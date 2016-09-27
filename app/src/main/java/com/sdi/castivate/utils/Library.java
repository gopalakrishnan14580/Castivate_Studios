package com.sdi.castivate.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONStringer;

import com.sdi.castivate.CastivateApplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;



/**
 * Created by Dinash on 21-Apr-15.
 */
@SuppressWarnings("deprecation")
public class Library {

	public static int DeviceIDCount = 1;
    public static String ethnicity;
    public static String androidUserID;
    public static String remainingDays;
    public static String loginStatus="0";
    public static boolean helpOverlayView=false;
                                                //8552fc16f1fbec2869abdcfe3359b126
   //public static final String MIXPANEL_TOKEN = "8552fc16f1fbec2869abdcfe3359b126"; //for testing
   public static final String MIXPANEL_TOKEN = "dad6ddc4632acf0de9934e5482267a93"; //for live
     
    public static final String MyPREFERENCES = "MyPrefs";
	public static final String CUSTOMER_ID = "cusidKey";
	public static final String PERFORMANCE_TYPE = "performance";
	public static final String UNION = "union";
	public static final String NON_UNION = "non_union";
	public static final String GENDER = "gender";
	public static final String USER_ID = "user_id";
	public static final String INITIAL_SCREEN = "initial_screen";
	public static final String BIRTH = "birth";
	public static final String CURRENT_LOCATION = "current_location";
	public static final String SELECTED_LOCATION = "selected_location";
	public static final String ETHNICITY = "ethnicity";
	public static final String RATEIT = "rate";
	public static final String RATEIT_FLAG = "rate_flag";
	public static final String HELP_OVERLAY = "help_overlay";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static String pushRoleId="";
	public static String pushGender="";
	public static String pushAgeRange="";
	public static String pushEthnicity="";
	public static String pushPerformanceType="";

    //17/08/2016
    public static final String REMAINING_DAYS = "remainingdays";
    public static final String LOGIN_STATUS = "loginstatus";

	public static final String NOTIFICATION = "notification";
    // =======================================================================================================
    // push notifications
    // =======================================================================================================
    public static void InsertDeviceID(String DeviceID, String User_ID) {
        DebugReportOnLocat.ln("Device ID>>> " + DeviceID);
        // =====================================================================

    }
    // Toast reusable component
    public static void showToast(Context context,String toastMessage){
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }
    // Google project id
  //  public static final String SENDER_ID = "619832204246"; 
    // Google live account project id
    public static final String SENDER_ID = "142057670383"; 
   // AIzaSyBZ-4n5N9sZFm4t6sgDwKoszEKrnVCR_nI   
    
    public static final String MESSAGE_KEY = "message";
    /**
     * Tag used on log messages.
     */
    public static final String TAG = "Castivate GCM";
 
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.sdi.castivate.DISPLAY_MESSAGE";
 
    public static final String EXTRA_MESSAGE = "message";
    public static String imageUpload = "";
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    

  public static String senData(JSONStringer envelope) {

      HttpPost httppost = null;

     httppost = new HttpPost(HttpUri.SUBMIT_IMAGE);

      String responseString = "";
      System.err.println("Envelope : " + envelope);
      final DefaultHttpClient httpClient = new DefaultHttpClient();
      // request parameters
      HttpParams params = httpClient.getParams();
      HttpConnectionParams.setConnectionTimeout(params, 50000);
      HttpConnectionParams.setSoTimeout(params, 30000);
      // set parameter
      HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);

      try {
          // the entity holds the request
          HttpEntity entity = new StringEntity(envelope.toString());
          httppost.setEntity(entity);
          // Response handler
          ResponseHandler<String> rh = new ResponseHandler<String>() {
              // invoked when client receives response
              public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                  // get response entity
                  HttpEntity entity = response.getEntity();
                  // read the response as byte array
                  StringBuffer out = new StringBuffer();
                  byte[] b = EntityUtils.toByteArray(entity);
                  out.append(new String(b, 0, b.length));
                  return out.toString();
              }
          };
          responseString = httpClient.execute(httppost, rh);
          imageUpload=responseString;
      } catch (Exception e) {
          e.printStackTrace();
          Log.d("me", "Exc : " + e.toString());
      }
      // close the connection
      httpClient.getConnectionManager().shutdown();
      return responseString;
  }


	public static boolean emailValidation(String email) {
		Pattern pattern;
		Matcher matcher;
		final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	
    public static String SAVE_FOLDER_NAME = CastivateApplication.getAppContext().getFilesDir().getAbsolutePath() + "/Castivate";
    public static String SAVE_FOLDER_TEMP_NAME =CastivateApplication.getAppContext().getFilesDir().getAbsolutePath() + "/CastivateTemp";

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    
//    public static String URLS[] = new String[]{};

}
