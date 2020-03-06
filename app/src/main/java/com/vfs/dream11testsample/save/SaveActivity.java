package com.vfs.dream11testsample.save;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vfs.dream11testsample.R;
import com.vfs.dream11testsample.db.ContactEntityModel;
import com.vfs.dream11testsample.db.DatabaseClient;
import com.vfs.dream11testsample.home.HomeActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SaveActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SaveActivity.class.getSimpleName();
    private TextView txtSave;
    private ImageView imgViewBack;
    private ImageView imgView;
    private static final int CAMERA_REQUEST_CODE_PERMISSION = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private Bitmap photo;
    private EditText editTextUserName, editTextPhoneNumber, edittextEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        //set action bar title
        setTitle(getString(R.string.save_contact));

        initViews();

        setListeners();
    }

    private void setListeners() {
        txtSave.setOnClickListener(this);
        imgViewBack.setOnClickListener(this);
        imgView.setOnClickListener(this);
    }

    private void initViews() {
        txtSave = findViewById(R.id.txtSave);
        imgViewBack = findViewById(R.id.imgViewBack);
        imgView = findViewById(R.id.imgView);

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        edittextEmail = findViewById(R.id.edittextEmail);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSave:
                saveDataToDb();
                break;
            case R.id.imgViewBack:
                finish();
                break;
            case R.id.imgView:
                checkPermisssionForCamera();
                break;
        }
    }

    private void checkPermisssionForCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE_PERMISSION);
                return;
            } else {
                showDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDialog();
                }
                break;
        }
    }

    private void showDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        fromCamera();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        pickFromGallery();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Select You Prefs").setPositiveButton("Camera", dialogClickListener)
                .setNegativeButton("gallery", dialogClickListener).show();
    }

    private void saveDataToDb() {
        AddContactAsyncTask st = new AddContactAsyncTask(editTextPhoneNumber.getText().toString(), edittextEmail.getText().toString(), editTextUserName.getText().toString());
        st.execute();
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void fromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                Log.d(TAG, "onActivityResult: called ak 1:" + imageReturnedIntent.getData());
                if (resultCode == RESULT_OK) {
                    photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    imgView.setImageBitmap(photo);
                }

                break;
            case GALLERY_REQUEST_CODE:
                Log.d(TAG, "onActivityResult: called ak 2:");
                if (resultCode == RESULT_OK) {
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageReturnedIntent.getData());
                        imgView.setImageBitmap(photo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    class AddContactAsyncTask extends AsyncTask<Void, Void, Void> {

        private String phone, email, userName;

        public AddContactAsyncTask(String phone, String email, String userName) {
            this.phone = phone;
            this.email = email;
            this.userName = userName;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //creating a task
            ContactEntityModel contact = new ContactEntityModel();
            contact.setPhoneNumer(phone);
            contact.setEmail(email);
            contact.setUserName(userName);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            photo.recycle();

            contact.setImage(byteArray);

            //adding to database
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .contactDao()
                    .insert(contact);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        }
    }
}
