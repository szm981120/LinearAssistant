package com.example.shen.linearassist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import myapis.MatrixStringToDouble;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  Button determinant_button;
  Button row_transition_button;
  Button inverse_matrix_button;
  Button calculation_button;
  Button rank_btn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    determinant_button = (Button) findViewById(R.id.determinant_button);
    determinant_button.setOnClickListener(this);
    row_transition_button = (Button) findViewById(R.id.row_transition_button);
    row_transition_button.setOnClickListener(this);
    inverse_matrix_button = (Button) findViewById(R.id.inverse_matrix_button);
    inverse_matrix_button.setOnClickListener(this);
    calculation_button = (Button) findViewById(R.id.calculation_button);
    calculation_button.setOnClickListener(this);
    rank_btn = (Button) findViewById(R.id.rank_btn);
    rank_btn.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.determinant_button:
        Intent intent = new Intent(MainActivity.this, DeterminantHome.class);
        startActivity(intent);
        break;
      case R.id.row_transition_button:
        Intent intent1 = new Intent(MainActivity.this, RowTransitionHome.class);
        startActivity(intent1);
        break;
      case R.id.inverse_matrix_button:
        Intent intent2 = new Intent(MainActivity.this, InverseMatrixHome.class);
        startActivity(intent2);
        break;
      case R.id.calculation_button:
        Intent intent3 = new Intent(MainActivity.this, MatrixCalculationHome.class);
        startActivity(intent3);
        break;
      case R.id.rank_btn:
        Intent intent4 = new Intent(MainActivity.this, MatrixRankHome.class);
        startActivity(intent4);
        break;
      default:
        break;
    }
  }
}
