package com.sdi.castivate;

import java.util.ArrayList;
import java.util.Calendar;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.Network;

public class InitialFilterScreen extends Activity implements  OnClickListener{
	
	SharedPreferences sharedpreferences;
	Editor editor;
	TextView skip;
	ImageView letsGo;
	RadioButton male, female;
	RadioGroup genderType;
	ArrayList<String> years = new ArrayList<String>();
	
   Context context;
	String birthYear, gender, ethnicityVAlue;
    boolean UnionType = false;
	String[] ethnicity = { "Caucasian", "African American", "Hispanic",
			"Asian", "Native American", "Middle Eastern", "Other" };
	String[] stockArr;
  WheelView bithYear, ethnicityView;
	int paidPos;
	 public boolean initalScreen=false;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.inital_screen);
	context=this;
	CastingScreen.isFavScreen=false;

	skip=(TextView)findViewById(R.id.skip);
	skip.setOnClickListener(this);
	letsGo =(ImageView)findViewById(R.id.lets_go);
	letsGo.setOnClickListener(this);
	int thisYear = Calendar.getInstance().get(Calendar.YEAR);
	for (int i = thisYear; i >= 1900; i--) {
		years.add(Integer.toString(i));
	}
	genderType = (RadioGroup) findViewById(R.id.radiogroup_union);
	male = (RadioButton) findViewById(R.id.radio_union);
	male.setChecked(true);
	gender = "male";
	 
	 birthYear="1990";
	
	 ethnicityVAlue="Caucasian";
	genderType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub

			// Method 1 For Getting Index of RadioButton
			paidPos = genderType.indexOfChild(findViewById(checkedId));

			gender = String.valueOf(paidPos);
			DebugReportOnLocat.ln("gender" + gender);
			switch (paidPos) {
			case 0:
				gender = "male";
				DebugReportOnLocat.ln("gender" + gender);
				break;
		
			case 2:
				gender = "female";
				DebugReportOnLocat.ln("gender" + gender);
				break;
			
			}
		}
	});
	stockArr = new String[years.size()];
	stockArr = years.toArray(stockArr);

	
	   ethnicityView= (WheelView) findViewById(R.id.ethnicity_list);
	    ethnicityView.setVisibleItems(3);
	   
	    ethnicityView.setCyclic(false);
	    ethnicityView.setViewAdapter(new DateArrayAdapter(this, ethnicity,2));
	    ethnicityView.setCurrentItem(0);
	   
	 OnWheelChangedListener listener = new OnWheelChangedListener() {
	        public void onChanged(WheelView wheel, int oldValue, int newValue) {
	        	updateDays(ethnicityView);
	        }
	    };
    
 
 
    ethnicityView.addChangingListener(listener);
    
    bithYear = (WheelView) findViewById(R.id.birth_year_list);
    bithYear.setVisibleItems(3);
    bithYear.setViewAdapter(new BirthYearArrayAdapter(this, stockArr,2));
    bithYear.setCurrentItem(25);
    bithYear.setCyclic(false);
    OnWheelChangedListener listener2 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
        	updateBirth(bithYear);
        }
    };

    bithYear.addChangingListener(listener2);
    
    
   
}
  
   
   
   void updateDays(WheelView month) {
      try {
    	  int pos=month.getCurrentItem();
          ethnicityVAlue=ethnicity[pos].toString();
          DebugReportOnLocat.ln("ethnicityValue:::"+ethnicityVAlue);
        
	} catch (IndexOutOfBoundsException e) {
		// TODO: handle exception
	}
       
   }
   void updateBirth(WheelView birth) {
	      try {
	    	  int pos=birth.getCurrentItem();
	          birthYear=stockArr[pos].toString();
	          DebugReportOnLocat.ln("birthYear:::"+birthYear);
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
		}
      
   }




/**
 * Adapter for string based wheel. Highlights the current value.
 */
private class DateArrayAdapter extends ArrayWheelAdapter<String> {
    // Index of current item
    int currentItem;
    // Index of item to be highlighted
    int currentValue;
    
    /**
     * Constructor
     */
    public DateArrayAdapter(Context context, String[] items, int current) {
        super(context, items);
        this.currentValue = current;
        setTextSize(18);
    }
    
    @Override
    protected void configureTextView(TextView view) {
        super.configureTextView(view);
        if (currentItem == currentValue) {
        //    view.setTextColor(0xFF0000F0);
        }
        view.setTypeface(Typeface.SANS_SERIF);
    }
    
    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        currentItem = index;
        return super.getItem(index, cachedView, parent);
    }
}

/**
 * Adapter for string based wheel. Highlights the current value.
 */
private class BirthYearArrayAdapter extends ArrayWheelAdapter<String> {
    // Index of current item
    int currentItem;
    // Index of item to be highlighted
    int currentValue;
    
    /**
     * Constructor
     */
    public BirthYearArrayAdapter(Context context, String[] items, int current) {
        super(context, items);
        this.currentValue = current;
        setTextSize(18);
    }
    
    @Override
    protected void configureTextView(TextView view) {
        super.configureTextView(view);
        if (currentItem == currentValue) {
          //  view.setTextColor(0xFF0000F0);
        }
        view.setTypeface(Typeface.SANS_SERIF);
    }
    
    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        currentItem = index;
        return super.getItem(index, cachedView, parent);
    }
}




@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.lets_go:
		if(Network.isNetworkConnected(context)){
			initalScreen=true;
		
			  sharedpreferences = getSharedPreferences(Library.MyPREFERENCES,
						Context.MODE_PRIVATE);
			
			   editor = sharedpreferences.edit();
			
				editor.putString(Library.ETHNICITY, ethnicityVAlue);
				editor.putString(Library.GENDER, gender);
				editor.putString(Library.BIRTH, birthYear);
				editor.putBoolean(Library.NON_UNION, UnionType);
				editor.putBoolean(Library.INITIAL_SCREEN, initalScreen);
				editor.commit();
		
			 startActivity(new Intent(InitialFilterScreen.this, CastingScreen.class));
		}else{
			Library.showToast(context, "Please check your internet connection.");
			
		}
	
		
		break;
	case R.id.skip:
		if(Network.isNetworkConnected(context)){
			
		gender = "";
		 birthYear="";
		 ethnicityVAlue="";
		 initalScreen=true;
		
		  sharedpreferences = getSharedPreferences(Library.MyPREFERENCES,
					Context.MODE_PRIVATE);
		
		   editor = sharedpreferences.edit();
		
			editor.putString(Library.ETHNICITY, ethnicityVAlue);
			editor.putString(Library.GENDER, gender);
			editor.putString(Library.BIRTH, birthYear);
			editor.putBoolean(Library.NON_UNION, UnionType);
			editor.putBoolean(Library.INITIAL_SCREEN, initalScreen);
			editor.commit();
			
		 startActivity(new Intent(InitialFilterScreen.this, CastingScreen.class));
		}else{
			Library.showToast(context, "Please check your internet connection.");
			
		}
		break;
	default:
		break;
	}
}
@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
	//super.onBackPressed();
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			context, android.R.style.Theme_DeviceDefault_Dialog);

	alertDialogBuilder
			.setMessage("Do you want to Exit from this Application?")
			.setCancelable(false)
			.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@SuppressLint("NewApi")
						public void onClick(DialogInterface dialog,
								int id) {
							finishAffinity();
							  
						}
					})

			.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {

							dialog.cancel();
						}
					});

	AlertDialog alertDialog = alertDialogBuilder.create();
	alertDialog.show();
}


}
