package com.mbv.pokket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.GeoLocation;

import java.util.List;

/**
 * Created by arindamnath on 28/02/16.
 */
public class LocationAdapter extends ArrayAdapter<GeoLocation> {

    private List<GeoLocation> geoLocations;

    public LocationAdapter(Context context, int resource, List<GeoLocation> objects) {
        super(context, resource, objects);
        this.geoLocations = objects;
    }

    @Override
    public GeoLocation getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.adapter_geo_location, null);
        }
        GeoLocation geoLocation = geoLocations.get(position);
        if (geoLocation != null) {
            TextView address = (TextView) v.findViewById(R.id.adapter_geo_address);
            TextView locality = (TextView) v.findViewById(R.id.adapter_geo_locality);
            if(address != null) {
                address.setText(geoLocation.getAddress());
            }
            if(locality != null) {
                locality.setText(geoLocation.getCity() + " | " + geoLocation.getState() + " | " + geoLocation.getCountry());
            }
        }
        return v;
    }
}
