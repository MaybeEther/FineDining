package edu.highpoint.finedining;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainClass extends AppCompatActivity {

    private int funMode = 0; // Variable toggled by switch
    private String selectedButton = "";  // Declare the variable outside the lambda
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference buttons and switch
        ImageButton buttonImagePrime = findViewById(R.id.buttonImagePrime);
        ImageButton buttonImageAlo = findViewById(R.id.buttonImageAlo);
        ImageButton buttonImageKazoku = findViewById(R.id.buttonImageKazoku);
        Switch switchFunMode = findViewById(R.id.switchFunMode);

        // Set listener for switch
        switchFunMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                funMode = 1; // Switch on
            } else {
                funMode = 0; // Switch off
            }
        });

        // Set image button click listeners
        buttonImagePrime.setOnClickListener(v -> {selectedButton = "Prime";  // Declare and set the selectedButton inside the lambda
            navigateToFormPage(selectedButton);});  // Pass the selectedButton to the navigateToFormPage method

        buttonImageKazoku.setOnClickListener(v -> {selectedButton = "Kazoku";  // Declare and set the selectedButton inside the lambda
            navigateToFormPage(selectedButton);});  // Pass the selectedButton to the navigateToFormPage method

        buttonImageAlo.setOnClickListener(v -> {selectedButton = "Alo";  // Declare and set the selectedButton inside the lambda
            navigateToFormPage(selectedButton);});  // Pass the selectedButton to the navigateToFormPage method

    }

    // Navigate to the form page
    private void navigateToFormPage(String selectedButton) {
        Intent intent = new Intent(MainClass.this, FormActivity.class);
        intent.putExtra("selectedButton", selectedButton);
        intent.putExtra("funMode", funMode);
        startActivity(intent);
    }
}
