package com.udacity.garuolis.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.garuolis.popularmovies.R;
import com.udacity.garuolis.popularmovies.model.MovieReview;

import java.util.ArrayList;
import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {
    final List<MovieReview> items;
    final Context mContext;

    public ReviewListAdapter(Context context) {
        mContext = context;
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieReview rv = items.get(position);
        holder.mAuthorLabel.setText(rv.author);
        holder.mContentLabel.setText(rv.content);
    }

    public void setItems(List<MovieReview> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mAuthorLabel;
        final TextView mContentLabel;

        public ViewHolder(View v) {
            super(v);
            mAuthorLabel = v.findViewById(R.id.tv_author);
            mContentLabel = v.findViewById(R.id.tv_content);
        }
    }

}
