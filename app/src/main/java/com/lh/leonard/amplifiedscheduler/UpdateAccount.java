package com.lh.leonard.amplifiedscheduler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

public class UpdateAccount extends AppCompatActivity {

    Boolean updatePerson;
    ProgressDialog ringProgressDialog;
    View v;
    Person personLoggedIn;
    EditText editTextUpdateFNameReg; // TODO set hints to user info
    EditText editTextUpdateLNameReg;
    EditText editTextUpdateEmail;
    EditText ediTextUpdatePassword;
    EditText editTextUpdatePasswordConfirmReg;
    BackendlessUser user;
    File f;
    Bitmap bitmap = null;
    Drawable tickIconDraw;
    Drawable crossIconDraw;
    Drawable emailIconDraw;
    Drawable userProfileIconDraw;
    Drawable passwordIconDraw;
    Drawable countryIconDraw;
    String my_var;
    Drawable emailGoodIconDraw;
    Drawable userGoodProfileDraw;
    Drawable passwordGoodIconDraw;
    Drawable countryGoodIconDraw;
    private Menu optionsMenu;
    Drawable passwordBadIconDraw;
    Drawable emailBadIconDraw;
    private Toolbar toolbar;
    String fname;
    String lname;
    String email;
    String password;
    String passwordConfirm;
    String country;
    Validator validator = new Validator();
    AutoCompleteTextView textViewCountry;
    ArrayAdapter<String> adapter;
    Boolean social = false;
    Button btnUpdateImage;
    AutoResizeTextView imagePathDirectory;

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
        emailGoodIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_email_good);
        userGoodProfileDraw = ContextCompat.getDrawable(this, R.drawable.ic_profile_good);
        passwordGoodIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_password_good);
        countryGoodIconDraw = ContextCompat.getDrawable(this, R.drawable.ic_country_good);

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
        editTextUpdateEmail = (EditText) findViewById(R.id.editTextUpdateEmail);
        ediTextUpdatePassword = (EditText) findViewById(R.id.ediTextUpdatePassword);
        editTextUpdatePasswordConfirmReg = (EditText) findViewById(R.id.editTextUpdatePasswordConfirm);
        imagePathDirectory = (AutoResizeTextView) findViewById(R.id.textViewPictureLocalDir);
        btnUpdateImage = (Button) findViewById(R.id.btnUpdatePicture);

        if (personLoggedIn.getSocial() != null) {
            if (personLoggedIn.getSocial().equals("Facebook")) {
                editTextUpdateLNameReg.setEnabled(false);
                editTextUpdateLNameReg.setInputType(InputType.TYPE_NULL);
                editTextUpdateFNameReg.setEnabled(false);
                editTextUpdateFNameReg.setInputType(InputType.TYPE_NULL);
                editTextUpdateEmail.setEnabled(false);
                editTextUpdateEmail.setInputType(InputType.TYPE_NULL);
                ediTextUpdatePassword.setEnabled(false);
                ediTextUpdatePassword.setInputType(InputType.TYPE_NULL);
                editTextUpdatePasswordConfirmReg.setEnabled(false);
                editTextUpdatePasswordConfirmReg.setInputType(InputType.TYPE_NULL);
                btnUpdateImage.setEnabled(false);
                imagePathDirectory.setText("Using Facebook profile image");
                editTextUpdateLNameReg.setHint("Using Facebook credential");
                editTextUpdateFNameReg.setHint("Using Facebook credential");
                editTextUpdateEmail.setHint("Using Facebook credential");
                ediTextUpdatePassword.setHint("Using Facebook credential");
                editTextUpdatePasswordConfirmReg.setHint("Using Facebook credential");
                social = true;
            }
        }

        final AutoResizeTextView txtLabelFnameUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelFnameUpdate);
        final AutoResizeTextView txtLabeLnameUpdate = (AutoResizeTextView) findViewById(R.id.txtLabeLnameUpdate);
        final AutoResizeTextView txtLabelTextPasswordUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelTextPasswordUpdate);
        final AutoResizeTextView txtLabelPasswordConfirmUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelPasswordConfirmUpdate);
        final AutoResizeTextView txtLabelEmailUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelEmailUpdate);
        final AutoResizeTextView txtLabelCountryUpdate = (AutoResizeTextView) findViewById(R.id.txtLabelUpdateCountry);
        AutoResizeTextView editTextNoticeUpdate = (AutoResizeTextView) findViewById(R.id.editTextNoticeUpdate);
        final Typeface RobotoBlack = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        Button updateDetailsBtn = (Button) findViewById(R.id.buttonUpdateUser);

        editTextNoticeUpdate.setTypeface(RobotoCondensedLightItalic);
        editTextUpdateFNameReg.setTypeface(RobotoCondensedLight);
        editTextUpdateLNameReg.setTypeface(RobotoCondensedLight);
        editTextUpdateEmail.setTypeface(RobotoCondensedLight);
        ediTextUpdatePassword.setTypeface(RobotoCondensedLight);
        editTextUpdatePasswordConfirmReg.setTypeface(RobotoCondensedLight);
        updateDetailsBtn.setTypeface(RobotoCondensedLight);
        txtLabelCountryUpdate.setTypeface(RobotoCondensedLight);
        txtLabelEmailUpdate.setTypeface(RobotoCondensedLight);
        txtLabelFnameUpdate.setTypeface(RobotoCondensedLight);
        txtLabeLnameUpdate.setTypeface(RobotoCondensedLight);
        txtLabelPasswordConfirmUpdate.setTypeface(RobotoCondensedLight);
        txtLabelTextPasswordUpdate.setTypeface(RobotoCondensedLight);
        textViewCountry.setTypeface(RobotoCondensedLight);
        btnUpdateImage.setTypeface(RobotoCondensedLight);

        if (personLoggedIn.getPicture() != null) {
            if (personLoggedIn.getPicture().equals("")) {
                imagePathDirectory.setText("No image set");
            }
        }
        final Intent intentFileDialog = new Intent(this, FilePickerActivity.class);

        updateDetailsBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ringProgressDialog = ProgressDialog.show(UpdateAccount.this, "Please wait ...", "Updating account ...", true);
                                                    ringProgressDialog.setCancelable(false);
                                                    if (!social) {
                                                        email = editTextUpdateEmail.getText().toString();
                                                        fname = editTextUpdateFNameReg.getText().toString();
                                                        lname = editTextUpdateLNameReg.getText().toString();
                                                        password = ediTextUpdatePassword.getText().toString();
                                                        passwordConfirm = editTextUpdatePasswordConfirmReg.getText().toString();
                                                    }
                                                    country = textViewCountry.getText().toString();
                                                    new Parse().execute();
                                                }
                                            }
        );

        if (!social) {
            btnUpdateImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentFileDialog.putExtra(FilePickerActivity.ARG_FILE_FILTER, Pattern.compile(".*\\.jpg$"));
                    intentFileDialog.putExtra(FilePickerActivity.ARG_DIRECTORIES_FILTER, false);
                    intentFileDialog.putExtra(FilePickerActivity.ARG_SHOW_HIDDEN, true);
                    startActivityForResult(intentFileDialog, 1);
                }
            });
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

        }
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
            updatePerson = false;

            // HashMap<String, Object> hashMapEvent = new HashMap<>();

            if (bitmap != null) {
                updatePerson = true;
            }

            if (!social) {
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

                if (validator.isPasswordValid(passwordConfirm) && validator.isPasswordValid(password)) {

                    if (password.equals(passwordConfirm)) {
                        user.setPassword(password);
                        updatePerson = true;
                    }
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
            }
            if (my_var != null) {
                personLoggedIn.setCountry(country);
                updatePerson = true;
            }

            if ((bitmap != null)) {
                //Store the image with the users object id
                try {
                    Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG, 50, personLoggedIn.getObjectId(), "pictures", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (updatePerson) {
                Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Backendless.Data.of(Person.class).save(personLoggedIn, new AsyncCallback<Person>() {
                            @Override
                            public void handleResponse(Person response) {
                                if (updatePerson) {
                                    if (!social) {

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


                                    textViewCountry.setText("");
                                    my_var = null;
                                    textViewCountry.setCompoundDrawablesWithIntrinsicBounds(countryIconDraw, null, null, null);
                                }

                                ringProgressDialog.dismiss();

                                if (updatePerson) {
                                    Toast.makeText(getApplicationContext(), "Account updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Cannot update account", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                ringProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        ringProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return updatePerson;
        }

        @Override
        protected void onPostExecute(Boolean result) {

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
                "Hey! Check out this free event/personal planner app: https://play.google.com/store/apps/details?id=com.lh.leonard.amplifiedscheduler");
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            f = new File(filePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                imagePathDirectory.setText(filePath);
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                btnUpdateImage.setCompoundDrawablesWithIntrinsicBounds(userGoodProfileDraw, null, null, null);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}