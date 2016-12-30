package com.bakigoal.dailyselfie;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bakigoal.dailyselfie.utils.FileManager;

public class SelfieActivity extends AppCompatActivity {

  private static final String TAG = "SelfieActivity";
  public static final String EXTRA_NAME = "name";
  public static final String EXTRA_PATH = "path";

  private ImageView imageView;
  private ProgressBar progressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    Log.i(TAG, "onCreate");
    setContentView(R.layout.activity_selfie);

    imageView = (ImageView) findViewById(R.id.selfie_bitmap);
    progressBar = (ProgressBar) findViewById(R.id.selfie_progressBar);

    String selfieName = getIntent().getStringExtra(EXTRA_NAME);
    Log.i(TAG, "displaying fullscreen for selfie " + selfieName);
    String filePath = getIntent().getStringExtra(EXTRA_PATH);
    setTitle(selfieName);
    new LoadBitmapTask().execute(filePath);
  }

  private class LoadBitmapTask extends AsyncTask<String, String, Bitmap> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
      String selfiePath = params[0];

      return scaleImage(selfiePath);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
      imageView.setImageBitmap(result);
      progressBar.setVisibility(ProgressBar.GONE);
      super.onPostExecute(result);
    }

    private Bitmap scaleImage(String selfiePath) {
      Bitmap bm = FileManager.correctOrientedBitmap(selfiePath);
      if(bm == null){
        return null;
      }
      int nh = (int) (bm.getHeight() * (1024.0 / bm.getWidth()));
      return Bitmap.createScaledBitmap(bm, 1024, nh, true);
    }
  }
}
