package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

public class NavDrawerActivity extends AppCompatActivity {


    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    ProgressDialog ringProgressDialog;

    Person personLoggedIn;

    int resourceIntPendingResponseEvents;
    int resourceIntPersonsRequestingMe;

    String valResponseEvents = "";
    String valPersonsRequestingMe = "";
    String valGoingToEvents = "";
    String valMyCreatedEvents = "";

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME;
    String EMAIL;
    int PROFILE = R.drawable.ic_currentcontact;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    FragmentManager fragmentManager;
    RecyclerView mRecyclerView = null;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter = null;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle
    Boolean updateNavDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);

        Backendless.Data.mapTableToClass("Person", Person.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            updateNavDrawer = extras.getBoolean("refresh");
        }

        // Set up a home fragment with some welcome in.
        Fragment home = new HomeFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container, home, "home_tag")
                .addToBackStack("home_stack").commit();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Backendless.Data.mapTableToClass("Person", Person.class);
        personLoggedIn = (Person) userLoggedIn.getProperty("persons");

        new GetNavInfo().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void selectItem(int position) {

        Fragment fragment = null;
        Intent intent = null;
        switch (position) {
            case 1:
                fragment = new HomeFragment();
                break;
            case 2:
                intent = new Intent(NavDrawerActivity.this, CreateSlot.class);
                break;
            case 3:
                fragment = new MyCreatedSlots();
                break;
            case 4:
                fragment = new SlotsImGoingTo();
                break;
            case 5:
                fragment = new SlotsAwaitingMyResponse();
                break;
            case 6:
                intent = new Intent(NavDrawerActivity.this, AddRemoveContactsTabbed.class);
                break;
            case 7:
                fragment = new UpdateAccount();
                break;
            case 8:
                Backendless.UserService.logout(new AsyncCallback<Void>() {

                    public void handleResponse(Void response) {

                        ringProgressDialog = ProgressDialog.show(NavDrawerActivity.this, "Please wait ...", "Logging out " + personLoggedIn.getFname() + " " + personLoggedIn.getLname() + " ...", true);
                        ringProgressDialog.setCancelable(false);
                        Intent logOutIntent = new Intent(NavDrawerActivity.this, MainActivity.class);
                        logOutIntent.putExtra("loggedoutperson", personLoggedIn.getFname() + "," + personLoggedIn.getLname());
                        startActivity(logOutIntent);
                    }

                    public void handleFault(BackendlessFault fault) {
                        // something went wrong and logout failed, to get the error code call fault.getCode()
                        ringProgressDialog.dismiss();


                    }
                });

            default:
                break;
        }

        if (fragment != null) {
            fragmentManager = getFragmentManager();
            if (position == 1 || getFragmentManager().findFragmentByTag("home_tag").isVisible()) {

//                Fragment fragmentA = new FragmentA();
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.MainFrameLayout, fragmentA, "YOUR_TARGET_FRAGMENT_TAG")
//                        .addToBackStack("YOUR_SOURCE_FRAGMENT_TAG").commit();


                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment
                        ).addToBackStack(null).commit();

            } else {
                fragmentManager.beginTransaction()
                        .add(R.id.frame_container, fragment).commit();
            }
        } else {
            // error in creating fragment
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {


        Fragment HomeFragment = getFragmentManager().findFragmentByTag("home_tag");


        if ((HomeFragment != null && HomeFragment.isVisible()) || fragmentManager.getBackStackEntryCount() <= 0) {

            if (Drawer.isDrawerOpen(mRecyclerView)) {
                Drawer.closeDrawer(mRecyclerView);
            } else {


                new AlertDialog.Builder(NavDrawerActivity.this)
                        .setTitle("Logging out").setMessage("You are about to logout out").
                        setIcon(R.drawable.ic_xclamationmark)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                ringProgressDialog = ProgressDialog.show(NavDrawerActivity.this, "Please wait ...", "Logging out " + personLoggedIn.getFname() + " " + personLoggedIn.getLname() + " ...", true);
                                ringProgressDialog.setCancelable(false);

                                Backendless.UserService.logout(new AsyncCallback<Void>() {

                                    @Override
                                    public void handleResponse(Void aVoid) {

                                        Intent logOutIntent = new Intent(NavDrawerActivity.this, MainActivity.class);
                                        logOutIntent.putExtra("loggedoutperson", personLoggedIn.getFname() + "," + personLoggedIn.getLname());
                                        ringProgressDialog.dismiss();
                                        startActivity(logOutIntent);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                        ringProgressDialog.dismiss();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                ).show();
            }
        } else {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStackImmediate();
            }
        }
    }

    private class GetNavInfo extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            List<String> relations = new ArrayList<String>();
            relations.add("personsRequestingMe");
            relations.add("goingToSlot");
            relations.add("myCreatedSlot");
            relations.add("pendingResponseSlot");
            Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

            int sizePendingResponseEvents = person.getPendingResponseSlot().size();
            int sizePersonsRequestingMe = person.getPersonsRequestingMe().size();
            int sizeGoingToEvents = person.getGoingToSlot().size();
            int sizeMyCreatedEvents = person.getMyCreatedSlot().size();

            valResponseEvents = " " + String.valueOf(sizePendingResponseEvents);
            valPersonsRequestingMe = " " + String.valueOf(sizePersonsRequestingMe);
            valGoingToEvents = " " + String.valueOf(sizeGoingToEvents);
            valMyCreatedEvents = " " + String.valueOf(sizeMyCreatedEvents);

            if (sizePendingResponseEvents >= 1) {
                resourceIntPendingResponseEvents = R.drawable.ic_actionrequiredinvitedevent;
            } else {
                resourceIntPendingResponseEvents = R.drawable.ic_pendingrequestslots;
            }
            if (sizePersonsRequestingMe >= 1) {
                resourceIntPersonsRequestingMe = R.drawable.ic_actionrequiredcontactspng;
            } else {
                resourceIntPersonsRequestingMe = R.drawable.ic_addcontact;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            int ICONS[] = {R.drawable.ic_home, R.drawable.ic_createslot
                    , R.drawable.ic_mycreatedslots, R.drawable.ic_goingtoslots,
                    resourceIntPendingResponseEvents, resourceIntPersonsRequestingMe, R.drawable.ic_updateaccount, R.drawable.ic_logout};

            String TITLES[] = {"Home", "Create schedule", "My schedules " + valMyCreatedEvents, "Going to schedules " +
                    valGoingToEvents, "Invited schedules " + valResponseEvents, "Manage contacts" +
                    valPersonsRequestingMe, "Update account", "Sign out"};

            NAME = personLoggedIn.getFullname();
            EMAIL = userLoggedIn.getEmail();

            mAdapter = new NavDrawerAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)

            mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

            mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

            // And passing the titles,icons,header view name, header view email,
            // and header view profile picture

            mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
            mLayoutManager = new LinearLayoutManager(NavDrawerActivity.this);                 // Creating a layout Manager
            mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


            Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
            mDrawerToggle = new ActionBarDrawerToggle(NavDrawerActivity.this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                    // open I am not going to put anything here)
                    // invalidateOptionsMenu();

                    mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {

                            selectItem(position);
                            Drawer.closeDrawer(mRecyclerView);
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                            // ...

                            //TODO: Dialog show, remove slot. Remove from list clear adapter, give adapter now list
                            //TODO Yes: get the ownerObjectId and remove from database
                        }
                    }
                    ));
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    // Code here will execute once drawer is closed
                    //  invalidateOptionsMenu();
                }

            }; // Drawer Toggle Object Made
            Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
            mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
            if (updateNavDrawer) {
                Drawer.openDrawer(mRecyclerView);
            }
        }
    }
}