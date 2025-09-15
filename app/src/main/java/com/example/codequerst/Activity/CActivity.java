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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codequerst.R;

public class CActivity extends AppCompatActivity {

    TextView backC;
    LinearLayout cBasic, cdataType, cKeywords, cOperators, cStatements, cLoops, cFunction,
            cRecursion, cArrays, cStrings, cPointers, cStructures, cDMA, cFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cactivity);

        backC = findViewById(R.id.backC);
        cBasic = findViewById(R.id.cBasic);
        cdataType = findViewById(R.id.cdataType);
        cKeywords = findViewById(R.id.cKeyword);
        cOperators = findViewById(R.id.cOperators);
        cStatements = findViewById(R.id.cStatements);
        cLoops = findViewById(R.id.cLoops);
        cFunction = findViewById(R.id.cFunctions);
        cRecursion = findViewById(R.id.cRecursion);
        cArrays = findViewById(R.id.cArrays);
        cStrings = findViewById(R.id.cStrings);
        cPointers = findViewById(R.id.cPointers);
        cStructures = findViewById(R.id.cStructures);
        cDMA = findViewById(R.id.cDMA);
        cFile = findViewById(R.id.cFile);

        backC.setOnClickListener(v -> finish());

        //  Click listeners with topic names
        cBasic.setOnClickListener(v -> showQuestionDialog("Basics of C Programming"));
        cdataType.setOnClickListener(v -> showQuestionDialog("DataTypes_Variables"));
        cKeywords.setOnClickListener(v -> showQuestionDialog("Constants_Keyword"));
        cOperators.setOnClickListener(v -> showQuestionDialog("Operators"));
        cStatements.setOnClickListener(v -> showQuestionDialog("Control_Statement"));
        cLoops.setOnClickListener(v -> showQuestionDialog("Loops"));
        cFunction.setOnClickListener(v -> showQuestionDialog("Functions in C"));
        cRecursion.setOnClickListener(v -> showQuestionDialog("Recursion"));
        cArrays.setOnClickListener(v -> showQuestionDialog("Arrays"));
        cStrings.setOnClickListener(v -> showQuestionDialog("Strings"));
        cPointers.setOnClickListener(v -> showQuestionDialog("Pointers"));
        cStructures.setOnClickListener(v -> showQuestionDialog("Structures"));
        cDMA.setOnClickListener(v -> showQuestionDialog("DMA"));
        cFile.setOnClickListener(v -> showQuestionDialog("File"));
    }

    //  Dialog with topic + circular buttons
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

        //  Dark background with rounded corners
        GradientDrawable bgDialog = new GradientDrawable();
        bgDialog.setColor(Color.parseColor("#2C2C2C")); // dark gray
        bgDialog.setCornerRadius(40f); // rounded corners
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

        // GridLayout (3 columns)
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(3);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        gridLayout.setUseDefaultMargins(true);

        // Center the grid inside parent
        LinearLayout.LayoutParams gridParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        gridParams.gravity = Gravity.CENTER;
        gridLayout.setLayoutParams(gridParams);

        int[] values = {5, 10, 15, 20, 25, 30};

        // Set the view on builder BEFORE creating dialog
        builder.setView(parentLayout);
        final AlertDialog dialog = builder.create(); // final so lambda can use it

        for (int value : values) {
            Button btn = new Button(this);

            // small circle size (responsive)
            int size = (int) (55 * density); // 55dp
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = size;
            params.height = size;
            int margin = (int) (8 * density);
            params.setMargins(margin, margin, margin, margin);
            btn.setLayoutParams(params);

            // circular drawable
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor("#9C27B0")); // purple fill
            bg.setStroke((int) (2 * density), Color.WHITE);

            btn.setBackground(bg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setBackgroundTintList(null);
            }

            btn.setText(String.valueOf(value));
            btn.setTextColor(Color.WHITE);
            btn.setTextSize(14);
            btn.setGravity(Gravity.CENTER);

            // Click listener
            btn.setOnClickListener(v -> {
                Intent i = new Intent(CActivity.this, QuizActivity.class);
                i.putExtra("language", "C");
                i.putExtra("topic", topicName);
                i.putExtra("numQuestions", value);
                startActivity(i);
                dialog.dismiss();
            });

            gridLayout.addView(btn);
        }

        parentLayout.addView(gridLayout);

        // show dialog with width adjustment
        dialog.setOnShowListener(d -> {
            Window window = dialog.getWindow();
            if (window != null) {
                // keep window transparent, only parentLayout has bg
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
                window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });

        dialog.show();

    }



}
