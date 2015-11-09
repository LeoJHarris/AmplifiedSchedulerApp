package com.lh.leonard.amplifiedscheduler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {


    ProgressDialog ringProgressDialog;
    Boolean loggedOutPersons = false;
    Boolean entered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        EditText editTextEmailMain = (EditText) findViewById(R.id.emailSignIn);
        EditText editTextPasswordMain = (EditText) findViewById(R.id.passwordSignIn);
        Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        AutoResizeTextView buttonForgotPassword = (AutoResizeTextView) findViewById(R.id.buttonForgotPassword);
        AutoResizeTextView buttonRegistration = (AutoResizeTextView) findViewById(R.id.buttonRegistration);
        AutoResizeTextView textViewMadeByMeMain = (AutoResizeTextView) findViewById(R.id.textViewMadeByMeMain);
        ImageView imageViewMainLogo = (ImageView) findViewById(R.id.imageViewMainLogo);

        final Typeface regularFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/GoodDog.otf");
        final Typeface fontWelcome = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Amatic-Bold.ttf");


      //  HashMap args = new HashMap();
       // args.put( "weather", "sunny" );

        //Backendless.Events.dispatch("AddAsContacts", args);

        buttonSignIn.setTypeface(regularFont);
        editTextEmailMain.setTypeface(regularFont);
        editTextPasswordMain.setTypeface(regularFont);
        buttonForgotPassword.setTypeface(regularFont);
        buttonRegistration.setTypeface(regularFont);
        textViewMadeByMeMain.setTypeface(regularFont);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //Fame
        if (width == 320 && height == 480) {
            imageViewMainLogo.requestLayout();
            imageViewMainLogo.getLayoutParams().height = 110;
            editTextEmailMain.setTextSize(21);
            editTextPasswordMain.setTextSize(21);
            buttonForgotPassword.setTextSize(21);
            buttonRegistration.setTextSize(21);
            buttonSignIn.setTextSize(21);
            textViewMadeByMeMain.setTextSize(14);
        }
        // 2.7" QVGA
        else if (width == 240 && height == 320) {
            imageViewMainLogo.requestLayout();
            imageViewMainLogo.getLayoutParams().height = 80;
            editTextEmailMain.setTextSize(12);
            editTextPasswordMain.setTextSize(12);
            buttonForgotPassword.setTextSize(12);
            buttonRegistration.setTextSize(12);
            textViewMadeByMeMain.setTextSize(12);
            buttonSignIn.setTextSize(12);
        }

        if (Backendless.UserService.CurrentUser() != null) {
            BackendlessUser user = Backendless.UserService.CurrentUser();
            final Person personLoggedOut = (Person) user.getProperty("loggedoutperson");

            Backendless.UserService.logout(new AsyncCallback<Void>() {
                public void handleResponse(Void response) {
                    Toast.makeText(getApplicationContext(), personLoggedOut.getFname() + "," + personLoggedOut.getFname(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("nameRegistered") != null) {

                String NameArray = extras.getString("nameRegistered");
                String[] NameSplit = NameArray.split(",");

                Toast.makeText(this,
                        "Successfully registered account for " + NameSplit[0] + " " + NameSplit[1],
                        Toast.LENGTH_LONG).show();
            } else if (extras.getString("loggedoutperson") != null) {
                String NameArray = extras.getString("loggedoutperson");
                String[] NameSplit = NameArray.split(",");

                Toast.makeText(this,
                        "Successfully logged out " + NameSplit[0] + " " + NameSplit[1],
                        Toast.LENGTH_LONG).show();
                loggedOutPersons = true;
            } else if (extras.getString("loggedoutpersonError") != null) {
                String NameArray = extras.getString("loggedoutperson");
                String[] NameSplit = NameArray.split(",");

                Toast.makeText(this,
                        "Error occurred: Logged out " + NameSplit[0] + " " + NameSplit[1],
                        Toast.LENGTH_LONG).show();
                loggedOutPersons = true;
            }
        }

        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

        final Button loginButton = (Button) findViewById(R.id.buttonSignIn);

        final TextView passwordRecoveryButton = (TextView) findViewById(R.id.buttonForgotPassword);

        final TextView registerButton = (TextView) findViewById(R.id.buttonRegistration);

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent registerIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(registerIntent);
            }
        });

        passwordRecoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recoveryPasswordIntent = new Intent(MainActivity.this, ForgotPasswordReset.class);
                startActivity(recoveryPasswordIntent);
            }
        });

        //TODO Threading when users registers, show spinner.


        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                entered = true;
                EditText emailField = (EditText) findViewById(R.id.emailSignIn);
                EditText passwordField = (EditText) findViewById(R.id.passwordSignIn);

                //temp
                 emailField.setText("leojharris@hotmail.com");
                 passwordField.setText("testing");

                if (new Validator().isValidEmail(emailField.getText())) {
                    if (new Validator().isPasswordValid(passwordField.getText())) {

                        ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Signing in ...", true);
                        ringProgressDialog.setCancelable(true);

                        Backendless.Data.mapTableToClass("Person", Person.class);
                        Backendless.Data.mapTableToClass("Slot", Slot.class);
                        Backendless.Persistence.mapTableToClass("Person", Person.class);
                        Backendless.Persistence.mapTableToClass("Slot", Slot.class);

                        Backendless.UserService.login(emailField.getText().toString(), passwordField.getText().toString(), new AsyncCallback<BackendlessUser>() {


                            public void handleResponse(BackendlessUser user) {
                                // user has been logged


                                entered = false;
                                Intent loggedInIntent = new Intent(MainActivity.this, NavDrawerActivity.class);
                                startActivity(loggedInIntent);
                            }

                            public void handleFault(BackendlessFault fault) {
                                // login failed, to get the error code call fault.getCode()
                                System.out.println(fault.getMessage());
                                System.out.println(fault.getCode());
                                if (ringProgressDialog != null) {
                                    entered = false;
                                    ringProgressDialog.dismiss();
                                }
                                Toast.makeText(getApplicationContext(), "Unable to sign in. Please check internet connection & credentials are correct.", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        entered = false;
                        Toast.makeText(getApplicationContext(), "That password is incorrect. Try again or click 'forgot password' to receive a new password.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    entered = false;
                    Toast.makeText(getApplicationContext(), "Please enter your email address in the format someone@example.com.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendSmsByManager() {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+64278720215",
                    null,
                    "Heooolll",
                    null,
                    null);
            Toast.makeText(getApplicationContext(), "Your sms has successfully sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Your sms has failed...",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public class Validator {


        private boolean isPasswordValid(CharSequence password) {
            // At least Length 5
            return password.toString().length() > 4;
        }

        private boolean isValidEmail(CharSequence target) {
            if (target == null)
                return false;

            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public void onBackPressed() {

        if (loggedOutPersons) {
            this.finishAffinity();
        } else {
            super.onBackPressed();
        }
    }
}

