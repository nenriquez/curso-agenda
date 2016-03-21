package com.curso.agenda.dao;

import com.curso.agenda.model.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ContactDAO {

    public static final String TABLE_CONTACTS = "CONTACTS";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LOC_X = "loc_x";
    public static final String KEY_LOC_Y = "loc_Y";
    public static final String KEY_IMG = "image";

    private static class DBPersonasHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 5;
        
        private static final String DB_NAME = "DbAgenda.db3";

        private static final String createTablePersonas =
                "CREATE TABLE " + TABLE_CONTACTS + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + KEY_NAME + " varchar(64) NOT NULL,"
                + KEY_PHONE + " varchar(64) NOT NULL,"
                + KEY_EMAIL + " varchar(64) NOT NULL,"
                + KEY_ADDRESS + " varchar(128) NOT NULL,"
                + KEY_LOC_X + " REAL NOT NULL,"
                + KEY_LOC_Y + " REAL NOT NULL,"
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
        Cursor c = db.query(TABLE_CONTACTS, new String[] {KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL,
                KEY_ADDRESS, KEY_LOC_X, KEY_LOC_Y, KEY_IMG}, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        
        return c;
    }

    public List<Contact> getAllContacts() {
        List<Contact> result = new ArrayList<Contact>();
        Cursor c = getAll();
        do {

            Contact contact = new Contact(
                c.getString(c.getColumnIndex(KEY_NAME)),
                c.getString(c.getColumnIndex(KEY_PHONE)),
                c.getString(c.getColumnIndex(KEY_EMAIL)),
                c.getString(c.getColumnIndex(KEY_IMG)),
                c.getString(c.getColumnIndex(KEY_ADDRESS)),
                c.getDouble(c.getColumnIndex(KEY_LOC_X)),
                c.getDouble(c.getColumnIndex(KEY_LOC_Y)));
            result.add(contact);

        } while (c.moveToNext());
        c.close();
        return result;
    }

    public long add(String name, String surname, String phone, String phoneType,
                    String address, String locX, String locY, String img) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_EMAIL, phoneType);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_LOC_X, locX);
        values.put(KEY_LOC_Y, locY);
        values.put(KEY_IMG, img);
        return db.insert(TABLE_CONTACTS, null, values);
    }

    public long add(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddres());
        values.put(KEY_LOC_X, contact.getLocX());
        values.put(KEY_LOC_Y, contact.getLocY());
        values.put(KEY_IMG, contact.getImage());
        return db.insert(TABLE_CONTACTS, null, values);
    }

    public Contact getContactBy(long id) {
        Cursor c = db.query(TABLE_CONTACTS, new String[] {KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL,
                KEY_ADDRESS, KEY_LOC_X, KEY_LOC_Y, KEY_IMG}, KEY_ID + " = ?", new String[] {String.valueOf(id)}, null, null, null);
        if (c != null) {
            c.moveToFirst();
            return new Contact(
                    c.getString(c.getColumnIndex(KEY_NAME)),
                    c.getString(c.getColumnIndex(KEY_PHONE)),
                    c.getString(c.getColumnIndex(KEY_EMAIL)),
                    c.getString(c.getColumnIndex(KEY_IMG)),
                    c.getString(c.getColumnIndex(KEY_ADDRESS)),
                    c.getDouble(c.getColumnIndex(KEY_LOC_X)),
                    c.getDouble(c.getColumnIndex(KEY_LOC_Y)));
        }
        return null;
    }

    public int delete(String name, String surname) {
        return db.delete(TABLE_CONTACTS, "name = ? AND surname = ?", new String[]{name, surname});
    }

    public int delete(long contactId) {
        return db.delete(TABLE_CONTACTS, "_id = ?", new String[]{String.valueOf(contactId)});
    }

}