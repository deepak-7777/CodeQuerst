package com.vmpk.codequerst.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;

import java.util.List;

public class LeaderboardAdapter
        extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private Context context;
    private List<UserScore> userList;
    private boolean showWeekly = false;

    // 🔹 Item click interface
    public interface OnItemClickListener {
        void onItemClick(UserScore user);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // 🔹 Constructor
    public LeaderboardAdapter(Context context, List<UserScore> userList) {
        this.context = context;
        this.userList = userList;
    }

    // 🔹 Toggle weekly / all-time
    public void setShowWeekly(boolean showWeekly) {
        this.showWeekly = showWeekly;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        UserScore user = userList.get(position);

        // 🏅 Rank (top 3 alag hote hain)
        holder.tvRank.setText(String.valueOf(position + 4));

        // 👤 Name
        holder.tvName.setText(
                user.getName() != null ? user.getName() : "User"
        );

        // ⭐ Points
        int points = showWeekly
                ? user.getWeeklyPoints()
                : user.getTotalPoints();

        holder.tvPoints.setText(points + " pts");

        // 🖼️ PROFILE IMAGE / AVATAR
        String imageValue = user.getProfileImage();

        if (imageValue != null && !imageValue.isEmpty()) {

            // 🌐 URL image (Firebase / Google / Gallery)
            if (imageValue.startsWith("http")) {

                Glide.with(context)
                        .load(imageValue)
                        .circleCrop()
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(holder.imgProfile);
            }

            // 🧑‍🎨 Avatar (avatar_1 → avatar_15)
            else if (imageValue.startsWith("avatar_")) {

                int resId = context.getResources().getIdentifier(
                        imageValue,           // avatar_1 ...
                        "drawable",
                        context.getPackageName()
                );

                if (resId != 0) {
                    holder.imgProfile.setImageResource(resId);
                } else {
                    holder.imgProfile.setImageResource(R.drawable.ic_person);
                }
            }

            // ❌ Unknown value
            else {
                holder.imgProfile.setImageResource(R.drawable.ic_person);
            }

        } else {
            // Default icon
            holder.imgProfile.setImageResource(R.drawable.ic_person);
        }

        // 🔘 Item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    // 🔹 ViewHolder
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

    // 🔹 Update list
    public void updateList(List<UserScore> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }
}
