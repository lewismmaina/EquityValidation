package com.example.equityvalidation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.equityvalidation.CustomerNew.KEY_IDNO;


public class SignatureActivity extends AppCompatActivity {
    private SessionHandler session;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private GestureOverlayView gestureOverlayView = null;
    private Button redrawButton = null;
    private Button saveButton = null;
    String tags;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        Button save_button= findViewById( R.id.save_button);
        save_button.setEnabled(false);
        session = new SessionHandler(getApplicationContext());
        //access id no from back id activity
        Intent i = getIntent();
        tags = i.getStringExtra(KEY_IDNO);
        init();
        gestureOverlayView.addOnGesturePerformedListener(new CustomGestureListener());
        redrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gestureOverlayView.clear(false);
            }

        });

        //when clicked check on write permission and save image to device
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndSaveSignature();
            }
        });
    }


    public void init()
    {
        if(gestureOverlayView==null)
        {
            gestureOverlayView = findViewById(R.id.sign_pad);
        }
        if(redrawButton==null)
        {
            redrawButton = findViewById(R.id.redraw_button);
        }

        if(saveButton==null)
        {
            saveButton = findViewById(R.id.save_button);
        }
    }
    public void checkPermissionAndSaveSignature()
    {
        try {
            //check write permission
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //on denied permission
            if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
            {
                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(SignatureActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }else
            {
                displayLoader();
                saveSignature();
            }
        } catch (Exception e) {
            Log.v("Signature Gestures", e.getMessage());
            e.printStackTrace();
        }
    }

    public void displayLoader() {
        pDialog = new ProgressDialog(SignatureActivity.this);
        pDialog.setMessage("Proceeding.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }
    public void saveSignature()
    {
        try {
            //  destroy cached image.
            gestureOverlayView.destroyDrawingCache();
            // enabling the drawing cache function.
            gestureOverlayView.setDrawingCacheEnabled(true);
            //get the cached bitmap
            Bitmap drawingCacheBitmap = gestureOverlayView.getDrawingCache();
            // Create a new bitmap
            Bitmap bitmap = Bitmap.createBitmap(drawingCacheBitmap);
            //get image file name and save path
            String filePath = Environment.getExternalStorageDirectory().toString();
            filePath += File.separator;
            filePath += "sign.png";
            File file = new File(filePath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Bitmap bitmap1 = BitmapFactory.decodeFile(filePath);
            uploadBitmap(bitmap1);
        } catch (Exception e) {
            Log.v("Signature Gestures", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveSignature();
                displayLoader();
            } else {
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
    //update request
    public void uploadBitmap(final Bitmap bitmap1) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_SIGNURL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Thank You... Account Opened Successfully", Toast.LENGTH_LONG).show();
                            Intent intent= new Intent(SignatureActivity.this,DashboardHome.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            //name parameter
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tags", tags);
                return params;
            }
            //signature image
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                String imagename = tags+"s";
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap1)));
                return params;
            }
        };
        //adding volley request
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap1) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        Button save_button= findViewById( R.id.save_button);
        switch(view.getId()) {
            case R.id.checkbox_condition:
                if (checked)
                    save_button.setEnabled(true);
                break;

        }
    }

}