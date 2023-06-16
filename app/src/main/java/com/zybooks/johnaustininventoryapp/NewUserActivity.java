package com.zybooks.johnaustininventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewUserActivity extends AppCompatActivity {

    private EditText user_login;
    private EditText password;
    private EditText confirm_password_login;
    private Button create_user;

    private InventoryDatabase mInventoryDb;


    @Override //creates the new user activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        //creates an instance of the inventory database
        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        // links ui elements to variables
        user_login = (EditText) findViewById(R.id.user_login);
        password = (EditText) findViewById(R.id.password_login);
        confirm_password_login = (EditText) findViewById(R.id.confirm_password_login);
        create_user = (Button) findViewById(R.id.create_user);

        //prevents the creation of a new line when enter key is pressed
        user_login.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        //Listens for enter key to move to the next field
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        //Listens for enter key to move to create user button
        confirm_password_login.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        // listens for user input to the login button and passes input to the validation function
        create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = user_login.getText().toString();
                String userPassword = password.getText().toString();
                String confPassword = confirm_password_login.getText().toString();

                //validates user input
                if (user.equals("") || userPassword.equals("") || confPassword.equals("")) {
                    Toast.makeText(NewUserActivity.this, "No Field Can Be Left Blank", Toast.LENGTH_LONG).show();
                }
                else { //if user input is valid, checks if passwords match
                    if (userPassword.equals(confPassword)) {

                        boolean addNewUser = mInventoryDb.addUser(user, userPassword);

                        //if user is added successfully, sends user to login screen
                        if (addNewUser) {
                            Toast.makeText(NewUserActivity.this, "User Created Successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            //if user is not added successfully, displays error message
                            Toast.makeText(NewUserActivity.this, "Error Creating User", Toast.LENGTH_LONG).show();
                        }

                    }
                    else {
                        //if passwords do not match, displays error message
                        Toast.makeText(NewUserActivity.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}