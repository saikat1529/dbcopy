package com.bit_makers.databasemod;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Developed by Fojle Rabbi Saikat on 4/16/2017.
 * Owned by Bitmakers Ltd.
 * Contact fojle.rabbi@bitmakers-bd.com
 */

public class DB extends SQLiteOpenHelper {

    public DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
