package com.lh.leonard.amplifiedscheduler;

import android.app.ProgressDialog;
import android.content.Context;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

/**
 * Created by Leonard on 17/04/2015.
 */
public abstract class LoadingCallback<T> implements AsyncCallback<T> {

    private Context context;
    private ProgressDialog progressDialog;

    public LoadingCallback(Context context, String s, boolean b) {
        this(context, "loading...");

    }

    public LoadingCallback(Context context, String loadingMessage) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(loadingMessage);
    }

    @Override
    public void handleFault(BackendlessFault fault) {
        //progressDialog.dismiss();
        //DialogHelper.createErrorDialog(context, "BackendlessFault", fault.getMessage()).show();

    }

    public void showLoading() {
        progressDialog.show();
    }

    public void hideLoading() {
        progressDialog.dismiss();
    }
}