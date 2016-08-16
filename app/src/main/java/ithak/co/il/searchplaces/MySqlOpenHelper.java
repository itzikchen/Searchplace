package ithak.co.il.searchplaces;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ithak on 8/10/2016.
 */
public class MySqlOpenHelper extends SQLiteOpenHelper {

    public MySqlOpenHelper(Context context) {
        super(context, DBConstants.placeName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String command="CREATE TABLE  "+DBConstants.tableName+" ( "+DBConstants.idcolumn+"  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " "+ DBConstants.placeName +" TEXT," +
                " "+ DBConstants.placeAddress +" TEXT," +
                " "+ DBConstants.placeLatitude +" TEXT," +
                " "+ DBConstants.placeLongitude +" TEXT )";

        db.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
