package com.example.shen.linearassist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;
import MyWidget.EditTextWithDel;
import myapis.ElementaryOperation;
import myapis.MatrixStringToDouble;
import myapis.NumericalCalculation;
import myapis.RowTransition;

public class InverseMatrixHome extends AppCompatActivity {

  Button matrix_inverse_button;
  EditTextWithDel matrix_content;

  private static final int ACCURACY = 4;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inverse_matrix_home);
    matrix_content = (EditTextWithDel) findViewById(R.id.matrix_context);
    matrix_inverse_button = (Button) findViewById(R.id.matrix_inverse_button);
    matrix_inverse_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        inverse();
      }
    });
  }

  private void inverse() {
    String result = "";
    ElementaryOperation eleOp = new ElementaryOperation();
    try {
      String matrixContent = matrix_content.getText().toString();
      double[][] oldMatrix = MatrixStringToDouble.matrixStringToDouble(matrixContent);
      if (oldMatrix.length == 0) {
        throw new NonSquareMatrixException("矩阵不能为空");
      }
      if (oldMatrix.length != oldMatrix[0].length) {
        throw new NonSquareMatrixException("非方阵认为不可逆");
      }
      int rowN = oldMatrix.length;
      int columnN = rowN > 0 ? oldMatrix[0].length : 0;
      double[][] matrix = eleOp.inverse(oldMatrix, ACCURACY);
      result = MatrixStringToDouble.matrixDoubleToString(matrix);
      String[] temp3 = result.split("\\n+");
      int rowN_ = temp3.length;
      int columnN_ = 0;
      if (rowN_ > 0) {
        columnN_ = temp3[0].split("\\s+").length;
      }
      String[][] temp4 = new String[rowN_][columnN_]; // 行列式二维文本数组
      for (int i = 0; i < rowN_; i++) {
        temp4[i] = temp3[i].split("\\s+");
      }
      for (int i = 0; i < rowN_; i++) { // 获取行列式二维数值数组
        boolean noInverse = true;
        for (int j = 0; j < columnN_; j++) {
          Log.d("ij", result);
          if (Math.abs(Double.parseDouble(temp4[i][j])) > 1e-4) {
            noInverse = false;
          }
        }
        if (noInverse) {
          result = "该矩阵不可逆";
          break;
        }
      }
    } catch (NonNumericalException e) {
      result = "行列式必须是纯数字！";
    } catch (NonSquareMatrixException e) {
      result = e.getMessage();
    } finally {
      final AlertDialog.Builder normalDialog = new AlertDialog.Builder(InverseMatrixHome.this);
      normalDialog.setTitle("结果");
      normalDialog.setMessage(result);
      normalDialog.setPositiveButton("复制到剪切板", new DialogInterface.OnClickListener() {
        private String result;

        @Override
        public void onClick(DialogInterface dialog, int which) {
          copy(result);
        }

        public DialogInterface.OnClickListener accept(String str) {
          this.result = str;
          return this;
        }
      }.accept(result));
      normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });
      normalDialog.create().show();
    }
  }

  private boolean copy(String copyStr) {
    try {
      //获取剪贴板管理器
      ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
      // 创建普通字符型ClipData
      ClipData mClipData = ClipData.newPlainText("Label", copyStr);
      // 将ClipData内容放到系统剪贴板里。
      cm.setPrimaryClip(mClipData);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
