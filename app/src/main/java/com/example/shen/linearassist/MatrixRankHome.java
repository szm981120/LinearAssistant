package com.example.shen.linearassist;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;
import MyWidget.EditTextWithDel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import myapis.MatrixStringToDouble;
import myapis.RowTransition;
import org.w3c.dom.Text;

public class MatrixRankHome extends AppCompatActivity {

  EditTextWithDel matrix_content;
  TextView rank_result_txt;
  Button matrix_rank_btn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_matrix_rank_home);
    matrix_content = (EditTextWithDel) findViewById(R.id.matrix_content);
    rank_result_txt = (TextView) findViewById(R.id.rank_result_txt);
    matrix_rank_btn = (Button) findViewById(R.id.matrix_rank_btn);
    matrix_rank_btn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String matrixContent = matrix_content.getText().toString();
        try {
          int rank = getRank(matrixContent);
          rank_result_txt.setText(String.valueOf(rank));
        } catch (NonNumericalException e) {
          rank_result_txt.setText("矩阵必须为纯数字！");
        } catch (NonSquareMatrixException e) {
          rank_result_txt.setText(e.getMessage());
        }
      }
    });
  }

  public int getRank(String matrixContent) throws NonNumericalException, NonSquareMatrixException {
    double[][] matrix = MatrixStringToDouble.matrixStringToDouble(matrixContent);
    matrix = MatrixStringToDouble.matrixStringToDouble(RowTransition.rowTransition(matrix, true));
    int rowN = matrix.length;
    int columnN = matrix[0].length;
    int rank = 0;
    for (int i = 0; i < rowN; i++) {
      if (matrix[i][columnN - 1] != 0) {
        rank++;
      } else {
        break;
      }
    }
    return rank;
  }
}
