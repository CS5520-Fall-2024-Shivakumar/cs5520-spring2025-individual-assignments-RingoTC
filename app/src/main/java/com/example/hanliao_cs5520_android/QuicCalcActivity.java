package com.example.hanliao_cs5520_android;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class QuicCalcActivity extends AppCompatActivity {
    private TextView calcView;
    private StringBuilder experssionBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quic_calc);

        calcView = findViewById(R.id.textViewResult);
        experssionBuffer = new StringBuilder();

        calcView.setText(R.string.defaultExpression);

        setButtonListener();
    }

    private void setButtonListener(){
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9, R.id.buttonPlus, R.id.buttonMinus,
                R.id.buttonDelete, R.id.buttonEquals
        };

        View.OnClickListener listener = this::onButtonClick;

        for (int buttonId : buttonIds) {
            findViewById(buttonId).setOnClickListener(listener);
        }
    }

    private void onButtonClick(View view) {
        int id = view.getId();

        if (id == R.id.button0 || id == R.id.button1 || id == R.id.button2 ||
                id == R.id.button3 || id == R.id.button4 || id == R.id.button5 ||
                id == R.id.button6 || id == R.id.button7 || id == R.id.button8 ||
                id == R.id.button9) {
            append2Exp(((TextView) view).getText().toString());
        } else if (id == R.id.buttonPlus || id == R.id.buttonMinus) {
            append2Exp(((TextView) view).getText().toString());
        } else if (id == R.id.buttonEquals) {
            calculate();
        } else if (id == R.id.buttonDelete) {
            deleteLastChar();
        }
    }

    private void append2Exp(String text) {
        if (text == null) {
            if (experssionBuffer.length() > 0) {
                experssionBuffer.deleteCharAt(experssionBuffer.length() - 1);
            }
        } else {
            experssionBuffer.append(text);
        }
        calcView.setText(experssionBuffer.toString());
    }


    private void calculate() {
        int result = (int) eval(experssionBuffer.toString());
        experssionBuffer.setLength(0);
        experssionBuffer.append(result);
        calcView.setText(String.valueOf(result));
    }

    private double eval(String expression) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scope = context.initStandardObjects();
            Object result = context.evaluateString(scope, expression, "Javascript", 1, null);
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            } else {
                throw new IllegalArgumentException("Invalid expression result");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid Expression", Toast.LENGTH_SHORT).show();
            return 0.0;
        } finally {
            Context.exit();
        }
    }


    private void deleteLastChar(){
        if (experssionBuffer.length() > 0) {
            experssionBuffer.deleteCharAt(experssionBuffer.length() - 1);
            calcView.setText(experssionBuffer.toString());
        } else {
            Toast.makeText(this, "No character to delete", Toast.LENGTH_SHORT).show();
        }
    }
}
