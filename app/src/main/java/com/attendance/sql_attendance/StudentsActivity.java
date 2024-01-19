package com.attendance.sql_attendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StudentsActivity extends AppCompatActivity{
    ImageView img,viewImg;
    ImageView picture;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        img=findViewById(R.id.attendance);
        picture=findViewById(R.id.studentImage);
        viewImg=findViewById(R.id.studentview);
        String userName = getIntent().getStringExtra("USER_NAME");
        String rollNo = getIntent().getStringExtra("ROLL_NO");
        TextView studentInfoTextView = findViewById(R.id.studentInfoTextView);
        studentInfoTextView.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        String formattedInfo="Student Name: "+userName+"\nRoll No: "+rollNo;
        studentInfoTextView.setText(formattedInfo);
        Log.d("NewActivity", "Student Name: " + userName);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentsActivity.this, MarkActivity.class);
                intent.putExtra("STUDENT_NAME", userName);
                intent.putExtra("ROLL_NO",rollNo);
                startActivity(intent);
            }
        });
        viewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentsActivity.this, StudentdataActivity.class);
                intent.putExtra("STUDENT_NAME", userName);
                intent.putExtra("ROLL_NO",rollNo);
                startActivity(intent);

            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(StudentsActivity.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it.
                    ActivityCompat.requestPermissions(StudentsActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    // Permission already granted, launch the camera intent.
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                }
            }

        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch the camera intent.
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            } else {
                // Permission denied, handle it (e.g., show a message to the user).
                // You can inform the user that the camera won't work without the necessary permission.
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Check if the data contains the captured image
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    // Set the captured image to the ImageView
                    picture.setImageBitmap(imageBitmap);
                }
            }
        }
    }
}