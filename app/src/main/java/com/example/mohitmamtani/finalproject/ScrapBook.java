package com.example.mohitmamtani.finalproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mohitmamtani.finalproject.common.CameraUtils;
import com.example.mohitmamtani.finalproject.common.Utils;
import com.example.mohitmamtani.finalproject.db.DatabaseHelper;
import com.example.mohitmamtani.finalproject.model.User;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ScrapBook extends Activity implements View.OnClickListener {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    private static String imageStoragePath;

    Button btnAddScrap, btnSaveScrap, btnViewScrap, btnLogout, btnDeleteScrap;
    ImageView imgScrap;
    EditText txtScrapTitle;
    private DatabaseHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap_book);
        userId = Utils.getLoggedInUser(this);
        // Checking availability of the camera
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device doesn't have camera
            finish();
        }
        db = new DatabaseHelper(this);
        txtScrapTitle = findViewById(R.id.txtScrapTitle);
        imgScrap = findViewById(R.id.imgScrap);
        btnAddScrap = findViewById(R.id.btnAddScrap);
        btnSaveScrap = findViewById(R.id.btnSaveScrap);
        btnViewScrap = findViewById(R.id.btnViewScrap);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteScrap = findViewById(R.id.btnDeleteScrap);

        /**
         * Capture image on button click
         */
        btnAddScrap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CameraUtils.checkPermissions(getApplicationContext())) {
                    captureImage();
                } else {
                    requestCameraPermission();
                }
            }
        });

        btnSaveScrap.setOnClickListener(this);
        btnViewScrap.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnDeleteScrap.setOnClickListener(this);

        // restoring storage image path from saved instance state
        // otherwise the path will be null on device rotation
        restoreFromBundle(savedInstanceState);
    }

    /**
     * Restoring store image path from saved instance state
     */
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals(".jpg")) {
                        previewCapturedImage();
                    }
                }
            }
        }
    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            // capture picture
                            CameraUtils.createDirectoryAtInitalTime(ScrapBook.this);
                            captureImage();

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(ScrapBook.this, userId);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Saving stored image path to saved instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restoring image path from saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }

    /**
     * Activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                restoreImage();
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                restoreImage();
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void restoreImage() {
        imageStoragePath = null;
        imgScrap.setImageBitmap(null);
        btnAddScrap.setText("Take Scrap");
    }

    /**
     * Display image from gallery
     */
    private void previewCapturedImage() {
        try {
//            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
//            imgScrap.setImageBitmap(bitmap);
            Picasso.get().load(new File(imageStoragePath)).into(imgScrap);
            btnAddScrap.setText("Retake Scrap");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(ScrapBook.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveScrap:
                String title = txtScrapTitle.getText().toString().trim();
                if (title.isEmpty()) {
                    Toast.makeText(this, "Please enter Title", Toast.LENGTH_LONG).show();
                    return;
                } else if (db.checkScrapByTitle(title, userId)) {
                    Toast.makeText(this, "Title already used, type another title", Toast.LENGTH_LONG).show();
                    return;
                } else if (imageStoragePath == null) {
                    Toast.makeText(this, "Please capture Image", Toast.LENGTH_LONG).show();
                    return;
                }

                db.insertScrap(title, imageStoragePath, userId);
                //  Toast.makeText(this, "Student db id is: " + user.getDbId(), Toast.LENGTH_SHORT).show();
                saveDialog();
                break;
            case R.id.btnViewScrap:
            case R.id.btnDeleteScrap:
                Intent intent = new Intent(this, ViewScrap.class);
                startActivity(intent);
                break;
            case R.id.btnLogout:
                logoutDialog();
                break;
        }
    }

    public void saveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_save_scrap_message)
                .setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                restoreImage();
                txtScrapTitle.setText("");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_logout_message)
                .setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                User user = db.getUser(userId);
                Utils.Instance().showNotification(ScrapBook.this,user.getName()+" is logged out");
                Utils.clearLoggedInUser(ScrapBook.this);
                Intent intent = new Intent(ScrapBook.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
