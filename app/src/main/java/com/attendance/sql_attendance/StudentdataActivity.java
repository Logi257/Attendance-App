package com.attendance.sql_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentdataActivity extends AppCompatActivity {
    private TextView vName, vID, presentStudentTV1, absentStudentTV1;
    private Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentdata);

        vName = findViewById(R.id.vName);
        vID = findViewById(R.id.vID);
        presentStudentTV1 = findViewById(R.id.presentStudentTV1);
        absentStudentTV1 = findViewById(R.id.absentStudentTV1);
       doneBtn=findViewById(R.id.doneBtn);

        // Get the student's name and roll number from the intent
        String studentName = getIntent().getStringExtra("STUDENT_NAME");
        String rollNo = getIntent().getStringExtra("ROLL_NO");

        // Update TextViews with student data
        vName.setText("Name: " + studentName);
        vID.setText("Roll No: " + rollNo);

        // Fetch attendance data for present and absent fields from the database
        new FetchAttendanceDataTask().execute(studentName);
       doneBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(StudentdataActivity.this, LoginActivity.class);
               startActivity(intent);
               finish();
           }
       });
        // Add additional UI elements and logic as needed
    }

    private class FetchAttendanceDataTask extends AsyncTask<String, Void, Void> {
        String error;

        @Override
        protected Void doInBackground(String... params) {
            String studentName = params[0];

            // JDBC variables
            String jdbcUrl = "jdbc:mysql://192.168.1.10:3306/attendance";
            String username = "atte";
            String password = "atte";

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

                // Fetch attendance data for present and absent fields
                String presentQuery = "SELECT COUNT(*) AS presentCount FROM attendance_records " +
                        "WHERE student_name = ? AND attendance_status = 'Present'";
                String leaveQuery = "SELECT COUNT(*) AS leaveCount FROM attendance_records " +
                        "WHERE student_name = ? AND attendance_status = 'Leave'";

                PreparedStatement presentStatement = connection.prepareStatement(presentQuery);
                presentStatement.setString(1, studentName);

                PreparedStatement leaveStatement = connection.prepareStatement(leaveQuery);
                leaveStatement.setString(1, studentName);

                ResultSet presentResultSet = presentStatement.executeQuery();
                ResultSet leaveResultSet = leaveStatement.executeQuery();

                int presentCount = 0;
                int leaveCount = 0;

                if (presentResultSet.next()) {
                    presentCount = presentResultSet.getInt("presentCount");
                }

                if (leaveResultSet.next()) {
                    leaveCount = leaveResultSet.getInt("leaveCount");
                }

                // Update TextViews with attendance data
                presentStudentTV1.setText(String.valueOf(presentCount));
                absentStudentTV1.setText(String.valueOf(leaveCount));

                presentResultSet.close();
                leaveResultSet.close();
                presentStatement.close();
                leaveStatement.close();
                connection.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                error = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (error != null) {
                Toast.makeText(StudentdataActivity.this, "Error fetching attendance data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
