package com.attendance.sql_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etName, etEmail, etPassword, etReenterPassword, etRollNo;
    private RadioGroup genderRadioGroup;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.edit_text_name);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
        etReenterPassword = findViewById(R.id.conpass);
        etRollNo = findViewById(R.id.age);
        genderRadioGroup = findViewById(R.id.gender);
        registerButton = findViewById(R.id.btnregister);
    }
    public void save(View view){}
}