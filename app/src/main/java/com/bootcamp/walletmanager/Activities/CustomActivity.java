package com.bootcamp.walletmanager.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.MediaSync;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.bootcamp.walletmanager.Application.LoggedAccount;
import com.bootcamp.walletmanager.Application.WalletManagerApplication;
import com.bootcamp.walletmanager.Datamodel.Account;
import com.bootcamp.walletmanager.Datamodel.DealType;
import com.bootcamp.walletmanager.Datamodel.Records;
import com.bootcamp.walletmanager.Datamodel.Wallets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class CustomActivity extends AppCompatActivity {

    public String TAG = "authentication";
    public Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        realm = Realm.getInstance(WalletManagerApplication.config);
    }


    // TODO: User account data persistence functions.

    public void createNewAccount(final String name, final String email, final String password) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Account account = realm.createObject(Account.class, UUID.randomUUID().toString()); // Create a new object
                account.setName(name);
                account.setEmail(email);
                account.setPassword(password);
                account.setLogged(true);
            }
        });
    }

    public void getCreatedAccounts() {
        RealmResults<Account> accounts = realm.where(Account.class).findAll();
        for (int i = 0; i < accounts.size(); i++) {
            Log.d(TAG, "name: " + accounts.get(i).getName());
            Log.d(TAG, "email: " + accounts.get(i).getEmail());
            Log.d(TAG, "logged: " + accounts.get(i).isLogged());
            Log.d(TAG, "password: " + accounts.get(i).getPassword());
            Log.d(TAG, " ");
        }
    }

    public Account getLoggedAccount(String email) {
        Account account = new Account();
        RealmResults<Account> accounts = realm.where(Account.class).findAll();
        for (int i = 0; i < accounts.size(); i++) {
            if (email.equals(accounts.get(i).getEmail())) {
                account = accounts.get(i);
                realm.beginTransaction();
                account.setLogged(true);
                realm.commitTransaction();
            }
        }
        return account;
    }

    public Boolean checkLogin(String email, String password) {
        Boolean currentLogin = false;

        RealmResults<Account> accounts = realm.where(Account.class).findAll();
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            if (account.getEmail().equals(email) && account.getPassword().equals(password)) {
                currentLogin = true;
            }
        }
        return currentLogin;
    }


    // TODO: Create wallet data functions.

    public Boolean findExistedWallet() {
        if (LoggedAccount.getCurrentLogin().getUserWallets().size() != 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public void createNewWallet(final String name, final int amount, final int walletType, final Date dayCreated) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Wallets wallet = realm.createObject(Wallets.class); // Create a new object
                wallet.setName(name);
                wallet.setAmount(amount);
                wallet.setWalletType(walletType);
                wallet.setDayCreated(dayCreated);
                LoggedAccount.getCurrentLogin().getUserWallets().add(wallet);
            }
        });
    }

    public void updateWallet(final String name, final String type, final int amount) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Wallets> wallets = realm.where(Wallets.class)
                        .equalTo("name", name)
                        .findAll();
                for (int i = 0; i < wallets.size(); i++) {
                    Wallets wallet = wallets.get(i);
                    if (type.equals("spending")) {
                        int amountAfter = wallet.getAmount() - amount;
                        wallet.setAmount(amountAfter);
                    }
                    else if (type.equals("income")){
                        int amountAfter = wallet.getAmount() + amount;
                        wallet.setAmount(amountAfter);
                    }
                }
            }
        });

    }

    // TODO: Create deal data functions.
    public void createNewRecord(final String amount, final String group, final Date date, final String fromWallet, final String notes, final String kind) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Records record = realm.createObject(Records.class, UUID.randomUUID().toString()); // Create a new object
                record.setAmount(amount);
                record.setType(group);
                record.setDate(date);
                record.setFromWallet(fromWallet);
                record.setNotes(notes);
                record.setKind(kind);
                LoggedAccount.getCurrentLogin().getUserRecords().add(record);
            }
        });
    }

    public void updateRecord(final String id, final String amount, final String group, final Date date, final String notes, final String kind) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Records> records = realm.where(Records.class)
                        .equalTo("recordID", id)
                        .findAll();
                for (int i = 0; i < records.size(); i++) {
                    Records record = records.get(i);
                    record.setAmount(amount);
                    record.setDate(date);
                    record.setKind(kind);
                    record.setType(group);
                    record.setNotes(notes);
                }
            }
        });
    }

    public void deleteRecord(final String id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Records> records = realm.where(Records.class)
                        .equalTo("recordID", id)
                        .findAll();
                for (int i = 0; i < records.size(); i++) {
                    Records record = records.get(i);
                    record.deleteFromRealm();
                }
            }
        });
    }

    // TODO: Create hitory records data functions.

    public List<Records> getMonthlyRecords() {
        List<Records> monthlyRecords = new ArrayList<>();
        String currentMonth  = (String) DateFormat.format("MM", Calendar.getInstance().getTime());
        String currentYear  = (String) DateFormat.format("yyyy", Calendar.getInstance().getTime());
        for (int i = 0; i < LoggedAccount.getCurrentLogin().getUserRecords().size(); i++) {
            Records record = LoggedAccount.getCurrentLogin().getUserRecords().get(i);
            String recordMonth = (String) DateFormat.format("MM", record.getDate());
            String recordYear = (String) DateFormat.format("yyyy", record.getDate());
            if (recordMonth.equals(currentMonth) && recordYear.equals(currentYear)) {
                monthlyRecords.add(record);
            }
        }
        Collections.sort(monthlyRecords, new Comparator<Records>(){
            public int compare(Records o1, Records o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        return monthlyRecords;
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //TODO: Record image capture
    public Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
