package com.sdi.castivate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by androidusr1 on 10/8/16.
 */
public class CastingPlan extends Activity implements View.OnClickListener {

    private RadioGroup radioPaymentGroup;
    private LinearLayout casting_plan_back_icon;
    private Button btn_subscribe_now;
    private  RadioButton rb;

    String payment="$9.99 for 12 Months";

    int payment_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_plan);

        findElements();

    }

    private void findElements() {

        casting_plan_back_icon=(LinearLayout) findViewById(R.id.casting_plan_back_icon);
        radioPaymentGroup=(RadioGroup)findViewById(R.id.radioPayment);
        btn_subscribe_now=(Button) findViewById(R.id.btn_subscribe_now);

        btn_subscribe_now.setOnClickListener(this);
        casting_plan_back_icon.setOnClickListener(this);


        radioPaymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                rb = (RadioButton) findViewById(checkedId);

                payment = String.valueOf(rb.getText());

            }
        });

        casting_plan_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.casting_plan_back_icon:
                finish();
                break;
            case R.id.btn_subscribe_now:
                subscribeNow();
                break;

        }
    }
    private void subscribeNow() {

        try {

            if (payment.equals("$9.99 for 12 Months")) {

                payment_type = 1;

            Intent intent = new Intent(CastingPlan.this, CastingRegistration.class);
            intent.putExtra("payment_type",payment_type);
            startActivity(intent);
                finish();
            } else {

                payment_type = 2;

            Intent intent = new Intent(CastingPlan.this, CastingRegistration.class);
            intent.putExtra("payment_type",payment_type);
            startActivity(intent);
                finish();
            }
        }catch (Exception e)
        {
            System.out.println("Exception : "+e.getMessage());
        }
    }
}
