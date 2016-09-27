package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdi.castivate.model.VideoUrl;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("deprecation")

public class CastingCustomVideoGallery extends Activity {

    private GridView grdVideos;
    private TextView btnSelectV;
    private VideoAdapter videoAdapter;
    private String[] arrPath;
    private String[] duration;

    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;

    private int max_count=4;
    private  int update_count = 0;

    private ArrayList<VideoUrl> videoUrls = new ArrayList<VideoUrl>();

    Cursor imagecursor;
    private LinearLayout custom_video_gallery_back_icon;
    Context context;

    /**
     * Overrides methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_video_gallery);

        context=this;

        grdVideos= (GridView) findViewById(R.id.grdVideos);
        btnSelectV= (TextView) findViewById(R.id.btnSelectV);
        custom_video_gallery_back_icon=(LinearLayout) findViewById(R.id.custom_video_gallery_back_icon);
        custom_video_gallery_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent Intent = getIntent();
        update_count= Intent.getIntExtra("update_count", 0);

        final String[] columns = {

                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DURATION
        };

        final String orderBy = MediaStore.Video.Media._ID;
        imagecursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Video.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        this.duration = new String[this.count];
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];

        for (int i = 0; i < this.count; i++) {
            //Video position
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            //Video path
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Video.Media.DATA);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
            //Video duration
            int durationColumnIndex = imagecursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            duration[i]=imagecursor.getString(durationColumnIndex);
        }

        videoAdapter = new VideoAdapter();
        grdVideos.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
        imagecursor.close();

        btnSelectV.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectVideos = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectVideos = selectVideos + arrPath[i] + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one video", Toast.LENGTH_LONG).show();
                } else {

                    Log.d("selectVideos", selectVideos);
                    Intent i = new Intent();
                    i.putExtra("data", selectVideos);
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
                return MediaStore.Video.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
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

    public class VideoAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public VideoAdapter() {
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

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;

            if (view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.custom_video_gallery_item, null);
                holder.videoImageView = (ImageView) view.findViewById(R.id.videoImageView);
                holder.videoImageViewOverlay = (ImageView) view.findViewById(R.id.videoImageViewOverlay);
                holder.videoCount=(TextView) view.findViewById(R.id.videoCount);
                holder.videoDuration=(TextView) view.findViewById(R.id.videoDuration);


                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.videoImageView.setId(position);
            holder.videoCount.setId(position);
            holder.videoDuration.setId(position);

            holder.videoImageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    int id = v.getId();
                    if (thumbnailsselection[position]) {
                        for (int i = 0; i < videoUrls.size(); i++) {
                            VideoUrl videoUrl = videoUrls.get(i);
                            if (Objects.equals(videoUrl.getUploadVideoUrl(), String.valueOf(position))) {
                                System.out.println("remove video");

                                videoUrls.remove(i);
                                update_count--;

                                holder.isSelect = false;
                                holder.videoCount.setVisibility(View.GONE);
                                holder.videoCount.setText(String.valueOf(update_count));
                                thumbnailsselection[position] = false;
                                holder.videoImageViewOverlay.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }
                        }
                    } else {

                        if (update_count < max_count) {

                            File file = new File(arrPath[position]);

                            long length = file.length();

                            length = length/(1024*1024);

                            System.out.println("File size : "+length);

                            if(length < 10)
                            {
                                System.out.println("File Size :"+length);
                                System.out.println("File Size Allowed");

                                VideoUrl videoUrl = new VideoUrl();
                                System.out.println("add video");

                                videoUrl.setUploadVideoUrl(String.valueOf(position));
                                videoUrls.add(videoUrl);
                                update_count++;
                                holder.videoCount.setVisibility(View.VISIBLE);
                                holder.videoCount.setText(String.valueOf(videoUrls.size()));
                                thumbnailsselection[position] = true;
                                holder.videoImageViewOverlay.setVisibility(View.VISIBLE);
                                notifyDataSetChanged();
                            }
                            else
                            {
                                System.out.println("File Size :"+length);
                                System.out.println("File Size Not Allowed");
                                showVideoAlert();
                            }
                        }
                    }
                }
            });

            try{
                long timeInmillisec = Long.parseLong(duration[position] );
                long duration = timeInmillisec / 1000;
                long hours = duration / 3600;
                long minutes = (duration - hours * 3600) / 60;
                long seconds = duration - (hours * 3600 + minutes * 60);

                holder.videoDuration.setText(String.format("%02d", minutes) +":"+String.format("%02d", seconds));

            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }

            try {
                setBitmap(holder.videoImageView, ids[position]);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            holder.isSelect = false;
            for (int i = 0; i < videoUrls.size(); i++) {

                VideoUrl videoUrl = videoUrls.get(i);
                if (Objects.equals(videoUrl.getUploadVideoUrl(), String.valueOf(position))) {
                    holder.isSelect = true;
                    holder.value = i+1;
                }
            }

            if(holder.isSelect){
                holder.videoImageViewOverlay.setVisibility(View.VISIBLE);
                holder.videoCount.setVisibility(View.VISIBLE);
                holder.videoCount.setText(String.valueOf( holder.value));
            }else{
                holder.videoImageViewOverlay.setVisibility(View.GONE);
                holder.videoCount.setVisibility(View.GONE);
                holder.videoCount.setText("");
            }

            holder.id = position;
            return view;
        }
    }

    /**
     * Inner class
     * @author tasol
     */
    class ViewHolder {
        ImageView videoImageView;
        ImageView videoImageViewOverlay;
        TextView videoCount;
        TextView videoDuration;
        boolean isSelect;
        int value;
        int id;
    }

    private void showVideoAlert()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Video File");

        // set dialog message
        alertDialogBuilder
                .setMessage("File Size Not Allowed")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
