package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.vmpk.codequerst.R;

public class JavaActivity extends AppCompatActivity {

    TextView backJava;
    LinearLayout javaIntroduction, javaDataType, javaOperators, javaInput, javaStatements, javaLoops, javaArrays,
            javaStrings, javaMethods, javaOOPs, javaConstructors, javaInheritance, javaPolymorphism, javaAbstraction, javaException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_java);

        backJava = findViewById(R.id.backJava);
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

        backJava.setOnClickListener(v -> finish());

        // ✅ Open ChooseJavaActivity on topic click
        javaIntroduction.setOnClickListener(v -> openChooseJavaActivity("Introduction to Java"));
        javaDataType.setOnClickListener(v -> openChooseJavaActivity("DataTypes & Variables"));
        javaOperators.setOnClickListener(v -> openChooseJavaActivity("Operators"));
        javaInput.setOnClickListener(v -> openChooseJavaActivity("Input & Output"));
        javaStatements.setOnClickListener(v -> openChooseJavaActivity("Control Statements"));
        javaLoops.setOnClickListener(v -> openChooseJavaActivity("Loops"));
        javaArrays.setOnClickListener(v -> openChooseJavaActivity("Arrays"));
        javaStrings.setOnClickListener(v -> openChooseJavaActivity("Strings in Java"));
        javaMethods.setOnClickListener(v -> openChooseJavaActivity("Methods in Java"));
        javaOOPs.setOnClickListener(v -> openChooseJavaActivity("OOPs"));
        javaConstructors.setOnClickListener(v -> openChooseJavaActivity("Constructors"));
        javaInheritance.setOnClickListener(v -> openChooseJavaActivity("Inheritance"));
        javaPolymorphism.setOnClickListener(v -> openChooseJavaActivity("Polymorphism"));
        javaAbstraction.setOnClickListener(v -> openChooseJavaActivity("Abstraction"));
        javaException.setOnClickListener(v -> openChooseJavaActivity("Exception Handling"));
    }

    private void openChooseJavaActivity(String topicName) {
        Intent intent = new Intent(JavaActivity.this, ChooseJavaActivity.class);
        intent.putExtra("language", "Java");
        intent.putExtra("topic", topicName);
        startActivity(intent);
    }
}
