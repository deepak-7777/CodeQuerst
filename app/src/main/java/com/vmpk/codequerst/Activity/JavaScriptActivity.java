package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
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

        // ✅ Click listener: directly open ChooseJavaScriptActivity with topic
        setTopicClick(jsIntroduction, "Introduction");
        setTopicClick(jsDataType, "Variables & DataTypes");
        setTopicClick(jsOperators, "Operators");
        setTopicClick(jsInput, "Input & Output");
        setTopicClick(jsStatements, "Control Statements");
        setTopicClick(jsLoops, "Loops");
        setTopicClick(jsFunctions, "Functions");
        setTopicClick(jsArrays, "Arrays");
        setTopicClick(jsStrings, "Strings");
        setTopicClick(jsObjects, "Objects");
        setTopicClick(jsDOM, "DOM Manipulation");
        setTopicClick(jsEvents, "Events");
        setTopicClick(jsClasses, "Classes & OOPs");
        setTopicClick(jsErrorHandling, "Error & Exception");
        setTopicClick(jsAsync, "Async or Await");
        setTopicClick(jsPromises, "Promises");
    }

    private void setTopicClick(LinearLayout layout, String topicName) {
        layout.setOnClickListener(v -> {
            Intent intent = new Intent(JavaScriptActivity.this, ChooseJavaScriptActivity.class);
            intent.putExtra("language", "JavaScript");
            intent.putExtra("topic", topicName);
            startActivity(intent);
        });
    }
}
