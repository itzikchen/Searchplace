package ithak.co.il.searchplaces;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ithak on 8/10/2016.
 */
public class MyPlacesAdapter extends CursorAdapter {

    Context context;

    public MyPlacesAdapter(Context context, Cursor c) {
        super(context, c);
        this.context=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v= LayoutInflater.from(context).inflate(R.layout.place_item, parent, false); // the XML file name
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView PlaceItemNameTV = (TextView) view.findViewById(R.id.PlaceItemNameTV);
        TextView PlaceItemAddressTV = (TextView) view.findViewById(R.id.PlaceItemAddressTV);
        TextView PlaceItemSQLIDTV = (TextView) view.findViewById(R.id.PlaceItemSQLIDTV);

        String name= cursor.getString(cursor.getColumnIndex(DBConstants.placeName));
        String address= cursor.getString(cursor.getColumnIndex(DBConstants.placeAddress));

        PlaceItemNameTV.setText(name);
        PlaceItemAddressTV.setText(address);
        PlaceItemSQLIDTV.setText(""+cursor.getPosition());

        ImageView imageView= (ImageView) view.findViewById(R.id.imageView);

        if(0==0)
            imageView.setImageResource(R.drawable.pizza);
    }
}
