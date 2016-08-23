package com.mbv.pokket.dao;

import android.content.Context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by arindamnath on 15/03/16.
 */
public class BankCodeDAO extends BaseDAO {

    private String ifscCode;

    private String bankName;

    private String bankBranch;

    private String bankAddress;

    private String bankCity;

    private String bankDistrict;

    private String bankState;

    private String bankLogoUrl;

    public BankCodeDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("id"));
        setIfscCode(jsonObject.get("ifscCode").toString());
        setBankName(jsonObject.get("bankName").toString());
        setBankBranch(jsonObject.get("bankBranch").toString());
        setBankAddress(jsonObject.get("bankAddress").toString());
        setBankCity(jsonObject.get("bankCity").toString());
        setBankDistrict(jsonObject.get("bankDistrict").toString());
        setBankState(jsonObject.get("bankState").toString());
        if(jsonObject.get("bankLogoUrl") != null) {
            setBankLogoUrl(jsonObject.get("bankLogoUrl").toString());
        }
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public String getBankDistrict() {
        return bankDistrict;
    }

    public void setBankDistrict(String bankDistrict) {
        this.bankDistrict = bankDistrict;
    }

    public String getBankState() {
        return bankState;
    }

    public void setBankState(String bankState) {
        this.bankState = bankState;
    }

    public String getBankLogoUrl() {
        return bankLogoUrl;
    }

    public void setBankLogoUrl(String bankLogoUrl) {
        this.bankLogoUrl = bankLogoUrl;
    }
}
