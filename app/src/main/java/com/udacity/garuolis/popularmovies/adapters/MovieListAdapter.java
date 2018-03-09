package com.udacity.garuolis.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.garuolis.popularmovies.R;
import com.udacity.garuolis.popularmovies.model.MovieItem;
import com.udacity.garuolis.popularmovies.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aurimas on 2018.02.24.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    final Context mContext;

    List<MovieItem> items;
    OnClickListener mClickListener;

    public MovieListAdapter(Context context) {
        mContext = context;
        items = new ArrayList<>();
    }

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movie, parent, false);
        float itemWidth = parent.getMeasuredWidth() / mContext.getResources().getInteger(R.integer.movie_grid_column_count);
        int itemHeight = Math.round(itemWidth / 0.67f);
        view.setMinimumHeight(itemHeight);
        view.getLayoutParams().height = itemHeight;
        return new ViewHolder(view);
    }

    public void setItems(List<MovieItem> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MovieItem mi = items.get(position);
        holder.mPosterImage.setContentDescription(mi.title);
        holder.mTitleLabel.setText(mi.title);
        Picasso.with(mContext).load(ApiUtils.ImageUrl(mi.poster, ApiUtils.IMG_SIZE_SMALL)).into(holder.mPosterImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements  View.OnClickListener {
        final ImageView mPosterImage;
        final TextView mTitleLabel;

        public ViewHolder(View v) {
            super(v);
            mTitleLabel = v.findViewById(R.id.tv_title);
            mPosterImage = v.findViewById(R.id.iv_poster);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnClickListener {
        void onItemClick(View v, int position);
    }

}
