package com.example.shen.linearassist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;
import MyWidget.EditTextWithDel;

public class RowTransitionHome extends AppCompatActivity {

    private static final int ACCURACY = 4;
    Button upper_triangle_transition_button;
    Button lower_triangle_transition_button;
    EditTextWithDel matrix_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_transition_home);
        upper_triangle_transition_button = (Button) findViewById(R.id.upper_triangle_transition_button);
        lower_triangle_transition_button = (Button) findViewById(R.id.lower_triangle_transition_button);
        matrix_content = (EditTextWithDel) findViewById(R.id.matrix_context);
        upper_triangle_transition_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowTransition(true);
            }
        });
        lower_triangle_transition_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowTransition(false);
            }
        });
    }

    private void rowTransition(boolean isUpper) {
        String result = "";
        try {
            String matrixContent = matrix_content.getText().toString(); // 获取行列式信息
            if (!isNumerical(matrixContent)) { // 非纯数字异常
                throw new NonNumericalException();
            }
            while (matrixContent.charAt(0) == '\n') {
                matrixContent = matrixContent.substring(1);
            }
            String[] temp1 = matrixContent.split("\\n+"); // 行列式文本按行截取
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
            double determinant[][] = new double[rowN][columnN]; // 行列式二维数值数组
            for (int i = 0; i < rowN; i++) {
                temp2[i] = temp1[i].split("\\s+");
            }
            for (int i = 0; i < rowN; i++) { // 获取行列式二维数值数组
                for (int j = 0; j < columnN; j++) {
                    determinant[i][j] = Double.parseDouble(temp2[i][j]);
                }
            }
            if (isUpper) { // 上三角行变换
                int pOrder = 0;
                int minRow = 0;
                /* 行初等变换 */
                int upperOrder = rowN > columnN ? columnN : rowN - 1;
                while (pOrder < upperOrder) {
                    /* minRow为除去已经变换后的行之外， 从左到右首个非零元最小的行索引*/
                    minRow = pOrder;
                    double tempMin = determinant[pOrder][pOrder];
                    for (int i = pOrder + 1; i < rowN; i++) {
                        if (determinant[i][pOrder] < tempMin && determinant[i][pOrder] != 0) {
                            tempMin = determinant[i][pOrder];
                            minRow = i;
                        }
                    }
                    /* minRow行由于有最小的首个非零元，将其换到剩余行的顶部有利于行变换时减少小数出现 */
                    if (determinant[minRow][pOrder] == 0) {
                        break;
                    }
                    if (minRow != pOrder) {
                        double[] tempRow = determinant[minRow];
                        determinant[minRow] = determinant[pOrder];
                        determinant[pOrder] = tempRow;
                    }
                    for (int i = pOrder + 1; i < rowN; i++) {
                        double div = bigDecimalDiv(determinant[i][pOrder], determinant[pOrder][pOrder]); // 对下列行实施第三类行变换的乘数因子
                        for (int j = 0; j < columnN; j++) { // 对下面的行实施第三类行变换，以求上三角阵
                            determinant[i][j] = bigDecimalSub(determinant[i][j], bigDecimalMul(determinant[pOrder][j], div));
                        }
                    }
                    pOrder++;
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
                    for (int i = iOrder - 1; i >= 0; i--) {
                        double div = bigDecimalDiv(determinant[i][jOrder], determinant[iOrder][jOrder]);
                        for (int j = columnN - 1; j >= 0; j--) {
                            determinant[i][j] = bigDecimalSub(determinant[i][j], bigDecimalMul(determinant[iOrder][j], div));
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
        } catch (
                NonNumericalException e)

        {
            result = "行列式必须是纯数字！";
        } catch (
                NonSquareMatrixException e)

        {
            result = e.getMessage();
        } finally

        {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(RowTransitionHome.this);
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

    private boolean isNumerical(String s) {
        Pattern pattern = Pattern.compile("[0-9|\\s]*");
        return pattern.matcher(s).matches();
    }

    private double bigDecimalSub(double d1, double d2) {
        BigDecimal bd1 = BigDecimal.valueOf(d1);
        BigDecimal bd2 = BigDecimal.valueOf(d2);
        return bd1.subtract(bd2).setScale(ACCURACY, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private double bigDecimalMul(double d1, double d2) {
        BigDecimal bd1 = BigDecimal.valueOf(d1);
        BigDecimal bd2 = BigDecimal.valueOf(d2);
        return bd1.multiply(bd2).setScale(ACCURACY, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private double bigDecimalDiv(double d1, double d2) {
        BigDecimal bd1 = BigDecimal.valueOf(d1);
        BigDecimal bd2 = BigDecimal.valueOf(d2);
        return bd1.divide(bd2, ACCURACY, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
