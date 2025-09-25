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

public class PythonActivity extends AppCompatActivity {

    TextView backPython;
    LinearLayout pythonBasic, pythonDataType, pythonOperators, pythonInput, pythonStatements,
            pythonLoops, pythonFunction, pythonDataStructures, pythonStringHandling,
            pythonModules, pythonFileHandling, pythonExceptionHandling, pythonOOPs,
            pythonIterators, pythonDecorators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_python);

        backPython = findViewById(R.id.backPython);
        pythonBasic = findViewById(R.id.pythonBasic);
        pythonDataType = findViewById(R.id.pythonDataType);
        pythonOperators = findViewById(R.id.pythonOperators);
        pythonInput = findViewById(R.id.pythonInput);
        pythonStatements = findViewById(R.id.pythonStatements);
        pythonLoops = findViewById(R.id.pythonLoops);
        pythonFunction = findViewById(R.id.pythonFunctions);
        pythonDataStructures = findViewById(R.id.pythonDataStructures);
        pythonStringHandling = findViewById(R.id.pythonStringHandling);
        pythonModules = findViewById(R.id.pythonModules);
        pythonFileHandling = findViewById(R.id.pythonFileHandling);
        pythonExceptionHandling = findViewById(R.id.pythonExceptionHandling);
        pythonOOPs = findViewById(R.id.pythonOOPs);
        pythonIterators = findViewById(R.id.pythonIterators);
        pythonDecorators = findViewById(R.id.pythonDecorators);

        backPython.setOnClickListener(v -> finish());

        // ✅ Har topic pe dialog call
        pythonBasic.setOnClickListener(v -> showQuestionDialog("Basics of Python"));
        pythonDataType.setOnClickListener(v -> showQuestionDialog("Variables & Data Types"));
        pythonOperators.setOnClickListener(v -> showQuestionDialog("Operators"));
        pythonInput.setOnClickListener(v -> showQuestionDialog("Input & Output"));
        pythonStatements.setOnClickListener(v -> showQuestionDialog("Control Statements"));
        pythonLoops.setOnClickListener(v -> showQuestionDialog("Loops"));
        pythonFunction.setOnClickListener(v -> showQuestionDialog("Functions"));
        pythonDataStructures.setOnClickListener(v -> showQuestionDialog("Data Structures"));
        pythonStringHandling.setOnClickListener(v -> showQuestionDialog("String Handling"));
        pythonModules.setOnClickListener(v -> showQuestionDialog("Modules & Packages"));
        pythonFileHandling.setOnClickListener(v -> showQuestionDialog("File Handling"));
        pythonExceptionHandling.setOnClickListener(v -> showQuestionDialog("Exception Handling"));
        pythonOOPs.setOnClickListener(v -> showQuestionDialog("Object-Oriented Programming"));
        pythonIterators.setOnClickListener(v -> showQuestionDialog("Iterators & Generators"));
        pythonDecorators.setOnClickListener(v -> showQuestionDialog("Decorators"));
    }

    // ✅ Same dialog as CActivity
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
            bg.setColor(Color.parseColor("#9C27B0"));
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
                Intent i = new Intent(PythonActivity.this, QuizActivity.class);
                i.putExtra("language", "Python");
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
