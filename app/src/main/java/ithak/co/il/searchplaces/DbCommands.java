package ithak.co.il.searchplaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by ithak on 8/10/2016.
 */
public class DbCommands {

    Context context;
    MySqlOpenHelper helper;

    public DbCommands(Context c)
    {
        context=c;
        helper = new MySqlOpenHelper(context);
    }

    public  void addPlace(Place place)
    {
        ContentValues cv= new ContentValues();
        cv.put(DBConstants.placeName, place.name);
        cv.put(DBConstants.placeAddress, place.address);
        cv.put(DBConstants.placeLatitude, place.latitude);
        cv.put(DBConstants.placeLongitude, place.longitude);
        helper.getWritableDatabase().insert(DBConstants.tableName, null,cv );
    }

    public void updatePlace(Place place)
    {
        ContentValues cv= new ContentValues();
        cv.put(DBConstants.placeName, place.name);
        cv.put(DBConstants.placeAddress, place.address);
        cv.put(DBConstants.placeLatitude, place.latitude);
        cv.put(DBConstants.placeLongitude, place.longitude);

        //update artists set name="" , year =1985 where id=8
        //update contacts set name="yossi1" lastname="cohen1" where name="yosef" and lastname="levi"
        //update contacts set name="yossi1" lastname="cohen1" where name="?" and lastname="?"

        helper.getWritableDatabase().update(DBConstants.tableName,cv, "_id=?",  new String []{""+place.sqlId } );
    }

    public Cursor getDataFromDBAsCursor()
    {
        Cursor tempTableDataCursor=   helper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.tableName, null);
        return  tempTableDataCursor;
    }

    public Cursor getDataFromDBAsCursor(int SQLID)
    {
        Cursor tempTableDataCursor=   helper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.tableName+" WHERE _id="+SQLID , null);
        return  tempTableDataCursor;
    }

    public void deletePlace(int dbID) {
        helper.getWritableDatabase().delete(DBConstants.tableName, "_id=?" , new String[]{""+dbID});
    }

    public void deleteAllPlaces() {
        try {
            helper.getWritableDatabase().delete(DBConstants.tableName, null, null);
        } catch(SQLiteException e) {
            Log.d("My App", "caught");
        }
    }
}
