package com.bakigoal.dailyselfie;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.bakigoal.dailyselfie.receiver.AlarmReceiver;
import com.bakigoal.dailyselfie.provider.DbConstants;
import com.bakigoal.dailyselfie.utils.FileManager;

import java.io.File;
import java.io.IOException;

public class SelfieListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "SelfieListActivity";
    private static final String SELFIE_KEY = "selfiePath";
    private static final int REQ_SNAP_PHOTO = 0;
    private static final long INITIAL_DELAY = 2 * 60 * 1000;
    private static final long REPEAT_DELAY = 2 * 60 * 1000;
    private static final String ALARM_KEY = "alarms";

    private CursorAdapter cursorAdapter;
    private String imagePath;
    private SharedPreferences sharedPreferences;
    private PendingIntent alarmPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cursorAdapter = new SelfieCursorAdapter(this);
        getListView().setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);

        sharedPreferences = getSharedPreferences("selfie", Context.MODE_PRIVATE);
        setAlarm(null, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DbConstants.SELFIE_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        cursorAdapter.swapCursor(newCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "configuration is changing, saving instance state");
        outState.putString(SELFIE_KEY, imagePath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        imagePath = state.getString(SELFIE_KEY);
        Log.d(TAG, "restored selfieImagePath");
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "click on item at position " + position);
        Selfie selfie = (Selfie) cursorAdapter.getItem(position);
        Log.d(TAG, "fetched item " + selfie.getName());
        Intent intent = new Intent(this, SelfieActivity.class);
        intent.putExtra(SelfieActivity.EXTRA_NAME, selfie.getName());
        intent.putExtra(SelfieActivity.EXTRA_PATH, selfie.getPath());
        Log.i(TAG, "opening fullscreen activity");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_alarm);
        setAlarm(item, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_picture:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    File imageFile = null;
                    try {
                        Log.i(TAG, "creating temp file");
                        imageFile = FileManager.createImageFile();
                        imagePath = imageFile.getAbsolutePath();
                        Log.d(TAG, "temp file stored at : " + imagePath);
                    } catch (IOException e) {

                        Log.w(TAG, "unable to create image file", e);
                    }
                    if (imageFile != null) {
                        Log.i(TAG, "starting camera intent to take selfie");
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                        startActivityForResult(cameraIntent, REQ_SNAP_PHOTO);
                    }
                }
                return true;
            case R.id.action_alarm:
                Log.d(TAG, "click on toggle alarm");
                setAlarm(item, true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQ_SNAP_PHOTO == requestCode) {
            if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "user canceled, deleting file...");
                new File(imagePath).delete();
            }
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "processing selfie");
                Selfie selfie = new Selfie();
                selfie.setName(new File(imagePath).getName());
                selfie.setPath(imagePath);

                Log.i(TAG, "creating thumb bitmap");
                Bitmap fullSized = FileManager.getBitmapFromFile(imagePath);
                Float aspectRatio = ((float) fullSized.getHeight()) / (float) fullSized.getWidth();
                Bitmap thumb = Bitmap.createScaledBitmap(
                        fullSized,
                        120,
                        (int) (120 * aspectRatio),
                        false);
                String thumbPath = FileManager.getThumbPath(imagePath);
                selfie.setThumbPath(thumbPath);
                FileManager.saveBitmapToFile(thumb, thumbPath);

                Log.i(TAG, "recycling resources");
                fullSized.recycle();
                thumb.recycle();

                imagePath = null;

                Log.i(TAG, "adding selfie to adapter");
                ((SelfieCursorAdapter) cursorAdapter).addSelfie(selfie);
            }
        }

    }

    /**
     * Triggers the alarm if needed.
     * Also set the correct label for the item if provided.
     * Also toggle the alarm setting if requested
     *
     * @param item   the menu item to edit the label
     * @param toggle if the alarm parameter needs to be toggled
     */
    protected void setAlarm(MenuItem item, boolean toggle) {
        //Setting the alarm
        if (alarmPendingIntent == null) {
            Log.d(TAG, "initiating alarm pending intent");
            alarmPendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    0,
                    new Intent(getApplicationContext(), AlarmReceiver.class),
                    0);
        }

        boolean alarmEnabled = sharedPreferences.getBoolean(ALARM_KEY, true);
        if (toggle) {
            Log.d(TAG, "requesting alarm toggle");
            alarmEnabled = !alarmEnabled;
            sharedPreferences.edit().putBoolean(ALARM_KEY, alarmEnabled).apply();
        }

        AlarmManager alarm = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        if (alarmEnabled) {
            Log.i(TAG, "programming alarm");
            alarm.setRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + INITIAL_DELAY,
                    REPEAT_DELAY, alarmPendingIntent);
        } else {
            Log.i(TAG, "alarm disabled, canceling");
            alarm.cancel(alarmPendingIntent);
        }

        if (item != null) {
            if (alarmEnabled)
                item.setTitle(R.string.action_disable_alarm);
            else
                item.setTitle(R.string.action_enable_alarm);
        }
    }
}
