package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.CalendarEventDAO;
import com.mbv.pokket.dao.LendDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 14/01/16.
 */
public class TransactionHistoryAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<CalendarEventDAO> calendarEventDAOs = new ArrayList<>();

    public TransactionHistoryAdapter(Context contenxt) {
        this.context = contenxt;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<CalendarEventDAO> data) {
        calendarEventDAOs = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return calendarEventDAOs.size();
    }

    @Override
    public CalendarEventDAO getItem(int position) {
        return calendarEventDAOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.adapter_dashboard_events, parent, false);
            holder.date = (TextView) convertView.findViewById(R.id.adapter_dashboard_repayment_date_text);
            holder.loanId = (TextView) convertView.findViewById(R.id.adapter_dashboard_loan_id);
            holder.amount = (TextView) convertView.findViewById(R.id.adapter_dashboard_repayment_value);
            holder.dateOfMonth = (TextView) convertView.findViewById(R.id.adapter_dashboard_loan_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.date.setText(calendarEventDAOs.get(position).getDate());
        holder.loanId.setText(calendarEventDAOs.get(position).getLoanID());
        holder.amount.setText(calendarEventDAOs.get(position).getAmount());
        holder.dateOfMonth.setVisibility(View.GONE);
        return convertView;
    }

    static class ViewHolder {
        TextView date, loanId, amount, dateOfMonth;
    }
}
