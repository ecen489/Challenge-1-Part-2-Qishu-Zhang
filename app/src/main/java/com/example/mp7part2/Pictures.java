package com.example.mp7part2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Pictures extends SQLiteOpenHelper {

    private static final String NAME_DB = "cameraDB";
    private static final int VERSION_DB = 1;

    Pictures(Context context) {
        super(context, NAME_DB, null, VERSION_DB);
    }

    private void update_db(SQLiteDatabase sqlDB, int prev_v, int new_v) {
        if(prev_v < 1) {
            sqlDB.execSQL("CREATE TABLE PICTURES (" + "ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "IMAGE BLOB NOT NULL);");
        }
    }

    public long insertPictureToDB(SQLiteDatabase sqlDB, byte[] image) {
        ContentValues pictureValues = new ContentValues();
        pictureValues.put("IMAGE", image);
        long idValue = sqlDB.insert("PICTURES", null, pictureValues);
        return idValue;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        update_db(db, 0, VERSION_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int prev_v, int new_v) {
        update_db(db, prev_v, new_v);
    }


}
