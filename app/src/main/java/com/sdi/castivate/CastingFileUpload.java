package com.sdi.castivate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sdi.castivate.model.ImageUrl;
import com.sdi.castivate.model.VideoUrl;
import com.sdi.castivate.utils.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by twilightuser on 1/9/16.
 */
public class CastingFileUpload extends Activity {

    private ImageView imageViewOne,imageViewTwo,imageViewThree,imageViewFour,
                      videoViewOne,videoViewTwo,videoViewThree,videoViewFour;
    private ImageView delImageViewOne,delImageViewTwo,delImageViewThree,delImageViewFour,
                      delVideoViewOne,delVideoViewTwo,delVideoViewThree,delVideoViewFour;
    private ImageView VideoViewIconOne,VideoViewIconTwo,VideoViewIconThree,VideoViewIconFour;

    private RelativeLayout addPhoto,addVideo;
    private String photoChoose,videoChoose;
    private final int PICK_IMAGE_MULTIPLE =1;
    private final int PICK_VIDEO_MULTIPLE =2;
    private final int TAKE_PICTURE_REQUEST =3;

    private ArrayList<ImageUrl> imageUrls = new ArrayList<ImageUrl>();
    private ArrayList<VideoUrl> videoUrls;

    private Bitmap takePictureBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_file_upload);

        //Add photo and video action
        addPhoto=(RelativeLayout) findViewById(R.id.add_Photo);
        addVideo=(RelativeLayout) findViewById(R.id.add_Video);

        //Photo upload image view
        imageViewOne=(ImageView) findViewById(R.id.imageViewOne);
        imageViewTwo=(ImageView) findViewById(R.id.imageViewTwo);
        imageViewThree=(ImageView) findViewById(R.id.imageViewThree);
        imageViewFour=(ImageView) findViewById(R.id.imageViewFour);

        //Video upload image view
        videoViewOne=(ImageView) findViewById(R.id.videoViewOne);
        videoViewTwo=(ImageView) findViewById(R.id.videoViewTwo);
        videoViewThree=(ImageView) findViewById(R.id.videoViewThree);
        videoViewFour=(ImageView) findViewById(R.id.videoViewFour);

        //Delete Photo upload image view
        delImageViewOne=(ImageView) findViewById(R.id.delImageViewOne);
        delImageViewTwo=(ImageView) findViewById(R.id.delImageViewTwo);
        delImageViewThree=(ImageView) findViewById(R.id.delImageViewThree);
        delImageViewFour=(ImageView) findViewById(R.id.delImageViewFour);

        //Delete Video upload image view
        delVideoViewOne=(ImageView) findViewById(R.id.delVideoViewOne);
        delVideoViewTwo=(ImageView) findViewById(R.id.delVideoViewTwo);
        delVideoViewThree=(ImageView) findViewById(R.id.delVideoViewThree);
        delVideoViewFour=(ImageView) findViewById(R.id.delVideoViewFour);

        // Video icon display for video image
        VideoViewIconOne=(ImageView) findViewById(R.id.VideoViewIconOne);
        VideoViewIconTwo=(ImageView) findViewById(R.id.VideoViewIconTwo);
        VideoViewIconThree=(ImageView) findViewById(R.id.VideoViewIconThree);
        VideoViewIconFour=(ImageView) findViewById(R.id.VideoViewIconFour);

        //Invisible video icon display for video image
        VideoViewIconOne.setVisibility(View.INVISIBLE);
        VideoViewIconTwo.setVisibility(View.INVISIBLE);
        VideoViewIconThree.setVisibility(View.INVISIBLE);
        VideoViewIconFour.setVisibility(View.INVISIBLE);

        //Invisible delete image icon for photo upload
        delImageViewOne.setVisibility(View.INVISIBLE);
        delImageViewTwo.setVisibility(View.INVISIBLE);
        delImageViewThree.setVisibility(View.INVISIBLE);
        delImageViewFour.setVisibility(View.INVISIBLE);

        //Invisible delete image icon for video upload
        delVideoViewOne.setVisibility(View.INVISIBLE);
        delVideoViewTwo.setVisibility(View.INVISIBLE);
        delVideoViewThree.setVisibility(View.INVISIBLE);
        delVideoViewFour.setVisibility(View.INVISIBLE);


        //Add photo choose Gallery or Capture
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Capture", "Gallery","Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CastingFileUpload.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(CastingFileUpload.this);

                        if (items[item].equals("Capture")) {
                            photoChoose = "Capture";
                            if (result) cameraIntent();

                        } else if (items[item].equals("Gallery")) {
                            photoChoose = "Gallery";
                            if (result) photoGallery();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        //Add video choose Gallery or Record
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Record", "Gallery","Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CastingFileUpload.this);
                builder.setTitle("Add Video!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(CastingFileUpload.this);

                        if (items[item].equals("Record")) {
                            videoChoose = "Record";
                            if (result) recordVideo();

                        } else if (items[item].equals("Gallery")) {
                            videoChoose = "Gallery";
                            if (result) videoGallery();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


        /*try {

            if (imageUrls.size() < 4) addPhoto.setEnabled(true);
            else addPhoto.setEnabled(false);

        }catch (Exception e)
        {
            e.printStackTrace();
        }*/

        //Remove video position for photo view image icon
        delImageViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(0);
            }
        });

        delImageViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(1);
            }
        });

        delImageViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(2);
            }
        });

        delImageViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(3);
            }
        });


        //Remove video position for video view image icon
        delVideoViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(0);
            }
        });

        delVideoViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(1);
            }
        });

        delVideoViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(2);
            }
        });

        delVideoViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(3);
            }
        });
    }

    private void cameraIntent() {

        startActivityForResult(new Intent(CastingFileUpload.this, CastingCustomCamera.class), TAKE_PICTURE_REQUEST);
    }

    private void photoGallery() {

        Intent intent = new Intent(CastingFileUpload.this,CastingCustomPhotoGallery.class);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
    }

    private void videoGallery() {
        Intent intent = new Intent(CastingFileUpload.this,CastingCustomVideoGallery.class);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
    }

    private void recordVideo() {
        /*Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_VIDEO));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, VIDEO_CAPTURE);*/

        Intent intent = new Intent(CastingFileUpload.this,CastingCustomVideoCamera.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_MULTIPLE){

                String[] imagesPath = data.getStringExtra("data").split("\\|");

                onSelectFromGalleryResult(imagesPath);
            }

            if(requestCode == PICK_VIDEO_MULTIPLE)
            {
                String[] videosPath = data.getStringExtra("data").split("\\|");

                onSelectFromVideoGalleryResult(videosPath);
            }

            if(requestCode == TAKE_PICTURE_REQUEST)
            {
                // Recycle the previous bitmap.
                if (takePictureBitmap != null) {
                    takePictureBitmap.recycle();
                    takePictureBitmap = null;
                }
                Bundle extras = data.getExtras();
                byte[] cameraData = extras.getByteArray(CastingCustomCamera.EXTRA_CAMERA_DATA);
                if (cameraData != null) {
                    takePictureBitmap = BitmapFactory.decodeByteArray(cameraData, 0, cameraData.length);

                    saveImageDisplay();
                }
            }
        }
    }

    private void saveImageDisplay()
    {
        File saveFile = openFileForImage();
        if (saveFile != null) {
            saveImageToFile(saveFile);
        } else {
            Toast.makeText(CastingFileUpload.this, "Unable to open file for saving image.",
                    Toast.LENGTH_LONG).show();
        }
    }
    private File openFileForImage()
    {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "sdi.com.castivate");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + "image_" +
                        dateFormat.format(new Date()) + ".png");
            }
        }
        return null;
    }
    private void saveImageToFile(File file) {
        if (takePictureBitmap != null) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                if (!takePictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                    Toast.makeText(CastingFileUpload.this, "Unable to save image to file.",
                            Toast.LENGTH_LONG).show();
                } else {
                    /*Toast.makeText(CastingFileUpload.this, "Saved image to: " + file.getPath(),
                            Toast.LENGTH_LONG).show();*/

                    ImageUrl imageUrl = new ImageUrl();

                    imageUrl.setUploadImageUrl(file.getPath());

                    imageUrls.add(imageUrl);

                    addImages();
                }
                outStream.close();
            } catch (Exception e) {
                Toast.makeText(CastingFileUpload.this, "Unable to save image to file.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onSelectFromGalleryResult(String[] imagesPath) {

        //imageUrls.clear();

        List<String> imageList = Arrays.asList(imagesPath);

        for(String s:imageList)
        {
            ImageUrl imageUrl = new ImageUrl();

            imageUrl.setUploadImageUrl(s);

            imageUrls.add(imageUrl);
        }

        addImages();
    }

    private void addImages() {

        if (imageUrls.size() < 4) addPhoto.setEnabled(true);
        else addPhoto.setEnabled(false);

        Toast.makeText(CastingFileUpload.this, "imageUrls" + imageUrls.size(), Toast.LENGTH_SHORT).show();

        for (int i = 1; i <= 4; i++) {
            try {
                switch (i) {
                    case 1:
                        if (imageUrls.get(0) != null) {
                            imageViewOne.setImageBitmap(BitmapFactory.decodeFile(imageUrls.get(0).getUploadImageUrl()));
                            delImageViewOne.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 2:
                        if (imageUrls.get(1) != null) {
                            imageViewTwo.setImageBitmap(BitmapFactory.decodeFile(imageUrls.get(1).getUploadImageUrl()));
                            delImageViewTwo.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 3:
                        if (imageUrls.get(2) != null) {
                            imageViewThree.setImageBitmap(BitmapFactory.decodeFile(imageUrls.get(2).getUploadImageUrl()));
                            delImageViewThree.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 4:
                        if (imageUrls.get(3) != null) {
                            imageViewFour.setImageBitmap(BitmapFactory.decodeFile(imageUrls.get(3).getUploadImageUrl()));
                            delImageViewFour.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void removeImage(int arrayId) {

        imageUrls.remove(arrayId);

        imageViewOne.setImageBitmap(null);
        imageViewTwo.setImageBitmap(null);
        imageViewThree.setImageBitmap(null);
        imageViewFour.setImageBitmap(null);

        delImageViewOne.setVisibility(View.INVISIBLE);
        delImageViewTwo.setVisibility(View.INVISIBLE);
        delImageViewThree.setVisibility(View.INVISIBLE);
        delImageViewFour.setVisibility(View.INVISIBLE);

        addImages();
    }

    private void onSelectFromVideoGalleryResult(String[] videosPath) {

        videoUrls= new ArrayList<VideoUrl>();

        videoUrls.clear();

        List<String> videoList = Arrays.asList(videosPath);

        for(String s:videoList)
        {
            VideoUrl videoUrl = new VideoUrl();

            videoUrl.setUploadVideoUrl(s);

            videoUrls.add(videoUrl);
        }

        showVideoThumbnail();

    }

    private void showVideoThumbnail() {

        if (videoUrls.size() < 4) addVideo.setEnabled(true);
        else addVideo.setEnabled(false);

        Toast.makeText(CastingFileUpload.this, "videoUrls = " + videoUrls.size(), Toast.LENGTH_SHORT).show();

        for (int i = 1; i <= 4; i++) {
            try {
                switch (i) {
                    case 1:
                        if (videoUrls.get(0) != null) {
                            videoViewOne.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoUrls.get(0).getUploadVideoUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                            delVideoViewOne.setVisibility(View.VISIBLE);
                            VideoViewIconOne.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 2:
                        if (videoUrls.get(1) != null) {
                            videoViewTwo.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoUrls.get(1).getUploadVideoUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                            delVideoViewTwo.setVisibility(View.VISIBLE);
                            VideoViewIconTwo.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 3:
                        if (videoUrls.get(2) != null) {
                            videoViewThree.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoUrls.get(2).getUploadVideoUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                            delVideoViewThree.setVisibility(View.VISIBLE);
                            VideoViewIconThree.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 4:
                        if (videoUrls.get(3) != null) {
                            videoViewFour.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoUrls.get(3).getUploadVideoUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                            delVideoViewFour.setVisibility(View.VISIBLE);
                            VideoViewIconFour.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void removeVideo(int arrayId) {

        videoUrls.remove(arrayId);

        videoViewOne.setImageBitmap(null);
        videoViewTwo.setImageBitmap(null);
        videoViewThree.setImageBitmap(null);
        videoViewFour.setImageBitmap(null);

        delVideoViewOne.setVisibility(View.INVISIBLE);
        delVideoViewTwo.setVisibility(View.INVISIBLE);
        delVideoViewThree.setVisibility(View.INVISIBLE);
        delVideoViewFour.setVisibility(View.INVISIBLE);

        VideoViewIconOne.setVisibility(View.INVISIBLE);
        VideoViewIconTwo.setVisibility(View.INVISIBLE);
        VideoViewIconThree.setVisibility(View.INVISIBLE);
        VideoViewIconFour.setVisibility(View.INVISIBLE);

        showVideoThumbnail();
    }
}
