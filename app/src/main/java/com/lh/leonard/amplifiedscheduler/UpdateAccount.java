package com.lh.leonard.amplifiedscheduler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class UpdateAccount extends AppCompatActivity {

    ProgressDialog ringProgressDialog;
    View v;
    Person personLoggedIn;
    EditText editTextUpdateFNameReg; // TODO set hints to user info
    EditText editTextUpdateLNameReg;
    EditText editTextUpdatePhoneReg;
    EditText editTextUpdateEmail;
    EditText ediTextUpdatePassword;
    EditText editTextUpdatePasswordConfirmReg;
    BackendlessUser user;

    Drawable tickIconDraw;
    Drawable crossIconDraw;
    Drawable emailIconDraw;
    Drawable userProfileIconDraw;
    Drawable passwordIconDraw;
    Drawable countryIconDraw;
    Drawable phoneIconDraw;
    String my_var;
    Drawable emailGoodIconDraw;
    Drawable userGoodProfileDraw;
    Drawable passwordGoodIconDraw;
    Drawable countryGoodIconDraw;
    Drawable phoneGoodIconDraw;
    private Menu optionsMenu;
    Drawable passwordBadIconDraw;
    Drawable emailBadIconDraw;
    private Toolbar toolbar;
    String fname;
    String lname;
    String phone;
    String email;
    String password;
    String passwordConfirm;
    String country;
    Validator validator = new Validator();
    AutoCompleteTextView textViewCountry;
    ArrayAdapter<String> adapter;
    Boolean social = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        tickIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_tick);
        crossIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_cross);
        emailIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_email);
        userProfileIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_user_profile);
        passwordIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_password);
        countryIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_country);
        phoneIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_phone);

        emailGoodIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_email_good);
        userGoodProfileDraw = ContextCompat.getDrawable(this, R.drawable.ic_profile_good);
        passwordGoodIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_password_good);
        countryGoodIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_country_good);
        phoneGoodIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_phone_good);

        emailBadIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_email_bad);
        passwordBadIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_password_bad);

        Backendless.Data.mapTableToClass("Person", Person.class);

        user = Backendless.UserService.CurrentUser();
        personLoggedIn = (Person) user.getProperty("persons");


        // Get a reference to the AutoCompleteTextView in the layout
        textViewCountry = (AutoCompleteTextView) findViewById(R.id.autocomplete_countryUpdate);
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.countries_array);
        // Create the adapter and set it to the AutoCompleteTextView
        adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);
        textViewCountry.setAdapter(adapter);

        editTextUpdateFNameReg = (EditText) findViewById(R.id.editTextUpdateFName);
        editTextUpdateLNameReg = (EditText) findViewById(R.id.editTextUpdateLName);
        editTextUpdatePhoneReg = (EditText) findViewById(R.id.editTextUpdatePhone);
        editTextUpdateEmail = (EditText) findViewById(R.id.editTextUpdateEmail);
        ediTextUpdatePassword = (EditText) findViewById(R.id.ediTextUpdatePassword);
        editTextUpdatePasswordConfirmReg = (EditText) findViewById(R.id.editTextUpdatePasswordConfirm);

        if (user.getProperty("socialAccount") == null) {
            editTextUpdateLNameReg.setEnabled(false);
            editTextUpdateLNameReg.setInputType(InputType.TYPE_NULL);
            editTextUpdateFNameReg.setEnabled(false);
            editTextUpdateFNameReg.setInputType(InputType.TYPE_NULL);
            editTextUpdatePhoneReg.setEnabled(false);
            editTextUpdatePhoneReg.setInputType(InputType.TYPE_NULL);
            editTextUpdateEmail.setEnabled(false);
            editTextUpdateEmail.setInputType(InputType.TYPE_NULL);
            ediTextUpdatePassword.setEnabled(false);
            ediTextUpdatePassword.setInputType(InputType.TYPE_NULL);
            editTextUpdatePasswordConfirmReg.setEnabled(false);
            editTextUpdatePasswordConfirmReg.setInputType(InputType.TYPE_NULL);
            social = true;
        }

        final AutoResizeTextView txtLabelFnameUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelFnameUpdate);
        final AutoResizeTextView txtLabeLnameUpdate = (AutoResizeTextView) findViewById(R.id.txtLabeLnameUpdate);
        final AutoResizeTextView txtLabelTextPasswordUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelTextPasswordUpdate);
        final AutoResizeTextView txtLabelPasswordConfirmUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelPasswordConfirmUpdate);
        final AutoResizeTextView txtLabelEmailUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelEmailUpdate);
        final AutoResizeTextView txtLabelCountryUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelUpdateCountry);
        final AutoResizeTextView txtLabelPhoneUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelPhoneUpdate);
        AutoResizeTextView editTextNoticeUpdate = (AutoResizeTextView) findViewById(R.id.editTextNoticeUpdate);

        final Typeface RobotoBlack = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        Button updateDetailsBtn = (Button) findViewById(R.id.buttonUpdateUser);

        editTextNoticeUpdate.setTypeface(RobotoCondensedLightItalic);
        editTextUpdateFNameReg.setTypeface(RobotoCondensedLight);
        editTextUpdateLNameReg.setTypeface(RobotoCondensedLight);
        editTextUpdatePhoneReg.setTypeface(RobotoCondensedLight);
        editTextUpdateEmail.setTypeface(RobotoCondensedLight);
        ediTextUpdatePassword.setTypeface(RobotoCondensedLight);
        editTextUpdatePasswordConfirmReg.setTypeface(RobotoCondensedLight);
        updateDetailsBtn.setTypeface(RobotoCondensedLight);
        txtLabelCountryUpdate.setTypeface(RobotoCondensedLight);
        txtLabelEmailUpdate.setTypeface(RobotoCondensedLight);
        txtLabelFnameUpdate.setTypeface(RobotoCondensedLight);
        txtLabeLnameUpdate.setTypeface(RobotoCondensedLight);
        txtLabelPasswordConfirmUpdate.setTypeface(RobotoCondensedLight);
        txtLabelPhoneUpdate.setTypeface(RobotoCondensedLight);
        txtLabelTextPasswordUpdate.setTypeface(RobotoCondensedLight);
        textViewCountry.setTypeface(RobotoCondensedLight);

        updateDetailsBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ringProgressDialog = ProgressDialog.show(UpdateAccount.this, "Please wait ...", "Updating account ...", true);
                                                    ringProgressDialog.setCancelable(false);
                                                    email = editTextUpdateEmail.getText().toString();
                                                    fname = editTextUpdateFNameReg.getText().toString();
                                                    lname = editTextUpdateLNameReg.getText().toString();
                                                    phone = editTextUpdatePhoneReg.getText().toString();
                                                    password = ediTextUpdatePassword.getText().toString();
                                                    passwordConfirm = editTextUpdatePasswordConfirmReg.getText().toString();
                                                    country = textViewCountry.getText().toString();
                                                    new Parse().execute();
                                                }
                                            }
        );


        editTextUpdateFNameReg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(editTextUpdateFNameReg.getText().toString().equals("")))) {
                    editTextUpdateFNameReg.setCompoundDrawablesWithIntrinsicBounds(userGoodProfileDraw, null, tickIconDraw, null);
                } else {
                    editTextUpdateFNameReg.setCompoundDrawablesWithIntrinsicBounds(userProfileIconDraw, null, null, null);
                }
            }
        });
        editTextUpdateLNameReg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(editTextUpdateLNameReg.getText().toString().equals("")))) {
                    editTextUpdateLNameReg.setCompoundDrawablesWithIntrinsicBounds(userGoodProfileDraw, null, tickIconDraw, null);
                } else {
                    editTextUpdateLNameReg.setCompoundDrawablesWithIntrinsicBounds(userProfileIconDraw, null, null, null);
                }
            }
        });
        editTextUpdateEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(editTextUpdateEmail.getText().toString().equals("")))) {
                    if (validator.isValidEmail(editTextUpdateEmail.getText().toString())) {
                        editTextUpdateEmail.setCompoundDrawablesWithIntrinsicBounds(emailGoodIconDraw, null, tickIconDraw, null);
                    } else {
                        editTextUpdateEmail.setCompoundDrawablesWithIntrinsicBounds(emailBadIconDraw, null, crossIconDraw, null);
                        Toast.makeText(getApplicationContext(), "Please enter your email address in the format someone@example.com", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    editTextUpdateEmail.setCompoundDrawablesWithIntrinsicBounds(emailIconDraw, null, null, null);
                }
            }
        });
        editTextUpdatePhoneReg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(editTextUpdatePhoneReg.getText().toString().equals("")))) {
                    editTextUpdatePhoneReg.setCompoundDrawablesWithIntrinsicBounds(phoneGoodIconDraw, null, tickIconDraw, null);
                } else {
                    editTextUpdatePhoneReg.setCompoundDrawablesWithIntrinsicBounds(phoneIconDraw, null, null, null);
                }
            }
        });
        ediTextUpdatePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(ediTextUpdatePassword.getText().toString().equals("")))) {
                    if (validator.isPasswordValid(ediTextUpdatePassword.getText().toString())) {
                        if (editTextUpdatePasswordConfirmReg.getText().toString().equals("")) {
                            ediTextUpdatePassword.setCompoundDrawablesWithIntrinsicBounds(passwordGoodIconDraw, null, tickIconDraw, null);
                        } else {
                            if (ediTextUpdatePassword.getText().toString().equals(editTextUpdatePasswordConfirmReg.getText().toString())) {
                                ediTextUpdatePassword.setCompoundDrawablesWithIntrinsicBounds(passwordGoodIconDraw, null, tickIconDraw, null);
                            } else {
                                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                                ediTextUpdatePassword.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                            }
                        }
                    } else {
                        ediTextUpdatePassword.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                        Toast.makeText(getApplicationContext(), "Password must contain at least 5 or more characters", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ediTextUpdatePassword.setCompoundDrawablesWithIntrinsicBounds(passwordIconDraw, null, null, null);
                }
            }
        });
        editTextUpdatePasswordConfirmReg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!(editTextUpdatePasswordConfirmReg.getText().toString().equals("")))) {
                    if (validator.isPasswordValid(editTextUpdatePasswordConfirmReg.getText().toString())) {
                        if (ediTextUpdatePassword.getText().toString().equals("")) {
                            editTextUpdatePasswordConfirmReg.setCompoundDrawablesWithIntrinsicBounds(passwordGoodIconDraw, null, tickIconDraw, null);
                        } else {
                            if (editTextUpdatePasswordConfirmReg.getText().toString().equals(ediTextUpdatePassword.getText().toString())) {
                                editTextUpdatePasswordConfirmReg.setCompoundDrawablesWithIntrinsicBounds(passwordGoodIconDraw, null, tickIconDraw, null);
                            } else {
                                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                                editTextUpdatePasswordConfirmReg.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password must contain at least 5 or more characters", Toast.LENGTH_SHORT).show();
                        editTextUpdatePasswordConfirmReg.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                    }
                } else {
                    editTextUpdatePasswordConfirmReg.setCompoundDrawablesWithIntrinsicBounds(passwordIconDraw, null, null, null);
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
        textViewCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                my_var = adapter.getItem(position).toString();
                textViewCountry.setCompoundDrawablesWithIntrinsicBounds(countryGoodIconDraw, null, tickIconDraw, null);

            }
        });
        textViewCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                my_var = null;
                textViewCountry.setCompoundDrawablesWithIntrinsicBounds(countryIconDraw, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private class Parse extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean firstNameChange = false;
            Boolean lastNameChange = false;
            Boolean updatePerson = false;

            if (validator.isValidEmail(email)) {
                personLoggedIn.setEmail(email);
                user.setEmail(email);
                updatePerson = true;
            }
            if (!(fname.equals(""))) {
                personLoggedIn.setFname(fname);
                firstNameChange = true;
                updatePerson = true;
            }
            if (!(lname.equals(""))) {
                personLoggedIn.setLname(lname);
                lastNameChange = true;
                updatePerson = true;
            }
            if (!(phone.equals(""))) {
                personLoggedIn.setPhone(phone.toString());
                updatePerson = true;
            }
            if (validator.isPasswordValid(passwordConfirm) && validator.isPasswordValid(password)) {

                if (password.equals(passwordConfirm)) {
                    user.setPassword(password);
                    updatePerson = true;
                }
            }
            if (my_var != null) {
                personLoggedIn.setCountry(country);
            }

            if (firstNameChange && lastNameChange) {
                personLoggedIn.setFullname(fname + " " + lname);
            } else {
                if (!fname.equals("")) {
                    personLoggedIn.setFullname(fname + " " + personLoggedIn.getLname());
                } else if (!lname.equals("")) {
                    personLoggedIn.setFullname(personLoggedIn.getFname() + " " + lname);
                }
            }
            if (updatePerson) {
                Backendless.UserService.update(user);
                Backendless.Data.of(Person.class).save(personLoggedIn);
            }
            return updatePerson;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                if (social != true) {
                    editTextUpdateEmail.setText("");
                    editTextUpdateFNameReg.setText("");
                    editTextUpdateLNameReg.setText("");
                    ediTextUpdatePassword.setText("");
                    editTextUpdatePasswordConfirmReg.setText("");
                    editTextUpdateEmail.setCompoundDrawablesWithIntrinsicBounds(emailIconDraw, null, null, null);
                    ediTextUpdatePassword.setCompoundDrawablesWithIntrinsicBounds(passwordIconDraw, null, null, null);
                    editTextUpdateFNameReg.setCompoundDrawablesWithIntrinsicBounds(userProfileIconDraw, null, null, null);
                    editTextUpdatePasswordConfirmReg.setCompoundDrawablesWithIntrinsicBounds(passwordIconDraw, null, null, null);
                    editTextUpdateLNameReg.setCompoundDrawablesWithIntrinsicBounds(userProfileIconDraw, null, null, null);
                }

                editTextUpdatePhoneReg.setText("");
                textViewCountry.setText("");
                my_var = null;
                editTextUpdatePhoneReg.setCompoundDrawablesWithIntrinsicBounds(phoneIconDraw, null, null, null);
                textViewCountry.setCompoundDrawablesWithIntrinsicBounds(countryIconDraw, null, null, null);
            }

            ringProgressDialog.dismiss();

            if (result) {
                Toast.makeText(getApplicationContext(), "Account updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Cannot update account", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NavDrawerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_other, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share);

        // Fetch and store ShareActionProvider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this free event making app: https://play.google.com/store/apps/details?id=com.lh.leonard.amplifiedscheduler");
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);
        return super.onCreateOptionsMenu(menu);
    }
}