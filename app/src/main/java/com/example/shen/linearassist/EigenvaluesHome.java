package com.example.shen.linearassist;

import Exceptions.GettingEigenvalueException;
import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;
import MyWidget.EditTextWithDel;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import myapis.ElementaryOperation;
import myapis.MatrixStringToDouble;
import myapis.NumericalCalculation;
import org.w3c.dom.Text;

public class EigenvaluesHome extends AppCompatActivity {

  EditTextWithDel matrix_content;
  Button get_eigenvalues_btn;

  private final DeterminantHome dt = new DeterminantHome();
  private final NumericalCalculation numCal = new NumericalCalculation();
  private final ElementaryOperation eleOp = new ElementaryOperation();
  private int count = 1;

  private static final int ACCURACY = 6;
  private static final double EPS = 1e-5;
  private static final int ITERATION_UPPER_BOUND = 300;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_eigenvalues_home);
    matrix_content = (EditTextWithDel) findViewById(R.id.matrix_content);
    get_eigenvalues_btn = (Button) findViewById(R.id.get_eigenvalues_btn);
    get_eigenvalues_btn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        count = 1;
        String matrixContent = matrix_content.getText().toString();
        List<Double> eigenvalues = new ArrayList<Double>();
        String result = "";
        try {
          eigenvalues = seekEigenvalue(MatrixStringToDouble.matrixStringToDouble(matrixContent));
          for (int i = 0; i < eigenvalues.size(); i++) {
            result += String.valueOf(eigenvalues.get(i));
            if (i != eigenvalues.size() - 1) {
              result += "\n";
            }
          }
        } catch (NonSquareMatrixException e) {
          result = e.getMessage();
        } catch (NonNumericalException e) {
          result = e.getMessage();
        } catch (GettingEigenvalueException e) {
          result = e.getMessage();
        } finally {
          final AlertDialog.Builder normalDialog = new AlertDialog.Builder(EigenvaluesHome.this);
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
    });
  }

  public List<Double> seekEigenvalue(double[][] A)
      throws NonSquareMatrixException, NonNumericalException, GettingEigenvalueException {
    int order = A.length;
    if (order == 1) {
      List<Double> result = new ArrayList<Double>();
      result.add(A[0][0]);
      return result;
    }
    if (!nonSingularMatrix(A)) { // 奇异阵不可QR分解
      throw new GettingEigenvalueException("奇异阵无法QR分解求特征值！");
    } else {
      if (!isHessenberg(A)) { // 判断是否为上hessenberg阵
        A = hessenberg(A);
      }
      return qrAlgorithm(A);
    }
  } // END seekEigenvalue()

  public double[][] hessenberg(double[][] A) {
    int n = A.length;
    for (int k = 1; k <= n - 2; k++) { // 循环n-2次
      int nn = n - k; // 第k+1个hessenberg阵的秩
      /* 初始化单位阵unitMatrix */
      double[][] unitMatrix = genUnitMatrix(nn);
      /* 获取矩阵A的第k列的从次对角线元素开始的列向量的模最大值 */
      double max = Math.abs(A[k][k - 1]);
      for (int i = 0; i < nn; i++) {
        if (Math.abs(max - Math.abs(A[i + k][k - 1])) < 0.001) {
          max = Math.abs(A[i + k][k - 1]);
        }
      }
      /* 对矩阵A的第k列的从此对角线开始的列向量kColumn做标准化 */
      double[] kColumn = new double[nn];
      for (int i = 0; i < nn; i++) {
        kColumn[i] = numCal.bigDecimalDiv(A[i + k][k - 1], max, ACCURACY);
      }
      /* 若kColumn已经是零向量，则已经得到了一个hessenberg阵，跳到下一迭代 */
      if (isZeroVector(kColumn)) {
        continue;
      }
      /* 获取kColumn的2-范数 */
      double kColumnTwoNorm = 0.0;
      for (int i = 0; i < nn; i++) {
        kColumnTwoNorm = numCal.bigDecimalAdd(kColumnTwoNorm,
            numCal.bigDecimalMul(kColumn[i], kColumn[i], ACCURACY), ACCURACY);
      }
      // Householder变换
      // v = c + ||c||e
      // H = I - 2 * vv{T} / v{T}v
      double sigma = sign(A[k][k - 1]) * Math.sqrt(kColumnTwoNorm);
      double[] v = new double[nn];
      v[0] = numCal.bigDecimalAdd(kColumn[0], sigma, ACCURACY);
      for (int j = 1; j < nn; j++) {
        v[j] = kColumn[j];
      }
      // ?
      double beta =
          numCal.bigDecimalMul(sigma, numCal.bigDecimalAdd(kColumn[0], sigma, ACCURACY), ACCURACY);
      // R = I - β{-1}v{T}v
      double[][] R = new double[nn][nn];
      for (int i = 0; i < nn; i++) {
        for (int j = 0; j < nn; j++) {
          R[i][j] = numCal.bigDecimalSub(unitMatrix[i][j],
              numCal.bigDecimalDiv(numCal.bigDecimalMul(v[i], v[j], ACCURACY), beta, ACCURACY),
              ACCURACY);
        }
      }
      double[][] T = genUnitMatrix(n);
      for (int i = 0; i < nn; i++) {
        for (int j = 0; j < nn; j++) {
          T[i + k][j + k] = R[i][j];
        }
      }
      double[][] B = eleOp.multiply(T, A, ACCURACY);
      double[][] C = eleOp.multiply(B, T, ACCURACY);
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          A[i][j] = C[i][j];
        }
      }
    }
    double[][] result = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        result[i][j] = A[i][j];
      }
    }
    return result;
  } // END hessenberg()

  public List<Double> qrAlgorithm(double[][] A)
      throws NonSquareMatrixException, GettingEigenvalueException {
    int n = A.length;
    double[][] Q = genUnitMatrix(n);
    double[][] R = new double[n][n];
    // 对矩阵A进行QR分解
    for (int m = 0; m < n - 1; m++) { // 循环n-1次
      // Givens变换，矩阵p是初等旋转阵
      double[][] p = genUnitMatrix(n);
      double c =
          numCal
              .bigDecimalDiv(A[m][m],
                  Math.sqrt(numCal.bigDecimalAdd(numCal.bigDecimalMul(A[m][m], A[m][m], ACCURACY),
                      numCal.bigDecimalMul(A[m + 1][m], A[m + 1][m], ACCURACY), ACCURACY)),
                  ACCURACY);
      double s =
          numCal
              .bigDecimalDiv(A[m + 1][m],
                  Math.sqrt(numCal.bigDecimalAdd(numCal.bigDecimalMul(A[m][m], A[m][m], ACCURACY),
                      numCal.bigDecimalMul(A[m + 1][m], A[m + 1][m], ACCURACY), ACCURACY)),
                  ACCURACY);
      p[m][m] = c;
      p[m][m + 1] = s;
      p[m + 1][m] = -s;
      p[m + 1][m + 1] = c;
      R = eleOp.multiply(p, A, ACCURACY);
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          A[i][j] = R[i][j];
        }
      }
      Q = eleOp.multiply(p, Q, ACCURACY);
    }
    // Q = Q{T} 转置矩阵Q
    Q = eleOp.transpose(Q);
    // A = RQ
    A = eleOp.multiply(R, Q, ACCURACY);
    // 当矩阵A的下三角全部为0（近似）时，迭代结束，主对角线上的元素即为特征值
    double max = Math.abs(A[1][0]);
    for (int i = 1; i < n; i++) {
      for (int j = 0; j < i; j++) {
        if (Math.abs(A[i][j]) > max) {
          max = Math.abs(A[i][j]);
        }
      }
    }
    if (max < EPS) {
      List<Double> eigenvalues = new ArrayList<Double>();
      for (int i = 0; i < n; i++) {
        eigenvalues.add(A[i][i]);
      }
      return eigenvalues;
    }
    count++;
    if (count > ITERATION_UPPER_BOUND) {
      throw new GettingEigenvalueException(
          "QR分解已经迭代超过" + String.valueOf(ITERATION_UPPER_BOUND) + "次，该矩阵特征值有可能为复数");
    }
    return qrAlgorithm(A);
  } // END qrAlgorithm()

  public boolean isHessenberg(double[][] matrix) {
    int order = matrix.length;
    for (int i = 2; i < order; i++) {
      for (int j = 0; j < i - 1; j++) {
        if (matrix[i][j] != 0) {
          return false;
        }
      }
    }
    return true;
  } // END isHessenberg()

  public boolean nonSingularMatrix(double[][] m)
      throws NonNumericalException, NonSquareMatrixException {
    if (Math.abs(dt.getDeterminant(MatrixStringToDouble.matrixDoubleToString(m))) < 0.001) {
      return false;
    } else {
      return true;
    }
  } // END nonSingularMatrix()

  public boolean isZeroVector(double[] a) {
    for (int i = 0; i < a.length; i++) {
      if (Math.abs(a[i]) > 0.001) {
        return false;
      }
    }
    return true;
  } // END isZeroVector()

  public int sign(double y) {
    if (y > 0) {
      return 1;
    } else if (y == 0) {
      return 0;
    } else {
      return -1;
    }
  } // END sign()

  public double[][] genUnitMatrix(int order) {
    double[][] unitMatrix = new double[order][order];
    for (int i = 0; i < order; i++) {
      for (int j = 0; j < order; j++) {
        if (i == j) {
          unitMatrix[i][j] = 1;
        } else {
          unitMatrix[i][j] = 0;
        }
      }
    }
    return unitMatrix;
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
