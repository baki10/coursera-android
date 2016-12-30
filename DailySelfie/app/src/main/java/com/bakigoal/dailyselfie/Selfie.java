package com.bakigoal.dailyselfie;

import android.database.Cursor;
import android.graphics.Bitmap;

import com.bakigoal.dailyselfie.provider.DbConstants;

public class Selfie {
    private int id;
    private String name;
    private String path;
    private String thumbPath;
    private Bitmap bmp;

    public static Selfie fromCursor(Cursor cursor) {
        Selfie selfie = new Selfie();

        selfie.setId(cursor.getInt(cursor.getColumnIndex(DbConstants.SELFIE_COLUMN_ID)));
        selfie.setPath(cursor.getString(cursor.getColumnIndex(DbConstants.SELFIE_COLUMN_PATH)));
        selfie.setName(cursor.getString(cursor.getColumnIndex(DbConstants.SELFIE_COLUMN_NAME)));
        return selfie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }
}
