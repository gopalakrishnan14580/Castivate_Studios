package com.sdi.castivate;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import com.sdi.castivate.model.DriveModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CastingResumeUpload extends Activity {

    private LinearLayout casting_resume_upload_back_icon;
    private TextView casting_resume_upload_done;
    private RelativeLayout resume_upload;

    private String cloudChoose;

    private ArrayList<DriveModel> driveModels = new ArrayList<DriveModel>();
    Context context;
    private GoogleAccountCredential mCredential;
    static final int 				REQUEST_ACCOUNT_PICKER = 1;
    static final int 				REQUEST_AUTHORIZATION = 2;
    private static Drive mService;
    private List<File> mResultList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_resume_upload);

        context=getApplicationContext();
        //Add resume upload action
        resume_upload=(RelativeLayout) findViewById(R.id.resume_upload);

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

    /*
    * {
    [ODClient authenticatedClientWithCompletion:^(ODClient *client, NSError *error){
        if (!error){
            self.client = client;

            dispatch_async(dispatch_get_main_queue(),
                           ^{
                               [self performSelector:@selector(ShowLoader:) withObject:nil afterDelay:0.0];
                           });
            ODChildrenCollectionRequest *childrenRequest = [[[[self.client drive] items:@"root"] children] request];
            [self loadChildrenWithRequest:childrenRequest];
        }
        else{
            [self showAlert:@"Castivate" message:error.localizedDescription];
        }
    }];
}
*/
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
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
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

                do
                {
                    try
                    {
                        request = f1.list();
                        request.setQ("trashed=false");
                        request.setQ("mimeType='application/vnd.google-apps.document' or mimeType='text/plain' or mimeType='application/rtf' or mimeType='application/vnd.oasis.opendocument.text' or mimeType='application/pdf' or mimeType='application/vnd.openxmlformats-officedocument.wordprocessingml.document' or mimeType='application/msword'");
                        com.google.api.services.drive.model.FileList fileList = request.execute();

                        mResultList.addAll(fileList.getItems());
                        request.setPageToken(fileList.getNextPageToken());
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
               // mFileArray = new String[mResultList.size()];
                //int i = 0;
                for(File tmp : mResultList)
                {
                    /*mFileArray[i] = tmp.getTitle();*/
                    System.out.println("getTitle : "+tmp.getTitle());
                    System.out.println("getMimeType : "+tmp.getMimeType());

                   // i++;
                }
                /*mAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mFileArray){

                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        View view =super.getView(position, convertView, parent);

                        TextView textView=(TextView) view.findViewById(android.R.id.text1);

            *//*YOUR CHOICE OF COLOR*//*
                        textView.setTextColor(Color.BLACK);

                        return view;
                    }
                };
                mListView.setAdapter(mAdapter);*/
            }
        });
    }

}
