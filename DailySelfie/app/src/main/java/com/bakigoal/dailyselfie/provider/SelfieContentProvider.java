package com.bakigoal.dailyselfie.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

public class SelfieContentProvider extends ContentProvider {

  private SelfieDatabaseHelper dbHelper;

  @Override
  public boolean onCreate() {
    dbHelper = new SelfieDatabaseHelper(getContext());
    return true;
  }

  @Override
  public int delete(@NonNull Uri uri, String id, String[] selectionArgs) {
    dbHelper.getWritableDatabase().delete(DbConstants.SELFIE_TABLE_NAME,
        DbConstants.SELFIE_COLUMN_ID + "=" + id, selectionArgs);
    if (getContext() != null) {

      getContext().getContentResolver().notifyChange(uri, null);
    }
    return 0;
  }

  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Override
  public Uri insert(@NonNull Uri uri, ContentValues values) {
    long rowID = dbHelper.getWritableDatabase().insert(DbConstants.SELFIE_TABLE_NAME, "", values);
    if (rowID > 0) {
      Uri fullUri = ContentUris.withAppendedId(DbConstants.SELFIE_URI, rowID);
      if (getContext() != null) {
        getContext().getContentResolver().notifyChange(fullUri, null);
      }
      return fullUri;
    }
    throw new SQLException("Failed to add record into" + uri);
  }

  @Override
  public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    qb.setTables(DbConstants.SELFIE_TABLE_NAME);

    Cursor cursor = qb.query(dbHelper.getWritableDatabase(), projection, selection,
        selectionArgs, null, null, sortOrder);

    if (getContext() == null) {
      return cursor;
    }
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Override
  public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    return 0;
  }
}