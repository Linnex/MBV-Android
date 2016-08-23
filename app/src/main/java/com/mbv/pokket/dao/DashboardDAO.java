package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 01/02/16.
 */
public class DashboardDAO extends BaseDAO {

    private Float totalAmount;
    private Float borrowed;
    private Float lent;
    private List<CalendarEventDAO> calendarEventDAOArrayList = new ArrayList<>();

    public DashboardDAO(Context context) {
        super(context);
    }

    public String getTotalAmount() {
        return getContext().getString(R.string.rupee) + getAmountFormatter().format(totalAmount);
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBorrowed() {
        return getContext().getString(R.string.rupee) + getAmountFormatter().format(borrowed);
    }

    public void setBorrowed(Float borrowed) {
        this.borrowed = borrowed;
    }

    public String getLent() {
        return getContext().getString(R.string.rupee) + getAmountFormatter().format(lent);
    }

    public void setLent(Float lent) {
        this.lent = lent;
    }

    public List<CalendarEventDAO> getCalendarEventDAOArrayList() {
        return calendarEventDAOArrayList;
    }

    public void setCalendarEventDAOArrayList(List<CalendarEventDAO> calendarEventDAOArrayList) {
        this.calendarEventDAOArrayList = calendarEventDAOArrayList;
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {

    }
}
