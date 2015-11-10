package com.lh.leonard.amplifiedscheduler;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class UpdateAccount extends Fragment {

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

    Drawable emailGoodIconDraw;
    Drawable userGoodProfileDraw;
    Drawable passwordGoodIconDraw;
    Drawable countryGoodIconDraw;
    Drawable phoneGoodIconDraw;

    Drawable passwordBadIconDraw;
    Drawable emailBadIconDraw;

    String fname;
    String lname;
    String phone;
    String email;
    String password;
    String passwordConfirm;
    Validator validator = new Validator();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_update_account, container, false);

        //TODO set the ic_.. icons to green and keep tick when validated

        getActivity().setTitle("Update Account");

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

        Backendless.Data.mapTableToClass("Person", Person.class);

        user = Backendless.UserService.CurrentUser();
        personLoggedIn = (Person) user.getProperty("persons");

        // Get a reference to the AutoCompleteTextView in the layout
        final AutoCompleteTextView textViewCountry = (AutoCompleteTextView) v.findViewById(R.id.autocomplete_countryUpdate);
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.countries_array);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, countries);
        textViewCountry.setAdapter(adapter);

        // final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");

        editTextUpdateFNameReg = (EditText) v.findViewById(R.id.editTextUpdateFName);
        editTextUpdateLNameReg = (EditText) v.findViewById(R.id.editTextUpdateLName);
        editTextUpdatePhoneReg = (EditText) v.findViewById(R.id.editTextUpdatePhone);
        editTextUpdateEmail = (EditText) v.findViewById(R.id.editTextUpdateEmail);
        ediTextUpdatePassword = (EditText) v.findViewById(R.id.ediTextUpdatePassword);
        editTextUpdatePasswordConfirmReg = (EditText) v.findViewById(R.id.editTextUpdatePasswordConfirm);

        final AutoResizeTextView txtLabelFnameUpdate = (AutoResizeTextView) v.findViewById(R.id.txtLabelFnameUpdate);
        final AutoResizeTextView txtLabeLnameUpdate = (AutoResizeTextView) v.findViewById(R.id.txtLabeLnameUpdate);
        final AutoResizeTextView txtLabelTextPasswordUpdate = (AutoResizeTextView) v.findViewById(R.id.txtLabelTextPasswordUpdate);
        final AutoResizeTextView txtLabelPasswordConfirmUpdate = (AutoResizeTextView) v.findViewById(R.id.txtLabelPasswordConfirmUpdate);
        final AutoResizeTextView txtLabelEmailUpdate = (AutoResizeTextView) v.findViewById(R.id.txtLabelEmailUpdate);
        final AutoResizeTextView txtLabelCountryUpdate = (AutoResizeTextView) v.findViewById(R.id.txtLabelUpdateCountry);
        final AutoResizeTextView txtLabelPhoneUpdate = (AutoResizeTextView) v.findViewById(R.id.txtLabelPhoneUpdate);
        AutoResizeTextView editTextNoticeUpdate = (AutoResizeTextView) v.findViewById(R.id.editTextNoticeUpdate);

        final Typeface RobotoBlack = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        Button updateDetailsBtn = (Button) v.findViewById(R.id.buttonUpdateUser);

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

                                                    ringProgressDialog = ProgressDialog.show(v.getContext(), "Please wait ...", "Updating account ...", true);
                                                    ringProgressDialog.setCancelable(false);

                                                    email = editTextUpdateEmail.getText().toString();
                                                    fname = editTextUpdateFNameReg.getText().toString();
                                                    lname = editTextUpdateLNameReg.getText().toString();
                                                    phone = editTextUpdatePhoneReg.getText().toString();
                                                    password = ediTextUpdatePassword.getText().toString();
                                                    passwordConfirm = editTextUpdatePasswordConfirmReg.getText().toString();
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
                        Toast.makeText(v.getContext(), "Please enter your email address in the format someone@example.com", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(v.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                                ediTextUpdatePassword.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                            }
                        }
                    } else {
                        ediTextUpdatePassword.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                        Toast.makeText(v.getContext(), "Password must contain at least 5 or more characters", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(v.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                                editTextUpdatePasswordConfirmReg.setCompoundDrawablesWithIntrinsicBounds(passwordBadIconDraw, null, crossIconDraw, null);
                            }
                        }
                    } else {
                        Toast.makeText(v.getContext(), "Password must contain at least 5 or more characters", Toast.LENGTH_SHORT).show();
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

        return v;
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
            Boolean inputSet = false;

            Person p = Backendless.Data.of(Person.class).findById(personLoggedIn);

            if (!(email.equals(""))) {
                p.setEmail(email);
                user.setEmail(email);
                inputSet = true;
            }
            if (!(fname.equals(""))) {
                p.setFname(fname);
                firstNameChange = true;
                inputSet = true;
            }
            if (!(lname.equals(""))) {
                p.setLname(lname);
                lastNameChange = true;
                inputSet = true;
            }
            if (!(phone.equals(""))) {
                p.setPhone(phone.toString());
                inputSet = true;
            }
            if ((!(password.equals(""))) && (!(passwordConfirm.equals("")))) {

                if (password.equals(passwordConfirm)) {
                    user.setPassword(password);
                    inputSet = true;
                }

            }

            if (firstNameChange && lastNameChange) {
                p.setFullname(fname + " " + lname);
            } else {
                if (fname != "") {
                    p.setFullname(fname + " " + p.getLname());
                } else {
                    p.setFullname(p.getFname() + " " + lname);
                }
            }
            if (inputSet) {

                Backendless.Data.of(Person.class).save(p);
                Backendless.UserService.update(user);
            }

            //TODO update the nav drawer names and email after update

            return inputSet;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            ringProgressDialog.dismiss();
            if (result) {
                Toast.makeText(getActivity(), "Inputs Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "No Inputs Submitted", Toast.LENGTH_SHORT).show();
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
}