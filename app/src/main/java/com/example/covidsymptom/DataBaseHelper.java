package com.example.covidsymptom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String COVID_TABLE_NAME = "COVID_DATA";
    public static final String COLUMN_SIGN = "SIGN";
    public static final String COLUMN_VALUE = "VALUE";
    public static final String COLUMN_ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "voona.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + COVID_TABLE_NAME + " (" + COLUMN_SIGN + " TEXT PRIMARY KEY, " + COLUMN_VALUE + " FLOAT )";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(PatientDataModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SIGN, model.getSign());
        cv.put(COLUMN_VALUE, model.getValue());

        //Insert
        long insert = db.insertWithOnConflict(COVID_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        if (insert == -1) {
            insert = db.update(COVID_TABLE_NAME, cv, COLUMN_SIGN + "=?", new String[]{model.getSign()});
        }
        return insert != -1;
    }

    public List<PatientDataModel> getAll() {
        List<PatientDataModel> returnList = new ArrayList<>();
        String query = "SELECT * FROM " + COVID_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String sign = cursor.getString(0);
                float value = cursor.getFloat(1);
                returnList.add(new PatientDataModel(sign, value));
            } while (cursor.moveToNext());
        } else {
            //No data
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public boolean deleteOne(PatientDataModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + COVID_TABLE_NAME + " WHERE " + COLUMN_SIGN + " = '" + model.getSign() + "'";
        Cursor cursor = db.rawQuery(query, null);
        return !!cursor.moveToFirst();
    }
}
