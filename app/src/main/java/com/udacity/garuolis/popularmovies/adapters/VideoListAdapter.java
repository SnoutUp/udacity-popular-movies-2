package com.udacity.garuolis.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.garuolis.popularmovies.R;
import com.udacity.garuolis.popularmovies.model.MovieVideo;
import com.udacity.garuolis.popularmovies.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aurimas on 2018.02.24.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
    final Context mContext;

    List<MovieVideo> items;
    OnClickListener mClickListener;

    public VideoListAdapter(Context context) {
        mContext = context;
        items = new ArrayList<>();
    }

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    public void setItems(List<MovieVideo> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MovieVideo mi = items.get(position);
        Picasso.with(mContext).load(ApiUtils.VideoThumbURL(mi.site, mi.key)).into(holder.mVideoImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements  View.OnClickListener {
        final ImageView mVideoImage;
        final ImageView mShareImage;

        ViewHolder(View v) {
            super(v);
            mVideoImage = v.findViewById(R.id.iv_thumbnail);
            mShareImage = v.findViewById(R.id.iv_share);

            mVideoImage.setOnClickListener(this);
            mShareImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                MovieVideo mv = items.get(getAdapterPosition());
                switch (v.getId()) {
                    case R.id.iv_thumbnail:
                        mClickListener.playVideo(ApiUtils.VideoURL(mv.site, mv.key));
                        break;

                    case R.id.iv_share:
                        mClickListener.shareVideo(ApiUtils.VideoURL(mv.site, mv.key));
                        break;
                }

            }
        }
    }

    public interface OnClickListener {
        void playVideo(String videoUrl);
        void shareVideo(String videoUrl);
    }

}
