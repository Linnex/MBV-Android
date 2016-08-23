package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.dao.enums.Gender;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by arindamnath on 07/02/16.
 */
public class UserProfileDAO extends BaseDAO {

    private List<TimelineDAO> timelineDAOList = new ArrayList<>();

    private String name;

    private String imageUrl;

    private Gender gender;

    private Long dob;

    public UserProfileDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setName(jsonObject.get("name").toString());
        setImageUrl(jsonObject.get("userImage").toString());
        setId((Long) jsonObject.get("id"));
        setGender(Gender.valueOf(jsonObject.get("gender").toString()));
        setDob((Long) jsonObject.get("dob"));
    }

    public List<TimelineDAO> getTimelineDAOList() {
        return timelineDAOList;
    }

    public void setTimelineDAOList(List<TimelineDAO> timelineDAOList) {
        this.timelineDAOList = timelineDAOList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getDob() {
        return getDateFormat().format(new Date(dob));
    }

    public void setDob(Long dob) {
        this.dob = dob;
    }
}
