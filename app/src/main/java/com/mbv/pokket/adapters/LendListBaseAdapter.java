package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.LendDAO;
import com.mbv.pokket.util.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 13/01/16.
 */
public class LendListBaseAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater layoutInflater;
    private List<LendDAO> lendDAOs = new ArrayList<>();

    public LendListBaseAdapter(Context contenxt) {
        this.context = contenxt;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<LendDAO> data) {
        lendDAOs = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lendDAOs.size();
    }

    @Override
    public LendDAO getItem(int position) {
        return lendDAOs.get(position);
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
            convertView = layoutInflater.inflate(R.layout.adapter_lend_details, parent, false);
            holder.userImage = (ImageView) convertView.findViewById(R.id.lend_list_image);
            holder.name = (TextView) convertView.findViewById(R.id.lend_list_name);
            holder.amount = (TextView) convertView.findViewById(R.id.lend_list_amount);
            holder.tenure = (TextView) convertView.findViewById(R.id.lend_list_tenure);
            holder.postTime = (TextView) convertView.findViewById(R.id.lend_list_post_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(lendDAOs.get(position).getName());
        holder.amount.setText(context.getText(R.string.rupee) + String.valueOf(lendDAOs.get(position).getAmount()));
        holder.tenure.setText("for " + lendDAOs.get(position).getTenure());
        holder.postTime.setText(lendDAOs.get(position).getPostTime());

        Picasso.with(context).cancelRequest(holder.userImage);
        Picasso.with(context)
                .load(lendDAOs.get(position).getImageURL())
                .transform(new CropCircleTransformation())
                .into(holder.userImage);
        return convertView;
    }

    static class ViewHolder {
        ImageView userImage;
        TextView name, amount, tenure, postTime;
    }
}
