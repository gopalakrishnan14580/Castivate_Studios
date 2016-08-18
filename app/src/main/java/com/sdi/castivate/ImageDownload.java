package com.sdi.castivate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Gnanaoly on 18-May-16.
 */
public class ImageDownload extends AsyncTask<String, Void, Bitmap> {

	private final WeakReference<ImageView> imageViewReference;
//	private final WeakReference<ProgressBar> progressBarWeakReference;
//	private final ProgressBar progressBar;

	public Context context;

	public ImageDownload(Context context, ImageView imageView/*, ProgressBar progressBar*/) {
		imageViewReference = new WeakReference<ImageView>(imageView);
//		progressBarWeakReference = new WeakReference<ProgressBar>(progressBar);
//		this.progressBar = progressBar;

		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Create a progressdialog

//		if (progressBar != null) {
//			ProgressBar progressBar = progressBarWeakReference.get();
//			progressBar.setVisibility(View.VISIBLE);
//		}

	}

	@Override
	protected Bitmap doInBackground(String... URL) {

		String imageURL = URL[0];

		Bitmap image = getFileName(imageURL, context, "SBProvider");

		return image;
	}

	@Override
	protected void onPostExecute(Bitmap bmRotated) {
		// Set the bitmap into ImageView

		/*
		 * Bitmap bitmap = BitmapFactory.decodeFile(result);
		 * 
		 * ExifInterface exif = new ExifInterface(result); int rotation =
		 * exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
		 * ExifInterface.ORIENTATION_NORMAL); Bitmap bmRotated =
		 * rotateBitmap(bitmap, rotation);
		 */
		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				if (bmRotated != null) {
					imageView.setVisibility(View.VISIBLE);
					imageView.setImageBitmap(bmRotated);
				}
			}
		}
//		if (progressBar != null) {
//			ProgressBar progressBar = progressBarWeakReference.get();
//			progressBar.setVisibility(View.GONE);
//		}

	}

	String SaveFolderName;

	private Bitmap getFileName(String url, Context context, String folderName) {
		Bitmap bitmap = null;
		try {
			SaveFolderName = context.getFilesDir().getAbsolutePath() + "/" + folderName;

			String filepath = url.substring(0, url.lastIndexOf("/"));
			String str_randomnumber = url.substring(url.lastIndexOf("/") + 1);
			File wallpaperDirectory = new File(SaveFolderName);
			if (!wallpaperDirectory.exists())
				wallpaperDirectory.mkdirs();
			String Photo_ImagePath = SaveFolderName + "/" + str_randomnumber;

			File f = new File(Photo_ImagePath);
//			if (f.exists()) {
//				f.delete();
//			}
			if (!f.exists()) {
				// DebugReportOnLocat.ln(" EEEEEEEEEEXXXXXXXXIIIIISSSSSSSTTTTTTT "
				// +
				// Photo_ImagePath);
				try {
					f.createNewFile();
					str_randomnumber = str_randomnumber.replace(" ", "%20");
					url = filepath + "/" + str_randomnumber;
					// DebugReportOnLocat.ln("filename url " + url);
					URL imageUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
					conn.setConnectTimeout(30000);
					conn.setReadTimeout(30000);
					InputStream is = conn.getInputStream();
					OutputStream os = new FileOutputStream(f);
					ImageDownloadUtils.CopyStream(is, os);
					os.close();

					bitmap = BitmapFactory.decodeFile(Photo_ImagePath);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return bitmap;
				}

			}else{
				bitmap = BitmapFactory.decodeFile(Photo_ImagePath);
			}

			// ExifInterface exif = new ExifInterface(Photo_ImagePath);
			// int rotation =
			// exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
			// ExifInterface.ORIENTATION_NORMAL);
			// bmRotated = rotateBitmap(bitmap, rotation);

		} catch (RuntimeException e) {
			e.printStackTrace();
			return bitmap;
		}
		return bitmap;
	}
	//
	// public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
	//
	// Matrix matrix = new Matrix();
	// switch (orientation) {
	// case ExifInterface.ORIENTATION_NORMAL:
	// return bitmap;
	// case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
	// matrix.setScale(-1, 1);
	// break;
	// case ExifInterface.ORIENTATION_ROTATE_180:
	// matrix.setRotate(180);
	// break;
	// case ExifInterface.ORIENTATION_FLIP_VERTICAL:
	// matrix.setRotate(180);
	// matrix.postScale(-1, 1);
	// break;
	// case ExifInterface.ORIENTATION_TRANSPOSE:
	// matrix.setRotate(90);
	// matrix.postScale(-1, 1);
	// break;
	// case ExifInterface.ORIENTATION_ROTATE_90:
	// matrix.setRotate(90);
	// break;
	// case ExifInterface.ORIENTATION_TRANSVERSE:
	// matrix.setRotate(-90);
	// matrix.postScale(-1, 1);
	// break;
	// case ExifInterface.ORIENTATION_ROTATE_270:
	// matrix.setRotate(-90);
	// break;
	// default:
	// return bitmap;
	// }
	// try {
	// Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	// bitmap.getHeight(), matrix, true);
	// bitmap.recycle();
	// return bmRotated;
	// } catch (OutOfMemoryError e) {
	// e.printStackTrace();
	// return null;
	// }
	// }
}