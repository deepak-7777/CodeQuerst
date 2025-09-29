package com.vmpk.codequerst.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private Context context;
    private List<UserScore> userList;
    private OnItemClickListener listener;
    private boolean showWeekly; // true = weekly, false = all-time

    // Interface for item click
    public interface OnItemClickListener {
        void onItemClick(UserScore user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Constructor
    public LeaderboardAdapter(Context context, List<UserScore> userList) {
        this.context = context;
        this.userList = userList;
        this.showWeekly = showWeekly;
    }

    // Update weekly/all-time mode
    public void setShowWeekly(boolean showWeekly) {
        this.showWeekly = showWeekly;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserScore user = userList.get(position);

        // Rank starts from 4 (podium 1-3 are separate)
        holder.tvRank.setText(String.valueOf(position + 4));
        holder.tvName.setText(user.getName() != null ? user.getName() : "User");

        // ✅ Show correct points (weekly or all-time)
        int points = showWeekly ? user.getWeeklyPoints() : user.getTotalPoints();
        holder.tvPoints.setText(points + " pts");

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            Glide.with(context).load(user.getProfileImage()).circleCrop().into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_person); // default image
        }

        // Item click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvPoints;
        ShapeableImageView imgProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvName);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }
    }

    // Update list
    public void updateList(List<UserScore> newList) {
        this.userList.clear();
        this.userList.addAll(newList);
        notifyDataSetChanged();
    }
}
