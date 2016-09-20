package com.sdi.castivate;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.sdi.castivate.adapter.DriveListAdapter;
import com.sdi.castivate.model.DriveModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    Context context;
    private GoogleAccountCredential mCredential;
    static final int 				REQUEST_ACCOUNT_PICKER = 1;
    static final int 				REQUEST_AUTHORIZATION = 2;
    private static Drive mService;
    private List<File> mResultList;
    private DriveListAdapter driveListAdapter;


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

    private String convertMimeType(String mimeType)
    {
        String mimeTypeConvert="";
        if(mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
        mimeTypeConvert ="docx";
        else if (mimeType.equals("application/msword"))
            mimeTypeConvert ="docx";
        else if (mimeType.equals("application/pdf"))
            mimeTypeConvert ="pdf";
        else if (mimeType.equals("application/rtf"))
            mimeTypeConvert ="rtf";
        else if (mimeType.equals("application/plain"))
            mimeTypeConvert ="txt";
        else if (mimeType.equals("application/vnd.oasis.opendocument.text"))
            mimeTypeConvert ="odt";

        return mimeTypeConvert;
    }
}
