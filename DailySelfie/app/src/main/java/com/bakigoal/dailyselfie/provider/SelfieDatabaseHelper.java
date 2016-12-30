package com.bakigoal.dailyselfie.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class SelfieDatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "selfie_db";
  private static final int DATABASE_VERSION = 1;

  private static final String CREATE_STATEMENT =
      "CREATE TABLE " + DbConstants.SELFIE_TABLE_NAME + " (" +
          DbConstants.SELFIE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
          DbConstants.SELFIE_COLUMN_NAME + " TEXT, " +
          DbConstants.SELFIE_COLUMN_PATH + " TEXT, " +
          DbConstants.SELFIE_COLUMN_THUMB + " TEXT" +
          ")";

  SelfieDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_STATEMENT);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + DbConstants.SELFIE_TABLE_NAME);
    onCreate(db);
  }

}
