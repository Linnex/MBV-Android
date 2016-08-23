package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.CalendarEventDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 01/02/16.
 */
public class EventsListBaseAdapter extends BaseAdapter {

    private static final int TYPE_EVENT = 0;
    private static final int TYPE_DIVIDER = 1;

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Object> calendarEventDAOs = new ArrayList<>();

    public EventsListBaseAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Object> data) {
        calendarEventDAOs = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return calendarEventDAOs == null ? 0 : calendarEventDAOs.size();
    }

    @Override
    public Object getItem(int position) {
        return calendarEventDAOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof CalendarEventDAO) {
            return TYPE_EVENT;
        }

        return TYPE_DIVIDER;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_EVENT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case TYPE_EVENT:
                    convertView = layoutInflater.inflate(R.layout.adapter_dashboard_events, parent, false);
                    holder.date = (TextView) convertView.findViewById(R.id.adapter_dashboard_repayment_date_text);
                    holder.loanId = (TextView) convertView.findViewById(R.id.adapter_dashboard_loan_id);
                    holder.amount = (TextView) convertView.findViewById(R.id.adapter_dashboard_repayment_value);
                    holder.dateOfMonth = (TextView) convertView.findViewById(R.id.adapter_dashboard_loan_date);
                    break;
                case TYPE_DIVIDER:
                    convertView = layoutInflater.inflate(R.layout.content_dashboard_header, parent, false);
                    holder.header = (TextView) convertView.findViewById(R.id.dashboard_header_text);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (type) {
            case TYPE_EVENT:
                CalendarEventDAO calendarEventDAO = (CalendarEventDAO)getItem(position);
                holder.date.setText(calendarEventDAO.getDate());
                holder.loanId.setText(calendarEventDAO.getLoanID());
                holder.amount.setText(calendarEventDAO.getAmount());
                holder.dateOfMonth.setText(calendarEventDAO.getDateOfMonth());
                break;
            case TYPE_DIVIDER:
                String titleString = (String)getItem(position);
                holder.header.setText(titleString);
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        TextView header, date, loanId, amount, dateOfMonth;
    }
}
