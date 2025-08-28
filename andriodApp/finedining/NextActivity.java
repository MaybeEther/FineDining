package edu.highpoint.finedining;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;


public class NextActivity extends AppCompatActivity {
    private Spinner guestSpinner, timeSpinner;
    private Button nextButton;
    private CalendarView datePicker;
    private String selectedDate; // Store selected date here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page);

        guestSpinner = findViewById(R.id.guestSpinner);
        timeSpinner = findViewById(R.id.timeSpinner);
        nextButton = findViewById(R.id.nextButton);
        datePicker = findViewById(R.id.datePicker);

        // Set initial date
        long todayMillis = System.currentTimeMillis();
        selectedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(todayMillis));

        datePicker.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Format the selected date and store it
            selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
        });

        // Retrieve the class type from MainClass
        String restaurant = getIntent().getStringExtra("selectedButton");

        // Number of guests dropdown based on class selection
        ArrayList<String> guestOptions = new ArrayList<>();
        switch (restaurant) {
            case "Alo":
                guestOptions.add("1");
                guestOptions.add("2");
                guestOptions.add("3");
                guestOptions.add("4");
                break;
            case "Kazoku":
                for (int i = 1; i <= 10; i++) guestOptions.add(String.valueOf(i));
                break;
            case "Prime":
                guestOptions.add("1");
                guestOptions.add("2");
                guestOptions.add("3");
                guestOptions.add("4");
                guestOptions.add("5");
                break;
            default:
                Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
                return;
        }

        ArrayAdapter<String> guestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, guestOptions);
        guestSpinner.setAdapter(guestAdapter);

        // Time options for dropdown
        ArrayList<String> timeOptions = new ArrayList<>();
        String[] times = {"4:30", "4:45", "5:00", "5:15", "5:30", "5:45", "6:00", "6:15",
                "6:30", "6:45", "7:00", "7:15", "7:30", "7:45", "8:00", "8:15"};
        for (String time : times) {
            timeOptions.add(time);
        }

        if ("Prime".equals(restaurant)) {
            timeOptions.add("8:30");
        }

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeOptions);
        timeSpinner.setAdapter(timeAdapter);

        // Handle Next button click
        nextButton.setOnClickListener(v -> {
            // Get selected values from spinners
            String selectedGuests = guestSpinner.getSelectedItem().toString();
            String selectedTime = timeSpinner.getSelectedItem().toString();

            // Use the stored selected date
            // Create intent to navigate to SummaryActivity
            Intent intent = new Intent(NextActivity.this, SummaryActivity.class);
            intent.putExtra("selectedGuests", selectedGuests);
            intent.putExtra("selectedTime", selectedTime);
            intent.putExtra("selectedDate", selectedDate);

            // Carry over any other necessary intent data
            String selectedButton = getIntent().getStringExtra("selectedButton");
            int funMode = getIntent().getIntExtra("funMode", 0);
            String email = getIntent().getStringExtra("email");
            String username = getIntent().getStringExtra("username");
            String password = getIntent().getStringExtra("password");
            String phoneNumber = getIntent().getStringExtra("phoneNumber");

            intent.putExtra("email", email);
            intent.putExtra("funMode", funMode);
            intent.putExtra("selectedButton", selectedButton);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            intent.putExtra("phoneNumber", phoneNumber);

            // Start SummaryActivity
            startActivity(intent);
        });
    }
}
