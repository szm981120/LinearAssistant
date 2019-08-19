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
            double[][] matrix = new double[rowN][columnN * 2];
            for (int i = 0; i < rowN; i++) {
                for (int j = 0; j < columnN; j++) {
                    matrix[i][j] = oldMatrix[i][j];
                }
                for (int j = columnN; j < columnN * 2; j++) {
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
            columnN *= 2;
            int iOrder = rowN - 1;
            int jOrder = columnN / 2 - 1;
            int minRow;
            while (iOrder > 0 && jOrder >= 0) {
                /* minRow为除去已经变换后的行之外， 从左到右首个非零元最小的行索引*/
                minRow = iOrder;
                if(matrix[iOrder][jOrder] == 0){
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
                for (int j = columnN - 1; j >= 0; j--) {
                    matrix[iOrder][j] = numCal.bigDecimalDiv(matrix[iOrder][j], pDiv, ACCURACY);
                }
                for (int i = iOrder - 1; i >= 0; i--) {
                    double div = numCal.bigDecimalDiv(matrix[i][jOrder], matrix[iOrder][jOrder], ACCURACY);
                    for (int j = columnN - 1; j >= 0; j--) {
                        matrix[i][j] = numCal.bigDecimalSub(matrix[i][j], numCal.bigDecimalMul(matrix[iOrder][j], div, ACCURACY), ACCURACY);
                    }
                    pDiv = matrix[i][jOrder - 1];
                    for (int j = columnN - 1; j >= 0; j--) {
                        matrix[i][j] = numCal.bigDecimalDiv(matrix[i][j], pDiv, ACCURACY);
                    }
                }
                iOrder--;
                jOrder--;
            }
            for (int i = 0; i < rowN; i++) {
                for (int j = columnN / 2; j < columnN; j++) {
                    result += String.valueOf(matrix[i][j]);
                    result += " ";
                }
                result += "\n";
            }
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
