package com.bakigoal.dailyselfie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bakigoal.dailyselfie.model.Selfie;
import com.bakigoal.dailyselfie.provider.DbConstants;
import com.bakigoal.dailyselfie.utils.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelfieCursorAdapter extends CursorAdapter {

  private static final String TAG = "SelfieCursorAdapter";
  private Context context;
  private List<Selfie> selfieList;
  private Map<String, Bitmap> bitmaps;

  public SelfieCursorAdapter(Context context) {
    super(context, null, 0);
    this.context = context;
    selfieList = new ArrayList<>();
    bitmaps = new HashMap<>();
    Log.d(TAG, "Adapter created");
  }

  private static class ViewHolder {
    ImageView image;
    TextView name;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View newView;
    ViewHolder holder = new ViewHolder();

    newView = LayoutInflater.from(context).inflate(R.layout.selfie_item_view, parent, false);
    holder.image = (ImageView) newView.findViewById(R.id.selfie_bitmap);
    holder.name = (TextView) newView.findViewById(R.id.selfie_name);

    newView.setTag(holder);

    return newView;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    ViewHolder holder = (ViewHolder) view.getTag();
    Bitmap bitmap;
    String path = cursor.getString(cursor.getColumnIndex(DbConstants.SELFIE_COLUMN_THUMB));
    if (bitmaps.containsKey(path)) {
      bitmap = bitmaps.get(path);
    } else {
      bitmap = FileManager.getBitmapFromFile(path);
      bitmaps.put(path, bitmap);
    }
    holder.image.setImageBitmap(bitmap);
    holder.name.setText(cursor.getString(cursor.getColumnIndex(DbConstants.SELFIE_COLUMN_NAME)));
  }

  @Override
  public Object getItem(int position) {
    return selfieList.get(position);
  }

  @Override
  public Cursor swapCursor(Cursor newCursor) {
    Cursor oldCursor = super.swapCursor(newCursor);
    selfieList.clear();
    if (newCursor != null) {
      newCursor.moveToFirst();
      while (!newCursor.isAfterLast()) {
        Selfie selfie = Selfie.fromCursor(newCursor);
        selfieList.add(selfie);
        newCursor.moveToNext();
      }
    }

    return oldCursor;
  }

  public void addSelfie(Selfie selfie) {
    selfieList.add(selfie);

    ContentValues values = new ContentValues();

    values.put(DbConstants.SELFIE_COLUMN_NAME, selfie.getName());
    values.put(DbConstants.SELFIE_COLUMN_PATH, selfie.getPath());
    values.put(DbConstants.SELFIE_COLUMN_THUMB, selfie.getThumbPath());

    context.getContentResolver().insert(DbConstants.SELFIE_URI, values);
  }

  public void removeSelfie(int position) {
    Selfie selfie = (Selfie) getItem(position);
    selfieList.remove(position);
    context.getContentResolver().delete(DbConstants.SELFIE_URI, String.valueOf(selfie.getId()), null);
  }
}
