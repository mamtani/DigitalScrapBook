package com.example.mohitmamtani.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mohitmamtani.finalproject.common.Utils;
import com.example.mohitmamtani.finalproject.db.DatabaseHelper;
import com.example.mohitmamtani.finalproject.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends Activity {
    EditText edtLoginEmailId, edtLoginPassword;
    Button btnLoginUser;
    TextView txtLoginPageDisplay;
    ListView lstDisplay;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(this);
        edtLoginEmailId = findViewById(R.id.edt_login_emailId);
        edtLoginPassword = findViewById(R.id.edt_login_password);
        btnLoginUser = findViewById(R.id.btn_login_user);
        txtLoginPageDisplay = findViewById(R.id.txt_login_page_display);
        lstDisplay = findViewById(R.id.lst_display);


        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String validateEmail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

                String emailIdLogin = edtLoginEmailId.getText().toString();
                Matcher matcher = Pattern.compile(validateEmail).matcher(emailIdLogin);
                if (!matcher.matches()) {
                    edtLoginEmailId.setError("This is not a valid email id");
                    return;
                }
                if (edtLoginPassword.getText().toString().length() == 0) {
                    edtLoginPassword.setError("Password can't be empty");
                    return;
                }
                validateUser(edtLoginEmailId.getText().toString(), edtLoginPassword.getText().toString());

            }
        });
    }

    private void validateUser(String email, String password) {
        User user = db.checkUser(email, password);
        if (user != null) {
            Utils.saveLogin(this, user);
            Utils.Instance().showNotification(Login.this,user.getName()+" is logged in");
            Intent intent = new Intent(Login.this, ScrapBook.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            alertDialog();
        }
    }

    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_register_message)
                .setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
