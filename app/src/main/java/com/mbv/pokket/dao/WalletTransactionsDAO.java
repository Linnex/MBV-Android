package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.enums.Status;
import com.mbv.pokket.dao.enums.TransactionType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

/**
 * Created by arindamnath on 15/03/16.
 */
public class WalletTransactionsDAO extends BaseDAO {

    private Long amount;

    private TransactionType transactionType;

    private Long date;

    private Status status;

    public WalletTransactionsDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setAmount((Long) jsonObject.get("amount"));
        setTransactionType(TransactionType.valueOf(jsonObject.get("type").toString()));
        setStatus(Status.valueOf(jsonObject.get("status").toString()));
        setDate((Long) jsonObject.get("createdOn"));
    }

    public String getAmount() {
        return getContext().getString(R.string.rupee) + amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        switch (transactionType) {
            case LOAN_RECEIVE:
                return "Loan";
            case LOAN_SEND:
                return "Loan";
            case PAYMENT_RECEIVE:
                return "Loan";
            case PAYMENT_SEND:
                return "Loan";
            case WALLET:
                return "Wallet";
            case VALIDITY_RECEIVE:
                return "Verification";
            case VALIDITY_SEND:
                return "Verification";
            default:
                return "Wallet";
        }
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDate() {
        return getDateFormat().format(new Date(date));
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
