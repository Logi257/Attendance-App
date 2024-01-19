package com.attendance.sql_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    private ListView listViewStudents;
    private ArrayAdapter<String> adapter;
    private Button btnFetchData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        listViewStudents = findViewById(R.id.listViewStudents);
        btnFetchData = findViewById(R.id.btnFetchData);

        listViewStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked student data
                String clickedStudentData = (String) parent.getItemAtPosition(position);

                // Start AttendanceHistoryActivity and pass the clicked student data
                Intent intent = new Intent(ViewActivity.this, DetailsActivity.class);
                intent.putExtra("clickedStudentData", clickedStudentData);
                startActivity(intent);
            }
        });


        // Initialize ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewStudents.setAdapter(adapter);

        btnFetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchStudentDataTask().execute();

            }
        });

    }

    class FetchStudentDataTask extends AsyncTask<Void, Void, ArrayList<String>> {
        String error;

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> studentDataList = new ArrayList<>();

            // JDBC variables
            String jdbcUrl = "jdbc:mysql://192.168.1.10:3306/attendance";
            String username = "atte";
            String password = "atte";

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

                // Use PreparedStatement to avoid SQL injection
                String query = "SELECT DISTINCT student_name, roll_no FROM attendance_records";
                PreparedStatement preparedStatement = connection.prepareStatement(query);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String studentName = resultSet.getString("student_name");
                    String rollNo = resultSet.getString("roll_no");

                    // Get the latest timestamp for the student
                    String latestTimestamp = getLatestTimestamp(studentName, rollNo, connection);

                    // Build a string representing each student's data
                    String studentData = "Name: " + studentName + "\nRoll No: " + rollNo + "\nDate/Time: " + latestTimestamp + "\n";

                    studentDataList.add(studentData);
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                error = e.toString();
            }
            return studentDataList;
        }

        private String getLatestTimestamp(String studentName, String rollNo, Connection connection) throws SQLException {
            // Use PreparedStatement to avoid SQL injection
            String query = "SELECT MAX(timestamp) AS latest_timestamp FROM attendance_records " +
                    "WHERE student_name = ? OR roll_no = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentName);
            preparedStatement.setString(2, rollNo);

            ResultSet resultSet = preparedStatement.executeQuery();

            String latestTimestamp = "";

            if (resultSet.next()) {
                latestTimestamp = resultSet.getString("latest_timestamp");
            }

            resultSet.close();
            preparedStatement.close();

            return latestTimestamp;
        }

        @Override
        protected void onPostExecute(ArrayList<String> studentDataList) {
            if (!studentDataList.isEmpty()) {
                // Update the ArrayAdapter with the fetched data
                adapter.clear();
                adapter.addAll(studentDataList);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ViewActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(studentDataList);
        }
    }
}
