package com.example.shen.linearassist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import myapis.NumericalCalculation;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;
import MyWidget.EditTextWithDel;

public class DeterminantHome extends AppCompatActivity {

  private static final int ACCURACY = 4;
  Button calculate_determinant_button;
  EditTextWithDel determinant_content;
  TextView determinant_result;

  private NumericalCalculation numCal = new NumericalCalculation();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_determinant_home);
    calculate_determinant_button = (Button) findViewById(R.id.calculate_determinant_button);
    determinant_content = (EditTextWithDel) findViewById(R.id.determinant_content);
    determinant_result = (TextView) findViewById(R.id.determinant_result);
    calculate_determinant_button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      String determinantText = determinant_content.getText().toString(); // 获取行列式信息
      try {
        double resultD = getDeterminant(determinantText);
        determinant_result.setText(new DecimalFormat("#.0000").format(resultD));
      } catch (NonNumericalException e) {
        determinant_result.setText(e.getMessage());
      } catch (NonSquareMatrixException e) {
        determinant_result.setText(e.getMessage());
      }
    }
  });
}

  public double getDeterminant(String determinantText)
      throws NonNumericalException, NonSquareMatrixException {
    if (!numCal.isNumerical(determinantText)) { // 非纯数字异常
      throw new NonNumericalException("行列式中存在非法字符！");
    }
    if (determinantText.isEmpty()) {
      throw new NonSquareMatrixException("矩阵不能为空");
    }
    while (determinantText.charAt(0) == '\n') {
      determinantText = determinantText.substring(1);
    }
    String[] temp1 = determinantText.split("\\n+"); // 行列式文本按行截取
    int order = temp1.length;
    for (int i = 0; i < order; i++) {
      while (temp1[i].charAt(0) == ' ') {
        temp1[i] = temp1[i].substring(1);
      }
    }
    for (int i = 0; i < order; i++) {
      if (temp1[i].split("\\s+").length != order) {
        throw new NonSquareMatrixException("第" + i + "行的列数与行数不符");
      }
    }
    String[][] temp2 = new String[order][order]; // 行列式二维文本数组
    double determinant[][] = new double[order][order]; // 行列式二维数值数组
    boolean noZero = true;
    for (int i = 0; i < order; i++) {
      temp2[i] = temp1[i].split("\\s+");
    }
    for (int i = 0; i < order; i++) { // 获取行列式二维数值数组
      for (int j = 0; j < order; j++) {
        determinant[i][j] = Double.parseDouble(temp2[i][j]);
      }
    }
    double resultD = 1; // 最终结果
    int minRow = 0;
    int pOrder = 0;
    /* 行初等变换 */
    while (pOrder < order - 1) {
      /* minRow为除去已经变换后的行之外， 从左到右首个非零元最小的行索引*/
      minRow = pOrder;
      double tempMin = Double.MAX_VALUE;
      for (int i = pOrder; i < order; i++) {
        if (Math.abs(determinant[i][pOrder]) < tempMin && determinant[i][pOrder] != 0) {
          tempMin = determinant[i][pOrder];
          minRow = i;
        }
      }
      if (determinant[minRow][pOrder] == 0) {
        break;
      }
      /* minRow行由于有最小的首个非零元，将其换到剩余行的顶部有利于行变换时减少小数出现 */
      if (minRow != pOrder) {
        double[] tempRow = determinant[minRow];
        determinant[minRow] = determinant[pOrder];
        determinant[pOrder] = tempRow;
        resultD = -resultD; // 第一类行变换变号
      }
      for (int i = pOrder + 1; i < order; i++) {
        double div = numCal.bigDecimalDiv(determinant[i][pOrder],
            determinant[pOrder][pOrder], ACCURACY); // 对下列行实施第三类行变换的乘数因子
        for (int j = pOrder; j < order; j++) { // 对下面的行实施第三类行变换，以求上三角阵
          determinant[i][j] =
              numCal.bigDecimalSub(determinant[i][j],
                  numCal.bigDecimalMul(determinant[pOrder][j], div, ACCURACY), ACCURACY);
        }
      }
      pOrder++;
    } // END 行初等变换
    /* 判断主对角线上是否有零元素 */
    for (int i = 0; i < order; i++) {
      if (determinant[i][i] == 0) noZero = false;
    }
    if (noZero) {
      for (int i = 0; i < order; i++) {
        resultD *= determinant[i][i];
      }
    } else { // 如果有零元素，结果为0
      resultD = 0;
    }
    return resultD;
  }
}
