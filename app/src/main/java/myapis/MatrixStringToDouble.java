package myapis;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;

public class MatrixStringToDouble {

  public static double[][] matrixStringToDouble(String matrixString)
      throws NonNumericalException, NonSquareMatrixException {
    NumericalCalculation numCal = new NumericalCalculation();
    if (!numCal.isNumerical(matrixString)) { // 非纯数字异常
      throw new NonNumericalException();
    }
    if (matrixString.isEmpty()) {
      throw new NonSquareMatrixException("矩阵不能为空");
    }
    while (matrixString.charAt(0) == '\n') {
      matrixString = matrixString.substring(1);
    }
    String[] temp1 = matrixString.split("\\n+"); // 行列式文本按行截取
    int rowN = temp1.length;
    for (int i = 0; i < rowN; i++) {
      while (temp1[i].charAt(0) == ' ') {
        temp1[i] = temp1[i].substring(1);
      }
    }
    for (int i = 0; i < rowN - 1; i++) {
      if (temp1[i].split("\\s+").length != temp1[i + 1].split("\\s+").length) {
        throw new NonSquareMatrixException("第" + i + "行的列数与其他行行数不符");
      }
    }
    int columnN = 0;
    if (rowN > 0) {
      columnN = temp1[0].split("\\s+").length;
    }
    String[][] temp2 = new String[rowN][columnN]; // 行列式二维文本数组
    double matrix[][] = new double[rowN][columnN]; // 行列式二维数值数组
    for (int i = 0; i < rowN; i++) {
      temp2[i] = temp1[i].split("\\s+");
    }
    for (int i = 0; i < rowN; i++) { // 获取行列式二维数值数组
      for (int j = 0; j < columnN; j++) {
        matrix[i][j] = Double.parseDouble(temp2[i][j]);
      }
    }
    return matrix;
  }

  public static String matrixDoubleToString(double[][] matrix) {
    String result = "";
    int rowN = matrix.length;
    int columnN = matrix[0].length;
    if (rowN == 0 || columnN == 0) {
      return null;
    }
    for (int i = 0; i < rowN; i++) {
      for (int j = 0; j < columnN; j++) {
        result += String.valueOf(matrix[i][j]);
        if (j != columnN - 1) {
          result += " ";
        }
      }
      result += "\n";
    }
    return result;
  }
}
