package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.enums.LoanStatus;
import com.mbv.pokket.dao.enums.Status;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

/**
 * Created by arindamnath on 13/01/16.
 */
public class RepaymentDAO extends BaseDAO {

    public Long date;
    public String tenure;
    public Long amount;
    private Status status;

    public RepaymentDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("loanId"));
        setAmount((Long) jsonObject.get("amount"));
        setDate((Long) jsonObject.get("date"));
        setStatus(Status.valueOf(jsonObject.get("status").toString()));
    }

    public String getDate() {
        return getDateFormat().format(new Date(date));
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAmount() {
        return getContext().getString(R.string.rupee) + getAmountFormatter().format(amount);
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
