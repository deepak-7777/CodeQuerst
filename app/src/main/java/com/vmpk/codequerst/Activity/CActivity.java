package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.vmpk.codequerst.R;

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

        // Click listeners -> open ChooseActivity for difficulty selection
        cBasic.setOnClickListener(v -> openChooseActivity("Basics of C"));
        cdataType.setOnClickListener(v -> openChooseActivity("DataTypes_Variables"));
        cKeywords.setOnClickListener(v -> openChooseActivity("Constants_Keyword"));
        cOperators.setOnClickListener(v -> openChooseActivity("Operators"));
        cStatements.setOnClickListener(v -> openChooseActivity("Control_Statement"));
        cLoops.setOnClickListener(v -> openChooseActivity("Loops"));
        cFunction.setOnClickListener(v -> openChooseActivity("Functions"));
        cRecursion.setOnClickListener(v -> openChooseActivity("Recursion"));
        cArrays.setOnClickListener(v -> openChooseActivity("Arrays"));
        cStrings.setOnClickListener(v -> openChooseActivity("Strings"));
        cPointers.setOnClickListener(v -> openChooseActivity("Pointers"));
        cStructures.setOnClickListener(v -> openChooseActivity("Structures"));
        cDMA.setOnClickListener(v -> openChooseActivity("DMA"));
        cFile.setOnClickListener(v -> openChooseActivity("File Handling"));
    }

    private void openChooseActivity(String topicName) {
        Intent intent = new Intent(CActivity.this, ChooseActivity.class);
        intent.putExtra("language", "C");
        intent.putExtra("topic", topicName);
        startActivity(intent);
    }

}
