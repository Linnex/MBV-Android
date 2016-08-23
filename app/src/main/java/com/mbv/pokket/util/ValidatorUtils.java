package com.mbv.pokket.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by arindamnath on 26/02/16.
 */
public class ValidatorUtils {

    private final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final String PHONE_NUMBER_PATTERN = "\\d+";

    private Pattern pattern;
    private Matcher matcher;

    public ValidatorUtils() {

    }

    public boolean validateEmail(final String hex) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public boolean validateMobile(final String hex) {
        if(hex.length() == 10) {
            pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
            matcher = pattern.matcher(hex);
            return matcher.matches();
        } else {
            return false;
        }
    }

    public boolean validateFullname(final String hex) {
        String[] userName = hex.split("\\s+");
        if(userName.length > 1) {
            return true;
        } else {
            return false;
        }
    }
}
