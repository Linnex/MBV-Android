package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.WalletTransactionsDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 15/03/16.
 */
public class WalletTransactionBaseAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<WalletTransactionsDAO> walletAccountsDAOs = new ArrayList<>();

    public WalletTransactionBaseAdapter(Context contenxt) {
        this.context = contenxt;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<WalletTransactionsDAO> data) {
        walletAccountsDAOs = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return walletAccountsDAOs.size();
    }

    @Override
    public WalletTransactionsDAO getItem(int position) {
        return walletAccountsDAOs.get(position);
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
            convertView = layoutInflater.inflate(R.layout.adapter_wallet_transaction, parent, false);
            holder.date = (TextView) convertView.findViewById(R.id.adapter_wallet_transaction_date);
            holder.amount = (TextView) convertView.findViewById(R.id.adapter_wallet_transaction_amount);
            holder.status = (TextView) convertView.findViewById(R.id.adapter_wallet_transaction_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.date.setText(walletAccountsDAOs.get(position).getDate());
        holder.amount.setText(walletAccountsDAOs.get(position).getAmount());
        String text = walletAccountsDAOs.get(position).getStatus().toString() + " | " + walletAccountsDAOs.get(position).getTransactionType();
        holder.status.setText(text);
        return convertView;
    }

    static class ViewHolder {
        TextView date, amount, status;
    }
}
