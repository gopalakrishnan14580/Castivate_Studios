package com.sdi.castivate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.model.fileUrlModel;
import com.sdi.castivate.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by twilightuser on 1/9/16.
 */
@SuppressWarnings({"deprecation","unchecked"})
@SuppressLint({ "ResourceAsColor", "InlinedApi", "ShowToast", "UseSparseArrays" })
public class CastingFileUpload extends Activity {

    private ImageView imageViewOne,imageViewTwo,imageViewThree,imageViewFour,
                      videoViewOne,videoViewTwo,videoViewThree,videoViewFour;
    private ImageView delImageViewOne,delImageViewTwo,delImageViewThree,delImageViewFour,
                      delVideoViewOne,delVideoViewTwo,delVideoViewThree,delVideoViewFour;
    private ImageView VideoViewIconOne,VideoViewIconTwo,VideoViewIconThree,VideoViewIconFour;

    private LinearLayout addPhoto,addVideo;
    private String photoChoose,videoChoose;
    private final int PICK_IMAGE_MULTIPLE =1;
    private final int PICK_VIDEO_MULTIPLE =2;
    private final int TAKE_PICTURE_REQUEST =3;
    private final int TAKE_VIDEO_REQUEST =4;

    private ArrayList<fileUrlModel> imageUrls = new ArrayList<fileUrlModel>();
    private ArrayList<fileUrlModel> videoUrls = new ArrayList<fileUrlModel>();

    private String video_path,image_path;
    private LinearLayout casting_file_upload_back_icon;
    private TextView casting_file_upload_done;

    private final static int PERMISSION_RQ = 84;
    private ArrayList<CastingDetailsModel> selectedCastingDetailsModels = new ArrayList<CastingDetailsModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_file_upload);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to save videos in external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
        }

        try{
            selectedCastingDetailsModels = (ArrayList<CastingDetailsModel>) getIntent().getSerializableExtra("selectedCastingDetailsModels");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //Add photo and video action
        addPhoto=(LinearLayout) findViewById(R.id.add_Photo);
        addVideo=(LinearLayout) findViewById(R.id.add_Video);

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

        //Casting file upload back
        casting_file_upload_back_icon=(LinearLayout) findViewById(R.id.casting_file_upload_back_icon);
        casting_file_upload_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Casting file upload done.
        casting_file_upload_done=(TextView) findViewById(R.id.casting_file_upload_done);
        casting_file_upload_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageUrls.size()>0 && videoUrls.size()>0)
                {
                    Toast.makeText(CastingFileUpload.this, "Casting file upload done.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CastingFileUpload.this,CastingResumeUpload.class);
                    intent.putExtra("imageUrls", imageUrls);
                    intent.putExtra("videoUrls", videoUrls);
                    intent.putExtra("selectedCastingDetailsModels",selectedCastingDetailsModels);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(CastingFileUpload.this, "Minimum one photo and video file can be upload. ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Add photo choose Gallery or Capture
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Take Photo", "Photo Library","Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CastingFileUpload.this);
                builder.setTitle("Add Photo");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(CastingFileUpload.this);

                        if (items[item].equals("Take Photo")) {
                            photoChoose = "Take Photo";
                            /*if (result)*/ cameraIntent();

                        } else if (items[item].equals("Photo Library")) {
                            photoChoose = "Photo Library";
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
                final CharSequence[] items = {"Take Video", "Photo Library","Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CastingFileUpload.this);
                builder.setTitle("Add Video");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(CastingFileUpload.this);

                        if (items[item].equals("Take Video")) {
                            videoChoose = "Take Video";
                            /*if (result)*/ recordVideo();

                        } else if (items[item].equals("Photo Library")) {
                            videoChoose = "Photo Library";
                            if (result) videoGallery();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

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

    private void photoGallery() {

        Intent intent = new Intent(CastingFileUpload.this,CastingCustomPhotoGallery.class);
        intent.setType("image/*");
        intent.putExtra("update_count",imageUrls.size());
        startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
    }

    private void videoGallery() {
        Intent intent = new Intent(CastingFileUpload.this,CastingCustomVideoGallery.class);
        intent.setType("video/*");
        intent.putExtra("update_count",videoUrls.size());
        startActivityForResult(intent, PICK_VIDEO_MULTIPLE);
    }

    private void cameraIntent() {

        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
            saveDir.mkdirs();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .stillShot()
                .labelConfirm(R.string.mcam_use_stillshot);
        materialCamera.start(TAKE_PICTURE_REQUEST);
    }

    private void recordVideo() {

        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "CastivateFiles");
            saveDir.mkdirs();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(false)
                .allowRetry(true)
                .defaultToFrontFacing(true)
                .labelConfirm(R.string.mcam_use_video);
        materialCamera.start(TAKE_VIDEO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // Sample was denied WRITE_EXTERNAL_STORAGE permission
            Toast.makeText(this, "Videos will be saved in a cache directory instead of an external storage directory since permission was denied.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_MULTIPLE)
            {
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                onSelectFromGalleryResult(imagesPath);
            }

            if(requestCode == PICK_VIDEO_MULTIPLE)
            {
                String[] videosPath = data.getStringExtra("data").split("\\|");
                onSelectFromVideoGalleryResult(videosPath);
            }

            if (requestCode == TAKE_VIDEO_REQUEST)
            {
                    final File file = new File(data.getData().getPath());
                    video_path=file.getAbsolutePath();
                    saveVideoImage();
            }

            if (requestCode == TAKE_PICTURE_REQUEST)
            {
                final File file = new File(data.getData().getPath());
                image_path=file.getAbsolutePath();
                saveImagePath();
            }
        }
    }

    private void saveImagePath()
    {
        fileUrlModel imageUrl = new fileUrlModel();
        imageUrl.setFileUrl(image_path);
        imageUrls.add(imageUrl);

        addImages();
    }
    private void saveVideoImage()
    {
        fileUrlModel videoUrl = new fileUrlModel();
        videoUrl.setFileUrl(video_path);
        videoUrls.add(videoUrl);

        showVideoThumbnail();
    }

    private void onSelectFromGalleryResult(String[] imagesPath) {

        List<String> imageList = Arrays.asList(imagesPath);

        for(String s:imageList)
        {
            fileUrlModel imageUrl = new fileUrlModel();
            imageUrl.setFileUrl(s);
            imageUrls.add(imageUrl);
        }
        addImages();
    }

    private void addImages() {

        if (imageUrls.size() < 4) addPhoto.setEnabled(true);
        else addPhoto.setEnabled(false);

        for (int i = 1; i <= 4; i++) {
            try {
                switch (i) {
                    case 1:
                        if (imageUrls.get(0) != null) {
                            imageViewOne.setImageBitmap(BitmapFactory.decodeFile(imageUrls.get(0).getFileUrl()));
                            delImageViewOne.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 2:
                        if (imageUrls.get(1) != null) {
                            imageViewTwo.setImageBitmap(BitmapFactory.decodeFile(imageUrls.get(1).getFileUrl()));
                            delImageViewTwo.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 3:
                        if (imageUrls.get(2) != null) {
                            imageViewThree.setImageBitmap(BitmapFactory.decodeFile(imageUrls.get(2).getFileUrl()));
                            delImageViewThree.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 4:
                        if (imageUrls.get(3) != null) {
                            imageViewFour.setImageBitmap(BitmapFactory.decodeFile(imageUrls.get(3).getFileUrl()));
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

        List<String> videoList = Arrays.asList(videosPath);

        for(String s:videoList)
        {
            fileUrlModel videoUrl = new fileUrlModel();
            videoUrl.setFileUrl(s);
            videoUrls.add(videoUrl);
        }
        showVideoThumbnail();
    }

    private void showVideoThumbnail() {

        if (videoUrls.size() < 4) addVideo.setEnabled(true);
        else addVideo.setEnabled(false);

        for (int i = 1; i <= 4; i++) {
            try {
                switch (i) {
                    case 1:
                        if (videoUrls.get(0) != null) {
                            videoViewOne.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoUrls.get(0).getFileUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                            delVideoViewOne.setVisibility(View.VISIBLE);
                            VideoViewIconOne.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 2:
                        if (videoUrls.get(1) != null) {
                            videoViewTwo.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoUrls.get(1).getFileUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                            delVideoViewTwo.setVisibility(View.VISIBLE);
                            VideoViewIconTwo.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 3:
                        if (videoUrls.get(2) != null) {
                            videoViewThree.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoUrls.get(2).getFileUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                            delVideoViewThree.setVisibility(View.VISIBLE);
                            VideoViewIconThree.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 4:
                        if (videoUrls.get(3) != null) {
                            videoViewFour.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoUrls.get(3).getFileUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
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
