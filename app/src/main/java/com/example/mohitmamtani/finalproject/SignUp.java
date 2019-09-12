package com.example.mohitmamtani.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mohitmamtani.finalproject.db.DatabaseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends Activity {

    EditText edtName, edtEmailId, edtPassword, edtConfirmPassword;
    Button btnRegisterUser;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = new DatabaseHelper(this);
        edtName = findViewById(R.id.edt_name);
        edtEmailId = findViewById(R.id.edt_emailId);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnRegisterUser = findViewById(R.id.btn_register_user);

        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String validateEmailId = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

                String emailId = edtEmailId.getText().toString();
                Matcher matcher = Pattern.compile(validateEmailId).matcher(emailId);
                if (!matcher.matches()) {
                    edtEmailId.setError("This is not a valid email id");
                }
                if (edtName.getText().toString().length() == 0) {
                    edtName.setError("Incorrect Name");
                }
                if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                    edtConfirmPassword.setError("The passwords do not match!");
                }
                if (edtConfirmPassword.getText().toString().length() == 0) {
                    edtConfirmPassword.setError("Incorrect input");
                }
                if (edtPassword.getText().toString().length() == 0) {
                    edtPassword.setError("Incorrect input");
                }
                if (edtName.getError() == null && edtEmailId.getError() == null && edtPassword.getError() == null && edtConfirmPassword.getError() == null) {
                    saveUser();
                }

            }
        });

    }

    public void saveUser() {
        String name = edtName.getText().toString();
        String emailId = edtEmailId.getText().toString();
        String password = edtPassword.getText().toString();

        if (db.checkUserByEmail(emailId) != null) {
            userExistsDialog();
            return;
        }
        db.insertUser(name, emailId, password);
        //  Toast.makeText(this, "Student db id is: " + user.getDbId(), Toast.LENGTH_SHORT).show();
        saveDialog();
    }

    public void saveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_save_message)
                .setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void userExistsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_user_exists)
                .setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
