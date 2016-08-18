package com.sdi.castivate.adapter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.sdi.castivate.R;
import com.sdi.castivate.model.EthnicityModel;

/**
 * Created by Balachandar on 22-Apr-15.
 */

public class EthnicityFilterAdapter extends BaseAdapter {
    private Context mContext;
 //   private final String[] web;
	public ArrayList<EthnicityModel> arrayList=new ArrayList<EthnicityModel>();
	
  //  boolean[] itemChecked;
   // static HashMap<Integer, Boolean> cartItems = new HashMap<Integer, Boolean>();
    public EthnicityFilterAdapter(Context c,/*String[] web,*/ ArrayList<EthnicityModel> ethnicityList) {
        mContext = c;
        arrayList=ethnicityList;
    
    }
    @Override
    public int getCount() {
   
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
     
        return  arrayList.get(position);
    }
    @Override
    public long getItemId(int position) {
       
        return 0;
    }
    
    int count = 0;
    @SuppressLint("InflateParams") @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View list;
        final int pos = position;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            list = new View(mContext);
            list = inflater.inflate(R.layout.ethnicity_view, null);
            TextView textView = (TextView) list.findViewById(R.id.ethnicity);

            CheckBox checkBox=(CheckBox)list.findViewById(R.id.ethnicity_checkbox);
            //  checkBox.setBackgroundResource(R.drawable.checkbox_selector);
            checkBox.setChecked(arrayList.get(pos)
					.isChecked());
//            try {
//                if (count != 0) {
//                    boolean b = cartItems.get(pos);
//                    if (b == false)
//                    	checkBox.setChecked(false);
//                    else
//                    	checkBox.setChecked(true);
//                }
//            } catch (NullPointerException e) {
//
//            }
         
            checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
				CheckBox cb= (CheckBox)arg0;
				EthnicityModel ethn=(EthnicityModel)cb.getTag();
				ethn.checked=true;
//				for(int i=0; i<arrayList.size();i++){
//					if(pos==i){
//						arrayList.get(i).checked=true;
//					}
//						else{
//						arrayList.get(i).checked=false;
//						
//					}
//				}
//				notifyDataSetChanged();
				}
			});
//            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//				@Override
//				public void onCheckedChanged(CompoundButton view,
//						boolean isChecked) {
//					int getPosition = (Integer) view.getTag();
//				
//					arrayList.get(getPosition).setChecked(view.isChecked());
//				}
//			});
            textView.setText(arrayList.get(pos).name);

            checkBox.setTag(arrayList.get(pos));
        } else {
            list = (View) convertView;
        }
        
        
        return list;
    }
    
//    public static HashMap<Integer, Boolean> getcartItems() {
//        return cartItems;
//    }
}
