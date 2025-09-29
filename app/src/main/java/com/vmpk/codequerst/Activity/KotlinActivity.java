package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
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

        //  Har topic ke liye ChooseKotlinActivity open hogi
        kotlinIntroduction.setOnClickListener(v -> openChooseKotlinActivity("Introduction to Kotlin"));
        kotlinDataType.setOnClickListener(v -> openChooseKotlinActivity("Variables & DataTypes"));
        kotlinOperators.setOnClickListener(v -> openChooseKotlinActivity("Operators"));
        kotlinInput.setOnClickListener(v -> openChooseKotlinActivity("Input & Output"));
        kotlinStatements.setOnClickListener(v -> openChooseKotlinActivity("Control Statements"));
        kotlinLoops.setOnClickListener(v -> openChooseKotlinActivity("Loops"));
        kotlinFunctions.setOnClickListener(v -> openChooseKotlinActivity("Functions"));
        kotlinNull.setOnClickListener(v -> openChooseKotlinActivity("Null Safety"));
        kotlinArrays.setOnClickListener(v -> openChooseKotlinActivity("Arrays"));
        kotlinCollections.setOnClickListener(v -> openChooseKotlinActivity("Collections"));
        kotlinStrings.setOnClickListener(v -> openChooseKotlinActivity("Strings"));
        kotlinClasses.setOnClickListener(v -> openChooseKotlinActivity("Classes & Objects"));
        kotlinInheritance.setOnClickListener(v -> openChooseKotlinActivity("Inheritance"));
        kotlinDataClasses.setOnClickListener(v -> openChooseKotlinActivity("Data Classes & Object"));
        kotlinException.setOnClickListener(v -> openChooseKotlinActivity("Exception Handling"));
    }

    private void openChooseKotlinActivity(String topicName) {
        Intent i = new Intent(KotlinActivity.this, ChooseKotlinActivity.class);
        i.putExtra("topic", topicName);
        startActivity(i);
    }
}
