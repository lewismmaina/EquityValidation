package com.example.equityvalidation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.equityvalidation.CustomerNew.KEY_IDNO;

public class PassportActivity extends AppCompatActivity {
    private SessionHandler session;
    private static final int PICTURE_RESULT = 100;
    ContentValues values;
    Uri imageUri;
    String imageurl;
    String tags;
    ImageView imageView;
    Bitmap bitmap;
    Button btnPassport,btnContinue, btnDashboard;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);
        session = new SessionHandler(getApplicationContext());
        final User user = session.getUserDetails();

        //get the user id no from the CustomerNew.java activity
        Intent i = getIntent();
        tags = i.getStringExtra(KEY_IDNO);

        imageView = findViewById(R.id.imageView);
        btnPassport= findViewById(R.id.buttonPassport);
        btnContinue=findViewById(R.id.btnContinue);
        btnDashboard=findViewById(R.id.btnDashboard);

        //runtime permission for camera access from android version 6.0

        //launch camera on click
        btnPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PICTURE_RESULT);
            }
        });

        //load homepage on click
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(PassportActivity.this,DashboardHome.class);
                startActivity(intent);
                finish();
            }
        });

        //on click proceed to capture front id
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayLoader();
                uploadBitmap();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        //if image is capture, check if its rotated, rotate, resize and display on image view
            case PICTURE_RESULT:
                if (requestCode == PICTURE_RESULT)
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), imageUri);
                            imageurl = getRealPathFromURI(imageUri);
                            ExifInterface exif = null;
                            try {
                                exif = new ExifInterface(imageurl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            bitmap = rotateBitmap(bitmap, orientation);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 480, 640, true);
                            imageView.setImageBitmap(bitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
        }
    }
    //rotate the image to portrait
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //get the image uri
    public String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    //method to display loader while loading image
    private void displayLoader() {
        pDialog = new ProgressDialog(PassportActivity.this);
        pDialog.setMessage("Proceeding.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }
    //takes bitmap as an argument, compresses it to 100% quality so as to send array to server
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    //volley request to server
    public void uploadBitmap() {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            imageView.setImageResource(R.drawable.ic_menu_camera);
                            pDialog.dismiss();
                            //takes string idno and passes it to the next activity
                            String idno= tags;
                            Intent intent= new Intent(PassportActivity.this,FrontidActivity.class);
                            intent.putExtra(KEY_IDNO,idno);
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }) {

            //use the parameter tags as name of image
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tags", tags);
                return params;
            }

            //passing the image with the unique name as the idno +p
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                String imagename = tags+"p";
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }


        };
        //where the request is added to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}
