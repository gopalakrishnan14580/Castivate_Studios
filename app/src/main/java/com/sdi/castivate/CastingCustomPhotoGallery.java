package com.sdi.castivate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.model.ImageUrl;

import java.util.ArrayList;

@SuppressWarnings("deprecation")

public class CastingCustomPhotoGallery extends Activity {

    private GridView grdImages;
    private TextView btnSelect;
    private ImageAdapter imageAdapter;
    private String[] arrPath;

    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;

    private int max_count=4;
    public   int update_count = 0;
    private ArrayList<ImageUrl> imageUpdates = new ArrayList<ImageUrl>();
    private LinearLayout custom_photo_gallery_back_icon;

    /**
     * Overrides methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_photo_gallery);

        grdImages= (GridView) findViewById(R.id.grdImages);
        btnSelect= (TextView) findViewById(R.id.btnSelect);
        custom_photo_gallery_back_icon=(LinearLayout) findViewById(R.id.custom_photo_gallery_back_icon);
        custom_photo_gallery_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent Intent = getIntent();
        update_count= Intent.getIntExtra("update_count", 0);

        loadGridView();
    }

    public  void  loadGridView()
    {
        final String[] columns = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };
        final String orderBy = MediaStore.Images.Media._ID;

        Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];

        for (int i = 0; i < this.count; i++) {
            //Image position
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            //Image path
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = imagecursor.getString(dataColumnIndex);

        }

        imageAdapter = new ImageAdapter();
        grdImages.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        imagecursor.close();

        grdImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), "imagePreview / "+arrPath[position], Toast.LENGTH_LONG).show();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {

                    Log.d("SelectedImages", selectImages);
                    Intent i = new Intent();
                    i.putExtra("data", selectImages);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }

    /**
     * Class method
     */

    /**
     * This method used to set bitmap.
     * @param iv represented ImageView
     * @param id represented id
     */

    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }

    /**
     * List adapter
     * @author tasol
     */

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_image_gallery_item, null);
                holder.photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
                holder.photoImageViewOverlay = (ImageView) convertView.findViewById(R.id.photoImageViewOverlay);
                holder.photoCount=(TextView) convertView.findViewById(R.id.photoCount);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.photoImageView.setId(position);
            holder.photoCount.setId(position);

            holder.photoImageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    int id = v.getId();
                    if (thumbnailsselection[id]) {

                        for (int i = 0; i < imageUpdates.size(); i++) {

                            ImageUrl imageUpdate = imageUpdates.get(i);
                            if (imageUpdate.getUploadImageUrl() == String.valueOf(id)) {

                                System.out.println("remove image");

                                imageUpdates.remove(i);
                                update_count--;
                                holder.isSelect = false;
                                holder.photoCount.setVisibility(View.GONE);
                                holder.photoCount.setText(String.valueOf(update_count));
                                thumbnailsselection[id] = false;
                                holder.photoImageViewOverlay.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }
                        }

                    } else {

                        if (update_count < max_count) {
                            ImageUrl imageUpdate = new ImageUrl();

                            System.out.println("add image");

                            imageUpdate.setUploadImageUrl(String.valueOf(v.getId()));
                            imageUpdates.add(imageUpdate);
                            update_count++;
                            holder.photoCount.setVisibility(View.VISIBLE);
                            holder.photoCount.setText(String.valueOf(imageUpdates.size()));
                            thumbnailsselection[id] = true;
                            holder.photoImageViewOverlay.setVisibility(View.VISIBLE);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
            try {
                setBitmap(holder.photoImageView, ids[position]);


            } catch (Throwable e) {
            }

            for (int i = 0; i < imageUpdates.size(); i++) {

                ImageUrl imageUpdate = imageUpdates.get(i);
                if (imageUpdate.getUploadImageUrl() == String.valueOf(holder.id) ) {
                    holder.isSelect = true;
                    holder.value = i+1;
                }
            }

            if(holder.isSelect == true){

                holder.photoCount.setVisibility(View.VISIBLE);
                holder.photoCount.setText(String.valueOf( holder.value));

            }else{

                holder.photoCount.setVisibility(View.GONE);
                holder.photoCount.setText("");
            }

            holder.id = position;
            return convertView;
        }
    }

    /**
     * Inner class
     * @author tasol
     */
    class ViewHolder {
        ImageView photoImageView;
        ImageView photoImageViewOverlay;
        TextView photoCount;
        boolean isSelect;
        int value;
        int id;
    }
}
