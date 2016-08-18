package com.sdi.castivate.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.sdi.castivate.R;
import com.sdi.castivate.image.ImageLoader;
import com.sdi.castivate.model.CastingImagesModel;
import com.sdi.castivate.utils.GridInterface;

public class ImageListAdapter extends ArrayAdapter {
	private Context context;
	private LayoutInflater inflater;

	private ArrayList<CastingImagesModel> imageUrls;
	ImageLoader imgLoader;
	int eachWidth, height;

	GridInterface gridInterface;
	public ImageListAdapter(Context context, ArrayList<CastingImagesModel> imageUrls,GridInterface gridInterface) {
		super(context, R.layout.listview_item_image, imageUrls);

		this.gridInterface = gridInterface;
		this.context = context;
		this.imageUrls = imageUrls;

		inflater = LayoutInflater.from(context);

		DisplayMetrics metrics = this.context.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		eachWidth = width / 3;
		int eacheight = eachWidth / 4;
		height = eacheight + eachWidth;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
			imgLoader = new ImageLoader(context.getApplicationContext());

		}

		convertView.getLayoutParams().width = eachWidth;
		convertView.getLayoutParams().height = height;

		// imgLoader.DisplayImage((imageUrls.get(position).imgThumb).replaceAll("http:",
		// "https:"),R.drawable.dd, (ImageView) convertView);

		//UrlImageViewHelper
		try {
			UrlImageViewHelper.setUrlDrawable((ImageView) convertView, imageUrls.get(position).imgThumb, R.drawable.dd);
		} catch (Exception e) {
			System.out.println("e " + e);
			Toast.makeText(context, "Crashed" + e, 1).show();
		}
		
		
		//Glide
//		Glide.with(context)
//	    .load(imageUrls.get(position).imgThumb.replaceAll("http:", "https:"))
//	    .placeholder(R.drawable.dd)
//	    .into((ImageView) convertView);
		
		
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

//				int get = (Integer)v.getTag();
				
				//CastingImagesModel models = (CastingImagesModel) v.getTag();
				
//				imgLoader.clearCache();

				gridInterface.get(position);

			}
		});
		
		
		return convertView;
	}
}
