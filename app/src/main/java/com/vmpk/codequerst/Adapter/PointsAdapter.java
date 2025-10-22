package com.vmpk.codequerst.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;

import java.util.List;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.PointsViewHolder> {

    private List<UserScore.QuizAttemptItem> quizItems;

    public PointsAdapter(List<UserScore.QuizAttemptItem> quizItems) {
        this.quizItems = quizItems;
    }

    @NonNull
    @Override
    public PointsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_points, parent, false);
        return new PointsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointsViewHolder holder, int position) {
        UserScore.QuizAttemptItem item = quizItems.get(position);
        holder.tvTopic.setText(item.getLanguage() + " - " + item.getTopic());
        holder.tvDifficulty.setText(item.getDifficulty());
        holder.tvPoints.setText(String.valueOf(item.getPoints()));

        // ✅ Set points color based on value
        int points = item.getPoints();
        if (points <= 4) {
            holder.tvPoints.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_dark));
        } else if (points >= 5 && points <= 7) {
            holder.tvPoints.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_orange_light));
        } else if (points >= 8) {
            holder.tvPoints.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return quizItems.size();
    }

    static class PointsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopic, tvDifficulty, tvPoints;

        public PointsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopic = itemView.findViewById(R.id.tvTopic);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvPoints = itemView.findViewById(R.id.tvPoints);
        }
    }
}
