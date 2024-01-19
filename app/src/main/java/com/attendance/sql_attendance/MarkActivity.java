package com.attendance.sql_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarkActivity extends AppCompatActivity {

    private CheckBox checkBoxPresent, checkBoxLeave;
    private Button btnMark;
    private String studentName, rollNo;
    private FusedLocationProviderClient fusedLocationClient;

    // Geofence parameters
    private static final double GEOFENCE_LATITUDE = 13.054791; // my area 8.793164 and outside area 8.810793 13.054791, 80.256020
    private static final double GEOFENCE_LONGITUDE =80.256020; // my area 78.113976 and outside area 78.137532
    private static final float GEOFENCE_RADIUS = 1000; // Example radius in meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);

        checkBoxPresent = findViewById(R.id.present);
        checkBoxLeave = findViewById(R.id.leave);
        btnMark = findViewById(R.id.btnmark);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        studentName = getIntent().getStringExtra("STUDENT_NAME");
        rollNo = getIntent().getStringExtra("ROLL_NO");

        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAttendance();
            }
        });
    }

    private void markAttendance() {
        if (!checkBoxPresent.isChecked() && !checkBoxLeave.isChecked()) {
            Toast.makeText(MarkActivity.this, "Please select attendance status", Toast.LENGTH_SHORT).show();
            return;
        }


        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get the attendance status
        String attendanceStatus = checkBoxPresent.isChecked() ? "Present" : "Leave";

        // Get the current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double currentLatitude = location.getLatitude();
                            double currentLongitude = location.getLongitude();

                            // Check if the device is within the specified geofence
                            if (isWithinGeofence(currentLatitude, currentLongitude)) {
                                // Execute AsyncTask to save data in the database
                                new SaveAttendanceTask().execute(studentName, rollNo, attendanceStatus);
                            } else {
                                Toast.makeText(MarkActivity.this, "You are outside the attendance area", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private boolean isWithinGeofence(double currentLatitude, double currentLongitude) {
        Location geofenceLocation = new Location("");
        geofenceLocation.setLatitude(GEOFENCE_LATITUDE);
        geofenceLocation.setLongitude(GEOFENCE_LONGITUDE);

        float distance = geofenceLocation.distanceTo(new Location(""){{setLatitude(currentLatitude); setLongitude(currentLongitude);}});

        return distance <= GEOFENCE_RADIUS;
    }


    class SaveAttendanceTask extends AsyncTask<String, Void, Void> {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        @Override
        protected Void doInBackground(String... strings) {
            String studentName = strings[0];
            String rollNo = strings[1];
            String attendanceStatus = strings[2];

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql:/192.168.1.10:3306/attendance", "atte", "atte");

                // Use PreparedStatement to avoid SQL injection
                String query = "INSERT INTO attendance_records (student_name, roll_no, attendance_status,timestamp) VALUES (?, ?, ?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, studentName);
                preparedStatement.setString(2, rollNo);
                preparedStatement.setString(3, attendanceStatus);
                //Get the current date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateTime = sdf.format(new Date());

                preparedStatement.setString(4, currentDateTime);

                preparedStatement.executeUpdate();
                connection.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MarkActivity.this, "Attendance marked successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MarkActivity.this, EndActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
