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

public class KotlinActivity extends AppCompatActivity {

    TextView backKotlin;
    LinearLayout kotlinIntroduction, kotlinDataType, kotlinOperators, kotlinInput, kotlinStatements,
            kotlinLoops, kotlinFunctions, kotlinNull, kotlinArrays, kotlinCollections,
            kotlinStrings, kotlinClasses, kotlinInheritance, kotlinDataClasses, kotlinException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kotlin);

        backKotlin = findViewById(R.id.backKotlin);
        kotlinIntroduction = findViewById(R.id.kotlinIntroduction);
        kotlinDataType = findViewById(R.id.kotlinDataType);
        kotlinOperators = findViewById(R.id.kotlinOperators);
        kotlinInput = findViewById(R.id.kotlinInput);
        kotlinStatements = findViewById(R.id.kotlinStatements);
        kotlinLoops = findViewById(R.id.kotlinLoops);
        kotlinFunctions = findViewById(R.id.kotlinFunctions);
        kotlinNull = findViewById(R.id.kotlinNull);
        kotlinArrays = findViewById(R.id.kotlinArrays);
        kotlinCollections = findViewById(R.id.kotlinCollections);
        kotlinStrings = findViewById(R.id.kotlinStrings);
        kotlinClasses = findViewById(R.id.kotlinClasses);
        kotlinInheritance = findViewById(R.id.kotlinInheritance);
        kotlinDataClasses = findViewById(R.id.kotlinDataClasses);
        kotlinException = findViewById(R.id.kotlinException);

        backKotlin.setOnClickListener(v -> finish());

        // ✅ Har topic ke liye dialog call
        kotlinIntroduction.setOnClickListener(v -> showQuestionDialog("Introduction to Kotlin"));
        kotlinDataType.setOnClickListener(v -> showQuestionDialog("Variables & DataTypes"));
        kotlinOperators.setOnClickListener(v -> showQuestionDialog("Operators"));
        kotlinInput.setOnClickListener(v -> showQuestionDialog("Input & Output"));
        kotlinStatements.setOnClickListener(v -> showQuestionDialog("Control Statements"));
        kotlinLoops.setOnClickListener(v -> showQuestionDialog("Loops"));
        kotlinFunctions.setOnClickListener(v -> showQuestionDialog("Functions"));
        kotlinNull.setOnClickListener(v -> showQuestionDialog("Null Safety"));
        kotlinArrays.setOnClickListener(v -> showQuestionDialog("Arrays"));
        kotlinCollections.setOnClickListener(v -> showQuestionDialog("Collections"));
        kotlinStrings.setOnClickListener(v -> showQuestionDialog("Strings"));
        kotlinClasses.setOnClickListener(v -> showQuestionDialog("Classes & Objects"));
        kotlinInheritance.setOnClickListener(v -> showQuestionDialog("Inheritance & Interfaces"));
        kotlinDataClasses.setOnClickListener(v -> showQuestionDialog("Data Classes & Object Keyword"));
        kotlinException.setOnClickListener(v -> showQuestionDialog("Exception Handling"));
    }

    // ✅ Same dialog method as PythonActivity
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
                Intent i = new Intent(KotlinActivity.this, QuizActivity.class);
                i.putExtra("language", "Kotlin");
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
