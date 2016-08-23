package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.DegreeDAO;

import java.util.List;

/**
 * Created by arindamnath on 16/03/16.
 */
public class DegreeAdapter extends ArrayAdapter<DegreeDAO> {

    private List<DegreeDAO> degreeDAOs;

    public DegreeAdapter(Context context, int resource, List<DegreeDAO> objects) {
        super(context, resource, objects);
        this.degreeDAOs = objects;
    }

    @Override
    public DegreeDAO getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.adapter_geo_location, null);
        }
        DegreeDAO geoLocation = degreeDAOs.get(position);
        if (geoLocation != null) {
            TextView address = (TextView) v.findViewById(R.id.adapter_geo_address);
            TextView locality = (TextView) v.findViewById(R.id.adapter_geo_locality);
            locality.setVisibility(View.GONE);
            if(address != null) {
                address.setText(geoLocation.getName());
            }
        }
        return v;
    }
}
