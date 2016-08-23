package com.mbv.pokket.dao.enums;

/**
 * Created by arindamnath on 24/02/16.
 */
public enum FeedbackType {
    GENERAL, LOAN, WALLET;
    public static FeedbackType valueOf(int ordinal) {
        FeedbackType retVal = null;
        for (FeedbackType deviceType : FeedbackType.values()) {
            if (deviceType.ordinal() == ordinal) {
                retVal = deviceType;
                break;
            }
        }
        return retVal;
    }
}
