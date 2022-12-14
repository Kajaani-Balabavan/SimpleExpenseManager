package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
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
    private static final int VERSION = 1;
    private static final String TABLE1 = "ACCOUNT";
    private static final String TABLE2 = "TRANSACTION_";
    private static final String ACCOUNT_ACCOUNT_NO = "accountNo";
    private static final String ACCOUNT_BANK = "bankName";
    private static final String ACCOUNT_ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static final String ACCOUNT_BALANCE = "balance";
    private static final String TRANSACTION_DATE = "date";
    private static final String TRANSACTION_TYPE = "expenseType";
    private static final String TRANSACTION_AMOUNT = "amount";

    public dbHandler(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        String CREATE_TABLE1 = "CREATE TABLE " + TABLE1 +
//                " ( " + ACCOUNT_ACCOUNT_NO + " INTEGER PRIMARY KEY , " + ACCOUNT_BANK + " TEXT ," + ACCOUNT_ACCOUNT_HOLDER_NAME + " TEXT ," + ACCOUNT_BALANCE + " REAL);";
//        sqLiteDatabase.execSQL(CREATE_TABLE1);
//        String CREATE_TABLE2 = "CREATE TABLE " + TABLE2 +
//                " (" + TRANSACTION_DATE + " TEXT , " + ACCOUNT_ACCOUNT_NO + " INTEGER ," + TRANSACTION_TYPE + " TEXT ," + TRANSACTION_AMOUNT + " REAL);";
//        sqLiteDatabase.execSQL(CREATE_TABLE1);
//        sqLiteDatabase.execSQL(CREATE_TABLE2);
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
        //database.close();
    }

    public void addTransaction(Date date,String accountNumber, ExpenseType expenseType, double amount) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        DateFormat format_ = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
        data.put("accountNo", accountNumber);
        data.put("date", format_.format(date));
        data.put("expenseType",expenseType.toString()) ;
        data.put("amount", amount);
        database.insert("TRANSACTION", null, data);
        //database.close();
    }

    public void removeAccount(String accountNo) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("ACCOUNT", "accountNo =?", new String[]{accountNo});
        //database.close();
    }

    public void updateAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("accountNo", account.getAccountNo());
        data.put("bankName", account.getBankName());
        data.put("accountHolderName", account.getAccountHolderName());
        data.put("balance", account.getBalance());
        database.update("ACCOUNT",data, "accountNo =?", new String[]{account.getAccountNo()});
        //database.close();
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
        //database.close();
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
        //database.close();
        return accounts;
    }


    public List<Transaction> getTransactions() {
        SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor res = database.rawQuery("select * from TRANSACTION_", null);
        ArrayList<Transaction> transaction = new ArrayList<>();
        //@SuppressLint("SimpleDateFormat") DateFormat format_ = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat format_ = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);

        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Date date = new Date();
                try {
                    date = format_.parse(res.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountNo = res.getString(0);
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                double amount = res.getDouble(3);
                transaction.add(new Transaction(date, accountNo, expenseType, amount));
            }
        }
        //database.close();
        return transaction;
    }

    public List<Transaction> getLimitedTransactions(int limit) {
        SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor res = database.rawQuery("select * from TRANSACTION_ limit " + limit, null);
        ArrayList<Transaction> transaction = new ArrayList<>();
        //@SuppressLint("SimpleDateFormat") DateFormat format_ = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat format_ = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);

        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Date date = new Date();
                try {
                    date = format_.parse(res.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountNo = res.getString(0);
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                double amount = res.getDouble(3);
                transaction.add(new Transaction(date, accountNo, expenseType, amount));
            }
        }
        //database.close();
        return transaction;
    }
}