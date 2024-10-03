package edu.highpoint.finedining;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import android.content.res.AssetManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp;

public class Script extends AppCompatActivity {
    private String host = "spock.highpoint.edu";
    private String user = "dmcwilliams";
    private String password = "";
    private String file1;
    private String file2;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.script);

        resultTextView = findViewById(R.id.resultTextView);
        password = getIntent().getStringExtra("password");

        // Retrieve the file from assets and assign to file1
        file1 = getFileFromAssets("exi.py");

        // Log the file path for debugging purposes
        //Log.d("FilePaths", "File1 path: " + file1);

        // Show a Toast to give immediate feedback
        //Toast.makeText(this, "File1 path: " + file1, Toast.LENGTH_LONG).show();

        // Retrieve the saved file from internal storage
        file2 = getFileFromInternalStorage("user_data.txt");

        // Log the file path for debugging purposes
        //Log.d("FilePaths", "File2 path: " + file2);

        // Show a Toast to give immediate feedback
        //Toast.makeText(this, "File2 path: " + file2, Toast.LENGTH_LONG).show();

        // Check if the fun mode is enabled
        int funMode = getIntent().getIntExtra("funMode", 0);
        boolean isFunMode = funMode == 1;

        if (isFunMode) {
            resultTextView.setText("Fun Mode is enabled. Displaying SSH output...");
            runSshCommands(true);
        } else {
            resultTextView.setText("Thank you! Running SSH commands in the background...");
            runSshCommands(false);
        }
    }

    // Method to retrieve the file saved in internal storage
    private String getFileFromInternalStorage(String fileName) {
        File file = new File(getFilesDir(), fileName);  // Locate the file in internal storage

        if (file.exists()) {
            //Log.d("InternalStorage", "File found: " + fileName);
            return file.getAbsolutePath();  // Return the file path
        } else {
           // Toast.makeText(this, "File not found: " + fileName, Toast.LENGTH_LONG).show();
           // Log.e("InternalStorage", "File not found: " + fileName);
            return null;  // If file is not found, return null
        }
    }

    private void runSshCommands(boolean displayOutput) {
        new Thread(() -> {
            try {
                // Initialize JSch and setup session
                JSch jsch = new JSch();
                Session session = jsch.getSession(user, host, 22);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");

                // Connect to the SSH server
                session.connect(30000);  // 30 seconds timeout

                // Check if session is connected
                if (session.isConnected()) {
                    Log.d("SSH", "Successfully connected to the SSH server");
                    runOnUiThread(() -> Toast.makeText(Script.this, "SSH connected successfully", Toast.LENGTH_SHORT).show());

                    // Execute 'ls' command to see where you are on the server
                    String command = "ls";
                    ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
                    channelExec.setCommand(command);
                    channelExec.setErrStream(System.err);

                    // Capture the output
                    InputStream inputStream = channelExec.getInputStream();
                    channelExec.connect();

                    // Read and log the output from the command
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder outputBuffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputBuffer.append(line).append("\n");
                    }

                    // Log the output of 'ls'
                   // Log.d("SSH", "Output of 'ls' command:\n" + outputBuffer.toString());

                    channelExec.disconnect();  // Close the exec channel after use

                } else {
                    Log.e("SSH", "Failed to connect to SSH server");
                    runOnUiThread(() -> Toast.makeText(Script.this, "Failed to connect to SSH server", Toast.LENGTH_LONG).show());
                    return;  // Exit if the connection fails
                }

                // Proceed with file transfers if connected
                if (file1 != null) {
                    sendFile(session, file1);
                } else {
                    Log.e("SSH", "File1 is null, cannot send.");
                    runOnUiThread(() -> Toast.makeText(Script.this, "File1 not found", Toast.LENGTH_LONG).show());
                }

                if (file2 != null) {
                    sendFile(session, file2);
                } else {
                    Log.e("SSH", "File2 is null, skipping send.");
                    runOnUiThread(() -> Toast.makeText(Script.this, "File2 not found", Toast.LENGTH_LONG).show());
                }

                // Execute the command and optionally display output
                if (file1 != null) {
                    String pythonCommand = "nohup python3 ~/Desktop/temp_exi.py &";  // Adjust as needed
                    executeCommand(session, pythonCommand, displayOutput);
                }

                // Disconnect from the SSH server
                session.disconnect();
                runOnUiThread(() -> Toast.makeText(Script.this, "SSH commands executed successfully", Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Script.this, "Error executing SSH commands: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void sendFile(Session session, String localFilePath) throws Exception {
        File localFile = new File(localFilePath);

        if (!localFile.exists()) {
            Log.e("FileTransfer", "Local file does not exist: " + localFilePath);
            throw new Exception("Local file does not exist: " + localFilePath);
        }

        // Construct the SCP command
        String remoteFilePath = "~/Desktop/" + localFile.getName();  // Adjust this path as needed
        String scpCommand = "scp -t " + remoteFilePath;

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(scpCommand);

        // Open the streams to interact with SCP
        OutputStream out = channelExec.getOutputStream();
        InputStream in = channelExec.getInputStream();

        channelExec.connect();

        // Check the SCP command's response from the server
        if (checkAck(in) != 0) {
            Log.e("FileTransfer", "SCP failed to initiate on server.");
            throw new Exception("SCP failed to initiate on server.");
        }

        // Send the file's information
        long fileSize = localFile.length();
        String command = "C0644 " + fileSize + " " + localFile.getName() + "\n";
        out.write(command.getBytes());
        out.flush();

        if (checkAck(in) != 0) {
            throw new Exception("Failed to send file metadata.");
        }

        // Send the file content
        try (FileInputStream fis = new FileInputStream(localFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }

        // Send end-of-file indicator
        out.write(new byte[]{0}, 0, 1);
        out.flush();

        if (checkAck(in) != 0) {
            throw new Exception("Failed to transfer file.");
        }

        // Close the streams
        out.close();
        channelExec.disconnect();

        Log.d("FileTransfer", "File sent successfully using SCP: " + localFilePath);
    }

    // Method to check the acknowledgment response during SCP
    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        if (b == 0) return b;  // Success
        if (b == -1) return b;  // End of stream

        if (b == 1 || b == 2) {
            StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) {  // Error
                Log.e("FileTransfer", "SCP error: " + sb.toString());
            }
            if (b == 2) {  // Fatal error
                Log.e("FileTransfer", "SCP fatal error: " + sb.toString());
            }
        }
        return b;
    }




    // Method to execute command and capture terminal output if requested
    private void executeCommand(Session session, String command, boolean displayOutput) throws Exception {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        InputStream in = channelExec.getInputStream();
        channelExec.connect();

        if (displayOutput) {
            // Read the output from the SSH command and display it in the TextView
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder output = new StringBuilder();
            String line;

            // Read output line by line
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");

                // Update the TextView on the UI thread
                runOnUiThread(() -> resultTextView.setText(output.toString()));
            }
        }

        channelExec.disconnect();
    }

    private String getFileFromAssets(String fileName) {
        String tempFilePath = null;
        try {
            // Create a temporary file to store the content in the app's internal storage
            File tempFile = new File(getFilesDir(), "temp_exi.py");
            tempFilePath = tempFile.getAbsolutePath();

            // Log file path for debugging
            Log.d("Assets", "Temporary file path: " + tempFilePath);

            // Read the file from assets and write to the temporary file
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(fileName);
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Assets", "Error reading asset file: " + e.getMessage());
            Toast.makeText(this, "Error reading asset file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return tempFilePath;  // Return the path to the temporary file
    }
}
