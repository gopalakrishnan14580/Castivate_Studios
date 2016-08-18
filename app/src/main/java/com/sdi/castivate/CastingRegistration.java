package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.utils.DatePickerFragment;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.KeyboardUtility;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;
import com.stripe.android.model.Card;

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
public class CastingRegistration extends Activity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    LinearLayout casting_registration_back_icon;
    RelativeLayout rel_casting_registration;
    TextView casting_registration_signin;
    EditText et_name,et_email,et_password,et_cardNumber,et_cvv;
    TextView et_monthYear;
    Button btn_submit;

    String name,email,password,cardNumber,monthYear,cvv;
    int payment_type;
    Context context;
    Activity activity;
    ProgressDialog pDialog;
    static String remainingDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_registration);

        context = this;
        activity=this;
        findElements();
    }

    private void findElements() {

        casting_registration_back_icon=(LinearLayout) findViewById(R.id.casting_registration_back_icon);
        casting_registration_signin=(TextView) findViewById(R.id.casting_registration_signin);
        rel_casting_registration=(RelativeLayout) findViewById(R.id.rel_casting_registration);

        et_name=(EditText) findViewById(R.id.et_name);
        et_email=(EditText) findViewById(R.id.et_email);
        et_password=(EditText) findViewById(R.id.et_password);
        et_cardNumber=(EditText) findViewById(R.id.et_cardNumber);
        et_monthYear=(TextView) findViewById(R.id.et_monthYear);
        et_cvv=(EditText) findViewById(R.id.et_cvv);

        btn_submit=(Button) findViewById(R.id.btn_submit);

        payment_type= getIntent().getExtras().getInt("payment_type");

        rel_casting_registration.setOnClickListener(this);
        casting_registration_back_icon.setOnClickListener(this);
        casting_registration_signin.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        et_monthYear.setOnClickListener(this);

        et_cardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.casting_registration_back_icon:
                regBack();
                break;
            case R.id.rel_casting_registration:
                KeyboardUtility.hideSoftKeyboard(activity);
                break;
            case R.id.casting_registration_signin:
                signIn();
                break;
            case R.id.btn_submit:
                getValues();
                validation();
                break;
            case R.id.et_monthYear:
                datePicker();
                break;
        }
    }

    private void datePicker() {

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(),"Date Picker");
    }

    private void signIn() {
        Intent intent = new Intent(CastingRegistration.this, CastingLogin.class);
        startActivity(intent);

    }

    private void regBack()
    {
        Intent intent = new Intent(CastingRegistration.this, CastingPlan.class);
        startActivity(intent);
        finish();
    }

    public void getValues() {

        name=et_name.getText().toString().trim();
        email=et_email.getText().toString().trim();
        password=et_password.getText().toString().trim();
        cardNumber=et_cardNumber.getText().toString().trim();
        monthYear=et_monthYear.getText().toString().trim();
        cvv=et_cvv.getText().toString().trim();

        cardNumber = cardNumber.replaceAll("\\s", "");
    }

    private void validation() {

        if (name.length() == 0) {

            Library.showToast(context, "Enter your Name");
        }
        else if (email.length() ==0) {

            Library.showToast(context, "Enter your Email ID");

        }else if (!Library.isValidEmail(email)) {

            Library.showToast(context, "Enter your valid Email ID");

        }else if (password.length() == 0) {

            Library.showToast(context, "Enter your Password");

        } else if (password.length() < 8 || password.length() > 21) {

            Library.showToast(context, "Please enter atleast 8 characters in Password");
        }
        else if (cardNumber.length()==0) {

            Library.showToast(context, "Enter your Card Number");

        }
        else if (cardNumber.length() > 16 ) {

            Library.showToast(context, "Enter your valid Card Number");

        }
        else if (monthYear.length() == 0) {

            Library.showToast(context, "Enter your month and year");

        }
        else if (cvv.length() == 0) {

            Library.showToast(context, "Enter your cvv number");

        }
        else if (cvv.length() >3) {

            Library.showToast(context, "Enter your valid cvv number");

        }

        else{


           /* System.out.println("Name       : "+name);
            System.out.println("Email      : "+email);
            System.out.println("Password   : "+password);
            System.out.println("CardNumber : "+cardNumber);
            System.out.println("MonthYear  : "+monthYear);
            System.out.println("Cvv        : "+cvv);
            System.out.println("paymentType: "+payment_type);
            System.out.println("UserID     : "+ Library.androidUserID);*/

            String[] parts = monthYear.split("/");
            String month = parts[0];
            String year = parts[1];

            if (month.length()==1) {
                month = "0" + month;
               // System.out.println("Month : "+ month);
            }
           /* else
            {
                System.out.println("Month : "+month);
            }*/

            //System.out.println("Month Res : "+month);

           // System.out.println("Year Res  : "+year);

            monthYear=month+"/"+year;

            //System.out.println("monthYear Res  : "+monthYear);



            Card card = new Card(cardNumber,Integer.valueOf(month),Integer.valueOf(year), cvv);
            //Card card = new Card(4242424242424242,08,2016,123);

            if ( !card.validateCard() ) {

                Library.showToast(context, "Enter your valid Card Number");


            }
            else {

                System.out.println("Credit card validation success!");

                if(Network.isNetworkConnected(context)) {
                    handlerRegisterSubmitCastingSync.sendEmptyMessage(0);
                }
                else {
                    Library.showToast(context,getResources().getString(R.string.internet_not_available));
                }

            }
        }
    }


    private Handler handlerRegisterSubmitCastingSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Network.isNetworkConnected(context)) {
                registerSubmitCastingASync = new RegisterSubmitCastingASync();
                registerSubmitCastingASync.execute();
            } else {
                Toast.makeText(context, "Internet connection is not available",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    private RegisterSubmitCastingASync registerSubmitCastingASync;

    public class RegisterSubmitCastingASync extends AsyncTask<String, Void, Void> {

        @Override
        public void onPreExecute() {
            pDialog = new ProgressDialog(CastingRegistration.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            castRegistration();
            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (json != null) {

                try {

                    JSONObject oneObject = new JSONObject(json);


                    System.out.println("Remaining Days ------------------------------> "+oneObject.getString("remainingdays"));

                    Library.remainingDays=oneObject.getString("remainingdays");
                    remainingDays=Library.remainingDays;

                    sharedpreferences = getSharedPreferences(Library.MyPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    editor.putString(Library.REMAINING_DAYS,Library.remainingDays);
                    editor.commit();

                    Intent intent = new Intent(CastingRegistration.this, CastingLogin.class);
                    startActivity(intent);
                    finish();
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

    private void castRegistration() {

            try {
                HttpPost request = new HttpPost(HttpUri.CASTING_REGISTRATION);
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type",
                        "application/x-www-form-urlencoded");

                JSONStringer item = null;

              /*  {
                "userid": "8",
                        "email": "ramsdevelop@gmail.com",
                        "password": "ram@123",
                        "username": "Ganesh",
                        "cardnum": "789456123698745632",
                        "cardexpiry": "08/2018",
                        "cardcvv": "963",
                        "paytype": "2"
            }*/

                try {

                    item = new JSONStringer()
                            .object()
                            .key("userid")
                            .value(Library.androidUserID)
                            .key("email")
                            .value(email)
                            .key("password")
                            .value(password)
                            .key("username")
                            .value(name)
                            .key("cardnum")
                            .value(cardNumber)
                            .key("cardexpiry")
                            .value(monthYear)
                            .key("cardcvv")
                            .value(cvv)
                            .key("paytype").value(payment_type)
                            .endObject();
                    Log.d("Casting Registration", item.toString());
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

    public static class FourDigitCardFormatWatcher implements TextWatcher {

        // Change this to what you want... ' ', '-' etc..
        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Remove spacing char
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            // Insert char where needed.
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }
}
