package com.mbv.pokket.dao;

import android.content.Context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by arindamnath on 28/02/16.
 */
public class GeoLocation extends BaseDAO {

    private String description;
    private String geoId;
    private String placeId;
    private String reference;
    private String address;
    private String city;
    private String state;
    private String country;
    private Long pincode;

    public GeoLocation(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject response) {
        this.description = response.get("description").toString();
        this.geoId = response.get("id").toString();
        this.placeId = response.get("place_id").toString();
        this.reference = response.get("reference").toString();
        JSONArray terms = (JSONArray) response.get("terms");
        if(terms.size() > 3) {
            address = "";
            for (int i =  0; i < terms.size(); i++) {
                if(i == terms.size()-3) {
                    this.city = ((JSONObject) terms.get(i)).get("value").toString();
                } else if(i == terms.size()-2) {
                    this.state = ((JSONObject) terms.get(i)).get("value").toString();
                } else if(i == terms.size()-1) {
                    this.country = ((JSONObject) terms.get(i)).get("value").toString();
                } else {
                    address += ((JSONObject) terms.get(i)).get("value").toString() + ", ";
                }
            }
        } else if(terms.size() > 2) {
            this.city = ((JSONObject) terms.get(0)).get("value").toString();
            this.state = ((JSONObject) terms.get(1)).get("value").toString();
            this.country = ((JSONObject) terms.get(2)).get("value").toString();
        } else if(terms.size() > 1) {
            this.state = ((JSONObject) terms.get(0)).get("value").toString();
            this.country = ((JSONObject) terms.get(1)).get("value").toString();
        } else if(terms.size() > 0) {
            this.country = ((JSONObject) terms.get(0)).get("value").toString();
        }
    }

    public String getDescription() {
        return description;
    }

    public String getGeoId() {
        return geoId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getReference() {
        return reference;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public Long getPincode() {
        return pincode;
    }
}
