package com.attendance.sql_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class EndActivity extends AppCompatActivity {
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        logout=findViewById(R.id.btnlogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(EndActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(EndActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

}

