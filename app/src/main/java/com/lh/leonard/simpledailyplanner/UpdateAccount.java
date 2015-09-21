package com.lh.leonard.simpledailyplanner;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class UpdateAccount extends Fragment {

    View v;
    Person personLoggedIn;
    EditText editTextUpdateFNameReg;
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

        Backendless.Persistence.mapTableToClass("Person", Person.class);

        user = Backendless.UserService.CurrentUser();
        personLoggedIn = (Person) user.getProperty("persons");

        final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");

        TextView textViewHeaderUpdateAccount = (TextView) v.findViewById(R.id.textViewHeaderUpdateAccount);
        editTextUpdateFNameReg = (EditText) v.findViewById(R.id.editTextUpdateFNameReg);
        editTextUpdateLNameReg = (EditText) v.findViewById(R.id.editTextUpdateLNameReg);
        editTextUpdatePhoneReg = (EditText) v.findViewById(R.id.editTextUpdatePhoneReg);
        editTextUpdateEmail = (EditText) v.findViewById(R.id.editTextUpdateEmail);
        ediTextUpdatePassword = (EditText) v.findViewById(R.id.ediTextUpdatePassword);
        editTextUpdatePasswordConfirmReg = (EditText) v.findViewById(R.id.editTextUpdatePasswordConfirmReg);

        Button updateDetailsBtn = (Button) v.findViewById(R.id.buttonUpdateUser);
        final Button buttonBackToLoggedIn = (Button) v.findViewById(R.id.buttonBackToLoggedIn);

        textViewHeaderUpdateAccount.setTypeface(regularFont);
        editTextUpdateFNameReg.setTypeface(regularFont);
        editTextUpdateLNameReg.setTypeface(regularFont);
        editTextUpdatePhoneReg.setTypeface(regularFont);
        editTextUpdateEmail.setTypeface(regularFont);
        ediTextUpdatePassword.setTypeface(regularFont);
        editTextUpdatePasswordConfirmReg.setTypeface(regularFont);
        buttonBackToLoggedIn.setTypeface(regularFont);
        updateDetailsBtn.setTypeface(regularFont);

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
        buttonBackToLoggedIn.setOnClickListener(new View.OnClickListener()

                                                {
                                                    public void onClick(View v) {
                                                        Intent loggedInIntent = new Intent(getActivity(), UserLoggedIn.class);
                                                        startActivity(loggedInIntent);

                                                    }
                                                }

        );
        return v;
    }


    private class Parse extends AsyncTask<Void, Integer, Void> {


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

            Boolean firstNameChange = false;
            Boolean lastNameChange = false;

            if (!(email.equals(""))) {
                personLoggedIn.setEmail(email);
                user.setEmail(email);
            }
            if (!(fname.equals(""))) {
                personLoggedIn.setFname(fname);
                firstNameChange = true;
            }
            if (!(lname.equals(""))) {
                personLoggedIn.setLname(lname);
                lastNameChange = true;
            }
            if (!(phone.equals(""))) {
                personLoggedIn.setPhone(phone.toString());
            }
            if ((!(password.equals(""))) && (!(passwordConfirm.equals("")))) {

                if (password.equals(passwordConfirm)) {
                    user.setPassword(password);
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

            Backendless.Data.of(Person.class).save(personLoggedIn);
            Backendless.UserService.update(user);

            //TODO update the nav drawer names and email after update

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Toast.makeText(getActivity(), "Inputs Updated", Toast.LENGTH_SHORT).show();
        }
    }
}