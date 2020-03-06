package com.vfs.dream11testsample.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vfs.dream11testsample.AppClass;
import com.vfs.dream11testsample.R;
import com.vfs.dream11testsample.db.ContactEntityModel;
import com.vfs.dream11testsample.db.DatabaseClient;
import com.vfs.dream11testsample.home.HomeActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditActivity.class.getSimpleName();
    private TextView txtDelete;
    private EditText editTextUserName, editTextPhoneNumber, edittextEmail;
    private ImageView imgView;
    private int pos;
    private Button btnEdit;
    private static final int CAMERA_REQUEST_CODE_PERMISSION = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private Bitmap photo;
    private ContactEntityModel contactEntityModel;
    private ImageView imgViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setTitle(getString(R.string.edit_contact));

        pos = getIntent().getExtras().getInt("delete_contact_position");

        initviews();

        setData();

        setListeners();
    }

    private void setListeners() {
        txtDelete.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        imgView.setOnClickListener(this);
        imgViewBack.setOnClickListener(this);
    }

    private void initviews() {
        imgViewBack = findViewById(R.id.imgViewBack);
        txtDelete = findViewById(R.id.txtDelete);

        imgView = findViewById(R.id.imgView);
        imgView.setEnabled(false);

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        edittextEmail = findViewById(R.id.edittextEmail);

        btnEdit = findViewById(R.id.btnEdit);
    }

    private void setData() {
        contactEntityModel = AppClass.getInstance().getContactEntityModels().get(pos);

        Bitmap bitmap = BitmapFactory.decodeByteArray(contactEntityModel.getImage(), 0, contactEntityModel.getImage().length);
        imgView.setImageBitmap(bitmap);

        editTextUserName.setText(contactEntityModel.getUserName());
        editTextPhoneNumber.setText(contactEntityModel.getPhoneNumer());
        edittextEmail.setText(contactEntityModel.getEmail());

        photo = BitmapFactory.decodeByteArray(contactEntityModel.getImage(), 0, contactEntityModel.getImage().length);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtDelete:
                if (txtDelete.getText().toString().equalsIgnoreCase("save")) {
                    AddContactAsyncTask addContactAsyncTask = new AddContactAsyncTask(editTextPhoneNumber.getText().toString(), edittextEmail.getText().toString(), editTextUserName.getText().toString());
                    addContactAsyncTask.execute();
                } else {
                    deleteUser();
                }
                break;
            case R.id.btnEdit:
                btnEdit.setVisibility(View.GONE);

                editTextUserName.setEnabled(true);
                editTextPhoneNumber.setEnabled(true);
                edittextEmail.setEnabled(true);
                imgView.setEnabled(true);

                txtDelete.setText("Save");
                break;
            case R.id.imgView:
                showDialog();
                break;
            case R.id.imgViewBack:
                finish();
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

    private void fromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
    }

    private void deleteUser() {


        ContactEntityModel contactEntityModel = AppClass.getInstance().getContactEntityModels().get(pos);

/*
        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                .contactDao()
                .delete(contactEntityModel);
*/

//        finish();

//        startActivity(new Intent(this, HomeActivity.class));

        RemoveContactAsyncTask removeContactAsyncTask = new RemoveContactAsyncTask(contactEntityModel);
        removeContactAsyncTask.execute();
    }

    class RemoveContactAsyncTask extends AsyncTask<Void, Void, Void> {

        private final ContactEntityModel contactEntityModel;


        public RemoveContactAsyncTask(ContactEntityModel contactEntityModel) {
            this.contactEntityModel = contactEntityModel;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //adding to database
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .contactDao()
                    .delete(contactEntityModel);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            Toast.makeText(getApplicationContext(), "data deleted successfuly", Toast.LENGTH_LONG).show();
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
            contactEntityModel.setPhoneNumer(phone);
            contactEntityModel.setEmail(email);
            contactEntityModel.setUserName(userName);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            photo.recycle();

            contactEntityModel.setImage(byteArray);

            //adding to database
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .contactDao()
                    .update(contactEntityModel);
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
