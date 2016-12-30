package com.bakigoal.moderartui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

public class Cell {

  private View view;
  private final int fromColor;

  public Cell(View view) {
    this.view = view;
    this.fromColor = ((ColorDrawable) view.getBackground()).getColor();
  }

  public void changeBackground(int progress) {
    if (fromColor == Color.WHITE || fromColor == Color.GRAY) {
      return;
    }
    int toColor = invertColor(fromColor);

    int fromRed = Color.red(fromColor);
    int fromGreen = Color.green(fromColor);
    int fromBlue = Color.blue(fromColor);

    int toRed = Color.red(toColor);
    int toGreen = Color.green(toColor);
    int toBlue = Color.blue(toColor);

    view.setBackgroundColor(Color.rgb(
        (int) (fromRed + (toRed - fromRed) * (progress / 100f)),
        (int) (fromGreen + (toGreen - fromGreen) * (progress / 100f)),
        (int) (fromBlue + (toBlue - fromBlue) * (progress / 100f))));
    view.invalidate();
  }

  private int invertColor(int fromColor) {
    return (0x00FFFFFF - (fromColor | 0xFF000000)) | (fromColor & 0xFF000000);
  }
}
