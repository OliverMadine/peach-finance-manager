package com.olivermadine.financeManager.financeManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

// Database of budget categories and spending progress.
public class FinanceSqlHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "category";
    private static final String CATEGORY_NAME = "name";
    private static final String BUDGET = "budget";
    private static final String SPENT = "spent";

    protected FinanceSqlHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createTable =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        CATEGORY_NAME + " TEXT " + "PRIMARY KEY," +
                        BUDGET + " FLOAT, " +
                        SPENT + " FLOAT" +
                        ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    protected void insert(String categoryName, float budget) {
        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues contentValues = new ContentValues();

        contentValues.put(CATEGORY_NAME, categoryName);
        contentValues.put(BUDGET, budget);
        contentValues.put(SPENT, 0f);

        db.insert(TABLE_NAME, null, contentValues);
    }

    protected void setSpent(String categoryName, float spent) {
        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues contentValues = new ContentValues();

        contentValues.put(SPENT, spent);
        db.update(TABLE_NAME, contentValues, CATEGORY_NAME + "= ?", new String[]{categoryName});
    }

    protected float getSpent(String categoryName) {
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(TABLE_NAME, new String[]{SPENT},
                CATEGORY_NAME + "= ?",
                new String[]{categoryName},
                null, null, null, null);
        cursor.moveToNext();

        return cursor.getFloat(0);
    }

    protected float getBudget(String categoryName) {
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(TABLE_NAME, new String[]{BUDGET},
                CATEGORY_NAME + "= ?",
                new String[]{categoryName},
                null, null, null, null);
        cursor.moveToNext();

        return cursor.getFloat(0);
    }

    protected List<String> getCategories() {
        final SQLiteDatabase db = getReadableDatabase();
        final List<String> categories = new ArrayList<>();
        Cursor row = db.query(TABLE_NAME, null, null, null, null, null, CATEGORY_NAME, null);
        while (row.moveToNext()) {
            categories.add(row.getString(0));
        }
        return categories;
    }
}
