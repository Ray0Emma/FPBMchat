package com.example.tp7_asyntask.localdatabase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class GroupsLocalDB extends SQLiteOpenHelper {

    public static final int DB_VERSION = 2;
    public static String DB_NAME = "UserData.db";

    public GroupsLocalDB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE user(username TEXT UNIQUE NOT NULL, userpassword TEXT NOT NULL, firstname TEXT , name) ";
        sqLiteDatabase.execSQL(query);

        query = "CREATE TABLE app_item(name TEXT, value TEXT) ";
        sqLiteDatabase.execSQL(query);
        // "YYYY-MM-DD HH:MM:SS.SSS" format of change_time
        query = "CREATE TABLE conversations(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, logo TEXT,change_time TEXT NOT NULL) ";
        sqLiteDatabase.execSQL(query);
        // conversation users
        query = "CREATE TABLE conversations_users(conversation_id INTEGER NOT NULL , username TEXT NOT NULL) ";
        sqLiteDatabase.execSQL(query);
        // conversation content
        query = "CREATE TABLE conversations_content(id INTEGER PRIMARY KEY AUTOINCREMENT , conversation_id INTEGER NOT NULL, content TEXT NOT NULL, add_time TEXT NOT NULL, is_seen INTEGER DEFAULT 0 NOT NULL) ";
        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i >= i1)
            return;
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS app_item");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS conversations");
        onCreate(sqLiteDatabase);
    }

    public String getUserName()
    {
        String result = "null";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM user" ;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst())
        {
            result = cursor.getString(0);
        }

        return result;

    }

    public void saveUser(String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("userpassword", password);

        db.insert("user", null, contentValues);
    }

    public boolean deleteUser()
    {
        SQLiteDatabase db =getWritableDatabase();
        boolean test = true;
        try
        {
            db.execSQL("DELETE FROM user ");
        }
        catch (Exception e)
        {
            test = false;
        }

        return test;
    }

    public boolean ifConnectedUser()
    {
        return !this.getUserName().equals("null");
    }

    public boolean editItem(String name, String newValue)
    {
        SQLiteDatabase db =getWritableDatabase();
        boolean test = true;
        try
        {
            db.execSQL("UPDATE user_item SET value=\"" + newValue + "\" WHERE name=\"" + name + "\"");

        }
        catch (Exception e)
        {
            test = false;
        }

        return test;

    }

    public boolean addNewItem(String name, String value)
    {
        SQLiteDatabase db =getWritableDatabase();
        boolean test = true;
        if(existanceOfItem(name))
        {
            return false;
        }
        try
        {
            db.execSQL("INSERT INTO user_item VALUES(\"" + name + "\",  value=\"" + value + "\"");

        }
        catch (Exception e)
        {
            test = false;
        }

        return test;
    }

    public boolean existanceOfItem(String name)
    {
        SQLiteDatabase db =getReadableDatabase();
        boolean test = true;
        try
        {
            Cursor cursor = db.rawQuery("SELECT * FROM user_item WHERE name=\"" + name + "\"", null);
            cursor.moveToFirst();
            String result = cursor.getString(1);
            if (result.isEmpty())
            {
                test = false;
            }
        }
        catch (Exception e)
        {
            test = false;
        }

        return test;
    }

    public String getItem(String name)
    {
        SQLiteDatabase db =getReadableDatabase();
        String result = null;
        if(!existanceOfItem(name))
        {
            return null;
        }
        try
        {
            Cursor cursor = db.rawQuery("SELECT value FROM user_item WHERE name=\"" + name + "\"" , null);
            cursor.moveToFirst();
            result = cursor.getString(0);
        }
        catch (Exception e)
        {
            return null;
        }

        return result;
    }
}
