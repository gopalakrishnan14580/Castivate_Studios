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
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.model.CastingDetailsModel;
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
import java.util.ArrayList;

/**
 * Created by androidusr1 on 11/8/16.
 */
@SuppressWarnings({"deprecation","unchecked"})
@SuppressLint({ "ResourceAsColor", "InlinedApi", "ShowToast", "UseSparseArrays" })
public class CastingLogin extends Activity implements View.OnClickListener {

    LinearLayout casting_login_back_icon;
    RelativeLayout rel_casting_login;
    TextView txt_forgot;
    EditText et_email,et_password;
    Button btn_signin;
    String email,password;
    Context context;
    Activity activity;
    ProgressDialog pDialog;
    private ArrayList<CastingDetailsModel> selectedCastingDetailsModels = new ArrayList<CastingDetailsModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_login);

        context = this;
        activity = this;
        findElements();
    }

    private void findElements() {

        try{
            selectedCastingDetailsModels = (ArrayList<CastingDetailsModel>) getIntent().getSerializableExtra("selectedCastingDetailsModels");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        txt_forgot=(TextView) findViewById(R.id.txt_forgot);
        casting_login_back_icon=(LinearLayout) findViewById(R.id.casting_login_back_icon);
        rel_casting_login=(RelativeLayout) findViewById(R.id.rel_casting_login);
        et_email=(EditText) findViewById(R.id.et_email);
        et_password=(EditText) findViewById(R.id.et_password);
        btn_signin=(Button) findViewById(R.id.btn_signin);
        rel_casting_login.setOnClickListener(this);
        btn_signin.setOnClickListener(this);
        casting_login_back_icon.setOnClickListener(this);
        txt_forgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.casting_login_back_icon:
                finish();
                break;
            case R.id.rel_casting_login:
                KeyboardUtility.hideSoftKeyboard(activity);
                break;
            case R.id.txt_forgot:
                forgotPassword();
                break;
            case R.id.btn_signin:
                getValues();
                validation();
                break;
        }
    }

    private void forgotPassword() {

        Intent intent = new Intent(CastingLogin.this, CastingForgotPassword.class);
        startActivity(intent);
        finish();
    }

    private void getValues() {

        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();

    }

    private void validation() {

        if (email.length() == 0) {

            Library.showToast(context, "Enter your Email ID");
        }
        else if (!Library.isValidEmail(email)) {
            Library.showToast(context, "Enter your valid Email ID");

        }else if (password.length() == 0) {
            Library.showToast(context, "Enter your Password");

        } else if (password.length() < 8 || password.length() > 21) {
            Library.showToast(context, "Password must be atleast 8 characters");

        } else {

            if(Network.isNetworkConnected(context)) {
                handlerLoginCastingSync.sendEmptyMessage(0);
            }
            else {
                Library.showToast(context,getResources().getString(R.string.internet_not_available));
            }
        }
    }
    private Handler handlerLoginCastingSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Network.isNetworkConnected(context)) {
                loginCastingASync = new LoginCastingASync();
                loginCastingASync.execute();
            } else {
                Toast.makeText(context, "Internet connection is not available",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    private LoginCastingASync loginCastingASync;

    public class LoginCastingASync extends AsyncTask<String, Void, Void> {

        @Override
        public void onPreExecute() {
            pDialog = new ProgressDialog(CastingLogin.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            castLogin();
            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (json != null) {

                try {

                    JSONObject oneObject = new JSONObject(json);

                    if(oneObject.getString("status").equals("200")) {

                        //System.out.println("Status ------------------------------> " + oneObject.getString("status"));

                        //System.out.println("Message ------------------------------> " + oneObject.getString("message"));
                        //go to file upload

                        //Library.showToast(context, "Login Successfully");

                        Intent intent = new Intent(CastingLogin.this,CastingFileUpload.class);
                        intent.putExtra("selectedCastingDetailsModels",selectedCastingDetailsModels);
                        startActivity(intent);
                        finish();

                    }
                    if(oneObject.getString("status").equals("201")) {

                        alert(context,oneObject.getString("message"));

                    }

                }
                catch (JSONException e) {
                    System.out.println("Exception : "+e.getMessage());
                    // TODO: handle exception
                }

            }else{

                System.out.println("Casting not register. Please try again.");
            }

        }

    }

    InputStream is = null;
    String json = "";
    StringBuilder sb;
    String Status;

    private void castLogin() {

        try {
            HttpPost request = new HttpPost(HttpUri.CASTING_SIGNIN);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type",
                    "application/x-www-form-urlencoded");

            JSONStringer item = null;

              /*  {
        "email": "ramsdevelop@gmail.com",
        "password": "ram@123",
        "userid": "51"
            }*/

            try {

                item = new JSONStringer()
                        .object()
                        .key("email")
                        .value(email)
                        .key("password")
                        .value(password)
                        .key("userid")
                        .value(Library.androidUserID)
                        .endObject();
                Log.d("Casting Login ", item.toString());
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

        alertDialogBuilder.setTitle("Sign In").setMessage(string)
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        et_email.setText("");
                        et_password.setText("");
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
