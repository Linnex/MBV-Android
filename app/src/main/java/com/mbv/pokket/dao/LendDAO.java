package com.mbv.pokket.dao;

import android.content.Context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

/**
 * Created by arindamnath on 13/01/16.
 */
public class LendDAO extends BaseDAO {

    private String name;
    private String imageURL;
    private Long amount;
    private Long tenure;
    private Long postTime;

    public LendDAO(Context context) {
        super(context);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenure() {
        return String.valueOf(tenure) + ((tenure > 1) ? "Months" : "Month");
    }

    public void setTenure(Long tenure) {
        this.tenure = tenure;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getPostTime() {
        return getDateFormat().format(new Date(postTime));
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("id"));
        setAmount((Long) jsonObject.get("amount"));
        setTenure((Long) jsonObject.get("period"));
        setPostTime((Long) jsonObject.get("createdOn"));
        if(jsonObject.containsKey("userData")) {
            JSONObject user = (JSONObject) jsonObject.get("userData");
            setName(user.get("name").toString());
            setImageURL(user.get("imageURL").toString());
        }
    }
}
