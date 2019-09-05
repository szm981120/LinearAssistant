package com.example.shen.linearassist;

import Exceptions.NonNumericalException;
import Exceptions.NonSquareMatrixException;
import Exceptions.NoninvertibleException;
import MyWidget.EditTextWithDel;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import myapis.ElementaryOperation;
import myapis.MatrixStringToDouble;

public class MatrixCalculationHome extends AppCompatActivity {

  Button calculate_button;
  Button next_matrix_button;
  EditTextWithDel matrix_content;
  RadioGroup rg_operator;

  private final Stack<Character> opStack = new Stack();
  private final List dataList = new ArrayList();
  private final ElementaryOperation elementaryOperation = new ElementaryOperation();
  private final static int ACCURACY = 4;
  private int choice = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_matrix_calculation_home);

    calculate_button = (Button) findViewById(R.id.calculate_button);
    matrix_content = (EditTextWithDel) findViewById(R.id.matrix_content);
    rg_operator = (RadioGroup) findViewById(R.id.rg_operator);
    //rg_operator.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
    //  @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
    //    if (checkedId == R.id.left_parenthesis) {
    //      choice = 1;
    //      Log.d("choice", "1");
    //    } else if (checkedId == R.id.right_parenthesis) {
    //      choice = 2;
    //    } else if (checkedId == R.id.multiply) {
    //      choice = 3;
    //      Log.d("choice", "3");
    //    } else if (checkedId == R.id.addition) {
    //      choice = 4;
    //    } else if (checkedId == R.id.subtraction) {
    //      choice = 5;
    //    } else if (checkedId == R.id.inverse) {
    //      choice = 6;
    //    } else {
    //      choice = 0;
    //    }
    //  }
    //});
    next_matrix_button = (Button) findViewById(R.id.next_matrix_button);
    next_matrix_button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int item = rg_operator.getCheckedRadioButtonId();
        switch (item) {
          case R.id.left_parenthesis:
            choice = 1;
            break;
          case R.id.right_parenthesis:
            choice = 2;
            break;
          case R.id.multiply:
            choice = 3;
            break;
          case R.id.addition:
            choice = 4;
            break;
          case R.id.subtraction:
            choice = 5;
            break;
          case R.id.inverse:
            choice = 6;
            break;
          default:
            break;
        }
        String matrixContent = matrix_content.getText().toString();
        try {
          double[][] matrix =
              choice == 0 || choice == 6 ? MatrixStringToDouble.matrixStringToDouble(matrixContent)
                  : null;
          switch (choice) {
            case 0:
              dataList.add(matrix);
              break;
            case 1:
              opStack.push('(');
              break;
            case 2:
              while (!opStack.peek().equals('(')) {
                dataList.add(opStack.pop());
              }
              opStack.pop();
              break;
            case 3:
              if (opStack.empty() || opLevel('*') >= opLevel(opStack.peek())) {
                opStack.push('*');
              } else {
                dataList.add(opStack.pop());
                while (opLevel('*') < opLevel(opStack.peek())) {
                  dataList.add(opStack.pop());
                }
                opStack.push('*');
              }
              break;
            case 4:
              if (opStack.empty() || opLevel('+') >= opLevel(opStack.peek())) {
                opStack.push('+');
              } else {
                dataList.add(opStack.pop());
                while (opLevel('+') < opLevel(opStack.peek())) {
                  dataList.add(opStack.pop());
                }
                opStack.push('+');
              }
              break;
            case 5:
              if (opStack.empty() || opLevel('-') >= opLevel(opStack.peek())) {
                opStack.push('-');
              } else {
                dataList.add(opStack.pop());
                while (opLevel('-') < opLevel(opStack.peek())) {
                  dataList.add(opStack.pop());
                }
                opStack.push('-');
              }
              break;
            case 6:
              try {
                dataList.add(elementaryOperation.inverse(matrix, ACCURACY));
              } catch (NoninvertibleException e) {
                e.printStackTrace();
              }
              break;
          }
          rg_operator.clearCheck();
          matrix_content.setText("");
          choice = 0;
        } catch (NonNumericalException e) {
          Toast.makeText(MatrixCalculationHome.this, "异常：矩阵必须是纯数字！", Toast.LENGTH_LONG).show();
          choice = 0;
        } catch (NonSquareMatrixException e) {
          Toast.makeText(MatrixCalculationHome.this, e.getMessage(), Toast.LENGTH_LONG).show();
          rg_operator.clearCheck();
          choice = 0;
        }
      }
    });
    calculate_button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        while (!opStack.empty()) {
          dataList.add(opStack.pop());
        }
        Stack<double[][]> numStack = new Stack<>();
        while (!dataList.isEmpty()) {
          Object o = MatrixCalculationHome.this.dataList.remove(0);
          if (o.getClass() == double[][].class) {
            Log.d("number", "number");
            numStack.push((double[][]) o);
          } else {
            Log.d("operator", String.valueOf(o));
            double[][] num1 = numStack.pop();
            double[][] num2 = numStack.pop();
            switch ((char) o) {
              case '*':
                numStack.push(elementaryOperation.multiply(num1, num2, ACCURACY));
                break;
              case '+':
                numStack.push(elementaryOperation.add(num1, num2, ACCURACY));
                break;
              case '-':
                numStack.push(elementaryOperation.substraction(num1, num2, ACCURACY));
                break;
              default:
                break;
            }
          }
        }
        double[][] resultMatrix = numStack.pop();
        int rowN = resultMatrix.length;
        int columnN = resultMatrix[0].length;
        String result = "";
        for (int i = 0; i < rowN; i++) {
          for (int j = 0; j < columnN; j++) {
            result += String.valueOf(resultMatrix[i][j]);
            result += " ";
          }
          result += "\n";
        }
        final AlertDialog.Builder normalDialog =
            new AlertDialog.Builder(MatrixCalculationHome.this);
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
    });
  }

  private int opLevel(char op) {
    switch (op) {
      case '(':
        return 0;
      case '+':
        return 1;
      case '-':
        return 2;
      case '*':
        return 3;
      case ')':
        return 4;
      default:
        return 0;
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
