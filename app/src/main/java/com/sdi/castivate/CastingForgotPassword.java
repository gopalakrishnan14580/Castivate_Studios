package com.sdi.castivate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.KeyboardUtility;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by androidusr1 on 11/8/16.
 */
@SuppressWarnings("deprecation")
public class CastingForgotPassword extends Activity implements View.OnClickListener{

    LinearLayout casting_forgot_back_icon;
    RelativeLayout rel_casting_forgot;
    Context context;
    Activity activity;
    Button btn_forgot_submit;
    EditText et_forgot_email;
    String forgot_email;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_forgot_password);

        context=this;
        activity=this;
        findElements();
    }

    private void findElements() {

        casting_forgot_back_icon=(LinearLayout) findViewById(R.id.casting_forgot_back_icon);
        btn_forgot_submit=(Button) findViewById(R.id.btn_forgot_submit);
        et_forgot_email=(EditText) findViewById(R.id.et_forgot_email);
        rel_casting_forgot=(RelativeLayout) findViewById(R.id.rel_casting_forgot);
        rel_casting_forgot.setOnClickListener(this);
        btn_forgot_submit.setOnClickListener(this);
        casting_forgot_back_icon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.casting_forgot_back_icon:
                backForgotPassword();
                break;
            case R.id.rel_casting_forgot:
                KeyboardUtility.hideSoftKeyboard(activity);
                break;
            case R.id.btn_forgot_submit:
                getValues();
                validation();
                break;
        }
    }

    private void backForgotPassword() {

        Intent intent = new Intent(CastingForgotPassword.this, CastingLogin.class);
        startActivity(intent);
        finish();
    }
    private void getValues() {

        forgot_email=et_forgot_email.getText().toString().trim();

    }

    private void validation() {

        if (forgot_email.length() == 0) {
            Library.showToast(context, "Enter your Email ID");
        }
        else if (!Library.isValidEmail(forgot_email)) {

            Library.showToast(context, "Enter your valid Email ID");
        }
        else {

            if(Network.isNetworkConnected(context)) {
                handlerForgotPasswordCastingSync.sendEmptyMessage(0);
            }
            else {
                Library.showToast(context,getResources().getString(R.string.internet_not_available));
            }
        }

    }

    private Handler handlerForgotPasswordCastingSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Network.isNetworkConnected(context)) {
                forgotPasswordCastingASync = new ForgotPasswordCastingASync();
                forgotPasswordCastingASync.execute();
            } else {
                Toast.makeText(context, "Internet connection is not available",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    private ForgotPasswordCastingASync forgotPasswordCastingASync;

    public class ForgotPasswordCastingASync extends AsyncTask<String, Void, Void> {

        @Override
        public void onPreExecute() {
            pDialog = new ProgressDialog(CastingForgotPassword.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            castForgotPassword();
            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (json != null) {

                System.out.println("Result :"+json.toString());

                try {

                    JSONObject oneObject = new JSONObject(json);

                    if(oneObject.getString("status").equals("200")) {

                        //System.out.println("Status ------------------------------> " + oneObject.getString("status"));

                        //System.out.println("Message ------------------------------> " + oneObject.getString("message"));

                        alert(context,oneObject.getString("message"));

                    }
                }
                catch (JSONException e) {
                    System.out.println("Exception : "+e.getMessage());
                }

            }else{

                System.out.println("Casting not getting response. Please try again.");
            }

        }

    }


    InputStream is = null;
    String json = "";
    StringBuilder sb;
    String Status;

    private void castForgotPassword() {

        try {
            HttpPost request = new HttpPost(HttpUri.CASTING_FORGOT_PASSWORD);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type",
                    "application/x-www-form-urlencoded");

            JSONStringer item = null;

              /*  {
        "email": "ramsdevelop@yopmail.com",
        "userid": "8"
}
*/

            try {

                item = new JSONStringer()
                        .object()
                        .key("email")
                        .value(forgot_email)
                        .key("userid")
                        .value(Library.androidUserID)
                        .endObject();
                Log.d("Casting Forgot Password  ", item.toString());
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

                        System.out.println("Result response : "+json);

                    } catch (Exception e) {
                        Log.e("Buffer Error",
                                "Error converting result " + e.toString());
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
    private void alert(Context context2, String string) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity, android.R.style.Theme_DeviceDefault_Dialog);

        alertDialogBuilder.setTitle("Forgot Password").setMessage(string)
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Intent intent = new Intent(CastingForgotPassword.this, CastingLogin.class);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
