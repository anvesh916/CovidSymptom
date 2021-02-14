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

    public boolean addOne(SymptomModel model, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(HEART_RATE, model.getHEART_RATE());
        cv.put(RESP_RATE, model.getRESP_RATE());
        cv.put(NAUSEA, model.getNAUSEA());
        cv.put(HEAD_ACHE, model.getHEAD_ACHE());
        cv.put(DIARRHEA, model.getDIARRHEA());
        cv.put(SOAR_THROAT, model.getSOAR_THROAT());
        cv.put(FEVER, model.getFEVER());
        cv.put(MUSCLE_ACHE, model.getMUSCLE_ACHE());
        cv.put(NO_SMELL_TASTE, model.getNO_SMELL_TASTE());
        cv.put(COUGH, model.getCOUGH());
        cv.put(SHORT_BREATH, model.getSHORT_BREATH());
        cv.put(FEEL_TIRED, model.getFEEL_TIRED());

        //Insert
        long insert = db.insertWithOnConflict(COVID_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        if (insert == -1) {
            insert = db.update(COVID_TABLE_NAME, cv, ID + "=?", new String[]{String.valueOf(id)});
        }
        db.close();
        return insert != -1;
    }

    public List<SymptomModel> getAll() {
        List<SymptomModel> returnList = new ArrayList<>();
        String query = "SELECT * FROM " + COVID_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
//                int sign = cursor.getInt(1);
//                float value = cursor.getFloat(1);
                //   returnList.add(new SymptomModel(sign, value));
            } while (cursor.moveToNext());
        } else {
            //No data
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public SymptomModel getByID(int id) {
        SymptomModel symptom = new SymptomModel();
        String query = "SELECT * FROM " + COVID_TABLE_NAME + " WHERE " + ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            symptom.setRESP_RATE(cursor.getInt(1));
            symptom.setHEART_RATE(cursor.getInt(2));
            symptom.setNAUSEA(cursor.getInt(3));
            symptom.setHEAD_ACHE(cursor.getInt(4));
            symptom.setDIARRHEA(cursor.getInt(5));
            symptom.setSOAR_THROAT(cursor.getInt(6));
            symptom.setFEVER(cursor.getInt(7));
            symptom.setMUSCLE_ACHE(cursor.getInt(8));
            symptom.setNO_SMELL_TASTE(cursor.getInt(9));
            symptom.setCOUGH(cursor.getInt(10));
            symptom.setSHORT_BREATH(cursor.getInt(11));
            symptom.setFEEL_TIRED(cursor.getInt(12));
        }
        cursor.close();
        db.close();
        return symptom;
    }

    public boolean deleteOne(SymptomModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        // String query = "DELETE FROM " + COVID_TABLE_NAME + " WHERE " + "COLUMN_SIGN" + " = '" + model.getSign() + "'";
        // Cursor cursor = db.rawQuery(query, null);
        // return !!cursor.moveToFirst();
        return true;
    }

    public boolean deleteById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + COVID_TABLE_NAME + " WHERE " + ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        return !!cursor.moveToFirst();
    }
}
