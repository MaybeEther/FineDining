package edu.highpoint.finedining;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FormActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword, editTextEmail, editTextPhoneNumber;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Reference form fields
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        Button buttonNext = findViewById(R.id.buttonNext);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        // Set listener for Next button
        buttonNext.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String email = editTextEmail.getText().toString();

            // Validate email format
            if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isValidEmail(email)) {
                String selectedButton = getIntent().getStringExtra("selectedButton");
                int funMode = getIntent().getIntExtra("funMode", 0);
                Intent intent = new Intent(FormActivity.this, NextActivity.class);
                // Put the input data into the intent to pass to NextActivity
                intent.putExtra("selectedButton", selectedButton);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("email", email);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("funMode", funMode);
                startActivity(intent);
            } else {
                Toast.makeText(FormActivity.this, "Invalid email format! Must be 22 characters and end with @highpoint.edu", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Validate email format and length
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && email.length() == 22 && email.endsWith("@highpoint.edu");
    }
}

