package com.attendance.sql_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AddActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etPassword, etRollNo;
    private RadioGroup genderRadioGroup;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        etName = findViewById(R.id.edit_text_name);
        etPhone = findViewById(R.id.edit_text_phone);
        etPassword = findViewById(R.id.edit_text_password);
        etRollNo = findViewById(R.id.age);
        genderRadioGroup = findViewById(R.id.gender);
        registerButton = findViewById(R.id.btnregister);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String password = etPassword.getText().toString();
                String rollNo = etRollNo.getText().toString();
                new SaveDataTask().execute(name, phone, password, rollNo);
                Toast.makeText(AddActivity.this, "Student Added Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
    class SaveDataTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            String name = strings[0];
            String phone = strings[1];
            String password = strings[2];
            String rollNo = strings[3];
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.10:3306/attendance", "atte", "atte");

                // Use PreparedStatement to avoid SQL injection
                String query = "INSERT INTO details (name, password, phone_no, roll_no) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, phone);
                preparedStatement.setString(4, rollNo);

                preparedStatement.executeUpdate();
                ((java.sql.Connection) connection).close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    }


