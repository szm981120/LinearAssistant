package com.example.shen.linearassist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button determinant_button;
    Button row_transition_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        determinant_button = (Button) findViewById(R.id.determinant_button);
        determinant_button.setOnClickListener(this);
        row_transition_button = (Button) findViewById(R.id.row_transition_button);
        row_transition_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.determinant_button:
                Intent intent = new Intent(MainActivity.this, DeterminantHome.class);
                startActivity(intent);
                break;
            case R.id.row_transition_button:
                Intent intent1 = new Intent(MainActivity.this, RowTransitionHome.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

}
