package com.mbv.pokket.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mbv.pokket.dao.enums.RoleType;

/**
 * Created by arindamnath on 27/12/15.
 */
public class AppPreferences {

    private SharedPreferences mAppPrefs;

    public AppPreferences(Context context) {
        mAppPrefs = context.getSharedPreferences("AppPreferences", 0);
    }

    public void saveUserInfo(String name, String email, String phone, String gender,
                             String maritalStatus, String workStatus, String residentialStatus,
                             long dob, long id, String fatherName) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        String[] userName = name.split("\\s+");
        if(userName.length == 1) {
            edit.putString("userFirstName", userName[0]);
            edit.putString("userMiddleName", "");
            edit.putString("userLastName", "");
        } else if(userName.length == 2) {
            edit.putString("userFirstName", userName[0]);
            edit.putString("userMiddleName", "");
            edit.putString("userLastName", userName[1]);
        } else if(userName.length > 2) {
            edit.putString("userFirstName", userName[0]);
            edit.putString("userMiddleName", userName[1]);
            edit.putString("userLastName", userName[2]);
        }
        edit.putString("userEmail", email);
        edit.putString("userPhone", phone);
        edit.putString("userGender", gender);
        edit.putString("userMaritalStatus", maritalStatus);
        edit.putString("userWorkStatus", workStatus);
        edit.putString("userResidentialStatus", residentialStatus);
        edit.putLong("userDOB", dob);
        edit.putLong("userId", id);
        edit.putString("userFather", fatherName);
        edit.commit();
    }

    public void updateUserInfo(String name, String gender, String maritalStatus, String workStatus,
                               String residentialStatus, long dob, String fatherName) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        String[] userName = name.split("\\s+");
        if(userName.length == 1) {
            edit.putString("userFirstName", userName[0]);
            edit.putString("userMiddleName", "");
            edit.putString("userLastName", "");
        } else if(userName.length == 2) {
            edit.putString("userFirstName", userName[0]);
            edit.putString("userMiddleName", "");
            edit.putString("userLastName", userName[1]);
        } else if(userName.length > 2) {
            edit.putString("userFirstName", userName[0]);
            edit.putString("userMiddleName", userName[1]);
            edit.putString("userLastName", userName[2]);
        }
        edit.putString("userGender", gender);
        edit.putString("userMaritalStatus", maritalStatus);
        edit.putString("userWorkStatus", workStatus);
        edit.putString("userResidentialStatus", residentialStatus);
        edit.putLong("userDOB", dob);
        edit.putString("userFather", fatherName);
        edit.commit();
    }

    public void saveHomeAddress(Long id, String address, String state, String city, String country,
                                String pincode, String type) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putLong("homeAddressId", id);
        edit.putString("homeAddressStreet", address);
        edit.putString("homeAddressState", state);
        edit.putString("homeAddressCity", city);
        edit.putString("homeAddressCountry", country);
        edit.putString("homeAddressPincode", pincode);
        edit.putString("homeAddressType", type);
        edit.commit();
    }

    public String[] getHomeAddress() {
        if(mAppPrefs.getLong("homeAddressId", -1l) != -1l) {
            String[] data = new String[7];
            data[0] = mAppPrefs.getString("homeAddressStreet", "");
            data[1] = mAppPrefs.getString("homeAddressState", "");
            data[2] = mAppPrefs.getString("homeAddressCity", "");
            data[3] = mAppPrefs.getString("homeAddressCountry", "");
            data[4] = mAppPrefs.getString("homeAddressType", "");
            data[5] = mAppPrefs.getString("homeAddressPincode", "");
            return data;
        } else {
            return null;
        }
    }

    public void saveCurrentAddress(Long id, String address, String state, String city, String country,
                                String pincode, String type) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putLong("currentAddressId", id);
        edit.putString("currentAddressStreet", address);
        edit.putString("currentAddressState", state);
        edit.putString("currentAddressCity", city);
        edit.putString("currentAddressCountry", country);
        edit.putString("currentAddressPincode", pincode);
        edit.putString("currentAddressType", type);
        edit.commit();
    }

    public String[] getCurrentAddress() {
        if(mAppPrefs.getLong("currentAddressId", -1l) != -1l) {
            String[] data = new String[7];
            data[0] = mAppPrefs.getString("currentAddressStreet", "");
            data[1] = mAppPrefs.getString("currentAddressState", "");
            data[2] = mAppPrefs.getString("currentAddressCity", "");
            data[3] = mAppPrefs.getString("currentAddressCountry", "");
            data[4] = mAppPrefs.getString("currentAddressType", "");
            data[5] = mAppPrefs.getString("currentAddressPincode", "");
            return data;
        } else {
            return null;
        }
    }

    public void saveUserImage(String imageURL) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putString("userImage", imageURL);
        edit.commit();
    }

    public void setUserRole(String role) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putString("userRole", role);
        edit.commit();
    }

    public void setUserToken(String token) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putString("userToken", token);
        edit.commit();
    }

    public void setWalletId(Long id) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putLong("setWalletId", id);
        edit.commit();
    }

    public String getUserImage() {
        return mAppPrefs.getString("userImage", null);
    }

    public String getUserFirstName() {
        return mAppPrefs.getString("userFirstName", null);
    }

    public String getUserMiddleName() {
        return mAppPrefs.getString("userMiddleName", null);
    }

    public String getUserLastName() {
        return mAppPrefs.getString("userLastName", null);
    }

    public String getUserEmail() {
        return mAppPrefs.getString("userEmail", null);
    }

    public String getUserPhone() {
        return mAppPrefs.getString("userPhone", null);
    }

    public String getUserFatherName() {
        return mAppPrefs.getString("userFather", null);
    }

    public String getUserGender() {
        return mAppPrefs.getString("userGender", null);
    }

    public String getUserMaritalStatus() {
        return mAppPrefs.getString("userMaritalStatus", null);
    }

    public String getUserWorkStatus() {
        return mAppPrefs.getString("userWorkStatus", null);
    }

    public String getUserResidientialStatus() {
        return mAppPrefs.getString("userResidentialStatus", null);
    }

    public String getUserToken() {
        return mAppPrefs.getString("userToken", null);
    }

    public RoleType getUserRole() {
        return RoleType.valueOf(mAppPrefs.getString("userRole", "ALL"));
    }

    public Long getUserId() {
        return mAppPrefs.getLong("userId", -1l);
    }

    public Long getWalletId() {
        return mAppPrefs.getLong("setWalletId", -1l);
    }

    public Long getUserDOB() {
        return mAppPrefs.getLong("userDOB", -1l);
    }

    public Long getHomeLocationId() {
        return mAppPrefs.getLong("homeAddressId", -1l);
    }

    public Long getCurrentLocationId() {
        return mAppPrefs.getLong("currentAddressId", -1l);
    }

    public void setLoggedIn() {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putBoolean("appLoggedIn", true);
        edit.commit();
    }

    public void setLoggedOut() {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.clear();
        edit.commit();
    }

    public void setSignUpActive() {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putBoolean("appSignUp", true);
        edit.commit();
    }

    public void setSignUpComplete() {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putBoolean("appSignUp", false);
        edit.commit();
    }

    public void setSignUpStep(int i) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putInt("appSignUpStep", i);
        edit.commit();
    }

    public void setGCMId(String id) {
        SharedPreferences.Editor edit = mAppPrefs.edit();
        edit.putString("appGCMId", id);
        edit.commit();
    }

    public String getGCMId() {
        return mAppPrefs.getString("appGCMId", null);
    }

    public boolean isSignUpActive() {
        return mAppPrefs.getBoolean("appSignUp", false);
    }

    public int getSignUpStep() {
        return mAppPrefs.getInt("appSignUpStep", -1);
    }

    public boolean isUserLoggedIn() {
        return mAppPrefs.getBoolean("appLoggedIn", false);
    }
}
