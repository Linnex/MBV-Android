package com.mbv.pokket.dao.constants;

/**
 * Created by arindamnath on 21/03/16.
 */
public class URLConstants {

    public static final String AUTH_URL = "authenticate/user";

    public static final String USER_PROFILE_UPDATE_URL = "user/%1$s/update";

    public static final String USER_PROFILE_UPDATE_GCM_URL = "user/%1$s/update/notification";

    public static final String USER_IMAGE_URL = "user/%1$s/upload/image/";

    public static final String APPROVE_LEND_REQUEST = "loans/user/%1$s/loan/%2$s/approve";

    public static final String SET_USER_ROLE = "user/%1$s/update/type/%2$s";

    public static final String LOAN_ELIGIBILITY = "loans/user/%1$s/type/%2$s/eligibility";

    public static final String OFFER_ELIGIBILITY = "loans/user/%1$s/offer/%2$s/eligibility";

    public static final String LOAN_DETAILS_URL = "loans/user/%1$s/details/loan/%2$s";

    public static final String EVENTS_URL = "loans/user/%1$s/events";

    public static final String DEGREE_URL = "education/user/categories/type/";

    public static final String OPEN_LOANS_URL = "loans/user/%1$s/open/requests";

    public static final String KYC_DETAILS_URL = "identity/user/%1$s/";

    public static final String TIMELINE_URL = "user/%1$s/timeline?offset=0";

    public static final String EDUCATION_URL = "education/user/%1$s/";

    public static final String USER_INFO_URL = "user/%1$s";

    public static final String WALLET_URL = "wallet/user/%1$s";

    public static final String WALLET_ACCOUNTS_URL = "wallet/user/%1$s/bank/accounts/";

    public static final String WALLET_TRANSACTIONS_URL = "wallet/user/%1$s/transactions/status/ALL";

    public static final String LOAN_TRANSACTION_HISTORY = "loans/user/%1$s/history/type/ALL/status/";

    public static final String IFSC_CODE_URL = "wallet/banks?searchField=ifscCode&searchText=";
}
