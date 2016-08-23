package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by arindamnath on 03/02/16.
 */
public class CalendarEventDAO extends BaseDAO {

    private String loanID;
    private Long date;
    private Long amount;

    public CalendarEventDAO(Context context) {
        super(context);
    }

    public String getLoanID() {
        return getContext().getResources().getString(R.string.ref_id) + " " + loanID;
    }

    public void setLoanID(String loanID) {
        this.loanID = loanID;
    }

    public String getDate() {
        try {
            return getDate(date);
        } catch (Exception e) {
            return "";
        }
    }

    public String getDateOfMonth() {
        try {
            return getDateOfTheMonth().format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getAmount() {
        return getContext().getString(R.string.rupee) + getAmountFormatter().format(amount);
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("loanId"));
        setLoanID(jsonObject.get("loanRefId").toString());
        setDate((Long) jsonObject.get("date"));
        setAmount((Long) jsonObject.get("amount"));
    }
}
