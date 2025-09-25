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

public class CppActivity extends AppCompatActivity {

    TextView backCpp;
    LinearLayout cppBasic, cppDataType, cppOperators, cppStatements, cppLoops, cppFunction, cppArrays,
            cppStrings, cppPointers, cppOPPs, cppConstructors, cppInheritance, cppPolymorphism, cppException, cppFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cpp);

        backCpp = findViewById(R.id.backCpp);
        cppBasic = findViewById(R.id.cppBasic);
        cppDataType = findViewById(R.id.cppDataType);
        cppOperators = findViewById(R.id.cppOperators);
        cppStatements = findViewById(R.id.cppStatement);
        cppLoops = findViewById(R.id.cppLoops);
        cppFunction = findViewById(R.id.cppFunctions);
        cppArrays = findViewById(R.id.cppArrays);
        cppStrings = findViewById(R.id.cppStrings);
        cppPointers = findViewById(R.id.cppPointers);
        cppOPPs = findViewById(R.id.cppOpp);
        cppConstructors = findViewById(R.id.cppConstructors);
        cppInheritance = findViewById(R.id.cppInheritance);
        cppPolymorphism = findViewById(R.id.cppPolymorphism);
        cppException = findViewById(R.id.cppException);
        cppFile = findViewById(R.id.cppFile);

        backCpp.setOnClickListener(v -> finish());

        // ✅ All topics with dialog
        cppBasic.setOnClickListener(v -> showQuestionDialog("Basics of C++"));
        cppDataType.setOnClickListener(v -> showQuestionDialog("DataTypes & Variables"));
        cppOperators.setOnClickListener(v -> showQuestionDialog("Operators"));
        cppStatements.setOnClickListener(v -> showQuestionDialog("Control & Statements"));
        cppLoops.setOnClickListener(v -> showQuestionDialog("Loops"));
        cppFunction.setOnClickListener(v -> showQuestionDialog("Functions"));
        cppArrays.setOnClickListener(v -> showQuestionDialog("Arrays"));
        cppStrings.setOnClickListener(v -> showQuestionDialog("Strings"));
        cppPointers.setOnClickListener(v -> showQuestionDialog("Pointers"));
        cppOPPs.setOnClickListener(v -> showQuestionDialog("Object-Oriented Programming"));
        cppConstructors.setOnClickListener(v -> showQuestionDialog("Constructors & Destructors"));
        cppInheritance.setOnClickListener(v -> showQuestionDialog("Inheritance"));
        cppPolymorphism.setOnClickListener(v -> showQuestionDialog("Polymorphism"));
        cppException.setOnClickListener(v -> showQuestionDialog("Exception & Handling"));
        cppFile.setOnClickListener(v -> showQuestionDialog("File & Handling"));
    }

    // ✅ Reusable dialog method
    private void showQuestionDialog(String topicName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout parentLayout = new LinearLayout(this);
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        float density = getResources().getDisplayMetrics().density;
        int padHor = (int) (24 * density);
        int padVer = (int) (20 * density);
        parentLayout.setPadding(padHor, padVer, padHor, padVer);
        parentLayout.setGravity(Gravity.CENTER);

        GradientDrawable bgDialog = new GradientDrawable();
        bgDialog.setColor(Color.parseColor("#2C2C2C")); // 🔵 dark gray
        bgDialog.setCornerRadius(40f);
        parentLayout.setBackground(bgDialog);

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

            int size = (int) (55 * density);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = size;
            params.height = size;
            int margin = (int) (8 * density);
            params.setMargins(margin, margin, margin, margin);
            btn.setLayoutParams(params);

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor("#007ACC")); // 💙 C++ Blue
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
                Intent i = new Intent(CppActivity.this, QuizActivity.class);
                i.putExtra("language", "C++");
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
