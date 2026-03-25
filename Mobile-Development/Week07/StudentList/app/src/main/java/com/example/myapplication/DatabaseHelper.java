package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SchoolManager.db";
    private static final int DATABASE_VERSION = 1;

    // Table Lop
    private static final String TABLE_LOP = "Lop";
    private static final String COLUMN_MA_LOP = "MaLop";
    private static final String COLUMN_TEN_LOP = "TenLop";

    // Table HocSinh
    private static final String TABLE_HOC_SINH = "HocSinh";
    private static final String COLUMN_MA_HS = "MaHS";
    private static final String COLUMN_TEN_HS = "TenHS";
    private static final String COLUMN_DTB = "DTB";
    private static final String COLUMN_MA_LOP_HS = "MaLop";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableLop = "CREATE TABLE " + TABLE_LOP + " (" +
                COLUMN_MA_LOP + " TEXT PRIMARY KEY, " +
                COLUMN_TEN_LOP + " TEXT)";
        
        String createTableHocSinh = "CREATE TABLE " + TABLE_HOC_SINH + " (" +
                COLUMN_MA_HS + " TEXT PRIMARY KEY, " +
                COLUMN_TEN_HS + " TEXT, " +
                COLUMN_DTB + " REAL, " +
                COLUMN_MA_LOP_HS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_MA_LOP_HS + ") REFERENCES " + TABLE_LOP + "(" + COLUMN_MA_LOP + "))";

        db.execSQL(createTableLop);
        db.execSQL(createTableHocSinh);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOC_SINH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOP);
        onCreate(db);
    }

    // KHÔNG gọi db.close() trong các hàm này để Database Inspector có thể giữ kết nối
    public void addLop(Lop lop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MA_LOP, lop.getMaLop());
        values.put(COLUMN_TEN_LOP, lop.getTenLop());
        db.insertWithOnConflict(TABLE_LOP, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void addHocSinh(HocSinh hs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MA_HS, hs.getMaHS());
        values.put(COLUMN_TEN_HS, hs.getTenHS());
        values.put(COLUMN_DTB, hs.getDtb());
        values.put(COLUMN_MA_LOP_HS, hs.getMaLop());
        db.insertWithOnConflict(TABLE_HOC_SINH, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<HocSinh> getAllHocSinh() {
        List<HocSinh> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HOC_SINH;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HocSinh hs = new HocSinh(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3)
                );
                list.add(hs);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
