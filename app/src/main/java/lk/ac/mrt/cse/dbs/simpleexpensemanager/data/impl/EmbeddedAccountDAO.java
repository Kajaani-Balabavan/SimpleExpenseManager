package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class EmbeddedAccountDAO implements AccountDAO {
    private final dbHandler dbHandler;

    public EmbeddedAccountDAO(Context context) {
        dbHandler = new dbHandler(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> accNo = new ArrayList<>();
        List<Account> account = dbHandler.Accounts();
        if(account.size()==0){
            return accNo;
        }else {
            for(Account a:account){
                accNo.add(a.getAccountNo());
            }
        }
        return accNo;
    }

    @Override
    public List<Account> getAccountsList() {
        return dbHandler.Accounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return dbHandler.accountDetails(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        dbHandler.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        dbHandler.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if(accountNo ==null){
            throw new InvalidAccountException("Invalid Account Number");
        }

        Account account = dbHandler.accountDetails(accountNo);
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        if(account.getBalance()<0 ){
            throw new InvalidAccountException("Insufficient balance");
        }
        else{
            dbHandler.updateAccount(account);
        }
    }
}
