package model;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import rutgers.cs213androidproject.R;

public class AlbumImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Photo> photolist;
    private LayoutInflater layoutInflater;

    public AlbumImageAdapter(Context mContext, ArrayList<Photo> photolist) {
        this.mContext = mContext;
        this.photolist = photolist;
    }

    @Override
    public int getCount() {
        return photolist.size();
    }

    @Override
    public Object getItem(int i) {
        return photolist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.photoview, parent, false);

            holder.imageView = convertView.findViewById(R.id.imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Uri uri = Uri.parse(photolist.get(position).getFilepath());
        holder.imageView.setImageURI(uri);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
