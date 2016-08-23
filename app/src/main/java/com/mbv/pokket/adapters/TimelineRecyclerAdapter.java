package com.mbv.pokket.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mbv.pokket.ActivityLoanDetails;
import com.mbv.pokket.R;
import com.mbv.pokket.ActivityViewUserProfile;
import com.mbv.pokket.dao.TimelineDAO;
import com.mbv.pokket.dao.enums.UserTimelineType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 15/03/16.
 */
public class TimelineRecyclerAdapter extends RecyclerView.Adapter<TimelineRecyclerAdapter.MyViewHolder> {

    private List<TimelineDAO> timelineDAOs = new ArrayList<>();
    private Activity activity;

    public TimelineRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_timeline,
                viewGroup, false);
        return new MyViewHolder(view);
    }

    public void setData(List<TimelineDAO> data) {
        timelineDAOs = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(activity != null) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimelineDAO timelineDAO = timelineDAOs.get(position);
                    if (timelineDAO.getTimelineEvents() == UserTimelineType.PROFILE_UPDATE
                            || timelineDAO.getTimelineEvents() == UserTimelineType.RATING_GIVEN
                            || timelineDAO.getTimelineEvents() == UserTimelineType.RATING_RECEIVED) {
                        Intent userProfile = new Intent(activity, ActivityViewUserProfile.class);
                        userProfile.putExtra("userId", timelineDAO.getProfileID());
                        //v.getContext().startActivity(userProfile);
                    } else if (timelineDAO.getTimelineEvents() == UserTimelineType.DEFAULTED
                            || timelineDAO.getTimelineEvents() == UserTimelineType.APPROVED_LOAN
                            || timelineDAO.getTimelineEvents() == UserTimelineType.LOAN_REQUESTED
                            || timelineDAO.getTimelineEvents() == UserTimelineType.LOAN_APPROVED) {
                        Intent loanDetails = new Intent(activity, ActivityLoanDetails.class);
                        loanDetails.putExtra("loanId", timelineDAO.getLoanID());
                        v.getContext().startActivity(loanDetails);
                    }
                }
            });
        }
        holder.content.setText(timelineDAOs.get(position).getContent());
        holder.date.setText(timelineDAOs.get(position).getDate());
        holder.typeImage.setImageResource(timelineDAOs.get(position).getTypeImage());
        holder.typeImage.setBackgroundResource(timelineDAOs.get(position).getTypeImageBG());
    }

    @Override
    public int getItemCount() {
        return timelineDAOs == null ? 0 : timelineDAOs.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView content, date;
        ImageView typeImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView.findViewById(R.id.adapter_timeline_container);
            content = (TextView) itemView.findViewById(R.id.adapter_timeline_content);
            date = (TextView) itemView.findViewById(R.id.adapter_timeline_date);
            typeImage = (ImageView) itemView.findViewById(R.id.adapter_timeline_type_image);
        }
    }
}
