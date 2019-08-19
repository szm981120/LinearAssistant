package myapis;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.shen.linearassist.RowTransitionHome;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;

public class RowTransition {
    private static final int ACCURACY = 4;

    public static String rowTransition(double[][] determinant, boolean isUpper) {
        String result = "";
        int rowN = determinant.length;
        int columnN = rowN > 0 ? determinant[0].length : 0;
        NumericalCalculation numCal = new NumericalCalculation();
        if (isUpper) { // 上三角行变换
            int iOrder = 0;
            int jOrder = 0;
            int minRow = 0;
            /* 行初等变换 */
            int upperOrder = rowN > columnN ? columnN : rowN - 1;
            while (iOrder < upperOrder) {
                /* minRow为除去已经变换后的行之外， 从左到右首个非零元最小的行索引*/
                minRow = iOrder;
                double tempMin = determinant[iOrder][jOrder];
                for (int i = iOrder + 1; i < rowN; i++) {
                    if (determinant[i][jOrder] < tempMin && determinant[i][jOrder] != 0) {
                        tempMin = determinant[i][jOrder];
                        minRow = i;
                    }
                }
                /* minRow行由于有最小的首个非零元，将其换到剩余行的顶部有利于行变换时减少小数出现 */
                if (determinant[minRow][jOrder] == 0) {
                    break;
                }
                if (minRow != iOrder) {
                    double[] tempRow = determinant[minRow];
                    determinant[minRow] = determinant[iOrder];
                    determinant[iOrder] = tempRow;
                }
                double pDiv = determinant[iOrder][jOrder];
                for (int j = jOrder; j < columnN; j++) {
                    determinant[iOrder][j] = numCal.bigDecimalDiv(determinant[iOrder][j], pDiv, ACCURACY);
                }
                for (int i = iOrder + 1; i < rowN; i++) {
                    double div = numCal.bigDecimalDiv(determinant[i][jOrder], determinant[iOrder][jOrder], ACCURACY); // 对下列行实施第三类行变换的乘数因子
                    for (int j = jOrder; j < columnN; j++) { // 对下面的行实施第三类行变换，以求上三角阵
                        determinant[i][j] = numCal.bigDecimalSub(determinant[i][j], numCal.bigDecimalMul(determinant[iOrder][j], div, ACCURACY), ACCURACY);
                    }
                    pDiv = determinant[i][jOrder + 1];
                    for (int j = jOrder + 1; j < columnN; j++) {
                        determinant[i][j] = numCal.bigDecimalDiv(determinant[i][j], pDiv, ACCURACY);
                    }
                }
                iOrder++;
                jOrder++;
            } // END 行初等变换
        } else {
            int iOrder = rowN - 1;
            int jOrder = columnN - 1;
            int minRow = 0;
            while (iOrder > 0 && jOrder >= 0) {
                /* minRow为除去已经变换后的行之外， 从左到右首个非零元最小的行索引*/
                minRow = iOrder;
                double tempMin = determinant[iOrder][jOrder];
                for (int i = iOrder - 1; i >= 0; i--) {
                    if (determinant[i][jOrder] < tempMin && determinant[i][jOrder] != 0) {
                        tempMin = determinant[i][jOrder];
                        minRow = i;
                    }
                }
                /* minRow行由于有最小的首个非零元，将其换到剩余行的顶部有利于行变换时减少小数出现 */
                if (determinant[minRow][jOrder] == 0) {
                    break;
                }
                if (minRow != iOrder) {
                    double[] tempRow = determinant[minRow];
                    determinant[minRow] = determinant[iOrder];
                    determinant[iOrder] = tempRow;
                }
                double pDiv = determinant[iOrder][jOrder];
                for (int j = jOrder; j >= 0; j--) {
                    determinant[iOrder][j] = numCal.bigDecimalDiv(determinant[iOrder][j], pDiv, ACCURACY);
                }
                for (int i = iOrder - 1; i >= 0; i--) {
                    double div = numCal.bigDecimalDiv(determinant[i][jOrder], determinant[iOrder][jOrder], ACCURACY);
                    for (int j = jOrder; j >= 0; j--) {
                        determinant[i][j] = numCal.bigDecimalSub(determinant[i][j], numCal.bigDecimalMul(determinant[iOrder][j], div, ACCURACY), ACCURACY);
                    }
                    pDiv = determinant[i][jOrder - 1];
                    for (int j = jOrder - 1; j >= 0; j--) {
                        determinant[i][j] = numCal.bigDecimalDiv(determinant[i][j], pDiv, ACCURACY);
                    }
                }
                iOrder--;
                jOrder--;
            }
        }


        for (int i = 0; i < rowN; i++) {
            for (int j = 0; j < columnN; j++) {
                result += String.valueOf(determinant[i][j]);
                result += " ";
            }
            result += "\n";
        }

        return result;
    }

}
