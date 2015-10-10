package com.lh.leonard.eventhub101;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


public class NavDrawerActivity extends AppCompatActivity {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    Person personLoggedIn;
    ProgressDialog ringProgressDialog;

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Home", "Manage Account", "Create Event", "Find Contacts",
            "My Events", "Going To Events", "Invited Events", "Log Out"};
    int ICONS[] = {R.drawable.ic_home, R.drawable.ic_updateaccount, R.drawable.ic_createslot,
            R.drawable.ic_addcontact, R.drawable.ic_mycreatedslots, R.drawable.ic_goingtoslots,
            R.drawable.ic_pendingrequestslots, R.drawable.ic_logout};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME;
    String EMAIL;
    int PROFILE = R.drawable.ic_currentcontact;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    FragmentManager fragmentManager;

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);

        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);

        //  if ((Person) userLoggedIn.getProperty("persons") != null) {
        personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        //  } else
        //   {

        // }

        NAME = personLoggedIn.getFname() + " " + personLoggedIn.getLname();
        EMAIL = userLoggedIn.getEmail();

        // Set up a home fragment with some welcome in.
        Fragment home = new HomeFragment();
        FragmentManager FM = getFragmentManager();
        FM
                .beginTransaction()
                .replace(R.id.frame_container, home)
                .commit();

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new NavDrawerAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
                invalidateOptionsMenu();

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
                invalidateOptionsMenu();
            }

        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
    }


    private void selectItem(int position) {

        Fragment fragment = null;
        Intent intent = null;
        switch (position) {
            case 1:
                fragment = new HomeFragment();
                break;
            case 2:
                fragment = new UpdateAccount();
                break;
            case 3:
                intent = new Intent(NavDrawerActivity.this, CreateSlot.class);
                break;
            case 4:
                intent = new Intent(NavDrawerActivity.this, AddRemoveContactsTabbed.class);
                break;
            case 5:
                fragment = new MyCreatedSlots();
                break;
            case 6:
                fragment = new SlotsImGoingTo();
                break;
            case 7:
                fragment = new SlotsAwaitingMyResponse();
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
            if (position != 1 && fragmentManager.getBackStackEntryCount() < 1) {

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("home").commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();
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

        if (Drawer.isDrawerOpen(mRecyclerView)) {
            Drawer.closeDrawer(mRecyclerView);
        } else if (fragmentManager != null) {
            if (fragmentManager.getBackStackEntryCount() >= 1) {
                // fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()).getName();
                System.out.println(fragmentManager.getBackStackEntryAt(0).getName());
                System.out.println(fragmentManager.getBackStackEntryCount());

                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                    fragmentManager.popBackStackImmediate();
                }
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
    }
}


