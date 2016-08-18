package com.sdi.castivate.adapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.sdi.castivate.ImageDownload;
import com.sdi.castivate.R;
import com.sdi.castivate.R.id;
import com.sdi.castivate.R.layout;
import com.sdi.castivate.image.ImageLoader;
import com.sdi.castivate.image.MemoryCache;
import com.sdi.castivate.model.CastingImagesModel;
import com.sdi.castivate.utils.GridInterface;
import com.sdi.castivate.utils.HttpRequest;
import com.sdi.castivate.utils.Library;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

@SuppressLint("ServiceCast")
public class CastivateGridAdapter extends BaseAdapter {

	GridInterface gridInterface;
	private Context mContext;
	public ArrayList<CastingImagesModel> list = new ArrayList<CastingImagesModel>();

	LayoutInflater inflater = null;
	ImageLoader imgLoader;

	int loader;

	public CastivateGridAdapter(Context context, ArrayList<CastingImagesModel> list, GridInterface gridInterface) {

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

		CastingImagesModel model = list.get(position);
		View rootView = convertView;
		ViewHolder holder = null;

		if (rootView == null) {

			holder = new ViewHolder();
			rootView = inflater.inflate(R.layout.casting_grid_item, parent, false);
			

			holder.imgViews = (ImageView) rootView.findViewById(R.id.imgViews);
			
		//	holder.relView= (RelativeLayout) rootView.findViewById(R.id.relView);
			holder.progressBar= (ProgressBar) rootView.findViewById(R.id.progressBar);
			
			
			
			imgLoader = new ImageLoader(mContext.getApplicationContext());

			rootView.setTag(holder);

			holder.imgViews.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					int get = (Integer)v.getTag();
					
					//CastingImagesModel models = (CastingImagesModel) v.getTag();
					
					imgLoader.clearCache();

					gridInterface.get(get);

				}
			});

		} else {

			holder = (ViewHolder) rootView.getTag();
		}

		// Uri uri = Uri.parse(
		// "https://www.gstatic.com/webp/gallery3/1_webp_ll.png" );

		// new ImageSet(uri.toString(),holder.imgViews).execute();

//		holder.imgViews.setTag(position);

		if (list.get(position).imgThumb != null) {

			//holder.imgViews.setImageBitmap(null);
			DisplayMetrics metrics = this.mContext.getResources().getDisplayMetrics();
			int width = metrics.widthPixels;
			int eachWidth = width / 3;
			int eacheight = eachWidth / 4;
			int height = eacheight + eachWidth;
			

			
//			AbsListView.LayoutParams layoutParamsNew = new AbsListView.LayoutParams(eachWidth, height);
//			holder.relView.setLayoutParams(layoutParamsNew);
			
//			holder.relView.getLayoutParams().width = eachWidth;
//			holder.relView.getLayoutParams().height= height;
			

//			Picasso.with(mContext).load((list.get(position).imgThumb).replaceAll("http", "https")).resize(eachWidth, height).into(holder.imgViews);
//			Picasso.with(mContext).load((list.get(position).imgThumb).replaceAll("http", "https")).into(holder.imgViews);
//		Picasso.with(mContext).load((list.get(position).imgThumb)).resize(eachWidth, height).into(holder.imgViews);
			//Sugumara New Changes ( 1 June 16 )
//			if(list.get(position).localFile!=null&&!list.get(position).localFile.equals("")){
//				 Picasso.with(mContext).load((new File(list.get(position).localFile))).into(holder.imgViews);
//                 holder.imgViews.setVisibility(View.VISIBLE);
//                 
//                 holder.progressBar.setVisibility(View.GONE);
//                 holder.relView.setBackgroundColor(Color.TRANSPARENT);
//                 
//			}else{
//				 holder.imgViews.setVisibility(View.GONE);
//				//(ProgressBar progressBar,ImageView imgView,RelativeLayout relView,int currentPos){
//				new DownloadMyFileAsyncs(holder.progressBar,holder.imgViews,holder.relView,position).execute();
//			}
			

			// Original
//			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(eachWidth, height);
//			holder.imgViews.setLayoutParams(layoutParams);
//			holder.progressBar.setVisibility(View.GONE);
//			imgLoader.DisplayImage((list.get(position).imgThumb).replaceAll("http:", "https:"),R.drawable.dd,  holder.imgViews);

//			try{
//			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(eachWidth, height);
//			holder.imgViews.setLayoutParams(layoutParams);
//			UrlImageViewHelper.setUrlDrawable(holder.imgViews, list.get(position).imgThumb, R.drawable.dd);
//			}catch(Exception e){
//				System.out.println("e "+e);
//				Toast.makeText(mContext, "Crashed"+e, 1).show();
//			}
			
			
//			 new ImageDownload(mContext, holder.imgViews).execute(list.get(position).imgThumb);
			 
			Glide.with(mContext)
		    .load(list.get(position).imgThumb.replaceAll("http:", "https:"))
		    .into(holder.imgViews);
			
		}

		

		return rootView;

	}

	public class ViewHolder {

		public ImageView imgViews;
		public TextView txtViews;
	//	public RelativeLayout relView;
		public ProgressBar progressBar;
		

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
	
	
	
	//String sFile = "";
    class DownloadMyFileAsyncs extends AsyncTask<String, String, String> {
        File outputFile = null;
        File tempFile = null;
        
        RelativeLayout relView;
        ProgressBar progressBar;
        ImageView imgView;
        int currentPos;
        
        public DownloadMyFileAsyncs(ProgressBar progressBar,ImageView imgView,RelativeLayout relView,int currentPos){
        	this.progressBar = progressBar;
        	this.imgView = imgView;
        	this.relView = relView;
        	this.currentPos = currentPos;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            imgView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            relView.setVisibility(View.VISIBLE);
            relView.setBackgroundColor(Color.BLACK);
        }

        @Override
        protected String doInBackground(String... aurl) {
            try {
                //String myaudioUrl = "http://res.cloudinary.com/demo/image/upload/sample.jpg";
                String myaudioUrl =list.get(currentPos).imgThumb;
                
                File gbDirectory = new File(Library.SAVE_FOLDER_NAME);
                if (!gbDirectory.exists())
                    gbDirectory.mkdirs();
                File gbDirectoryTemp = new File(Library.SAVE_FOLDER_TEMP_NAME);
                if (!gbDirectoryTemp.exists())
                    gbDirectoryTemp.mkdirs();
                
                String imgName = list.get(currentPos).imgId+"."+MimeTypeMap.getFileExtensionFromUrl(list.get(currentPos).imgThumb);
                
                String Path = Library.SAVE_FOLDER_NAME + "/"+imgName;
                String pathTemp = Library.SAVE_FOLDER_TEMP_NAME + "/"+imgName;
                outputFile = new File(Path);
                tempFile = new File(pathTemp);

                if (tempFile.exists())
                    tempFile.delete();
                if (outputFile.exists())
                    outputFile.delete();

                if (!outputFile.exists()) {
                    int count;
                    URL url = new URL(myaudioUrl);
                    URLConnection conexion = url.openConnection();
                    conexion.setRequestProperty("Accept-Encoding", "identity");
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                   // double totalMb = ServiceCall.megabytesAvailable();
                    float file_size = lenghtOfFile / 1048576;
                    System.out.println("File mb : " + file_size);
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(tempFile);
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {
        	
            progressBar.setVisibility(View.GONE);
            relView.setBackgroundColor(Color.TRANSPARENT);
            if (outputFile.exists()) {
            	
                Picasso.with(mContext).load((outputFile)).into(imgView);
                imgView.setVisibility(View.VISIBLE);
                
            } else {
            	
                    int getResult = copyFile(tempFile, outputFile);
                    if (getResult == 1) {
                        tempFile.delete();
                        
                        list.get(currentPos).localFile = outputFile.toString();
                        
                        Picasso.with(mContext).load((outputFile)).into(imgView);
                        imgView.setVisibility(View.VISIBLE);
                        
                    } else {
                        System.out.println("File copy failed..");
                    }
            }
        }
    }



	public int copyFile(File src, File dst) {

		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

	}

}
