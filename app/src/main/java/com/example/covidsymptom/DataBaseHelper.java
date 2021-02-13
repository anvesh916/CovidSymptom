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

    public static final String COVID_TABLE_NAME = "VOONA";
    public static final String ID = "ID";
    public static final String RESP_RATE = "RESPIRATION_RATE";
    public static final String HEART_RATE = "HEART_RATE";
    public static final String NAUSEA = "NAUSEA";
    public static final String HEAD_ACHE = "HEAD_ACHE";
    public static final String DIARRHEA = "DIARRHEA";
    public static final String SOAR_THROAT = "SOAR_THROAT";
    public static final String FEVER = "FEVER";
    public static final String MUSCLE_ACHE = "MUSCLE_ACHE";
    public static final String NO_SMELL_TASTE = "NO_SMELL_TASTE";
    public static final String COUGH = "COUGH";
    public static final String SHORT_BREATH = "SHORT_BREATH";
    public static final String FEEL_TIRED = "FEEL_TIRED";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "voona.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + COVID_TABLE_NAME + "( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HEART_RATE + " INTEGER, "
                + RESP_RATE + " INTEGER, "
                + NAUSEA + " INTEGER, "
                + HEAD_ACHE + " INTEGER, "
                + DIARRHEA + " INTEGER, "
                + SOAR_THROAT + " INTEGER, "
                + FEVER + " INTEGER, "
                + MUSCLE_ACHE + " INTEGER, "
                + NO_SMELL_TASTE + " INTEGER, "
                + COUGH + " INTEGER, "
                + SHORT_BREATH + " INTEGER, "
                + FEEL_TIRED + " INTEGER" + " )";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(SymptomModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
      //  cv.put("COLUMN_SIGN", model.getSign());
       // cv.put("COLUMN_VALUE", model.getValue());

        //Insert
        long insert = db.insertWithOnConflict(COVID_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        if (insert == -1) {
       //     insert = db.update(COVID_TABLE_NAME, cv, "COLUMN_SIGN" + "=?", new String[]{model.getSign()});
        }
        return insert != -1;
    }

    public List<SymptomModel> getAll() {
        List<SymptomModel> returnList = new ArrayList<>();
        String query = "SELECT * FROM " + COVID_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String sign = cursor.getString(0);
                float value = cursor.getFloat(1);
             //   returnList.add(new SymptomModel(sign, value));
            } while (cursor.moveToNext());
        } else {
            //No data
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public boolean deleteOne(SymptomModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
       // String query = "DELETE FROM " + COVID_TABLE_NAME + " WHERE " + "COLUMN_SIGN" + " = '" + model.getSign() + "'";
       // Cursor cursor = db.rawQuery(query, null);
       // return !!cursor.moveToFirst();
        return true;
    }
}
