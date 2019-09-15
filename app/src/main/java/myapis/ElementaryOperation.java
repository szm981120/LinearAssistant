package myapis;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;
import Exceptions.NoninvertibleException;
import com.example.shen.linearassist.DeterminantHome;

public class ElementaryOperation {
  private static NumericalCalculation numCal = new NumericalCalculation();

  public double[][] multiply(double[][] m1, double[][] m2, int accuracy) {
    int rRow = m1.length;
    int rColumn = m2[0].length;
    double[][] result = new double[rRow][rColumn];
    for (int i = 0; i < rRow; i++) {
      for (int j = 0; j < rColumn; j++) {
        double temp = 0;
        for (int k = 0; k < m1[i].length; k++) {
          temp = numCal.bigDecimalAdd(temp, numCal.bigDecimalMul(m1[i][k], m2[k][j], accuracy),
              accuracy);
        }
        result[i][j] = temp;
      }
    }
    return result;
  }

  public double[][] add(double[][] m1, double[][] m2, int accuracy) {
    int rRow = m1.length;
    int rColumn = m2.length;
    double[][] result = new double[rRow][rColumn];
    for (int i = 0; i < rRow; i++) {
      for (int j = 0; j < rColumn; j++) {
        result[i][j] = numCal.bigDecimalAdd(m1[i][j], m2[i][j], accuracy);
      }
    }
    return result;
  }

  public double[][] substraction(double[][] m1, double[][] m2, int accuracy) {
    int rRow = m1.length;
    int rColumn = m2.length;
    double[][] result = new double[rRow][rColumn];
    for (int i = 0; i < rRow; i++) {
      for (int j = 0; j < rColumn; j++) {
        result[i][j] = numCal.bigDecimalSub(m1[i][j], m2[i][j], accuracy);
      }
    }
    return result;
  }

  public double[][] inverse(double[][] m, int accuracy)
      throws NonNumericalException, NonSquareMatrixException, NoninvertibleException {
    int rowN = m.length;
    int columnN = rowN > 0 ? m[0].length : 0;
    int columnN2 = columnN * 2;
    double[][] matrix = new double[rowN][columnN2];
    double[][] inverse = new double[rowN][columnN];
    for (int i = 0; i < rowN; i++) {
      for (int j = 0; j < columnN; j++) {
        matrix[i][j] = m[i][j];
      }
      for (int j = columnN; j < columnN2; j++) {
        if (j - rowN == i) {
          matrix[i][j] = 1;
        } else {
          matrix[i][j] = 0;
        }
      }
    }
    String upperResult = RowTransition.rowTransition(matrix, true);
    matrix = MatrixStringToDouble.matrixStringToDouble(upperResult);
    NumericalCalculation numCal = new NumericalCalculation();
    int iOrder = rowN - 1;
    int jOrder = columnN - 1;
    int minRow;
    while (iOrder >= 0 && jOrder >= 0) {
      /* minRow为除去已经变换后的行之外， 从左到右首个非零元最小的行索引*/
      minRow = iOrder;
      if (matrix[iOrder][jOrder] == 0) {
        for (int i = iOrder - 1; i >= 0; i--) {
          if (matrix[i][jOrder] != 0) {
            minRow = i;
          }
        }
      }
      /* minRow行由于有最小的首个非零元，将其换到剩余行的顶部有利于行变换时减少小数出现 */
      if (matrix[minRow][jOrder] == 0) {
        break;
      }
      if (minRow != iOrder) {
        double[] tempRow = matrix[minRow];
        matrix[minRow] = matrix[iOrder];
        matrix[iOrder] = tempRow;
      }
      double pDiv = matrix[iOrder][jOrder];
      for (int j = columnN2 - 1; j >= 0; j--) {
        matrix[iOrder][j] = numCal.bigDecimalDiv(matrix[iOrder][j], pDiv, accuracy);
      }
      for (int i = iOrder - 1; i >= 0; i--) {
        double div = numCal.bigDecimalDiv(matrix[i][jOrder], matrix[iOrder][jOrder], accuracy);
        for (int j = columnN2 - 1; j >= 0; j--) {
          matrix[i][j] = numCal.bigDecimalSub(matrix[i][j],
              numCal.bigDecimalMul(matrix[iOrder][j], div, accuracy), accuracy);
        }
        pDiv = matrix[i][jOrder - 1];
        for (int j = columnN2 - 1; j >= 0; j--) {
          matrix[i][j] = numCal.bigDecimalDiv(matrix[i][j], pDiv, accuracy);
        }
      }
      iOrder--;
      jOrder--;
    }
    for (int i = 0; i < rowN; i++) {
      for (int j = 0; j < columnN; j++) {
        inverse[i][j] = matrix[i][j + columnN];
      }
    }
    DeterminantHome dt = new DeterminantHome();
    if (Math.abs(dt.getDeterminant(MatrixStringToDouble.matrixDoubleToString(inverse))) < 0.001) {
      throw new NoninvertibleException("该矩阵不可逆");
    }
    return inverse;
  }

  public double[][] transpose(double[][] matrix) throws NonSquareMatrixException {
    int rowN = matrix.length;
    if (rowN == 0) {
      throw new NonSquareMatrixException("矩阵不能为空");
    }
    int columnN = matrix[0].length;
    double[][] matrixT = new double[rowN][columnN];
    for (int i = 0; i < rowN; i++) {
      for (int j = 0; j < columnN; j++) {
        matrixT[i][j] = matrix[j][i];
      }
    }
    return matrixT;
  }
}
