package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.enums.KYCType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by arindamnath on 29/02/16.
 */
public class UserKycDAO extends BaseDAO{

    public Long userId;
    public String kycId;
    public KYCType kycType;
    public String imageUrl;
    public Boolean isVerified;

    public UserKycDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("id"));
        setUserId((Long) jsonObject.get("userId"));
        setKycId(jsonObject.get("kycId").toString());
        setKycType(KYCType.valueOf(jsonObject.get("type").toString()));
        setImageUrl(jsonObject.get("imageUrl").toString());
        setIsVerified((Boolean) jsonObject.get("isVerified"));
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKycId() {
        return kycId;
    }

    public void setKycId(String kycId) {
        this.kycId = kycId;
    }

    public KYCType getKycType() {
        return kycType;
    }

    public void setKycType(KYCType kycType) {
        this.kycType = kycType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public int getTypeImageBG() {
        switch (kycType) {
            case PAN:
                return R.drawable.shape_oval_color_primary;
            case PASSPORT:
                return R.drawable.shape_oval_purple;
            case ADHAAR:
                return R.drawable.shape_oval_red;
            case VOTER_ID:
                return R.drawable.shape_oval_color_primary;
            case STUDENT_ID:
                return R.drawable.shape_oval_blue;
            case BANK:
                return R.drawable.shape_oval_purple;
            default:
                return R.drawable.shape_oval_color_primary;
        }
    }

    public String getTypeText() {
        switch (kycType) {
            case PAN:
                return "Pan";
            case PASSPORT:
                return "Passport";
            case ADHAAR:
                return "Adhaar";
            case VOTER_ID:
                return "Voter\nId";
            case STUDENT_ID:
                return "Student\nId";
            case BANK:
                return "Bank\nAccount";
            default:
                return "";
        }
    }
}
