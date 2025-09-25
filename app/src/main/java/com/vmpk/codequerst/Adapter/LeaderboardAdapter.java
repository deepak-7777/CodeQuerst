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

    public LeaderboardAdapter(Context context, List<UserScore> userList) {
        this.context = context;
        this.userList = userList;
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
        holder.tvRank.setText(String.valueOf(position + 4)); // podium में 1-3 गए, यहाँ से 4th से शुरू
        holder.tvName.setText(user.getName());
        holder.tvPoints.setText(user.getCorrectAnswers() + " pts");

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            Glide.with(context).load(user.getProfileImage()).circleCrop().into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_person); // default image
        }
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
    public void updateList(List<UserScore> newList) {
        this.userList.clear();
        this.userList.addAll(newList);
        notifyDataSetChanged();
    }

}

