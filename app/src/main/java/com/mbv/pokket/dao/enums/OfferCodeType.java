package com.mbv.pokket.dao.enums;

/**
 * Created by arindamnath on 04/03/16.
 */
public enum OfferCodeType {
    BORROWER, LENDER;
    public static OfferCodeType valueOf(int ordinal) {
        OfferCodeType retVal = null;
        for (OfferCodeType deviceType : OfferCodeType.values()) {
            if (deviceType.ordinal() == ordinal) {
                retVal = deviceType;
                break;
            }
        }
        return retVal;
    }
}
