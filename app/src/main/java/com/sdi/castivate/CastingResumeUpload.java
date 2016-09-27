package com.sdi.castivate;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.sdi.castivate.adapter.DriveListAdapter;
import com.sdi.castivate.model.CastingDetailsModel;
import com.sdi.castivate.model.DriveModel;
import com.sdi.castivate.model.fileUrlModel;
import com.sdi.castivate.utils.HttpUri;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.MultipartUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SuppressWarnings({"deprecation","unchecked"})
@SuppressLint({ "ResourceAsColor", "InlinedApi", "ShowToast", "UseSparseArrays" })
public class CastingResumeUpload extends Activity {

    private LinearLayout casting_resume_upload_back_icon;
    private TextView casting_resume_upload_done,drive_files_display;
    private RelativeLayout resume_upload;
    private LinearLayout upload_hide,drive_files_display_lay;
    private ListView drive_files;
    private ImageView drive_files_delete;

    private String cloudChoose;

    private ArrayList<DriveModel> googleDriveModels;
    private ArrayList<DriveModel> googleDriveModelsSelected;
    private ArrayList<fileUrlModel> imageUrls = new ArrayList<fileUrlModel>();
    private ArrayList<fileUrlModel> videoUrls = new ArrayList<fileUrlModel>();
    private ArrayList<fileUrlModel> resumeUrls = new ArrayList<fileUrlModel>();
    private ArrayList<fileUrlModel> zipFiles = new ArrayList<fileUrlModel>();

    Context context;
    private GoogleAccountCredential mCredential;
    static final int 				REQUEST_ACCOUNT_PICKER = 1;
    static final int 				REQUEST_AUTHORIZATION = 2;
    private static Drive mService;
    private List<File> mResultList;
    private DriveListAdapter driveListAdapter;
    private String 					mDLVal;
    private  String zipName;

    private ArrayList<CastingDetailsModel> selectedCastingDetailsModels = new ArrayList<CastingDetailsModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_resume_upload);

        context=getApplicationContext();
        //Add resume upload action
        resume_upload=(RelativeLayout) findViewById(R.id.resume_upload);
        upload_hide=(LinearLayout) findViewById(R.id.upload_hide);
        drive_files=(ListView) findViewById(R.id.drive_files);
        drive_files.setVisibility(View.GONE);

        drive_files_display=(TextView) findViewById(R.id.drive_files_display);
        drive_files_display.setVisibility(View.GONE);

        drive_files_delete=(ImageView) findViewById(R.id.drive_files_delete);
        drive_files_display_lay=(LinearLayout) findViewById(R.id.drive_files_display_lay);
        drive_files_display_lay.setVisibility(View.GONE);

        try{
            imageUrls = (ArrayList<fileUrlModel>) getIntent().getSerializableExtra("imageUrls");
            videoUrls = (ArrayList<fileUrlModel>) getIntent().getSerializableExtra("videoUrls");
            selectedCastingDetailsModels = (ArrayList<CastingDetailsModel>) getIntent().getSerializableExtra("selectedCastingDetailsModels");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Casting resume upload back
        casting_resume_upload_back_icon=(LinearLayout) findViewById(R.id.casting_resume_upload_back_icon);
        casting_resume_upload_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Casting resume upload done.
        casting_resume_upload_done=(TextView) findViewById(R.id.casting_resume_upload_done);
        casting_resume_upload_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(CastingResumeUpload.this, "Casting Resume upload done.", Toast.LENGTH_SHORT).show();
                if(googleDriveModelsSelected.size()>0)
                {
                    System.out.println("Download file");
                    downloadItemFromList();
                }


            }
        });

        //Resume upload from cloud
        resume_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] items = {"Dropbox", "Google Drive","OneDrive","Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CastingResumeUpload.this);
                builder.setTitle("Resume upload");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Dropbox")) {
                            cloudChoose = "Dropbox";
                            dropboxApi();

                        } else if (items[item].equals("Google Drive")) {
                            cloudChoose = "Google Drive";
                            googleDriveApi();

                        } else if (items[item].equals("OneDrive")) {
                            cloudChoose = "OneDrive";
                            oneDriveApi();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

    }

    //call drop box cloud
    private void dropboxApi()
    {
        Toast.makeText(CastingResumeUpload.this, "DropboxApi", Toast.LENGTH_SHORT).show();

    }
    //call google drive cloud
    private void googleDriveApi()
    {
        Toast.makeText(CastingResumeUpload.this, "GoogleDriveApi", Toast.LENGTH_SHORT).show();

        mCredential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE));
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

    }
    //call one drive cloud
    private void oneDriveApi()
    {
        Toast.makeText(CastingResumeUpload.this, "OneDriveApi", Toast.LENGTH_SHORT).show();

        //OneDriveClient oneDriveClient
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
                {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        mService = getDriveService(mCredential);
                        getDriveContents();
                    }
                }
                break;
        }
    }
    private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(context.getPackageName())
                .build();
    }
    private void getDriveContents()
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                mResultList = new ArrayList<File>();
                com.google.api.services.drive.Drive.Files f1 = mService.files();
                com.google.api.services.drive.Drive.Files.List request = null;

                assert request != null;
                do
                {
                    try
                    {
                        request = f1.list();
                        request.setFields("*");
                        request.setQ("");
                        //request.setQ("mimeType='application/vnd.google-apps.document' or mimeType='text/plain' or mimeType='application/rtf' or mimeType='application/vnd.oasis.opendocument.text' or mimeType='application/pdf' or mimeType='application/vnd.openxmlformats-officedocument.wordprocessingml.document' or mimeType='application/msword' or mimeType='application/vnd.google-apps.folder'");
                        com.google.api.services.drive.model.FileList fileList = request.execute();

                        mResultList.addAll(fileList.getItems());
                        //request.setPageToken(fileList.getNextPageToken());
                    } catch (UserRecoverableAuthIOException e) {
                        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (request != null)
                        {
                            request.setPageToken(null);
                        }
                    }
                } while (request.getPageToken() !=null && request.getPageToken().length() > 0);

                populateListView();
            }
        });
        t.start();
    }

    private void populateListView()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                googleDriveModels = new ArrayList<DriveModel>();
                for(File tmp : mResultList)
                {
                    if(tmp.getOriginalFilename() !=null) {

                        DriveModel driveModel = new DriveModel();
                        driveModel.setFileName(tmp.getOriginalFilename());
                        driveModel.setFileId(tmp.getId());
                        driveModel.setMimeType(tmp.getMimeType());
                        driveModel.setItemSize(String.valueOf(tmp.getFileSize()));
                        driveModel.setWebContentLink(tmp.getWebContentLink());
                        driveModel.setWebViewLink(tmp.getWebViewLink());
                        driveModel.setFileExtension(tmp.getFileExtension());

                        String pathExtension = "";

                        try {
                            pathExtension = tmp.getOriginalFilename().substring(tmp.getOriginalFilename().lastIndexOf(".")+1);
                        } catch (Exception e) {
                            pathExtension = "";
                        }

                        if (pathExtension.isEmpty()) {

                            if (tmp.getMimeType().equals("application/vnd.google-apps.document")) {
                                driveModel.setImgType("doc_file");
                            } else if (tmp.getMimeType().equals("application/pdf")) {
                                driveModel.setImgType("pdf_file");
                            } else if (tmp.getMimeType().equals("application/vnd.google-apps.spreadsheet")) {
                                driveModel.setImgType("excel_file");
                            } else {
                                driveModel.setImgType("folder");
                            }
                        } else if ((pathExtension.equals("jpg")) || (pathExtension.equals("jpeg")) || (pathExtension.equals("png"))) {
                            driveModel.setImgType("img_file");
                        } else if ((pathExtension.equals("pdf"))) {
                            driveModel.setImgType("pdf_file");
                        } else if ((pathExtension.equals("doc")) || (pathExtension.equals("docx")) || (pathExtension.equals("odt"))) {
                            driveModel.setImgType("doc_file");
                        } else if ((pathExtension.equals("txt")) || (pathExtension.equals("rtf"))) {
                            driveModel.setImgType("note_file");
                        } else {
                            driveModel.setImgType("unknown_file");
                        }
                            googleDriveModels.add(driveModel);
                    }
                }

                if(googleDriveModels.size() >0)
                {
                    upload_hide.setVisibility(View.GONE);
                    drive_files.setVisibility(View.VISIBLE);
                    driveListAdapter= new DriveListAdapter(context,googleDriveModels);
                    drive_files.setAdapter(driveListAdapter);
                    drive_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            googleDriveModelsSelected =new ArrayList<DriveModel>();

                            if(googleDriveModels.get(position).getImgType().equals("doc_file")||googleDriveModels.get(position).getImgType().equals("pdf_file") || googleDriveModels.get(position).getImgType().equals("folder"))
                            {
                                if(googleDriveModels.get(position).getImgType().equals("folder"))
                                {
                                    System.out.println("folder");
                                }
                                else {

                                    //if()

                                    long length = Long.parseLong(googleDriveModels.get(position).getItemSize());

                                    length = length/(1024*1024);

                                    System.out.println("File size : "+length);

                                    if(length < 2)
                                    {
                                        System.out.println("File Size :"+length);
                                        System.out.println("File Size Allowed");

                                        googleDriveModelsSelected.clear();
                                        googleDriveModelsSelected.add(googleDriveModels.get(position));
                                        System.out.println("Selected");
                                        drive_files.setVisibility(View.GONE);
                                        resume_upload.setVisibility(View.GONE);
                                        drive_files_display_lay.setVisibility(View.VISIBLE);
                                        upload_hide.setVisibility(View.VISIBLE);
                                        drive_files_display.setVisibility(View.VISIBLE);
                                        drive_files_display.setText(googleDriveModelsSelected.get(0).getFileName());
                                        drive_files_delete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                googleDriveModelsSelected.remove(0);
                                                System.out.println("deleted");
                                                drive_files_display_lay.setVisibility(View.GONE);
                                                resume_upload.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                    else {

                                        System.out.println("File Size :"+length);
                                        System.out.println("File Size Not Allowed");
                                        showAlert("File Size Not Allowed");
                                    }

                                }
                            }
                            else
                            {
                                System.out.println("alert");
                            }
                        }
                    });
                }
            }
        });
    }
    private void downloadItemFromList()
    {
        mDLVal = googleDriveModelsSelected.get(0).getFileName();

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for(File tmp : mResultList)
                {
                    if (tmp.getTitle().equalsIgnoreCase(mDLVal))
                    {
                        if (tmp.getDownloadUrl() != null && tmp.getDownloadUrl().length() >0)
                        {
                            try
                            {
                                com.google.api.client.http.HttpResponse resp =
                                        mService.getRequestFactory()
                                                .buildGetRequest(new GenericUrl(tmp.getDownloadUrl()))
                                                .execute();
                                InputStream iStream = resp.getContent();

                                try
                                {
                                    final java.io.File file = new java.io.File(Environment
                                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(),
                                            tmp.getOriginalFilename());

                                    fileUrlModel resumeUrl = new fileUrlModel();
                                    resumeUrl.setFileUrl(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/"+tmp.getTitle());
                                    resumeUrls.add(resumeUrl);

                                    storeFile(file, iStream);

                                    getFileList();

                                } finally {
                                    iStream.close();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        t.start();
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void storeFile(java.io.File file, InputStream iStream)
    {
        try
        {
            final OutputStream oStream = new FileOutputStream(file);
            try
            {
                try
                {
                    final byte[] buffer = new byte[1024];
                    int read;
                    while ((read = iStream.read(buffer)) != -1)
                    {
                        oStream.write(buffer, 0, read);
                    }
                    oStream.flush();
                } finally {
                    oStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFileList()
    {
       // System.out.println("imageUrls Size :"+imageUrls.size());
        //System.out.println("videoUrls Size :"+videoUrls.size());
        //System.out.println("resumeUrls Size :"+resumeUrls.size());

        zipFiles.addAll(imageUrls);
        zipFiles.addAll(videoUrls);
        zipFiles.addAll(resumeUrls);

        //System.out.println("zip Files Size : "+zipFiles.size());

        java.io.File dir = new java.io.File(Environment.getExternalStorageDirectory(),"/ZipFile.zip");
         zipName = dir.toString();

       // System.out.println("zip Files path : "+zipName);

        ArrayList<fileUrlModel> fileUrlModels = new ArrayList<fileUrlModel>(zipFiles);
        Compress c = new Compress(fileUrlModels, zipName);
        c.zip();
        uploadFiles();
    }

    public class Compress {

        private static final int BUFFER = 2048;
        ArrayList<fileUrlModel> _files;
        private String _zipFile;

        public Compress(ArrayList<fileUrlModel> files, String zipFile) {
            _files = files;
            _zipFile = zipFile;
        }

        public void zip() {
            try  {
                BufferedInputStream origin = null;
                FileOutputStream dest = new FileOutputStream(_zipFile);
                ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

                byte data[] = new byte[BUFFER];

                for(int i=0; i < _files.size(); i++) {
                    Log.v("Compress", "Adding: " + _files.get(i).getFileUrl());
                    FileInputStream fi = new FileInputStream(String.valueOf(_files.get(i).getFileUrl()));
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(_files.get(i).getFileUrl().substring(_files.get(i).getFileUrl().lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
                out.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    InputStream is = null;
    String json = "";
    JSONObject jObj = null;
    JSONArray jArr = null;
    StringBuilder sb;

    String Status;
    private void uploadFiles()
    {
        try {


           /* ArrayList<String> imgPaths = new ArrayList<String>();

            for (int i=0;i<zipFiles.size();i++){

                fileUrlModel fileUrlModel = new fileUrlModel();

            }
            String[] array = new String[zipFiles.size()];

            File sourceFile[] = new File[imgPaths.size()];
            for (int i=0;i<zipFiles.size();i++){
                sourceFile[i] = new File(imgPaths.get(i));
                // Toast.makeText(getApplicationContext(),imgPaths.get(i),Toast.LENGTH_SHORT).show();
            }
*/

            java.io.File zipFile = new java.io.File(zipName);

            MultipartUtility multipart = new MultipartUtility(HttpUri.CASTING_FILE_UPLOAD, "UTF-8");

            multipart.addFormField("userid", Library.androidUserID);
            multipart.addFormField("role_id", selectedCastingDetailsModels.get(0).roleId);
            multipart.addFormField("enthicity",selectedCastingDetailsModels.get(0).roleForEthnicity);
            multipart.addFormField("age_range",selectedCastingDetailsModels.get(0).ageRange);
            multipart.addFormField("gender",selectedCastingDetailsModels.get(0).roleForGender);

            multipart.addFilePart("uploads[]", zipFile);

           /* for(int j=0;imageUrls.size();j++)
            {
                java.io.File zipFile = new java.io.File(zipName);
            multipart.addFilePart("uploads[]", filimageUrls.get(j).getFileUrl());
            }*/

            //multipart.addFilePart("uploads1", zipFile);

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println("result : "+line);
                Status=line;
            }

            System.out.println("Server Response : "+Status);

            /*
            *
            * {
        "res": "Casting  inserted",
        "userid": "8",
        "message": "Casting  applied",
        "status": 200
}
*/
            try {

                JSONObject oneObject = new JSONObject(Status);

                if(oneObject.getString("status").equals("200")) {

                    System.out.println("Message ------------------------------> " + oneObject.getString("message"));

                    System.out.println("zipFile Delete : "+zipFile.delete());

                    castingApplayAlert(oneObject.getString("message"));

                }
                else
                {
                    System.out.println("File upload field ............... ");
                }
            }
            catch (JSONException e) {
                System.out.println("Exception : "+e.getMessage());
                // TODO: handle exception
            }

            //applyFlag ="1"

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void showAlert(String message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Alert");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
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

    private void castingApplayAlert(String message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Alert");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();

                    Intent intent = new Intent(CastingResumeUpload.this,CastingScreen.class);
                    startActivity(intent);
                    finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
