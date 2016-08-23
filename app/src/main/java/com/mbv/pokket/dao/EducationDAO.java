package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.dao.enums.EducationDegreeType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by arindamnath on 27/02/16.
 */
public class EducationDAO extends BaseDAO {

    private String instituteName;
    private EducationDegreeType educationDegreeType;
    private Long degreeId;
    private String degreeName;
    private String description;
    private Long startDate;
    private Long passDate;
    private String city;
    private String state;
    private String country;
    private Long pincode;
    private Double score;
    private String reportUrl;
    private boolean isVerified;

    public EducationDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setId((Long) jsonObject.get("id"));
        setInstituteName(jsonObject.get("institutionName").toString());
        setEducationDegreeType(EducationDegreeType.valueOf(jsonObject.get("degreeType").toString()));
        setDegreeId((Long) jsonObject.get("degreeCategory"));
        setDegreeName(jsonObject.get("degreeCategoryName").toString());
        setDescription(jsonObject.get("description").toString());
        setStartDate((Long) jsonObject.get("startDate"));
        setPassDate((Long) jsonObject.get("endDate"));
        setCity(jsonObject.get("city").toString());
        setCountry(jsonObject.get("country").toString());
        setState(jsonObject.get("state").toString());
        setPincode((Long) jsonObject.get("pincode"));
        if(jsonObject.get("score") != null) {
            setScore((Double) jsonObject.get("score"));
        }
        if(jsonObject.get("reportUrl") != null) {
            setReportUrl(jsonObject.get("reportUrl").toString());
        }
        setIsVerified((Boolean) jsonObject.get("isVerified"));
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public EducationDegreeType getEducationDegreeType() {
        return educationDegreeType;
    }

    public void setEducationDegreeType(EducationDegreeType educationDegreeType) {
        this.educationDegreeType = educationDegreeType;
    }

    public Long getDegreeId() {
        return degreeId;
    }

    public void setDegreeId(Long degreeId) {
        this.degreeId = degreeId;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public String getStartDate() {
        return getMonthYearFromat().format(startDate);
    }

    public Long getStartDateAsLong() {
        return startDate;
    }


    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public String getPassDate() {
        return getMonthYearFromat().format(passDate);
    }

    public Long getPassDateAsLong() {
        return passDate;
    }

    public void setPassDate(Long passDate) {
        this.passDate = passDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getPincode() {
        return pincode;
    }

    public void setPincode(Long pincode) {
        this.pincode = pincode;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
