package com.lh.leonard.amplifiedscheduler;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;


public class RegistrationActivity extends ActionBarActivity {

    Drawable tickIconDraw;
    Drawable crossIconDraw;

    Drawable emailIconDraw;
    Drawable userProfileIconDraw;
    Drawable passwordIconDraw;
    Drawable countryIconDraw;
    Drawable phoneIconDraw;

    Drawable emailGoodIconDraw;
    Drawable userGoodProfileDraw;
    Drawable passwordGoodIconDraw;
    Drawable countryGoodIconDraw;
    Drawable phoneGoodIconDraw;

    Drawable passwordBadIconDraw;
    Drawable emailBadIconDraw;

    Validator validator = new Validator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

        final Typeface regularFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/GoodDog.otf");

        final Button registerButton = (Button) findViewById(R.id.buttonRegisterUser);

        final EditText emailField = (EditText) findViewById(R.id.editTextEmail);
        final EditText passwordField = (EditText) findViewById(R.id.editTextPassword);
        final EditText passwordConfirmField = (EditText) findViewById(R.id.editTextPasswordConfirmReg);
        final EditText fnameField = (EditText) findViewById(R.id.editTextFNameReg);
        final EditText lnameField = (EditText) findViewById(R.id.editTextLNameReg);
        final EditText phoneField = (EditText) findViewById(R.id.editTextPhoneReg);

        final AutoResizeTextView txtLabelFnameReg = (AutoResizeTextView) findViewById(R.id.txtLabelFnameReg);
        final AutoResizeTextView txtLabeLnameReg = (AutoResizeTextView) findViewById(R.id.txtLabeLnameReg);
        final AutoResizeTextView txtLabelTextPasswordReg = (AutoResizeTextView) findViewById(R.id.txtLabelTextPasswordReg);
        final AutoResizeTextView txtLabelPasswordConfirmReg = (AutoResizeTextView) findViewById(R.id.txtLabelPasswordConfirmReg);
        final AutoResizeTextView txtLabelEmailReg = (AutoResizeTextView) findViewById(R.id.txtLabelEmailReg);
        final AutoResizeTextView txtLabelCountryReg = (AutoResizeTextView) findViewById(R.id.txtLabelCountryReg);
        final AutoResizeTextView txtLabelPhone = (AutoResizeTextView) findViewById(R.id.txtLabelPhone);
        TextView textViewHeaderReg = (TextView) findViewById(R.id.textViewHeaderRegister);

        tickIconDraw = getResources().getDrawable(R.drawable.ic_tick);
        crossIconDraw = getResources().getDrawable(R.drawable.ic_cross);
        emailIconDraw = getResources().getDrawable(R.drawable.ic_email);
        userProfileIconDraw = getResources().getDrawable(R.drawable.ic_user_profile);
        passwordIconDraw = getResources().getDrawable(R.drawable.ic_password);
        countryIconDraw = getResources().getDrawable(R.drawable.ic_country);
        phoneIconDraw = getResources().getDrawable(R.drawable.ic_phone);

        emailGoodIconDraw = getResources().getDrawable(R.drawable.ic_email_good);
        userGoodProfileDraw = getResources().getDrawable(R.drawable.ic_profile_good);
        passwordGoodIconDraw = getResources().getDrawable(R.drawable.ic_password_good);
        countryGoodIconDraw = getResources().getDrawable(R.drawable.ic_country_good);
        phoneGoodIconDraw = getResources().getDrawable(R.drawable.ic_phone_good);

        emailBadIconDraw = getResources().getDrawable(R.drawable.ic_email_bad);
        passwordBadIconDraw = getResources().getDrawable(R.drawable.ic_password_bad);

        // Get a reference to the AutoCompleteTextView in the layout
        final AutoCompleteTextView textViewCountry = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.countries_array);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        textViewCountry.setAdapter(adapter);


        emailField.setText("");
        passwordConfirmField.setText("");
        passwordField.setText("");
        fnameField.setText("");
        lnameField.setText("");
        phoneField.setText("");
        textViewCountry.setText("");
        emailField.setTypeface(regularFont);
        textViewCountry.setTypeface(regularFont);
        passwordField.setTypeface(regularFont);
        passwordConfirmField.setTypeface(regularFont);
        fnameField.setTypeface(regularFont);
        lnameField.setTypeface(regularFont);
        phoneField.setTypeface(regularFont);
        registerButton.setTypeface(regularFont);
        textViewHeaderReg.setTypeface(regularFont);
        txtLabelCountryReg.setTypeface(regularFont);
        txtLabelEmailReg.setTypeface(regularFont);
        txtLabelFnameReg.setTypeface(regularFont);
        txtLabeLnameReg.setTypeface(regularFont);
        txtLabelPasswordConfirmReg.setTypeface(regularFont);
        txtLabelPhone.setTypeface(regularFont);
        txtLabelTextPasswordReg.setTypeface(regularFont);

        Backendless.Persistence.mapTableToClass("Person", Person.class);

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                CharSequence email = emailField.getText(); //TODO regex tester, might be it on server, handle fault with code

                final CharSequence fname = fnameField.getText();
                final CharSequence lname = lnameField.getText();
                final CharSequence phone = phoneField.getText();
                CharSequence county = textViewCountry.getText();
                CharSequence password = passwordField.getText();
                CharSequence passwordConfirm = passwordConfirmField.getText();


                if (!(fname.toString().trim().equals("") && lname.toString().equals("")
                        && phone.toString().equals("") && county.toString().equals("")
                        && password.toString().equals("") &&
                        passwordConfirm.toString().equals("")
                        && email.toString().equals(""))) {
                    {

                        //TODO Create Person instance with name and additional info

                        if (password.toString().equals(passwordConfirm.toString())) {

                            if (validator.isPasswordValid(passwordConfirm)) {

                                if (validator.isValidEmail(email)) {

                                    BackendlessUser user = new BackendlessUser();

                                    user.setEmail(email.toString());
                                    user.setPassword(password.toString());

                                    //TODO SEND ME TEXT; check that Cell phone is correct


                                    Person person = new Person();
                                    person.setFname(fname.toString());
                                    person.setLname(lname.toString());
                                    person.setEmail(email.toString());
                                    person.setPhone(phone.toString());
                                    person.setCountry(county.toString());
                                    person.setFullname(fname.toString() + " " + lname.toString()); // TODO Make textbox for full name

                                    user.setProperty("persons", person);

                                    Backendless.UserService.register(user, new BackendlessCallback<BackendlessUser>() {
                                        @Override
                                        public void handleResponse(BackendlessUser backendlessUser) {

                                            //TODO Threading when users registers, show spinner.

                                            Log.i("Registration", backendlessUser.getEmail() + " successfully registered");

                                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                            intent.putExtra("nameRegistered", fname + "," + lname);

                                            startActivity(intent);
                                        }

//                        public void handleFault(BackendlessFault fault) {
//                            // an error has occurred, the error code can be retrieved with fault.getCode() // TODO: use getCode and provide appriate user feedback http://backendless.com/documentation/users/android/users_user_registration.htm
//                            Toast.makeText(getApplicationContext(), "Please re-enter valid details" + fault.getCode(), Toast.LENGTH_LONG).show();
//                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please enter your email address in the format someone@example.com", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Password must contain at least 5 or more characters", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Passwords did not match, please check passwords match", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Please input all fields to complete sign up", Toast.LENGTH_LONG).show();
                }
            }
        });

        fnameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(fnameField.getText().toString().equals("")))) {
                    fnameField.setCompoundDrawablesWithIntrinsicBounds(userGoodProfileDraw, null, tickIconDraw, null);


                } else {
                    fnameField.setCompoundDrawablesWithIntrinsicBounds(userProfileIconDraw, null, null, null);
                }
            }
        });
        lnameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(lnameField.getText().toString().equals("")))) {
                    lnameField.setCompoundDrawablesWithIntrinsicBounds(userGoodProfileDraw, null, tickIconDraw, null);
                } else {
                    lnameField.setCompoundDrawablesWithIntrinsicBounds(userProfileIconDraw, null, null, null);
                }
            }
        });
        emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(emailField.getText().toString().equals("")))) {
                    if (validator.isValidEmail(emailField.getText().toString())) {
                        emailField.setCompoundDrawablesWithIntrinsicBounds(emailGoodIconDraw, null, tickIconDraw, null);
                    } else {
                        emailField.setCompoundDrawablesWithIntrinsicBounds(emailBadIconDraw, null, crossIconDraw, null);
                        Toast.makeText(getApplicationContext(), "Please enter your email address in the format someone@example.com", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    emailField.setCompoundDrawablesWithIntrinsicBounds(emailIconDraw, null, null, null);
                }
            }
        });
        phoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(phoneField.getText().toString().equals("")))) {
                    phoneField.setCompoundDrawablesWithIntrinsicBounds(phoneGoodIconDraw, null, tickIconDraw, null);
                } else {
                    phoneField.setCompoundDrawablesWithIntrinsicBounds(phoneIconDraw, null, null, null);
                }
            }
        });
        passwordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(passwordField.getText().toString().equals("")))) {
                    if (validator.isPasswordValid(passwordField.getText().toString())) {
                        if (passwordConfirmField.getText().toString().equals("")) {
                            passwordField.setCompoundDrawablesWithIntrinsicBounds(passwordGoodIconDraw, null, tickIconDraw, null);
                        } else {
                            if (passwordField.getText().toString().equals(passwordConfirmField.getText().toString())) {
                                passwordField.setCompoundDrawablesWithIntrinsicBounds(passwordGoodIconDraw, null, tickIconDraw, null);
                            } else {
                                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                                passwordField.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                            }
                        }
                    } else {
                        passwordField.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                        Toast.makeText(getApplicationContext(), "Password must contain at least 5 or more characters", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    passwordField.setCompoundDrawablesWithIntrinsicBounds(passwordIconDraw, null, null, null);
                }
            }
        });
        passwordConfirmField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(passwordConfirmField.getText().toString().equals("")))) {
                    if (validator.isPasswordValid(passwordConfirmField.getText().toString())) {
                        if (passwordField.getText().toString().equals("")) {
                            passwordConfirmField.setCompoundDrawablesWithIntrinsicBounds(passwordGoodIconDraw, null, tickIconDraw, null);
                        } else {
                            if (passwordConfirmField.getText().toString().equals(passwordField.getText().toString())) {
                                passwordConfirmField.setCompoundDrawablesWithIntrinsicBounds(passwordGoodIconDraw, null, tickIconDraw, null);
                            } else {
                                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                                passwordConfirmField.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password must contain at least 5 or more characters", Toast.LENGTH_SHORT).show();
                        passwordConfirmField.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                    }
                } else {
                    passwordConfirmField.setCompoundDrawablesWithIntrinsicBounds(passwordIconDraw, null, null, null);
                }
            }
        });
        textViewCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(textViewCountry.getText().toString().equals("")))) {
                    textViewCountry.setCompoundDrawablesWithIntrinsicBounds(countryGoodIconDraw, null, tickIconDraw, null);
                } else {
                    textViewCountry.setCompoundDrawablesWithIntrinsicBounds(countryIconDraw, null, null, null);
                }
            }
        });
    }

    /**
     * Created by Leonard on 17/04/2015.
     */
    public class Validator {


        private boolean isPasswordValid(CharSequence password) {
            return password.toString().length() > 4;
        }

        private boolean isValidEmail(CharSequence target) {
            if (target == null)
                return false;
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}

