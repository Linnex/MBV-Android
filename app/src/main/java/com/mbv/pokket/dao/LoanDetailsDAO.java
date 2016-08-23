package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.enums.RoleType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by arindamnath on 18/01/16.
 */
public class LoanDetailsDAO extends BaseDAO {

    private Long borrowerId;
    private Long lenderId;
    private String loanId;
    private String borrowerName;
    private String lenderName;
    private Long loanAmt;
    private Long interest;
    private Double appUsageFee;
    private Double serviceTax;
    private String status;
    private String loanTenure;
    private Long issuedDate;
    private RoleType roleType;
    private List<RepaymentDAO> repaymentDAO = new ArrayList<>();

    public LoanDetailsDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setLoanAmt((Long) jsonObject.get("amount"));
        if(jsonObject.get("roleType") != null) {
            setRoleType(RoleType.valueOf(jsonObject.get("roleType").toString()));
        }
        setStatus(jsonObject.get("status").toString());
        setLoanTenure(jsonObject.get("period").toString() + " Months");
        setIssuedDate((Long) jsonObject.get("requestDate"));
        setLoanId(jsonObject.get("loanId").toString());
        if(jsonObject.get("loanBorrowerInfo") != null) {
            JSONObject borrower = (JSONObject) jsonObject.get("loanBorrowerInfo");
            setBorrowerName(borrower.get("name").toString());
            setBorrowerId((Long) borrower.get("id"));
        }
        if(jsonObject.get("loanLenderInfo") != null) {
            JSONObject lender = (JSONObject) jsonObject.get("loanLenderInfo");
            setLenderName(lender.get("name").toString());
            setLenderId((Long) lender.get("id"));
        }
        if(jsonObject.get("loanInterestData") != null) {
            JSONObject interestInfo = (JSONObject) jsonObject.get("loanInterestData");
            setInterest((Long) interestInfo.get("interest"));
            setAppUsageFee((Double) interestInfo.get("appUsageFee"));
            setServiceTax((Double) interestInfo.get("serviceTax"));
        }
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public String getLoanAmt() {
        return getContext().getString(R.string.rupee) + String.valueOf(loanAmt);
    }

    public void setLoanAmt(Long loanAmt) {
        this.loanAmt = loanAmt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoanTenure() {
        return loanTenure;
    }

    public void setLoanTenure(String loanTenure) {
        this.loanTenure = loanTenure;
    }

    public String getIssuedDate() {
        return getDateFormat().format(new Date(issuedDate));
    }

    public void setIssuedDate(Long issuedDate) {
        this.issuedDate = issuedDate;
    }

    public List<RepaymentDAO> getRepaymentDAO() {
        return repaymentDAO;
    }

    public void setRepaymentDAO(List<RepaymentDAO> repaymentDAO) {
        this.repaymentDAO = repaymentDAO;
    }

    public String getLoanId() {
        return "Ref Id: " + loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public Long getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Long borrowerId) {
        this.borrowerId = borrowerId;
    }

    public Long getLenderId() {
        return lenderId;
    }

    public void setLenderId(Long lenderId) {
        this.lenderId = lenderId;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Long getLoanAmount() {
        return loanAmt;
    }

    public String getInterest() {
        return getContext().getString(R.string.rupee) + String.valueOf(interest);
    }

    public void setInterest(Long interest) {
        this.interest = interest;
    }

    public String getLenderTotalAmt() {
        return getContext().getString(R.string.rupee) + String.valueOf(loanAmt + interest);
    }

    public String getAppUsageFee() {
        return getContext().getString(R.string.rupee) + String.valueOf(appUsageFee);
    }

    public void setAppUsageFee(Double appUsageFee) {
        this.appUsageFee = appUsageFee;
    }

    public String getServiceTax() {
        return getContext().getString(R.string.rupee) + String.valueOf(serviceTax);
    }

    public void setServiceTax(Double serviceTax) {
        this.serviceTax = serviceTax;
    }
}
