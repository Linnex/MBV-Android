package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.WalletAccountsDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 15/03/16.
 */
public class WalletAccountsBaseAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<WalletAccountsDAO> walletAccountsDAOs = new ArrayList<>();

    public WalletAccountsBaseAdapter(Context contenxt) {
        this.context = contenxt;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<WalletAccountsDAO> data) {
        walletAccountsDAOs = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return walletAccountsDAOs.size();
    }

    @Override
    public WalletAccountsDAO getItem(int position) {
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
            convertView = layoutInflater.inflate(R.layout.adapter_wallet_account, parent, false);
            holder.bankName = (TextView) convertView.findViewById(R.id.adapter_account_bank_name);
            holder.accountNumber = (TextView) convertView.findViewById(R.id.adapter_account_id);
            holder.primary = (TextView) convertView.findViewById(R.id.adapter_account_primary);
            holder.isVerfied = (ImageView) convertView.findViewById(R.id.adapter_account_verified_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String text = "";
        holder.bankName.setText(walletAccountsDAOs.get(position).getBankCodeDAO().getBankName());
        holder.accountNumber.setText(walletAccountsDAOs.get(position).getAccountId());
        if(walletAccountsDAOs.get(position).getIsReceiveVerified()
                && walletAccountsDAOs.get(position).getIsSendVerified()) {
            text += "Verified";
            holder.isVerfied.setImageResource(R.drawable.ic_done_all_white_18dp);
        } else {
            text += "Pending Verification";
            holder.isVerfied.setImageResource(R.drawable.ic_error_outline_white_18dp);
        }
        if(walletAccountsDAOs.get(position).getIsPrimary()) {
            text += " | Primary";
        }
        holder.primary.setText(text);
        return convertView;
    }

    static class ViewHolder {
        TextView bankName, accountNumber, primary;
        ImageView isVerfied;
    }
}
