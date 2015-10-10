package com.lh.leonard.eventhub101;

import android.content.Intent;
import android.graphics.Typeface;
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
        TextView textViewHeaderReg = (TextView) findViewById(R.id.textViewHeaderReg);

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

                BackendlessUser user = new BackendlessUser();

                //TODO Create Person instance with name and additional info

                if (password.toString().equals(passwordConfirm.toString())) {

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
                    // person.setPhone(Integer.parseInt(phone.toString()));
                    // person.setMeAsContact(contact); // TODO Might not need this property

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
                    Toast.makeText(getApplicationContext(), "Passwords did not match, please check passwords match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//
//    public boolean isRegistrationValuesValid(CharSequence name, CharSequence email, CharSequence password,
//                                             CharSequence passwordConfirm) {
//        Validator v = new Validator();
//        return v.isValidEmail(email) &&
//                v.isPasswordValid(password) && isPasswordsMatch(password, passwordConfirm);
//
//
//    }

//    public boolean isPasswordsMatch(CharSequence password, CharSequence passwordConfirm) {
//        if (!TextUtils.equals(password, passwordConfirm)) {
//            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
//            return false;
//
//        }
//        return true;
//
//    }

//    public void registerUser(String name, String email, String password,
//                             AsyncCallback<BackendlessUser> registrationCallback) {
//        BackendlessUser user = new BackendlessUser();
//        user.setEmail(email);
//        user.setPassword(password);
//        user.setProperty("name", name);
//
//        //handles hashing by itself
//        Backendless.UserService.register(user, registrationCallback);
//    }

//    public LoadingCallback<BackendlessUser> createRegistrationCallback() {
//        return new LoadingCallback<BackendlessUser>(this, "Sending registration request...") {
//            @Override
//            public void handleResponse(BackendlessUser registerUser) {
//                handleResponse(registerUser);
//                Toast.makeText(RegistrationActivity.this, String.format("Register. ObjectId: \n",
//                        registerUser.getObjectId()), Toast.LENGTH_LONG).show(); // check this
//
//
//            }
//        };
//    }

//    public View.OnClickListener createRegisterButtonClickListener() {
//
//        return (v) -> {
//
//
//            if (isRegistrationValuesValid(name, email, password, passwordConfirm)) {
//
//                LoadingCallback<BackendlessUser> registrationCallback = createRegistrationCallback();
//
//                registrationCallback.showLoading();
//                registerUser(name.toString(), email.toString(), password.toString(), registrationCallback);
//
//            }
//        };
//    }

    /**
     * Created by Leonard on 17/04/2015.
     */
//    public class Validator {
//
//
//        private boolean isPasswordValid(CharSequence password) {
//            //TODO: Replace this with your own logic
//            return password.toString().length() > 4;
//        }
//
//        private boolean isValidEmail(CharSequence target) {
//            if (target == null)
//                return false;
//
//            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
//        }
//    }
}

