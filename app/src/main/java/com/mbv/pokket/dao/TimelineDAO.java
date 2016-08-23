package com.mbv.pokket.dao;

import android.content.Context;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.enums.UserTimelineType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by arindamnath on 07/02/16.
 */
public class TimelineDAO extends BaseDAO {

    private UserTimelineType timelineEvents;
    private String content;
    private Long loanID;
    private Long profileID;
    private Long timestamp;

    public TimelineDAO(Context context) {
        super(context);
    }

    @Override
    public void parse(JSONParser jsonParser, JSONObject jsonObject) {
        setTimelineEvents(UserTimelineType.valueOf(jsonObject.get("userTimelineType").toString()));
        setId((Long) jsonObject.get("userId"));
        setContent(jsonObject.get("description").toString());
        setLoanID((jsonObject.get("referenceLoanId") != null) ?
                (Long) jsonObject.get("referenceLoanId") : -1l);
        setProfileID((jsonObject.get("referenceUserId") != null) ?
                (Long) jsonObject.get("referenceUserId") : -1l);
        setTimestamp((Long) jsonObject.get("createdOn"));
    }

    public void setTimelineEvents(UserTimelineType timelineEvents) {
        this.timelineEvents = timelineEvents;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLoanID(Long loanID) {
        this.loanID = loanID;
    }

    public void setProfileID(Long profileID) {
        this.profileID = profileID;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate(){
        try {
            return getDate(timestamp);
        } catch (Exception e) {
            return "";
        }
    }

    public String getContent() {
        return content;
    }

    public Long getLoanID() {
        return loanID;
    }

    public Long getProfileID() {
        return profileID;
    }

    public int getTypeImage() {
        switch (timelineEvents) {
            case PROFILE_UPDATE:
                return R.drawable.ic_face_white_24dp;
            case APPROVED_LOAN:
                return R.drawable.ic_gavel_white_48dp;
            case DEFAULTED:
                return R.drawable.ic_error_outline_white_18dp;
            case RATING_RECEIVED:
                return R.drawable.ic_thumbs_up_down_white_48dp;
            case LOAN_APPROVED:
                return R.drawable.ic_gavel_white_48dp;
            case LOAN_REQUESTED:
                return R.drawable.ic_play_for_work_white_48dp;
            case RATING_GIVEN:
                return R.drawable.ic_thumbs_up_down_white_48dp;
            case WALLET:
                return R.drawable.ic_account_balance_wallet_black_48dp;
            default:
                return 0;
        }
    }

    public int getTypeImageBG() {
        switch (timelineEvents) {
            case PROFILE_UPDATE:
                return R.drawable.shape_oval_orange;
            case APPROVED_LOAN:
                return R.drawable.shape_oval_purple;
            case DEFAULTED:
                return R.drawable.shape_oval_red;
            case RATING_RECEIVED:
                return R.drawable.shape_oval_color_primary;
            case LOAN_APPROVED:
                return R.drawable.shape_oval_blue;
            case LOAN_REQUESTED:
                return R.drawable.shape_oval_blue;
            case RATING_GIVEN:
                return R.drawable.shape_oval_color_primary;
            case WALLET:
                return R.drawable.shape_oval_blue;
            default:
                return R.drawable.shape_oval_orange;
        }
    }

    public UserTimelineType getTimelineEvents() {
        return timelineEvents;
    }
}
