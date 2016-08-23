package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.UserKycDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 29/02/16.
 */
public class KYCListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<UserKycDAO> kycDAOs = new ArrayList<>();

    public KYCListAdapter(Context contenxt) {
        this.context = contenxt;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<UserKycDAO> data) {
        kycDAOs = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return kycDAOs.size();
    }

    @Override
    public UserKycDAO getItem(int position) {
        return kycDAOs.get(position);
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
            convertView = layoutInflater.inflate(R.layout.adapter_kyc, parent, false);
            holder.type = (TextView) convertView.findViewById(R.id.adapter_kyc_type);
            holder.id = (TextView) convertView.findViewById(R.id.adapter_kyc_type_id);
            holder.address = (TextView) convertView.findViewById(R.id.adapter_kyc_proof_address);
            holder.identity = (TextView) convertView.findViewById(R.id.adapter_kyc_proof_identity);
            holder.student = (TextView) convertView.findViewById(R.id.adapter_kyc_proof_student);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.type.setText(kycDAOs.get(position).getTypeText().substring(0,1));
        holder.type.setBackgroundResource(kycDAOs.get(position).getTypeImageBG());
        holder.id.setText(kycDAOs.get(position).getTypeText() + " - " + kycDAOs.get(position).getKycId());
        switch (kycDAOs.get(position).getKycType()) {
            case PAN:
                holder.address.setVisibility(View.GONE);
                holder.identity.setVisibility(View.VISIBLE);
                holder.student.setVisibility(View.GONE);
                break;
            case PASSPORT:
                holder.address.setVisibility(View.VISIBLE);
                holder.identity.setVisibility(View.VISIBLE);
                holder.student.setVisibility(View.GONE);
                break;
            case ADHAAR:
                holder.address.setVisibility(View.VISIBLE);
                holder.identity.setVisibility(View.VISIBLE);
                holder.student.setVisibility(View.GONE);
                break;
            case VOTER_ID:
                holder.address.setVisibility(View.VISIBLE);
                holder.identity.setVisibility(View.VISIBLE);
                holder.student.setVisibility(View.GONE);
                break;
            case STUDENT_ID:
                holder.address.setVisibility(View.GONE);
                holder.identity.setVisibility(View.VISIBLE);
                holder.student.setVisibility(View.VISIBLE);
                break;
            case BANK:
                holder.address.setVisibility(View.VISIBLE);
                holder.identity.setVisibility(View.VISIBLE);
                holder.student.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        return convertView;
    }

    static class ViewHolder {
        TextView type, id, address, identity, student;
    }
}
