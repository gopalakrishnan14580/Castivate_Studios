package com.sdi.castivate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdi.castivate.R;
import com.sdi.castivate.model.DriveModel;

import java.util.ArrayList;

public class DriveListAdapter extends BaseAdapter {

	Context activity;
	private LayoutInflater inflater = null;
	ArrayList<DriveModel> list = new ArrayList<DriveModel>();


	public DriveListAdapter(Context activity, ArrayList<DriveModel> list) {
		this.activity = activity;
		this.list = list;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
		VIewHolder holder = null;


		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		if (convertView == null) {
			holder = new VIewHolder();
			rowView = new View(activity);
			rowView = inflater.inflate(R.layout.drive_row_item, null);
			holder.drive_file_item = (TextView) rowView.findViewById(R.id.drive_file_item);
			rowView.setTag(holder);

//
		} else {
			holder = (VIewHolder) rowView.getTag();
		}

		try {
			holder.drive_file_item.setText(list.get(position).getFileName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return rowView;
	}

	public class VIewHolder {
		//LinearLayout rootLay;
		TextView drive_file_item;
	}


}
