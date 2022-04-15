package com.example.tp7_asyntask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class MembersListAdapter extends ArrayAdapter {

    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    private int mResource;
    private ArrayList arralist;
//    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        Chip name;
        ChipGroup chip_group;
    }


    public MembersListAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        arralist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information

        Members members = (Members) getItem(position);
        String name = arralist.get(position).toString();
//        String birthday = getItem(position).getBirthday();
//        String sex = getItem(position).getSex();

        //Create the person object with the information
//        Members person = new Members(name);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.name = (Chip) convertView.findViewById(R.id.member);
            holder.chip_group = (ChipGroup) convertView.findViewById(R.id.chipGroup);

//            holder.birthday = (TextView) convertView.findViewById(R.id.textView2);
//            holder.sex = (TextView) convertView.findViewById(R.id.textView3);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


//        Animation animation = AnimationUtils.loadAnimation(mContext,
//                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
//        result.startAnimation(animation);
//        lastPosition = position;

//        holder.name.setText(person.getName());
        holder.name.setText(members.getName());

//        holder.name.setOnCloseIconClickListener(holder.chip_group.removeView(get));
//        holder.birthday.setText(person.getBirthday());
//        holder.sex.setText(person.getSex());


        return convertView;
    }
}
