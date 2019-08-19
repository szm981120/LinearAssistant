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
import myapis.MatrixStringToDouble;
import myapis.NumericalCalculation;
import myapis.RowTransition;

public class RowTransitionHome extends AppCompatActivity {

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
            double[][] matrix = MatrixStringToDouble.matrixStringToDouble(matrixContent);
            result = RowTransition.rowTransition(matrix, isUpper);
        } catch (NonNumericalException e) {
            result = "行列式必须是纯数字！";
        } catch (NonSquareMatrixException e) {
            result = e.getMessage();
        } finally {
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
}
