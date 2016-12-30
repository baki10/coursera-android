package com.bakigoal.moderartui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private Cell[] cells = new Cell[5];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    cells[0] = new Cell(findViewById(R.id.cell_1));
    cells[1] = new Cell(findViewById(R.id.cell_2));
    cells[2] = new Cell(findViewById(R.id.cell_3));
    cells[3] = new Cell(findViewById(R.id.cell_4));
    cells[4] = new Cell(findViewById(R.id.cell_5));

    SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        for (Cell cell : cells) {
          cell.changeBackground(i);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.more_information) {
      new MoreInfoDialog().show(getFragmentManager(), TAG);
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
}
