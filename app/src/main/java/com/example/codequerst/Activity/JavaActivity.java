package com.example.codequerst.Activity;

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

import com.example.codequerst.R;

public class JavaActivity extends AppCompatActivity {

    TextView backjava;
    LinearLayout javaIntroduction, javaDataType, javaOperators, javaInput, javaStatements, javaLoops, javaArrays,
            javaStrings, javaMethods, javaOOPs, javaConstructors, javaInheritance, javaPolymorphism, javaAbstraction, javaException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_java);

        backjava = findViewById(R.id.backJava);
        javaIntroduction = findViewById(R.id.javaIntroduction);
        javaDataType = findViewById(R.id.javaDataType);
        javaOperators = findViewById(R.id.javaOperators);
        javaInput = findViewById(R.id.javaInput);
        javaStatements = findViewById(R.id.javaStatements);
        javaLoops = findViewById(R.id.javaLoops);
        javaArrays = findViewById(R.id.javaArrays);
        javaStrings = findViewById(R.id.javaStrings);
        javaMethods = findViewById(R.id.javaMethods);
        javaOOPs = findViewById(R.id.javaOOPs);
        javaConstructors = findViewById(R.id.javaConstructors);
        javaInheritance = findViewById(R.id.javaInheritance);
        javaPolymorphism = findViewById(R.id.javaPolymorphism);
        javaAbstraction = findViewById(R.id.javaAbstraction);
        javaException = findViewById(R.id.javaException);

        backjava.setOnClickListener(v -> finish());

        // ✅ Topics with dialog
        javaIntroduction.setOnClickListener(v -> showQuestionDialog("Introduction to Java & Setup"));
        javaDataType.setOnClickListener(v -> showQuestionDialog("Data Types & Variables"));
        javaOperators.setOnClickListener(v -> showQuestionDialog("Operators"));
        javaInput.setOnClickListener(v -> showQuestionDialog("Input & Output"));
        javaStatements.setOnClickListener(v -> showQuestionDialog("Control Statements"));
        javaLoops.setOnClickListener(v -> showQuestionDialog("Loops"));
        javaArrays.setOnClickListener(v -> showQuestionDialog("Arrays"));
        javaStrings.setOnClickListener(v -> showQuestionDialog("Strings in Java"));
        javaMethods.setOnClickListener(v -> showQuestionDialog("Methods in Java"));
        javaOOPs.setOnClickListener(v -> showQuestionDialog("Object-Oriented Programming"));
        javaConstructors.setOnClickListener(v -> showQuestionDialog("Constructors & Destructors"));
        javaInheritance.setOnClickListener(v -> showQuestionDialog("Inheritance"));
        javaPolymorphism.setOnClickListener(v -> showQuestionDialog("Polymorphism"));
        javaAbstraction.setOnClickListener(v -> showQuestionDialog("Abstraction & Encapsulation"));
        javaException.setOnClickListener(v -> showQuestionDialog("Exception Handling"));
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
            bg.setColor(Color.parseColor("#FF5722")); // orange
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
                Intent i = new Intent(JavaActivity.this, QuizActivity.class);
                i.putExtra("language", "Java");
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
