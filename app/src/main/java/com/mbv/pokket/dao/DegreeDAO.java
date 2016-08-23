package com.mbv.pokket.dao;

import android.content.Context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by arindamnath on 16/03/16.
 */
public class DegreeDAO extends BaseDAO {

    private String name;

    public DegreeDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("id"));
        setName(jsonObject.get("categoryName").toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
