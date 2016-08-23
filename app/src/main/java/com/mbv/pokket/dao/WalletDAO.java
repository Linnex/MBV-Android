package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

/**
 * Created by arindamnath on 15/03/16.
 */
public class WalletDAO extends BaseDAO {

    private Double amount;

    private String walletId;

    public WalletDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("id"));
        setAmount((Double) jsonObject.get("amount"));
        setWalletId(jsonObject.get("walletId").toString());
    }

    public String getAmount() {
        return getContext().getString(R.string.rupee) + amount.toString();
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }
}
