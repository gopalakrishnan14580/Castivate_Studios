package com.sdi.castivate;

import java.util.ArrayList;
import java.util.HashMap;

import com.sdi.castivate.model.CastingDetailsModel;

import android.app.Application;
import android.content.Context;



/**
 * Created by Balachandar on 21-Apr-15.
 */



public class CastivateApplication extends Application {

    private static CastivateApplication instance ;
     static Context context;
   
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        instance = this;
        context = this;



    }


    public static  Context getAppContext() {
        return CastivateApplication.context;
    }


	/* Static 'instance' method */
	public static CastivateApplication getInstance() {
		return instance;
	}

	/* Other methods protected by singleton-ness */
	protected static void demoMethod() {
		System.out.println("demoMethod for singleton");
		
	}
	
	HashMap<Integer, ArrayList<CastingDetailsModel>> hashMap = new HashMap<Integer, ArrayList<CastingDetailsModel>>();
	
   

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();


    }
}