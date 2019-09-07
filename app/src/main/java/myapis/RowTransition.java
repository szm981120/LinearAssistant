package myapis;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.shen.linearassist.RowTransitionHome;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;

public class RowTransition {
  private static final int ACCURACY = 4;

  public static String rowTransition(double[][] matrix, boolean isUpper) {
    String result = "";
    int rowN = matrix.length;
    int columnN = rowN > 0 ? matrix[0].length : 0;
    NumericalCalculation numCal = new NumericalCalculation();
    if (isUpper) { // 上三角行变换
      int iOrder = 0;
      int jOrder = 0;
      /* 行初等变换 */
      int upperOrder = rowN > columnN ? columnN : rowN - 1;
      while (iOrder < upperOrder) {
        if (Math.abs(matrix[iOrder][jOrder]) < 0.001) {
          for (int i = iOrder + 1; i < rowN; i++) {
            if (Math.abs(matrix[i][jOrder]) > 0.001) {
              double[] tempRow = matrix[i];
              matrix[i] = matrix[iOrder];
              matrix[iOrder] = tempRow;
            }
          }
        }
        if (Math.abs(matrix[iOrder][jOrder]) < 0.001) {
          iOrder++;
          jOrder++;
          continue;
        }
        double pDiv = matrix[iOrder][jOrder];
        for (int j = jOrder; j < columnN; j++) {
          matrix[iOrder][j] = numCal.bigDecimalDiv(matrix[iOrder][j], pDiv, ACCURACY);
        }
        for (int i = iOrder + 1; i < rowN; i++) {
          double div = numCal.bigDecimalDiv(matrix[i][jOrder], matrix[iOrder][jOrder],
              ACCURACY); // 对下列行实施第三类行变换的乘数因子
          for (int j = jOrder; j < columnN; j++) { // 对下面的行实施第三类行变换，以求上三角阵
            matrix[i][j] = numCal.bigDecimalSub(matrix[i][j],
                numCal.bigDecimalMul(matrix[iOrder][j], div, ACCURACY), ACCURACY);
          }
        }
        iOrder++;
        jOrder++;
      }
      double div = jOrder == columnN ? matrix[iOrder][columnN - 1] : matrix[iOrder][jOrder];
      if (Math.abs(div) > 0.001) {
        for (int j = jOrder; j < columnN; j++) {
          matrix[iOrder][j] =
              numCal.bigDecimalDiv(matrix[iOrder][j], div, ACCURACY);
        }
      }
      // END 行初等变换
    } else {
      int iOrder = rowN - 1;
      int jOrder = columnN - 1;
      while (iOrder > 0 && jOrder >= 0) {
        /* minRow为除去已经变换后的行之外， 从左到右首个非零元最小的行索引*/
        if (Math.abs(matrix[iOrder][jOrder]) < 0.001) {
          for (int i = iOrder - 1; i >= 0; i--) {
            if (Math.abs(matrix[i][jOrder]) > 0.001) {
              double[] tempRow = matrix[i];
              matrix[i] = matrix[iOrder];
              matrix[iOrder] = tempRow;
            }
          }
        }
        if (Math.abs(matrix[iOrder][jOrder]) < 0.001) {
          iOrder--;
          jOrder--;
          continue;
        }
        double pDiv = matrix[iOrder][jOrder];
        for (int j = jOrder; j >= 0; j--) {
          matrix[iOrder][j] = numCal.bigDecimalDiv(matrix[iOrder][j], pDiv, ACCURACY);
        }
        for (int i = iOrder - 1; i >= 0; i--) {
          double div =
              numCal.bigDecimalDiv(matrix[i][jOrder], matrix[iOrder][jOrder], ACCURACY);
          for (int j = jOrder; j >= 0; j--) {
            matrix[i][j] = numCal.bigDecimalSub(matrix[i][j],
                numCal.bigDecimalMul(matrix[iOrder][j], div, ACCURACY), ACCURACY);
          }
        }
        iOrder--;
        jOrder--;
      }
      double div = jOrder == -1 ? matrix[iOrder][0] : matrix[iOrder][jOrder];
      if (Math.abs(div) > 0.001) {
        for (int j = columnN - 1; j >= 0; j--) {
          matrix[iOrder][j] =
              numCal.bigDecimalDiv(matrix[iOrder][j], div, ACCURACY);
        }
      }
    }
    result = MatrixStringToDouble.matrixDoubleToString(matrix);
    return result;
  }
}
