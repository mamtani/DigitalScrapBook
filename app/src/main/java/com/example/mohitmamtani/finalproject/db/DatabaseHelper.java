package com.example.mohitmamtani.finalproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mohitmamtani.finalproject.common.Const;
import com.example.mohitmamtani.finalproject.model.Scrap;
import com.example.mohitmamtani.finalproject.model.User;

import java.util.ArrayList;




public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create Users table
        db.execSQL(User.CREATE_TABLE);
        db.execSQL(Scrap.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Scrap.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public long insertUser(String name, String email, String password) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(User.COLUMN_NAME, name);
        values.put(User.COLUMN_EMAIL, email);
        values.put(User.COLUMN_PASSWORD, password);

        // insert row
        long id = db.insert(User.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public User getUser(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(User.TABLE_NAME,
                new String[]{User.COLUMN_ID, User.COLUMN_NAME, User.COLUMN_EMAIL, User.COLUMN_PASSWORD},
                User.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        // prepare User object
        User user = new User(
                cursor.getInt(cursor.getColumnIndex(User.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(User.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(User.COLUMN_PASSWORD)));

        // close the db connection
        cursor.close();

        return user;
    }

    public User checkUser(String email, String password) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(User.TABLE_NAME,
                new String[]{User.COLUMN_ID, User.COLUMN_NAME, User.COLUMN_EMAIL, User.COLUMN_PASSWORD},
                User.COLUMN_EMAIL + "=? AND " + User.COLUMN_PASSWORD + "=?",
                new String[]{String.valueOf(email), String.valueOf(password)}, null, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
//            cursor.moveToFirst();
            // prepare User object
            user = new User(
                    cursor.getInt(cursor.getColumnIndex(User.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_PASSWORD)));

            // close the db connection
            cursor.close();
        }
        return user;
    }

    public User checkUserByEmail(String email) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(User.TABLE_NAME,
                new String[]{User.COLUMN_ID, User.COLUMN_NAME, User.COLUMN_EMAIL, User.COLUMN_PASSWORD},
                User.COLUMN_EMAIL + "=?",
                new String[]{String.valueOf(email)}, null, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
//            cursor.moveToFirst();
            // prepare User object
            user = new User(
                    cursor.getInt(cursor.getColumnIndex(User.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_PASSWORD)));

            // close the db connection
            cursor.close();
        }
        return user;
    }





    public long insertScrap(String title, String imagePath, int userId) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Scrap.COLUMN_TITLE, title);
        values.put(Scrap.COLUMN_IMAGE_PATH, imagePath);
        values.put(Scrap.COLUMN_USER_ID, userId);

        // insert row
        long id = db.insert(Scrap.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Scrap getScrap(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Scrap.TABLE_NAME,
                new String[]{Scrap.COLUMN_ID, Scrap.COLUMN_TITLE, Scrap.COLUMN_IMAGE_PATH, Scrap.COLUMN_USER_ID},
                Scrap.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Scrap scrap = null;
        if (cursor != null && cursor.moveToFirst()) {
            scrap = new Scrap(
                    cursor.getInt(cursor.getColumnIndex(Scrap.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Scrap.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(Scrap.COLUMN_IMAGE_PATH)),
                    cursor.getInt(cursor.getColumnIndex(Scrap.COLUMN_USER_ID)));
            cursor.close();
        }


        return scrap;
    }

    public boolean checkScrapByTitle(String title, int userId) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Scrap.TABLE_NAME,
                new String[]{Scrap.COLUMN_ID, Scrap.COLUMN_TITLE, Scrap.COLUMN_IMAGE_PATH, Scrap.COLUMN_USER_ID},
                Scrap.COLUMN_TITLE + "=? AND " + Scrap.COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(title), String.valueOf(userId)}, null, null, null, null);
        Scrap scrap = null;
        if (cursor != null && cursor.moveToFirst()) {
            scrap = new Scrap(
                    cursor.getInt(cursor.getColumnIndex(Scrap.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Scrap.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(Scrap.COLUMN_IMAGE_PATH)),
                    cursor.getInt(cursor.getColumnIndex(Scrap.COLUMN_USER_ID)));
            cursor.close();
        }


        return scrap != null;
    }

    public ArrayList<Scrap> getAllScrap(int userId) {
        ArrayList<Scrap> arrScrap = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Scrap.TABLE_NAME + " WHERE " + Scrap.COLUMN_USER_ID + "=" + userId + " ORDER BY " +
                Scrap.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Scrap scrap = new Scrap();
                scrap.setId(cursor.getInt(cursor.getColumnIndex(Scrap.COLUMN_ID)));
                scrap.setTitle(cursor.getString(cursor.getColumnIndex(Scrap.COLUMN_TITLE)));
                scrap.setImagePath(cursor.getString(cursor.getColumnIndex(Scrap.COLUMN_IMAGE_PATH)));
                scrap.setUserId(cursor.getInt(cursor.getColumnIndex(Scrap.COLUMN_USER_ID)));
                scrap.setTimestamp(cursor.getString(cursor.getColumnIndex(Scrap.COLUMN_TIMESTAMP)));

                arrScrap.add(scrap);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return Users list
        return arrScrap;
    }


    public void deleteScrap(Scrap scrap) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Scrap.TABLE_NAME, Scrap.COLUMN_ID + " = ?",
                new String[]{String.valueOf(scrap.getId())});
        db.close();
    }
}
