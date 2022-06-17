package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Mirka on 08/07/2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News currentNews = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
            holder.newsCategory = (TextView) convertView.findViewById(R.id.category);
            holder.url = (TextView) convertView.findViewById(R.id.url);
            //catch holder object inside the convertView
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Format the title of the current news in that TextView
        holder.newsTitle.setText(currentNews.getTitle());

        // Format the section of the current news in that TextView
        holder.newsCategory.setText(currentNews.getCategory());

        holder.url.setText(currentNews.getUrl());

        // Return the view
        return convertView;
    }

    //View lookup
    private static class ViewHolder {
        TextView newsTitle;
        TextView newsCategory;
        TextView url;
    }
}
