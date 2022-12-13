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
        String CREATE_TABLE1 = "CREATE TABLE " + TABLE1 +
                " ( " + ACCOUNT_ACCOUNT_NO + " INTEGER PRIMARY KEY , " + ACCOUNT_BANK + " TEXT ," + ACCOUNT_ACCOUNT_HOLDER_NAME + " TEXT ," + ACCOUNT_BALANCE + " REAL);";
        sqLiteDatabase.execSQL(CREATE_TABLE1);
        String CREATE_TABLE2 = "CREATE TABLE " + TABLE2 +
                " (" + TRANSACTION_DATE + " TEXT , " + ACCOUNT_ACCOUNT_NO + " INTEGER ," + TRANSACTION_TYPE + " TEXT ," + TRANSACTION_AMOUNT + " REAL);";
        sqLiteDatabase.execSQL(CREATE_TABLE1);
        sqLiteDatabase.execSQL(CREATE_TABLE2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ACCOUNT");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TRANSACTION_");
        onCreate(sqLiteDatabase);
    }

    public void addAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(ACCOUNT_ACCOUNT_NO, account.getAccountNo());
        data.put(ACCOUNT_BANK, account.getBankName());
        data.put(ACCOUNT_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        data.put(ACCOUNT_BALANCE, account.getBalance());
        database.insert(TABLE1, null, data);
        database.close();
    }

    public void addTransaction(String date,String accountNumber, ExpenseType expenseType, double amount) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(TRANSACTION_DATE, date);
        data.put(ACCOUNT_ACCOUNT_NO, accountNumber);
        data.put(TRANSACTION_TYPE, amount);
        data.put(TRANSACTION_AMOUNT, expenseType.toString());
        database.insert(TABLE2, null, data);
        database.close();
    }

    public void removeAccount(String accountNo) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE1, ACCOUNT_ACCOUNT_NO + "=?", new String[]{accountNo});
        database.close();
    }

    public void updateAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(ACCOUNT_ACCOUNT_NO, account.getAccountNo());
        data.put(ACCOUNT_BANK, account.getBankName());
        data.put(ACCOUNT_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        data.put(ACCOUNT_BALANCE, account.getBalance());
        database.update(TABLE1, data, ACCOUNT_ACCOUNT_NO + "=?", new String[]{account.getAccountNo()});
        database.close();
    }

    public Account accountDetails(String accountNo) {
        SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor details = database.rawQuery("select * from " + TABLE1 + " where accountNo =?", new String[]{accountNo});

        Account account = null;
        if (details.getCount() != 0) {
            while (details.moveToNext()) {
                //String accountNo = details.getString(0);
                String bankName = details.getString(1);
                String accountHolderName = details.getString(2);
                double balance = details.getDouble(3);
                account = new Account(accountNo, bankName, accountHolderName, balance);
            }
        }
        return account;
    }

    public List<Account> Accounts() {
        List<Account> array_list = new ArrayList<Account>();

        //hp = new HashMap();
        SQLiteDatabase database = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = database.rawQuery("select * from " + TABLE1, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add((Account) res);
            res.moveToNext();
        }
        return array_list;
    }


    public List<Transaction> getTransactions() {
        SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor res = database.rawQuery("select * from " + TABLE2, null);
        ArrayList<Transaction> transaction = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") DateFormat format_ = new SimpleDateFormat("dd-MM-yyyy");
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
        @SuppressLint("Recycle") Cursor res = database.rawQuery("select * from " + TABLE2 + " limit " + limit, null);
        ArrayList<Transaction> transaction = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") DateFormat format_ = new SimpleDateFormat("dd-MM-yyyy");
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