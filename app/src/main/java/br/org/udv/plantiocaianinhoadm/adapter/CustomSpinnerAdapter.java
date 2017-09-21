package br.org.udv.plantiocaianinhoadm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    public CustomSpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        setCustom(view, position);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        setCustom(view, position);
        return view;
    }

    private void setCustom(View view, int position) {
        TextView textView = (TextView) view;
        textView.setGravity(Gravity.CENTER);
        if (position == 0) {
            textView.setTextColor(Color.GRAY);
        } else {
            textView.setTextColor(Color.BLACK);
        }
    }
}