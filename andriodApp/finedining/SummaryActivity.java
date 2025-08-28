package edu.highpoint.finedining;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class SummaryActivity extends AppCompatActivity {
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submission_page);

        nextButton = findViewById(R.id.nextButton);


        String selectedButton = getIntent().getStringExtra("selectedButton");
        String email = getIntent().getStringExtra("email");
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String date = getIntent().getStringExtra("selectedDate");
        String numberOfGuests = getIntent().getStringExtra("selectedGuests");
        String time = getIntent().getStringExtra("selectedTime");
        int funMode = getIntent().getIntExtra("funMode", 0);

        TextView buttonTextView = findViewById(R.id.buttonTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        TextView passwordTextView = findViewById(R.id.passwordTextView);
        TextView phoneTextView = findViewById(R.id.phoneTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView guestsTextView = findViewById(R.id.guestsTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);

        buttonTextView.setText("Button Pressed: " + selectedButton);
        emailTextView.setText("Email: " + email);
        usernameTextView.setText("Username: " + username);
        passwordTextView.setText("Password: " + password);
        phoneTextView.setText("Phone Number: " + phoneNumber);
        dateTextView.setText("Date: " + date);
        guestsTextView.setText("Number of Guests: " + numberOfGuests);
        timeTextView.setText("Time: " + time);

        // Write the data to a .txt file in internal storage
        saveDataToFile(selectedButton, username, password, phoneNumber, date, numberOfGuests, time);

        // Handle the "Next" button click
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(SummaryActivity.this, Script.class);
            intent.putExtra("funMode", funMode); // Pass the funMode value
            intent.putExtra("password", password);
            startActivity(intent);
        });
    }

    // Method to write data to a .txt file in internal storage
    private void saveDataToFile(String selectedButton, String username, String password, String phoneNumber, String date, String numberOfGuests, String time) {
        String fileName = "user_data.txt";  // Internal storage filename

        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        String minutes = timeParts[1];
        hour += 12;

        String fileContents = selectedButton + "\n" + date + "\n" + numberOfGuests +
                "\n" + hour + "\n" + minutes + "\n" + username + "\n" + password + "\n" + phoneNumber;

        try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving data to file", Toast.LENGTH_SHORT).show();
        }
    }
}
