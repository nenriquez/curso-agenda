package com.curso.agenda.dao;

import com.curso.agenda.model.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDAO {

    public static final String TABLE_CONTACTS = "CONTACTS";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IMG = "image";

    private static class DBPersonasHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 2;
        
        private static final String DB_NAME = "DbAgenda.db3";

        private static final String createTablePersonas =
                "CREATE TABLE " + TABLE_CONTACTS + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + KEY_NAME + " varchar(64) NOT NULL,"
                + KEY_PHONE + " varchar(64) NOT NULL,"
                + KEY_EMAIL + " varchar(64) NOT NULL,"
                + KEY_IMG + " varchar(64) DEFAULT NULL)";
        
        
        public DBPersonasHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createTablePersonas);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + TABLE_CONTACTS);
            db.execSQL(createTablePersonas);
        }
    }

    private DBPersonasHelper dbHelper;
    private SQLiteDatabase db;

    public ContactDAO(Context context) {
        this.open(context);
    }
    
    private void open(Context context) throws SQLException {
        dbHelper = new DBPersonasHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public Cursor getAll() {
        Cursor c = db.query(TABLE_CONTACTS, new String[] {KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_IMG}, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        
        return c;
    }

    public long add(String name, String surname, String phone, String phoneType, String img) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_EMAIL, phoneType);
        values.put(KEY_IMG, img);
        return db.insert(TABLE_CONTACTS, null, values);
    }

    public long add(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_IMG, contact.getImage());
        return db.insert(TABLE_CONTACTS, null, values);
    }

    public int delete(String name, String surname) {
        return db.delete(TABLE_CONTACTS, "name = ? AND surname = ?", new String[]{name, surname});
    }

    public int delete(long contactId) {
        return db.delete(TABLE_CONTACTS, "_id = ?", new String[]{String.valueOf(contactId)});
    }

}