package com.mbv.pokket.dao;

import android.content.Context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by arindamnath on 15/03/16.
 */
public class WalletAccountsDAO extends BaseDAO {

    private String accountId;

    private Long walletId;

    private Boolean isPrimary;

    private Boolean isSendVerified;

    private Boolean isReceiveVerified;

    private BankCodeDAO bankCodeDAO;

    public WalletAccountsDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("id"));
        setAccountId(jsonObject.get("bankAccount").toString());
        setWalletId((Long) jsonObject.get("walletId"));
        setIsPrimary((Boolean) jsonObject.get("isPrimary"));
        setIsSendVerified((Boolean) jsonObject.get("isSendVerified"));
        setIsReceiveVerified((Boolean) jsonObject.get("isReceiveVerified"));
        bankCodeDAO = new BankCodeDAO(getContext());
        bankCodeDAO.parse(jsonParser, (JSONObject) jsonObject.get("bankIFSCData"));
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Boolean getIsSendVerified() {
        return isSendVerified;
    }

    public void setIsSendVerified(Boolean isSendVerified) {
        this.isSendVerified = isSendVerified;
    }

    public Boolean getIsReceiveVerified() {
        return isReceiveVerified;
    }

    public void setIsReceiveVerified(Boolean isReceiveVerified) {
        this.isReceiveVerified = isReceiveVerified;
    }

    public BankCodeDAO getBankCodeDAO() {
        return bankCodeDAO;
    }

    public void setBankCodeDAO(BankCodeDAO bankCodeDAO) {
        this.bankCodeDAO = bankCodeDAO;
    }
}
