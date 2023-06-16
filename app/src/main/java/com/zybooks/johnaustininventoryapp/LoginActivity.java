package com.zybooks.johnaustininventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.KeyEvent;

public class LoginActivity extends AppCompatActivity {

    private EditText user_login;
    private EditText password;
    private Button login;

    private InventoryDatabase mInventoryDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //creates an instance of the inventory database
        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        boolean userExists = mInventoryDb.checkUser();

        if (!userExists) {
            Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
            startActivity(intent);
        }

        // links ui elements to variables
        user_login = (EditText) findViewById(R.id.user_login);
        password = (EditText) findViewById(R.id.password_login);
        login = (Button) findViewById(R.id.login_user);

        //prevents creating a new line when enter is pressed while also validating
        // the user credentials
        user_login.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    validate(user_login.getText().toString(), password.getText().toString());
                    return true;
                }
                return false;
            }
        });

        //listens for user to press enter key and validates user input to login
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    validate(user_login.getText().toString(), password.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // listens for user input to the login button and passes input to the validation function
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(user_login.getText().toString(), password.getText().toString());
            }
        });



    }

    //validation function for user login
    private void validate (String username, String password) {

        //pass the username and password value to the grantUserLogin function from
        //inventory db and stores value in result variable
        boolean result = mInventoryDb.grantUserLogin(username, password);

        //checks if the function returns either a true or false result and handles the results
        //if true opens category activity page
        if (result) {
            Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
            startActivity(intent);
        }

        //invalid login
        else {
            Toast.makeText(this, "Invalid Username/Password", Toast.LENGTH_LONG).show();
        }
    }
}