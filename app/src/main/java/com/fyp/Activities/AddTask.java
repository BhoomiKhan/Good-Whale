package com.fyp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fyp.Const.Constants;
import com.fyp.R;
import com.fyp.Utils.FilePath;
import com.fyp.Utils.Utilities;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AddTask extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1, LIBRARY_REQUEST = 2;
    private static final String TAG = AddTask.class.getSimpleName();
    private TextView mFileName;
    private Button finish, addNew, choose_image;
    private EditText mDescription;
    private String description, trainerName, courseID;
    private boolean doubleBackToExitPressedOnce = false;
    private String hasChoosenFile = "no", selectedFilePath;
    private Uri fileUri;
    private PowerManager.WakeLock wakeLock;
    private ProgressDialog dialog;
    private int dialogNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        init();
    }

    private void init() {
        mFileName = (TextView) findViewById(R.id.fileName);
        mDescription = (EditText) findViewById(R.id.description);
        finish = (Button) findViewById(R.id.finish);

        choose_image = (Button) findViewById(R.id.choose_image);
        mDescription.setOnClickListener(this);
        finish.setOnClickListener(this);
        choose_image.setOnClickListener(this);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
        wakeLock.acquire();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.choose_image) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), LIBRARY_REQUEST);
        } else if (v.getId()==R.id.finish){
            finish();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LIBRARY_REQUEST) {
                fileUri = intent.getData();
                selectedFilePath = FilePath.getPath(AddTask.this, fileUri);
                hasChoosenFile = "yes";
                afterSelection(selectedFilePath);
            }
        }
    }

    private void afterSelection(String filePath) {
        if (!(mFileName.getVisibility() == View.VISIBLE)) {
            mFileName.setVisibility(View.VISIBLE);
        }
        String[] parts = filePath.split("/");
        final String fileName = parts[parts.length - 1];
        mFileName.setText(fileName);
    }

    public boolean validation() {
        description = mDescription.getText().toString();
        if (Utilities.haveNetworkConnection(AddTask.this)) {
            if (!description.equals("")) {
                getTrainerDetail();
                return true;
            } else {
                mDescription.requestFocus();
                Toast.makeText(this, "Description is Empty", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            openDialog();
            return false;
        }
    }

    private void getTrainerDetail() {
        SharedPreferences mSharedPreferences = getSharedPreferences("TaskManagementSys", MODE_PRIVATE);
        trainerName = mSharedPreferences.getString("trainerName", "Ismael Yousaf");
        courseID = mSharedPreferences.getString("courseID", "Ismael Yousaf");
        Log.d("trainername", trainerName);
        Log.d("corseID", courseID);
    }

    //this method will call when user will click on "SendRequest" method
    public void startUploading(View v) {
        if (!Utilities.haveNetworkConnection(AddTask.this)) {
            dialogNumber = 1;
            openDialog();
        } else {
            //before uploding check either user has select a file or he is trying to press button without selecing a file/photo.
            if (hasChoosenFile.equals("yes")) {
                if (validation()) {
                    //check either user has fill signees form or not
                    dialog = ProgressDialog.show(AddTask.this, "", "Uploading File...", true);
                    dialog.setCancelable(true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //creating new thread to handle Http Operations
                                uploadFile(selectedFilePath);
                            } catch (OutOfMemoryError e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddTask.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            } else {
                Toast.makeText(this, "First choose a file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int uploadFile(final String selectedFilePath) {
        int serverResponseCode = 0;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);
        String[] parts = selectedFilePath.split("/");
        if (!selectedFile.isFile()) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddTask.this, "suurce file does not exist", Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(Constants.fileUploading);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);
                dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + selectedFilePath + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];
                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    try {
                        dataOutputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {

                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"trainerName\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(trainerName);

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"flag\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes("1");
                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(description);
                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"coursID\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(courseID);
                dataOutputStream.writeBytes(lineEnd);
                // get server ok responce
                try {
                    serverResponseCode = connection.getResponseCode();
                } catch (OutOfMemoryError e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddTask.this, "Out of Memory", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    // imageList.add(selectedFilePath);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //connection build with server
                        }
                    });
                }
                //reading server echo responce
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //onSuccess
                        dialogNumber = 2;
                        openDialog();
                    }
                });
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddTask.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddTask.this, "URL Error!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            return serverResponseCode;
        }
    }


    public void openDialog() {
        //Dial log box will be appear when there will be no Internet connection
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = null;
        if (dialogNumber == 1) {
            dialogView = inflater.inflate(R.layout.dialog_connection, null);
        } else if (dialogNumber == 2) {
            dialogView = inflater.inflate(R.layout.dialog_file_uploaded, null);
            if (dialog.isShowing()) {
                dialog.dismiss();
                resetField();
            }
        }
        dialogBuilder.setView(dialogView);
        final AlertDialog findMeDialog = dialogBuilder.create();
        findMeDialog.show();
        LinearLayout reset_btn = (LinearLayout) dialogView.findViewById(R.id.ok);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findMeDialog.dismiss();
            }
        });
    }

    private void resetField(){
        mFileName.setText("");
        mDescription.setText("");
    }

    @Override
    public void onBackPressed() {
        if (false) {
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}