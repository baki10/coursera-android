package com.bakigoal.dailyselfie.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FileManager {

  /**
   * Read a bitmap from a file
   *
   * @param filePath the full path of the bitmap
   * @return the decoded bitmap in full size
   */
  public static Bitmap getBitmapFromFile(String filePath) {
    return BitmapFactory.decodeFile(filePath);
  }

  /**
   * Saves a bitmap to a file
   *
   * @param bitmap   the bitmap to save
   * @param filePath the full path of the file
   * @return true if save is successful, false otherwise
   */
  public static boolean saveBitmapToFile(Bitmap bitmap, String filePath) {
    if (!externalStorageMounted()) {
      return false;
    }

    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
      bos.flush();
      bos.close();
    } catch (FileNotFoundException e) {
      return false;
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  private static boolean externalStorageMounted() {
    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  }

  /**
   * Creates a path for storing the image thumbnail based on full sized picture
   *
   * @param imagePath the path to the full sized bitmap
   * @return the full path to the thumb bitmap
   */
  public static String getThumbPath(String imagePath) {
    File image = new File(imagePath);
    String path = image.getAbsolutePath();

    String fileWithoutExt = image.getName().split("\\.")[0];
    return path + fileWithoutExt + "_thumb.jpg";
  }

  /**
   * Creates a temporary empty image file in the selfie folder
   * It is used for the camera intent
   *
   * @return the filename
   * @throws IOException
   */
  public static File createImageFile(Context context) throws IOException {
    // Create an image file name
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    String timeStamp = dateFormat.format(Calendar.getInstance().getTime());
    String imageFileName = "selfie_" + timeStamp;

    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

    return File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );
  }
}
