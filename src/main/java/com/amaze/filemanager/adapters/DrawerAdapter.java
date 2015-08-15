package com.amaze.filemanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amaze.filemanager.R;
import com.amaze.filemanager.activities.MainActivity;
import com.amaze.filemanager.ui.drawer.EntryItem;
import com.amaze.filemanager.ui.drawer.Item;
import com.amaze.filemanager.ui.icons.IconUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class DrawerAdapter extends ArrayAdapter<Item> {
    private final Context context;
    private final ArrayList<Item> values;
    private RelativeLayout l;
    MainActivity m;
    IconUtils icons;
    private SparseBooleanArray myChecked = new SparseBooleanArray();
    HashMap<String, Float[]> colors = new HashMap<String, Float[]>();

    public void toggleChecked(int position) {
        toggleChecked(false);
        myChecked.put(position, true);
        notifyDataSetChanged();
    }

    public void toggleChecked(boolean b) {

        for (int i = 0; i < values.size(); i++) {
            myChecked.put(i, b);
        }
        notifyDataSetChanged();
    }
    LayoutInflater inflater;
    public DrawerAdapter(Context context, ArrayList<Item> values, MainActivity m, SharedPreferences Sp) {
        super(context, R.layout.drawerrow, values);

        this.context = context;
        this.values = values;

        for (int i = 0; i < values.size(); i++) {
            myChecked.put(i, false);
        }
        icons = new IconUtils(Sp, m);
        this.m = m;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (values.get(position).isSection()) {
            ImageView view = new ImageView(context);
            view.setImageResource(R.color.divider);
            view.setClickable(false);
            view.setFocusable(false);
            view.setBackgroundColor(Color.WHITE);
            view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m.dpToPx(17)));
            view.setPadding(0, m.dpToPx(8), 0, m.dpToPx(8));
            return view;
        } else {
            View  view = inflater.inflate(R.layout.drawerrow, parent, false);
            final TextView txtTitle=(TextView) view.findViewById(R.id.firstline);
            final ImageView imageView=(ImageView) view.findViewById(R.id.icon);
            view.setBackgroundResource(R.drawable.safr_ripple_white);
            view.setOnClickListener(new View.OnClickListener() {

                public void onClick(View p1) {
                    m.selectItem(position, false);
                }
                // TODO: Implement this method

            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // not to remove the first bookmark (storage) and permanent bookmarks
                    if (position > m.storage_count && position < values.size()-5) {
                        String path = ((EntryItem) getItem(position)).getPath();
                        if (!getItem(position).isSection() && path.startsWith("smb:/")) {
                            m.createSmbDialog(path, true, null);
                            return true;
                        }
                        imageView.setImageResource(R.drawable.ic_action_cancel_light);
                        imageView.setClickable(true);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                m.selectItem(position, true);
                            }
                        });
                    }

                    // return true to denote no further processing
                    return true;
                }
            });

            txtTitle.setText(((EntryItem) (values.get(position))).getTitle());
            imageView.setImageDrawable(getDrawable(position));
            imageView.clearColorFilter();
            if (myChecked.get(position)) {
                view.setBackgroundColor(Color.parseColor("#ffeeeeee"));
            } else {
                imageView.setColorFilter(Color.parseColor("#666666"));
                txtTitle.setTextColor(m.getResources().getColor(android.R.color.black));
            }

            return view;
        }
    }

    Drawable getDrawable(int position){
        Drawable drawable=((EntryItem)getItem(position)).getIcon();
        return drawable;  }
}