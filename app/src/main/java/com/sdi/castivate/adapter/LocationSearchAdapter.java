package com.sdi.castivate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;



import java.util.ArrayList;

import com.sdi.castivate.R;


/**
 * Created by Balachandar on 23-Mar-15.
 */
public class LocationSearchAdapter extends BaseAdapter implements Filterable {

    Context mContext;
    LayoutInflater inflater;
    ArrayList<String> list;
    public ArrayList<String> listSearch;

    public LocationSearchAdapter(Context context1, ArrayList<String> stringList) {
        this.mContext = context1;
        this.list = stringList;
        this.listSearch = stringList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HoldersView holdersView;
        View rootView = convertView;
        if (rootView == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = inflater.inflate(R.layout.us_search_item, parent, false);

            holdersView = new HoldersView();
            holdersView.textView = (TextView) rootView.findViewById(R.id.txtSearch);
            rootView.setTag(holdersView);
        } else {

            holdersView = (HoldersView) convertView.getTag();
        }
        // searchModel = list.get(position);
        holdersView.textView.setText(list.get(position));

        return rootView;
    }

    public class HoldersView {
        TextView textView;
    }

    //Filterable methods

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<String> tempList = new ArrayList<String>();
            if (constraint != null || listSearch != null) {
                int len = listSearch.size();
                int i = 0;
                while (i < len) {
                    String getItem = listSearch.get(i);
                    if (getItem.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(getItem);
                    }
                    i++;
                }
                results.values = tempList;
                results.count = tempList.size();
            } /*else {
                ArrayList<String> nlist = new ArrayList<String>();
                for (String p : list) {
                    if (p.toUpperCase().contains(constraint.toString().toUpperCase()))
                        nlist.add(p);
                }
                results.values = nlist;
                results.count = nlist.size();
            }*/
            return results;
        }

        @SuppressWarnings("unchecked")
		@Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    };

}
