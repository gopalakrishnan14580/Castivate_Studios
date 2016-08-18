package com.sdi.castivate.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdi.castivate.R;
import com.sdi.castivate.model.CastingDetailsModel;

public class CastingsListAdapter extends BaseAdapter {

	Context activity;
	private LayoutInflater inflater = null;

	//ListInterface listInterface;

	ArrayList<CastingDetailsModel> list = new ArrayList<CastingDetailsModel>();

	CastingsListAdapter adapetr;

	public CastingsListAdapter(Context activity, ArrayList<CastingDetailsModel> list/*, ListInterface listInterface*/) {
		this.activity = activity;
		this.list = list;
//		this.listInterface = listInterface;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		VIewHolder holder = null;
		CastingDetailsModel model = list.get(position);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		if (convertView == null) {
			holder = new VIewHolder();
			rowView = new View(activity);
			rowView = inflater.inflate(R.layout.casting_list_item, null);

			holder.castingTitle = (TextView) rowView.findViewById(R.id.cast_title_no_image);
			holder.castingPaidStatus = (TextView) rowView.findViewById(R.id.paid_status_no_image);
			holder.castingType = (TextView) rowView.findViewById(R.id.cast_type_no_image);

			//holder.rootLay = (LinearLayout) rowView.findViewById(R.id.rootLay);

			rowView.setTag(holder);

//			holder.rootLay.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//
//					CastingListDetailsModel model = list.get(position);
////					listInterface.getList(model);
//				}
//			});

		} else {
			holder = (VIewHolder) rowView.getTag();
			//rowView = (View) convertView;
		}

		holder.castingTitle.setText(model.castingTitle);
		holder.castingPaidStatus.setText(model.castingPaidStatus);
		holder.castingType.setText(model.castingType);

		// rowView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// CastingScreen.castingsList.setVisibility(View.GONE);
		// CastingScreen.castingViewNoImage.setVisibility(View.VISIBLE);
		// Toast.makeText(activity, "vvv", Toast.LENGTH_LONG).show();
		//
		// }
		// });

		return rowView;
	}

	public class VIewHolder {
		//LinearLayout rootLay;
		TextView castingTitle, castingPaidStatus, castingType;
	}

	public synchronized void refreshAdapter(ArrayList<CastingDetailsModel> items) {
		// itemDetailsrrayList.clear();
		list = items;
		notifyDataSetChanged();
	}

}
