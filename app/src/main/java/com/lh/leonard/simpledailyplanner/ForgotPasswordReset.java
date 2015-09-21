package com.lh.leonard.simpledailyplanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ForgotPasswordReset extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_reset);

        final EditText emailPasswordRecovery = (EditText) findViewById(R.id.emailPasswordRecovery);
        TextView textViewHeaderPasswordReset = (TextView) findViewById(R.id.textViewHeaderPasswordReset);
        Button passwordwordRecoveryButton = (Button) findViewById(R.id.buttonEmailNewPassword);
        Button forgotPasswordToMainBack = (Button) findViewById(R.id.buttonForgotPasswordBackToMain);

        final Typeface regularFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/GoodDog.otf");

        textViewHeaderPasswordReset.setTypeface(regularFont);
        emailPasswordRecovery.setTypeface(regularFont);
        passwordwordRecoveryButton.setTypeface(regularFont);
        forgotPasswordToMainBack.setTypeface(regularFont);

        forgotPasswordToMainBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backToMainIntent);
            }
        });

        passwordwordRecoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String emailForPasswordRecovery = emailPasswordRecovery.getText().toString();

                if (!emailForPasswordRecovery.toString().toString().isEmpty()) {

                    //TODO Check if email exists first

                    //TODO not working

                    Backendless.UserService.restorePassword(emailForPasswordRecovery, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void aVoid) {
                            System.out.println("Temporary password has been emailed to " + emailForPasswordRecovery);
                            //TODO Message on the Main page that password sent though, int.addextras
                            //  Intent passwordRecoverySent = new Intent(getApplicationContext(), MainActivity.class);
                            //   startActivity(passwordRecoverySent);

                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            System.out.println("Server reported an error - " + backendlessFault.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your email address.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password_reset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
