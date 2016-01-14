package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NavDrawerActivity extends AppCompatActivity {

    ShareActionProvider mShareActionProvider;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    ProgressDialog ringProgressDialog;

    Person personLoggedIn;
    int resourceIntPendingResponseEvents;
    int resourceIntPersonsRequestingMe;

    int sizePersonsRequestingMe;
    int sizeGoingToEvents;
    int sizePendingResponseEvents;
    int sizeMyCreatedEvents;

    Boolean OpenDrawer = false;

    private Menu optionsMenu;

    String valResponseEvents = "";
    String valPersonsRequestingMe = "";
    String valGoingToEvents = "";
    String valMyCreatedEvents = "";

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
        Backendless.Persistence.mapTableToClass("Person", Person.class);

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

        if (userLoggedIn != null) {
            personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setRefreshActionButtonState(true);
        getNav();
        new Refresh().execute();
        setRefreshActionButtonState(false);

    }

    private void selectItem(int position) {

        Fragment fragment = null;
        Intent intent = null;
        switch (position) {
            case 1:
                intent = new Intent(NavDrawerActivity.this, CreateSlot.class);
                break;
            case 2:
                intent = new Intent(NavDrawerActivity.this, MyCreatedSlots.class);
                break;
            case 3:
                intent = new Intent(NavDrawerActivity.this, SlotsImGoingTo.class);
                break;
            case 4:
                intent = new Intent(NavDrawerActivity.this, SlotsAwaitingMyResponse.class);
                break;
            case 5:
                intent = new Intent(NavDrawerActivity.this, AddRemoveContactsTabbed.class);
                break;
            case 6:
                intent = new Intent(NavDrawerActivity.this, UpdateAccount.class);
                break;
            case 7:
                ringProgressDialog = ProgressDialog.show(NavDrawerActivity.this, "Please wait ...",
                        "Logging out " + personLoggedIn.getFname() + " " + personLoggedIn.getLname() + " ...", true);
                ringProgressDialog.setCancelable(false);
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    public void handleResponse(Void response) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this free event making app: https://play.google.com/store/apps/details?id=com.lh.leonard.amplifiedscheduler");
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:

                // Complete with your code
                //  OpenDrawer = true;
                new Refresh().execute();
                setRefreshActionButtonState(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        Fragment HomeFragment = getFragmentManager().findFragmentByTag("home_tag");

        if ((HomeFragment != null && HomeFragment.isVisible()) || fragmentManager.getBackStackEntryCount() <= 0) {

            if (Drawer == null && mRecyclerView == null) {
                new AlertDialog.Builder(NavDrawerActivity.this)
                        .setTitle("Logout").setMessage("You are about to logout")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
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
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                ).show();
            } else if (Drawer.isDrawerOpen(mRecyclerView)) {
                Drawer.closeDrawer(mRecyclerView);
            } else {

                new AlertDialog.Builder(NavDrawerActivity.this)
                        .setTitle("Logout").setMessage("You are about to logout")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
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
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                ).show();
            }
        } else {
            if (Drawer.isDrawerOpen(mRecyclerView)) {
                Drawer.closeDrawer(mRecyclerView);
            } else {
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                    fragmentManager.popBackStackImmediate();
                }
            }
        }
    }

    public void getNav() {
        setRefreshActionButtonState(true);
        resourceIntPersonsRequestingMe = R.drawable.ic_addcontact;
        resourceIntPendingResponseEvents = R.drawable.ic_pendingrequestslots;

        int ICONS[] = {R.drawable.ic_createslot, R.drawable.ic_mycreatedslots, R.drawable.ic_goingtoslots,
                resourceIntPendingResponseEvents, resourceIntPersonsRequestingMe, R.drawable.ic_updateaccount, R.drawable.ic_logout};

        String TITLES[] = {"Create event", "My events " + valMyCreatedEvents, "Going to events " +
                valGoingToEvents, "Invited events " + valResponseEvents, "Manage contacts" +
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
        setRefreshActionButtonState(false);
    }


    private class Refresh extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<String> relationProps = new ArrayList<>();
            relationProps.add("personsRequestingMe");
            relationProps.add("contacts");
            relationProps.add("personsImRequesting");
            relationProps.add("goingToSlot");
            relationProps.add("myCreatedSlot");
            relationProps.add("pendingResponseSlot");
            Backendless.Data.of(Person.class).loadRelations(personLoggedIn, relationProps);

            sizePersonsRequestingMe = personLoggedIn.getPersonsRequestingMe().size();
            sizePendingResponseEvents = personLoggedIn.getPendingResponseSlot().size();
            sizeGoingToEvents = personLoggedIn.getGoingToSlot().size();
            sizeMyCreatedEvents = personLoggedIn.getMyCreatedSlot().size();

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

            Collections.sort(personLoggedIn.getMyCreatedSlot(), new Comparator<Slot>() {
                public int compare(Slot e1, Slot e2) {
                    if (e1.getStartCalendar().getTime() == null || e2.getStartCalendar().getTime() == null)
                        return 0;
                    return e1.getStartCalendar().getTime().compareTo(e2.getStartCalendar().getTime());
                }
            });
            Collections.sort(personLoggedIn.getPendingResponseSlot(), new Comparator<Slot>() {
                public int compare(Slot e1, Slot e2) {
                    if (e1.getStartCalendar().getTime() == null || e2.getStartCalendar().getTime() == null)
                        return 0;
                    return e1.getStartCalendar().getTime().compareTo(e2.getStartCalendar().getTime());
                }
            });
            Collections.sort(personLoggedIn.getGoingToSlot(), new Comparator<Slot>() {
                public int compare(Slot e1, Slot e2) {
                    if (e1.getStartCalendar().getTime() == null || e2.getStartCalendar().getTime() == null)
                        return 0;
                    return e1.getStartCalendar().getTime().compareTo(e2.getStartCalendar().getTime());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            int ICONS[] = {R.drawable.ic_createslot
                    , R.drawable.ic_mycreatedslots, R.drawable.ic_goingtoslots,
                    resourceIntPendingResponseEvents, resourceIntPersonsRequestingMe,
                    R.drawable.ic_updateaccount, R.drawable.ic_logout};

            String TITLES[] = {"Create event", "My events " + valMyCreatedEvents, "Going to events " +
                    valGoingToEvents, "Invited events " + valResponseEvents, "Manage contacts" +
                    valPersonsRequestingMe, "Update account", "Sign out"};

            NAME = personLoggedIn.getFullname();
            EMAIL = userLoggedIn.getEmail();

            mAdapter = new NavDrawerAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)

            mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

            mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
            mLayoutManager = new LinearLayoutManager(NavDrawerActivity.this);                 // Creating a layout Manager
            mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

            mDrawerToggle = new ActionBarDrawerToggle(NavDrawerActivity.this,
                    Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

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

//            if (OpenDrawer) {
//                Drawer.openDrawer(mRecyclerView);
//            }
//            OpenDrawer = false;

            Fragment frag = getFragmentManager().findFragmentByTag("home_tag");

            if (sizePersonsRequestingMe >= 1 || sizePendingResponseEvents >= 1) {
                Drawable drawableNotification = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_noification);
                ((ImageView) frag.getView().findViewById(R.id.imageViewNotification)).setImageDrawable(
                        drawableNotification);
                Drawer.openDrawer(mRecyclerView);
            } else {
                Drawable drawableNoNotification = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_no_noification);
                ((ImageView) frag.getView().findViewById(R.id.imageViewNotification)).setImageDrawable(
                        drawableNoNotification);
            }

            if (!personLoggedIn.getMyCreatedSlot().isEmpty()) {
                ((AutoResizeTextView) frag.getView().findViewById(R.id.textViewMyEvents))
                        .setText(personLoggedIn.getMyCreatedSlot().get(0).getSubject());
                ((AutoResizeTextView) frag.getView().findViewById(R.id.textViewMyEventsDate))
                        .setText(personLoggedIn.getMyCreatedSlot().get(0).getStartCalendar().getTime().toString());
            } else {
                //Do something no events
            }
            if (!personLoggedIn.getGoingToSlot().isEmpty()) {
                ((AutoResizeTextView) frag.getView().findViewById(R.id.textViewGoingToEvents))
                        .setText(personLoggedIn.getGoingToSlot().get(0).getSubject() + " "
                                + personLoggedIn.getGoingToSlot().get(0).getStartCalendar().getTime());
            } else {

            }
            if (!personLoggedIn.getPendingResponseSlot().isEmpty()) {
                ((AutoResizeTextView) frag.getView().findViewById(R.id.textViewInvitedEvent))
                        .setText(personLoggedIn.getPendingResponseSlot().get(0).getSubject() + " "
                                + personLoggedIn.getPendingResponseSlot().get(0).getStartCalendar().getTime());
            } else {

            }
            setRefreshActionButtonState(false);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
}