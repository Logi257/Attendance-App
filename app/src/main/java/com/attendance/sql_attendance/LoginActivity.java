package com.attendance.sql_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {
    private EditText etName, etPassword, etRoll;

    private Button loginButton;
    private Spinner userTypeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnlogin);
        etRoll = findViewById(R.id.roll_no);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);


        String[] userTypes = {"Admin", "Student"};

        // Create an ArrayAdapter to populate the Spinner with the user types
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        userTypeSpinner.setAdapter(adapter);
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUserType = userTypes[position];
                if (selectedUserType.equals("Admin")) {
                    etRoll.setVisibility(View.GONE); // Hide the "Roll No" field
                } else {
                    etRoll.setVisibility(View.VISIBLE); // Show the "Roll No" field for other user types
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUserType = userTypeSpinner.getSelectedItem().toString();
                String name = etName.getText().toString();
                String password = etPassword.getText().toString();
                String rollNo = etRoll.getText().toString();
                if (selectedUserType.equals("Admin") && name.equals("admin") && password.equals("admin123")) {
                    Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                    Intent adminIntent = new Intent(LoginActivity.this, AdminActivity.class);
                    adminIntent.putExtra("USER_NAME", name);
                    startActivity(adminIntent);
                } else if (selectedUserType.equals("Student")) {
                    //(name, password, rollNo);
                    // Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                  //   Intent studentIntent = new Intent(LoginActivity.this, StudentsActivity.class);
                  //   studentIntent.putExtra("USER_NAME", name);
                    //studentIntent.putExtra("ROLL_NO", rollNo);
                    // startActivity(studentIntent);


                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }

                // Execute the AsyncTask to check login credentials in the background
                new LoginTask().execute(name, password, rollNo);


            }
        });
    }

    class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String name = strings[0].trim();
            String password = strings[1].trim();
            String rollNo = strings[2].trim();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.10:3306/attendance", "atte", "atte");

                // Use PreparedStatement to avoid SQL injection
                String query = "SELECT * FROM details WHERE name = ? AND password = ? AND roll_no = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, rollNo);


                ResultSet resultSet = preparedStatement.executeQuery();
                boolean loginSuccessful = resultSet.next();
                connection.close();

                return loginSuccessful;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();

            }

            return false;


        }

        @Override
        protected void onPostExecute(Boolean loginSuccessful) {
            String name = etName.getText().toString();
            String rollNo = etRoll.getText().toString();
            if (loginSuccessful) {

                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, StudentsActivity.class);
                intent.putExtra("USER_NAME", name);
                intent.putExtra("ROLL_NO", rollNo);
                startActivity(intent);
            } else {
                // Login failed, show an error message
                //Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(loginSuccessful);

        }
    }
}