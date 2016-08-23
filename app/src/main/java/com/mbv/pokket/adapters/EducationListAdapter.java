package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.EducationDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 27/02/16.
 */
public class EducationListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<EducationDAO> educationDAOs = new ArrayList<>();

    public EducationListAdapter(Context contenxt) {
        this.context = contenxt;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<EducationDAO> data) {
        educationDAOs = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return educationDAOs.size();
    }

    @Override
    public EducationDAO getItem(int position) {
        return educationDAOs.get(position);
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
            convertView = layoutInflater.inflate(R.layout.adapter_education, parent, false);
            holder.instituteName = (TextView) convertView.findViewById(R.id.adapter_education_institute);
            holder.date = (TextView) convertView.findViewById(R.id.adapter_education_date_range);
            holder.degreeType = (TextView) convertView.findViewById(R.id.adapter_education_degree_type);
            holder.degreeName = (TextView) convertView.findViewById(R.id.adapter_education_degree_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.instituteName.setText(educationDAOs.get(position).getInstituteName());
        holder.degreeType.setText(context.getResources().getStringArray(R.array.educational_degree)
                [educationDAOs.get(position).getEducationDegreeType().ordinal()]);
        holder.degreeName.setText(educationDAOs.get(position).getDegreeName());
        holder.date.setText(educationDAOs.get(position).getStartDate() + " - " + educationDAOs.get(position).getPassDate());
        return convertView;
    }

    static class ViewHolder {
        TextView date, instituteName, degreeType, degreeName;
    }
}
