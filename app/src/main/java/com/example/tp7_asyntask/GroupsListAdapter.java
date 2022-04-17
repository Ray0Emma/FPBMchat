package com.example.tp7_asyntask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupsListAdapter extends ArrayAdapter {

    private static final String TAG = "GroupsListAdapter";

    private Context mContext;
    private int mResource;
    private ArrayList arralist;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name, last_msg, txt;
        ImageView icon;
    }


    public GroupsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        arralist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information

        Groups groups = (Groups) getItem(position);
        String name = arralist.get(position).toString();
        String icon = arralist.get(position).toString();
        String last_msg = arralist.get(position).toString();
//        String sex = getItem(position).getSex();

        //Create the person object with the information
//        Members person = new Members(name);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        GroupsListAdapter.ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new GroupsListAdapter.ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.grp_name);
//            holder.last_msg = (TextView) convertView.findViewById(R.id.last_msg);
            holder.icon = (ImageView) convertView.findViewById(R.id.group_image);
            holder.txt = (TextView) convertView.findViewById(R.id.no_groups);


            result = convertView;

            convertView.setTag(holder);
        }
        else{

            holder = (GroupsListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

//        holder.txt.setText("");
        holder.name.setText(groups.getName());
//        holder.last_msg.setText(groups.getLast_msg());


        Picasso.get().load(groups.getIcon()).into(holder.icon);
//        int id = holder.icon.getId();
//        holder.icon.setTag(R.drawable.id);
        holder.icon.setVisibility(View.VISIBLE);





        return convertView;
    }
}
