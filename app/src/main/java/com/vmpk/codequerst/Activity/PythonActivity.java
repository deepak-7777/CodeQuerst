package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
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

        // ✅ Open ChoosePythonActivity on topic click
        pythonBasic.setOnClickListener(v -> openChoosePythonActivity("Basics of Python"));
        pythonDataType.setOnClickListener(v -> openChoosePythonActivity("Variables & DataTypes"));
        pythonOperators.setOnClickListener(v -> openChoosePythonActivity("Operators"));
        pythonInput.setOnClickListener(v -> openChoosePythonActivity("Input & Output"));
        pythonStatements.setOnClickListener(v -> openChoosePythonActivity("Control Statements"));
        pythonLoops.setOnClickListener(v -> openChoosePythonActivity("Loops"));
        pythonFunction.setOnClickListener(v -> openChoosePythonActivity("Functions"));
        pythonDataStructures.setOnClickListener(v -> openChoosePythonActivity("Data Structures"));
        pythonStringHandling.setOnClickListener(v -> openChoosePythonActivity("String Handling"));
        pythonModules.setOnClickListener(v -> openChoosePythonActivity("Modules & Packages"));
        pythonFileHandling.setOnClickListener(v -> openChoosePythonActivity("File Handling"));
        pythonExceptionHandling.setOnClickListener(v -> openChoosePythonActivity("Exception Handling"));
        pythonOOPs.setOnClickListener(v -> openChoosePythonActivity("OPPs"));
        pythonIterators.setOnClickListener(v -> openChoosePythonActivity("Iterators & Generators"));
        pythonDecorators.setOnClickListener(v -> openChoosePythonActivity("Decorators"));
    }

    private void openChoosePythonActivity(String topicName) {
        Intent intent = new Intent(PythonActivity.this, ChoosePythonActivity.class);
        intent.putExtra("language", "Python");
        intent.putExtra("topic", topicName);
        startActivity(intent);
    }
}
