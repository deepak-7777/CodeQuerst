package com.vmpk.codequerst.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.vmpk.codequerst.R;

public class JavaScriptActivity extends AppCompatActivity {

    TextView backJavaScript;
    LinearLayout jsIntroduction, jsDataType, jsOperators, jsInput, jsStatements, jsLoops, jsFunctions,
            jsArrays, jsStrings, jsObjects, jsDOM, jsEvents, jsClasses, jsErrorHandling, jsAsync, jsPromises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_java_script);

        backJavaScript = findViewById(R.id.backJavaScript);
        jsIntroduction = findViewById(R.id.jsIntroduction);
        jsDataType = findViewById(R.id.jsDataType);
        jsOperators = findViewById(R.id.jsOperators);
        jsInput = findViewById(R.id.jsInput);
        jsStatements = findViewById(R.id.jsStatements);
        jsLoops = findViewById(R.id.jsLoops);
        jsFunctions = findViewById(R.id.jsFunctions);
        jsArrays = findViewById(R.id.jsArrays);
        jsStrings = findViewById(R.id.jsStrings);
        jsObjects = findViewById(R.id.jsObjects);
        jsDOM = findViewById(R.id.jsDOM);
        jsEvents = findViewById(R.id.jsEvents);
        jsClasses = findViewById(R.id.jsClasses);
        jsErrorHandling = findViewById(R.id.jsErrorHandling);
        jsAsync = findViewById(R.id.jsAsync);
        jsPromises = findViewById(R.id.jsPromises);

        backJavaScript.setOnClickListener(v -> finish());

        // ✅ Topics ke click par dialog open hoga
        jsIntroduction.setOnClickListener(v -> showQuestionDialog("Introduction to JavaScript"));
        jsDataType.setOnClickListener(v -> showQuestionDialog("Variables & Data Types"));
        jsOperators.setOnClickListener(v -> showQuestionDialog("Operators"));
        jsInput.setOnClickListener(v -> showQuestionDialog("Input & Output"));
        jsStatements.setOnClickListener(v -> showQuestionDialog("Control Statements"));
        jsLoops.setOnClickListener(v -> showQuestionDialog("Loops"));
        jsFunctions.setOnClickListener(v -> showQuestionDialog("Functions"));
        jsArrays.setOnClickListener(v -> showQuestionDialog("Arrays"));
        jsStrings.setOnClickListener(v -> showQuestionDialog("Strings"));
        jsObjects.setOnClickListener(v -> showQuestionDialog("Objects"));
        jsDOM.setOnClickListener(v -> showQuestionDialog("DOM Manipulation"));
        jsEvents.setOnClickListener(v -> showQuestionDialog("Events"));
        jsClasses.setOnClickListener(v -> showQuestionDialog("Classes & OOPs"));
        jsErrorHandling.setOnClickListener(v -> showQuestionDialog("Error & Exception Handling"));
        jsAsync.setOnClickListener(v -> showQuestionDialog("Async or Await"));
        jsPromises.setOnClickListener(v -> showQuestionDialog("Promises"));
    }

    // ✅ Reusable dialog method
    private void showQuestionDialog(String topicName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        // Parent layout
        LinearLayout parentLayout = new LinearLayout(this);
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        float density = getResources().getDisplayMetrics().density;
        int padHor = (int) (24 * density);
        int padVer = (int) (20 * density);
        parentLayout.setPadding(padHor, padVer, padHor, padVer);
        parentLayout.setGravity(Gravity.CENTER);

        // Background for dialog
        GradientDrawable bgDialog = new GradientDrawable();
        bgDialog.setColor(Color.parseColor("#2C2C2C")); // dark gray
        bgDialog.setCornerRadius(40f);
        parentLayout.setBackground(bgDialog);

        // Title
        TextView title = new TextView(this);
        title.setText("Select Number of Questions");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = (int) (12 * density);
        title.setLayoutParams(titleParams);
        parentLayout.addView(title);

        // GridLayout
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(3);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        gridLayout.setUseDefaultMargins(true);

        LinearLayout.LayoutParams gridParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        gridParams.gravity = Gravity.CENTER;
        gridLayout.setLayoutParams(gridParams);

        int[] values = {5, 10, 15, 20, 25, 30};

        builder.setView(parentLayout);
        final AlertDialog dialog = builder.create();

        for (int value : values) {
            Button btn = new Button(this);

            int size = (int) (55 * density); // circle size
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = size;
            params.height = size;
            int margin = (int) (8 * density);
            params.setMargins(margin, margin, margin, margin);
            btn.setLayoutParams(params);

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor("#9C27B0")); // purple
            bg.setStroke((int) (2 * density), Color.WHITE);
            btn.setBackground(bg);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setBackgroundTintList(null);
            }

            btn.setText(String.valueOf(value));
            btn.setTextColor(Color.WHITE);
            btn.setTextSize(14);
            btn.setGravity(Gravity.CENTER);

            btn.setOnClickListener(v -> {
                Intent i = new Intent(JavaScriptActivity.this, QuizActivity.class);
                i.putExtra("language", "JavaScript");
                i.putExtra("topic", topicName);
                i.putExtra("numQuestions", value);
                startActivity(i);
                dialog.dismiss();
            });

            gridLayout.addView(btn);
        }

        parentLayout.addView(gridLayout);

        dialog.setOnShowListener(d -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
                window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });

        dialog.show();
    }
}
