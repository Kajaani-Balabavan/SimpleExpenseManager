package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.EmbeddedAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.EmbeddedTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.dbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentExpenseManager extends ExpenseManager{
    Context context;
    public PersistentExpenseManager(Context context) throws ExpenseManagerException {
        this.context=context;
        setup();
    }

    @Override
    public void setup(){

        TransactionDAO embeddedTransactionDAO = new EmbeddedTransactionDAO(context);
        setTransactionsDAO(embeddedTransactionDAO);

        AccountDAO embeddedAccountDAO = new EmbeddedAccountDAO(context);
        setAccountsDAO(embeddedAccountDAO);

        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

    }
}
