package com.sdi.castivate.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdi.castivate.R;
import com.sdi.castivate.R.id;
import com.sdi.castivate.R.layout;
import com.sdi.castivate.image.ImageLoader;
import com.sdi.castivate.model.CastingImagesModel;
import com.sdi.castivate.model.FavImagesModel;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.GridInterface;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

@SuppressLint("ServiceCast")
public class FavoriteGridAdapter extends BaseAdapter {

	GridInterface gridInterface;
	private Context mContext;
	public ArrayList<FavImagesModel> list = new ArrayList<FavImagesModel>();

	LayoutInflater inflater = null;
	
	ImageLoader imgLoader;
	int loader;


	public FavoriteGridAdapter(Context context, ArrayList<FavImagesModel> list, GridInterface gridInterface) {

		this.gridInterface = gridInterface;
		this.list = list;
		mContext = context;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loader = R.drawable.ic_launcher;

	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		FavImagesModel model = list.get(position);
		View rootView = convertView;
		ViewHolder holder = null;

		if (rootView == null) {

			holder = new ViewHolder();
			rootView = inflater.inflate(R.layout.casting_grid_item, null, false);

			holder.imgViews = (ImageView) rootView.findViewById(R.id.imgViews);

			imgLoader = new ImageLoader(mContext.getApplicationContext());


			rootView.setTag(holder);

			holder.imgViews.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					int get = (Integer)v.getTag();
					
					//CastingImagesModel models = (CastingImagesModel) v.getTag();

					gridInterface.get(get);

				}
			});

		} else {

			holder = (ViewHolder) rootView.getTag();
		}

		// Uri uri = Uri.parse(
		// "https://www.gstatic.com/webp/gallery3/1_webp_ll.png" );

		// new ImageSet(uri.toString(),holder.imgViews).execute();

		holder.imgViews.setTag(position);

		/*if (list.get(position).imgThumb != null) {

			
			DisplayMetrics metrics = this.mContext.getResources().getDisplayMetrics();
			int width = metrics.widthPixels;
			int eachWidth = width / 3;
			int eacheight = eachWidth / 4;
			int height = eacheight + eachWidth;
			
			//replace http to https
			String getUrl = list.get(position).imgThumb;
			//getUrl = getUrl.replaceAll("http:", "https:");
			DebugReportOnLocat.ln("After getUrl ::: "+getUrl);
			
			
			imgLoader.DisplayImage(getUrl,0,  holder.imgViews);

			
	
		}*/
		
		if (list.get(position).imgThumb != null) {

			holder.imgViews.setImageBitmap(null);
			DisplayMetrics metrics = this.mContext.getResources().getDisplayMetrics();
			int width = metrics.widthPixels;
			int eachWidth = width / 3;
			int eacheight = eachWidth / 4;
			int height = eacheight + eachWidth;
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(eachWidth, height);
			holder.imgViews.setLayoutParams(layoutParams);
		
			imgLoader.DisplayImage((list.get(position).imgThumb).replaceAll("http:", "https:"),0,  holder.imgViews);

			
		}

	

		return rootView;

	}

	public class ViewHolder {

		public ImageView imgViews;
		public TextView txtViews;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

}
