package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
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

        // ✅ Open ChooseCppActivity for difficulty selection
        cppBasic.setOnClickListener(v -> openChooseCppActivity("Basics of C++"));
        cppDataType.setOnClickListener(v -> openChooseCppActivity("DataTypes & Variables"));
        cppOperators.setOnClickListener(v -> openChooseCppActivity("Operators"));
        cppStatements.setOnClickListener(v -> openChooseCppActivity("Control & Statements"));
        cppLoops.setOnClickListener(v -> openChooseCppActivity("Loops"));
        cppFunction.setOnClickListener(v -> openChooseCppActivity("Functions"));
        cppArrays.setOnClickListener(v -> openChooseCppActivity("Arrays"));
        cppStrings.setOnClickListener(v -> openChooseCppActivity("Strings"));
        cppPointers.setOnClickListener(v -> openChooseCppActivity("Pointers"));
        cppOPPs.setOnClickListener(v -> openChooseCppActivity("OOPs"));
        cppConstructors.setOnClickListener(v -> openChooseCppActivity("Constructors"));
        cppInheritance.setOnClickListener(v -> openChooseCppActivity("Inheritance"));
        cppPolymorphism.setOnClickListener(v -> openChooseCppActivity("Polymorphism"));
        cppException.setOnClickListener(v -> openChooseCppActivity("Exception & Handling"));
        cppFile.setOnClickListener(v -> openChooseCppActivity("File & Handling"));
    }

    private void openChooseCppActivity(String topicName){
        Intent intent = new Intent(CppActivity.this, ChooseCppActivity.class);
        intent.putExtra("language", "C++");
        intent.putExtra("topic", topicName);
        startActivity(intent);
    }
}
