package com.vmpk.codequerst.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Activity.LevelQuizActivity;
import com.vmpk.codequerst.R;

import java.util.List;
import java.util.Map;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private final Context context;
    private final List<Integer> levels;
    private Map<Integer, Boolean> levelStatus;
    private Map<Integer, Integer> levelStars;

    public LevelAdapter(Context context, List<Integer> levels, Map<Integer, Boolean> levelStatus) {
        this.context = context;
        this.levels = levels;
        this.levelStatus = levelStatus;
    }

    public void setLevelStars(Map<Integer, Integer> levelStars) {
        this.levelStars = levelStars;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_level_card, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        int level = levels.get(position);
        boolean unlocked = (level == 1) || levelStatus.getOrDefault(level - 1, false);
        boolean completed = levelStatus.getOrDefault(level, false);

        holder.txtLevel.setText("Level " + level);
        holder.imgLock.setImageResource(unlocked ? R.drawable.ic_lock_open : R.drawable.ic_lock);

        holder.cardView.setCardBackgroundColor(
                completed ? Color.parseColor("#4CAF50") :
                        unlocked ? Color.parseColor("#80DEEA") :
                                Color.parseColor("#7E57C2")
        );

        // Show stars only for completed levels
        if (completed) {
            holder.starLayout.setVisibility(View.VISIBLE);
            int stars = levelStars != null ? levelStars.getOrDefault(level, 0) : 0;
            updateStarIcons(holder, stars);
        } else {
            holder.starLayout.setVisibility(View.GONE);
        }

        holder.cardView.setOnClickListener(v -> {
            if (unlocked) {
                Intent intent = new Intent(context, LevelQuizActivity.class);
                intent.putExtra("levelNumber", level);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public void updateStatus(Map<Integer, Boolean> newStatus) {
        this.levelStatus = newStatus;
        notifyDataSetChanged();
    }

    private void updateStarIcons(LevelViewHolder holder, int stars) {
        int filled = R.drawable.ic_star_yellow;
        int empty = R.drawable.ic_star_border;

        holder.star1.setImageResource(stars >= 1 ? filled : empty);
        holder.star2.setImageResource(stars >= 2 ? filled : empty);
        holder.star3.setImageResource(stars >= 3 ? filled : empty);
    }

    static class LevelViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgLock, star1, star2, star3;
        TextView txtLevel;
        LinearLayout starLayout;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            imgLock = itemView.findViewById(R.id.imgLock);
            txtLevel = itemView.findViewById(R.id.txtLevel);
            starLayout = itemView.findViewById(R.id.starLayout);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
        }
    }
}
