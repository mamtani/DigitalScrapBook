package com.example.mohitmamtani.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohitmamtani.finalproject.db.DatabaseHelper;
import com.example.mohitmamtani.finalproject.model.Scrap;
import com.squareup.picasso.Picasso;

import java.io.File;

public class DeleteScrap extends Activity implements View.OnClickListener {


    Button btnBack, btnDeleteScrap, btnOpenImage;
    ImageView imgScrap;
    TextView txtTitle;
    Scrap scrap;
    private DatabaseHelper db;
    int scrap_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_scrap);

        txtTitle = findViewById(R.id.txtTitle);
        imgScrap = findViewById(R.id.imgScrap);
        btnOpenImage = findViewById(R.id.btnOpenImage);
        btnBack = findViewById(R.id.btnBack);
        btnDeleteScrap = findViewById(R.id.btnDeleteScrap);

        btnOpenImage.setOnClickListener(this);
        btnDeleteScrap.setOnClickListener(this);
        btnBack.setOnClickListener(this);


        scrap_id = getIntent().getIntExtra("scrap_id", -1);

        if (scrap_id == -1) {
            return;
        }

        db = new DatabaseHelper(this);
        populateData();
    }

    public void populateData() {
        scrap = db.getScrap(scrap_id);
        if (scrap != null) {
            txtTitle.setText(scrap.getTitle());
            Picasso.get().load(new File(scrap.getImagePath())).into(imgScrap);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnDeleteScrap:
                deleteDialog();
                break;
            case R.id.btnOpenImage:
                openDialog();
                break;
        }
    }

    public void openDialog() {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.setContentView(R.layout.custom_fullimage_dialog);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.image_layout,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView image = layout.findViewById(R.id.imgScrap);
//        image.setImageDrawable(imageView.getDrawable());
        Picasso.get().load(new File(scrap.getImagePath())).into(image);
        Button btnBack = layout.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(layout);
        dialog.show();
    }

    public void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_scrap_message)
                .setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                File file = new File(scrap.getImagePath());
                file.delete();
                db.deleteScrap(scrap);
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
