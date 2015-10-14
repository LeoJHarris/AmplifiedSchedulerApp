package com.lh.leonard.eventhub101;

import android.app.Fragment;
import android.graphics.Typeface;
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

    View v;
    Person personLoggedIn;
    EditText editTextUpdateFNameReg; // TODO set hints to user info
    EditText editTextUpdateLNameReg;
    EditText editTextUpdatePhoneReg;
    EditText editTextUpdateEmail;
    EditText ediTextUpdatePassword;
    EditText editTextUpdatePasswordConfirmReg;
    BackendlessUser user;

    String fname;
    String lname;
    String phone;
    String email;
    String password;
    String passwordConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_update_account, container, false);

        getActivity().setTitle("Update Account");

        Backendless.Persistence.mapTableToClass("Person", Person.class);

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

        final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");

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

        editTextNoticeUpdate.setTypeface(regularFont);

        Button updateDetailsBtn = (Button) v.findViewById(R.id.buttonUpdateUser);

        editTextUpdateFNameReg.setTypeface(regularFont);
        editTextUpdateLNameReg.setTypeface(regularFont);
        editTextUpdatePhoneReg.setTypeface(regularFont);
        editTextUpdateEmail.setTypeface(regularFont);
        ediTextUpdatePassword.setTypeface(regularFont);
        editTextUpdatePasswordConfirmReg.setTypeface(regularFont);
        updateDetailsBtn.setTypeface(regularFont);
        txtLabelCountryUpdate.setTypeface(regularFont);
        txtLabelEmailUpdate.setTypeface(regularFont);
        txtLabelFnameUpdate.setTypeface(regularFont);
        txtLabeLnameUpdate.setTypeface(regularFont);
        txtLabelPasswordConfirmUpdate.setTypeface(regularFont);
        txtLabelPhoneUpdate.setTypeface(regularFont);
        txtLabelTextPasswordUpdate.setTypeface(regularFont);
        textViewCountry.setTypeface(regularFont);


        updateDetailsBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

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


            if (!(email.equals(""))) {
                personLoggedIn.setEmail(email);
                user.setEmail(email);
                inputSet = true;
            }
            if (!(fname.equals(""))) {
                personLoggedIn.setFname(fname);
                firstNameChange = true;
                inputSet = true;
            }
            if (!(lname.equals(""))) {
                personLoggedIn.setLname(lname);
                lastNameChange = true;
                inputSet = true;
            }
            if (!(phone.equals(""))) {
                personLoggedIn.setPhone(phone.toString());
                inputSet = true;
            }
            if ((!(password.equals(""))) && (!(passwordConfirm.equals("")))) {

                if (password.equals(passwordConfirm)) {
                    user.setPassword(password);
                    inputSet = true;
                }

            }

            if (firstNameChange && lastNameChange) {
                personLoggedIn.setFullname(fname + " " + lname);
            } else {
                if (fname != "") {
                    personLoggedIn.setFullname(fname + " " + personLoggedIn.getLname());
                } else {
                    personLoggedIn.setFullname(personLoggedIn.getFname() + " " + lname);
                }
            }
            if (inputSet) {
                Backendless.Data.of(Person.class).save(personLoggedIn);
                Backendless.UserService.update(user);
            }

            //TODO update the nav drawer names and email after update

            return inputSet;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(getActivity(), "Inputs Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "No Inputs Submitted", Toast.LENGTH_SHORT).show();
            }

        }
    }
}