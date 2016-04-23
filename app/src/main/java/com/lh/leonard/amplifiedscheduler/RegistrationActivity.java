package com.lh.leonard.amplifiedscheduler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity {

    File f;
    String my_var;
    Drawable tickIconDraw;
    Drawable crossIconDraw;
    ProgressDialog ringProgressDialog;
    Drawable emailIconDraw;
    Drawable userProfileIconDraw;
    Drawable passwordIconDraw;
    Drawable countryIconDraw;
    Bitmap bitmap = null;
    Drawable emailGoodIconDraw;
    Drawable userGoodProfileDraw;
    Drawable passwordGoodIconDraw;
    Drawable countryGoodIconDraw;
    private Toolbar toolbar;
    Drawable passwordBadIconDraw;
    Drawable emailBadIconDraw;
    ArrayAdapter<String> adapter;
    Validator validator = new Validator();
    String EMAILBAD = "";
    Button btnRegPicture;
    AutoResizeTextView textViewPictureLocalDir;
    CharSequence fname;
    CharSequence lname;
    Drawable userBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

        final Typeface RobotoBlack = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        final Button registerButton = (Button) findViewById(R.id.buttonRegisterUser);

        final EditText emailField = (EditText) findViewById(R.id.editTextEmail);
        final EditText passwordField = (EditText) findViewById(R.id.editTextPassword);
        final EditText passwordConfirmField = (EditText) findViewById(R.id.editTextPasswordConfirmReg);
        final EditText fnameField = (EditText) findViewById(R.id.editTextFNameReg);
        final EditText lnameField = (EditText) findViewById(R.id.editTextLNameReg);

        final AutoResizeTextView txtLabelFnameReg = (AutoResizeTextView) findViewById(R.id.txtLabelFnameReg);
        final AutoResizeTextView txtLabeLnameReg = (AutoResizeTextView) findViewById(R.id.txtLabeLnameReg);
        final AutoResizeTextView txtLabelTextPasswordReg = (AutoResizeTextView) findViewById(R.id.txtLabelTextPasswordReg);
        final AutoResizeTextView txtLabelPasswordConfirmReg = (AutoResizeTextView) findViewById(R.id.txtLabelPasswordConfirmReg);
        final AutoResizeTextView txtLabelEmailReg = (AutoResizeTextView) findViewById(R.id.txtLabelEmailReg);
        final AutoResizeTextView txtLabelCountryReg = (AutoResizeTextView) findViewById(R.id.txtLabelCountryReg);
        final AutoResizeTextView txtLabelGender = (AutoResizeTextView) findViewById(R.id.txtLabelGender);
        final RadioButton radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        final RadioButton radioButtonFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        btnRegPicture = (Button) findViewById(R.id.btnRegPicture);
        textViewPictureLocalDir = (AutoResizeTextView) findViewById(R.id.textViewPictureLocalDir);

        tickIconDraw = getResources().getDrawable(R.drawable.ic_tick);
        crossIconDraw = getResources().getDrawable(R.drawable.ic_cross);
        emailIconDraw = getResources().getDrawable(R.drawable.ic_email);
        userProfileIconDraw = getResources().getDrawable(R.drawable.ic_user_profile);
        passwordIconDraw = getResources().getDrawable(R.drawable.ic_password);
        countryIconDraw = getResources().getDrawable(R.drawable.ic_country);

        emailGoodIconDraw = getResources().getDrawable(R.drawable.ic_email_good);
        userBlank = getResources().getDrawable(R.drawable.user_blank_new);
        userGoodProfileDraw = getResources().getDrawable(R.drawable.ic_profile_good);
        passwordGoodIconDraw = getResources().getDrawable(R.drawable.ic_password_good);
        countryGoodIconDraw = getResources().getDrawable(R.drawable.ic_country_good);

        emailBadIconDraw = getResources().getDrawable(R.drawable.ic_email_bad);
        passwordBadIconDraw = getResources().getDrawable(R.drawable.ic_password_bad);

        // Get a reference to the AutoCompleteTextView in the layout
        final AutoCompleteTextView textViewCountry = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.countries_array);
        // Create the adapter and set it to the AutoCompleteTextView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);
        textViewCountry.setAdapter(adapter);

        radioButtonMale.setChecked(false);
        radioButtonFemale.setChecked(false);
        emailField.setText("");
        passwordConfirmField.setText("");
        passwordField.setText("");
        fnameField.setText("");
        lnameField.setText("");
        textViewCountry.setText("");

        radioButtonMale.setTypeface(RobotoCondensedLight);
        radioButtonFemale.setTypeface(RobotoCondensedLight);
        emailField.setTypeface(RobotoCondensedLight);
        textViewCountry.setTypeface(RobotoCondensedLight);
        passwordField.setTypeface(RobotoCondensedLight);
        passwordConfirmField.setTypeface(RobotoCondensedLight);
        fnameField.setTypeface(RobotoCondensedLight);
        lnameField.setTypeface(RobotoCondensedLight);
        registerButton.setTypeface(RobotoCondensedLight);
        txtLabelCountryReg.setTypeface(RobotoCondensedLight);
        txtLabelEmailReg.setTypeface(RobotoCondensedLight);
        txtLabelFnameReg.setTypeface(RobotoCondensedLight);
        txtLabeLnameReg.setTypeface(RobotoCondensedLight);
        txtLabelPasswordConfirmReg.setTypeface(RobotoCondensedLight);
        txtLabelTextPasswordReg.setTypeface(RobotoCondensedLight);
        txtLabelGender.setTypeface(RobotoCondensedLight);
        btnRegPicture.setTypeface(RobotoCondensedLight);
        textViewPictureLocalDir.setTypeface(RobotoCondensedLight);

        Backendless.Persistence.mapTableToClass("Person", Person.class);

        final Intent intentFileDialog = new Intent(this, FilePickerActivity.class);

        btnRegPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentFileDialog.putExtra(FilePickerActivity.ARG_FILE_FILTER, Pattern.compile(".*\\.jpg$"));
                intentFileDialog.putExtra(FilePickerActivity.ARG_DIRECTORIES_FILTER, false);
                intentFileDialog.putExtra(FilePickerActivity.ARG_SHOW_HIDDEN, true);
                startActivityForResult(intentFileDialog, 1);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final CharSequence email = emailField.getText(); //TODO regex tester, might be it on server, handle fault with code
                fname = fnameField.getText();
                lname = lnameField.getText();

                CharSequence county = textViewCountry.getText();
                CharSequence password = passwordField.getText();
                CharSequence passwordConfirm = passwordConfirmField.getText();
                Boolean genderChecked = false;
                if (radioButtonFemale.isChecked() || radioButtonMale.isChecked()) {
                    genderChecked = true;
                }

                if (!(fname.toString().trim().equals("") && lname.toString().equals("")
                        && county.toString().equals("")
                        && password.toString().equals("") &&
                        passwordConfirm.toString().equals("")
                        && email.toString().equals("")) && my_var != null && genderChecked != false) {

                    if (password.toString().equals(passwordConfirm.toString())) {

                        if (validator.isPasswordValid(passwordConfirm)) {

                            if (validator.isValidEmail(email)) {

                                ringProgressDialog = ProgressDialog.show(RegistrationActivity.this, "Please wait ...", "Creating Account ...", true);
                                ringProgressDialog.setCancelable(false);

                                BackendlessUser user = new BackendlessUser();
                                user.setEmail(email.toString());
                                user.setPassword(password.toString());

                                Person person = new Person();
                                person.setFname(fname.toString());
                                person.setLname(lname.toString());
                                person.setGender((radioButtonMale.isChecked() ? "Male" : "Female"));
                                person.setEmail(email.toString());
                                person.setCountry(county.toString());
                                person.setFullname(fname.toString() + " " + lname.toString());

                                user.setProperty("persons", person);

                                Backendless.UserService.register(user, new BackendlessCallback<BackendlessUser>() {
                                    @Override
                                    public void handleResponse(final BackendlessUser backendlessUser) {

                                        //Store the image with the users object id
                                        Backendless.Files.Android.upload((bitmap != null) ? bitmap : ((BitmapDrawable) userBlank).getBitmap(), Bitmap.CompressFormat.PNG, 50, backendlessUser.getObjectId(), "pictures",
                                                new AsyncCallback<BackendlessFile>() {
                                                    @Override
                                                    public void handleResponse(final BackendlessFile backendlessFile) {

                                                        new ProfilePic(backendlessFile, backendlessUser).execute();
                                                    }

                                                    @Override
                                                    public void handleFault(BackendlessFault backendlessFault) {
                                                        Toast.makeText(RegistrationActivity.this, backendlessFault.toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    public void handleFault(BackendlessFault fault) {
                                        ringProgressDialog.dismiss();
                                        int errorCode = Integer.valueOf(fault.getCode());
                                        if (errorCode == 3033) {
                                            EMAILBAD = email.toString();
                                            Toast.makeText(getApplicationContext(), "Error: User with the same email address already exists", Toast.LENGTH_LONG).show();
                                            emailField.setCompoundDrawablesWithIntrinsicBounds(emailBadIconDraw, null, crossIconDraw, null);
                                        } else if (errorCode == 3009) {
                                            Toast.makeText(getApplicationContext(), "Error: User registration is currently disabled by admin, please try again soon", Toast.LENGTH_LONG).show();
                                        } else if (errorCode == 3013) {
                                            Toast.makeText(getApplicationContext(), "Error: Missing email property", Toast.LENGTH_LONG).show();
                                        } else if (errorCode == 3014) {
                                            Toast.makeText(getApplicationContext(), "Error: External registration failed with an error", Toast.LENGTH_LONG).show();
                                        } else if (errorCode == 3039 || errorCode == 3041 || errorCode == 3040 || errorCode == 3043
                                                || errorCode == 8000 || errorCode == 3012 || errorCode == 3011 || errorCode == 3021) {
                                            Toast.makeText(getApplicationContext(), "Error: " + fault.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
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

                } else {
                    Toast.makeText(getApplicationContext(), "Please input all fields to complete sign up", Toast.LENGTH_LONG).show();
                }
            }
        });
        radioButtonFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonMale.setChecked(false);
            }
        });
        radioButtonMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonFemale.setChecked(false);
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
                    if (!emailField.getText().toString().equals(EMAILBAD)) {
                        if (validator.isValidEmail(emailField.getText().toString())) {
                            emailField.setCompoundDrawablesWithIntrinsicBounds(emailGoodIconDraw, null, tickIconDraw, null);
                        } else {
                            emailField.setCompoundDrawablesWithIntrinsicBounds(emailBadIconDraw, null, crossIconDraw, null);
                            Toast.makeText(getApplicationContext(), "Please enter your email address in the format someone@example.com",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        emailField.setCompoundDrawablesWithIntrinsicBounds(emailBadIconDraw, null, crossIconDraw, null);
                        Toast.makeText(getApplicationContext(), "User with the same email address already exists",
                                Toast.LENGTH_LONG).show();

                    }
                } else {
                    emailField.setCompoundDrawablesWithIntrinsicBounds(emailIconDraw, null, null, null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  image.setImageBitmap(bitmap);
//        Bitmap bitmap = (Bitmap) Bitmap.
//                File file = new File("test.txt");
//        String filePath = file.getAbsolutePath();
//        Bitmap photo = (Bitmap) getIntent().getExtras().get("data");


        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            f = new File(filePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                textViewPictureLocalDir.setText(filePath);
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class ProfilePic extends AsyncTask<Void, Integer, Void> {

        BackendlessFile localBackendlessFile;
        BackendlessUser localBackendlessUser;

        ProfilePic(BackendlessFile backendlessFile, BackendlessUser backendlessUser) {
            this.localBackendlessFile = backendlessFile;
            this.localBackendlessUser = backendlessUser;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Person p = (Person) localBackendlessUser.getProperty("persons");
            p.setOwnerId(localBackendlessUser.getObjectId());
            p.setPicture(localBackendlessFile.getFileURL());
            Backendless.Data.of(Person.class).save(p);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            intent.putExtra("nameRegistered", fname + "," + lname);
            startActivity(intent);
        }
    }
}

