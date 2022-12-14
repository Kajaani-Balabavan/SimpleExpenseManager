package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class dbHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "200279N";

    public dbHandler(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table ACCOUNT"+
                        "(accountNo text primary key, bankName text, accountHolderName text, balance integer)"
        );
        sqLiteDatabase.execSQL(
                "create table TRANSACTION_"+
                        "(date text, accountNo text , expenseType text, amount integer, foreign key (accountNo) references ACCOUNT (accountNo) )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ACCOUNT");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TRANSACTION_");
        onCreate(sqLiteDatabase);
    }

    public void addAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("accountNo", account.getAccountNo());
        data.put("bankName", account.getBankName());
        data.put("accountHolderName", account.getAccountHolderName());
        data.put("balance", account.getBalance());
        database.insert("ACCOUNT", null, data);
    }

    public void addTransaction(Date date,String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        SimpleDateFormat format_ = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);

        data.put("date", format_.format(date));
        data.put("accountNo", accountNo);
        data.put("expenseType",expenseType.toString()) ;
        data.put("amount", amount);
        database.insert("TRANSACTION_", null, data);
    }

    public void removeAccount(String accountNo) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("ACCOUNT", "accountNo =?", new String[]{accountNo});
    }

    public void updateAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("accountNo", account.getAccountNo());
        data.put("bankName", account.getBankName());
        data.put("accountHolderName", account.getAccountHolderName());
        data.put("balance", account.getBalance());
        database.update("ACCOUNT",data, "accountNo =?", new String[]{account.getAccountNo()});
    }

    public Account accountDetails(String accNo) {
        SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor details = database.rawQuery("select * from ACCOUNT where accountNo =?", new String[]{accNo});

        Account account = null;
        if (details.getCount() != 0) {
            while (details.moveToNext()) {
                String accountNo = details.getString(0);
                String bankName = details.getString(1);
                String accountHolderName = details.getString(2);
                double balance = details.getDouble(3);
                account = new Account(accountNo, bankName, accountHolderName, balance);
            }
        }
        return account;
    }

    public List<Account> Accounts() {
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor details = database.rawQuery("select * from ACCOUNT ", null);
        details.moveToFirst();

        while (!details.isAfterLast()) {
            String accountNo = details.getString(0);
            String bankName = details.getString(1);
            String accountHolderName = details.getString(2);
            double balance = details.getDouble(3);
            Account account = new Account(accountNo, bankName, accountHolderName, balance);
            accounts.add(account);
            details.moveToNext();
        }
        return accounts;
    }


    public List<Transaction> getTransactions() {
        SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor res = database.rawQuery("select * from TRANSACTION_", null);
        ArrayList<Transaction> transaction = new ArrayList<>();

        SimpleDateFormat format_ = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Date date = new Date();
                try {
                    date = format_.parse(res.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountNo = res.getString(1);
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                double amount = res.getDouble(3);
                transaction.add(new Transaction(date, accountNo, expenseType, amount));
            }
        }
        return transaction;
    }

    public List<Transaction> getLimitedTransactions(int limit) {
        SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor res = database.rawQuery("select * from TRANSACTION_ limit " + limit, null);
        ArrayList<Transaction> transaction = new ArrayList<>();

        SimpleDateFormat format_ = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Date date = new Date();
                try {
                    date = format_.parse(res.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountNo = res.getString(1);
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                double amount = res.getDouble(3);
                transaction.add(new Transaction(date, accountNo, expenseType, amount));
            }
        }
        return transaction;
    }
}