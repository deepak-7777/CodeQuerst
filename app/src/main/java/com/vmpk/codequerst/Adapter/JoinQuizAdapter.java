package com.vmpk.codequerst.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Model.BJoinQuiz;
import com.vmpk.codequerst.R;

import java.util.List;

public class JoinQuizAdapter extends RecyclerView.Adapter<JoinQuizAdapter.SolutionViewHolder> {

    private Context context;
    private List<BJoinQuiz> questionList;

    public JoinQuizAdapter(Context context, List<BJoinQuiz> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public SolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_joinquizsolution, parent, false);
        return new SolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionViewHolder holder, int position) {
        BJoinQuiz q = questionList.get(position);

        holder.tvQuestion.setText((position + 1) + ". " + q.question);
        holder.tvOption1.setText("1. " + q.options.get(0));
        holder.tvOption2.setText("2. " + q.options.get(1));
        holder.tvOption3.setText("3. " + q.options.get(2));
        holder.tvOption4.setText("4. " + q.options.get(3));

        // Reset default colors (RecyclerView reuses views)
        resetColors(holder);

        // Highlight logic
        if (q.selectedIndex != -1) {
            if (q.selectedIndex == q.correctIndex) {
                getOptionView(holder, q.correctIndex).setTextColor(Color.GREEN);
            } else {
                getOptionView(holder, q.selectedIndex).setTextColor(Color.RED);
                getOptionView(holder, q.correctIndex).setTextColor(Color.GREEN);
            }
        } else {
            // skipped -> only correct in green
            getOptionView(holder, q.correctIndex).setTextColor(Color.GREEN);
        }
    }

    private void resetColors(SolutionViewHolder holder) {
        holder.tvOption1.setTextColor(Color.BLACK);
        holder.tvOption2.setTextColor(Color.BLACK);
        holder.tvOption3.setTextColor(Color.BLACK);
        holder.tvOption4.setTextColor(Color.BLACK);
    }

    private TextView getOptionView(SolutionViewHolder holder, int index) {
        switch (index) {
            case 0: return holder.tvOption1;
            case 1: return holder.tvOption2;
            case 2: return holder.tvOption3;
            default: return holder.tvOption4;
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class SolutionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvOption1, tvOption2, tvOption3, tvOption4;

        SolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvOption1 = itemView.findViewById(R.id.tvOption1);
            tvOption2 = itemView.findViewById(R.id.tvOption2);
            tvOption3 = itemView.findViewById(R.id.tvOption3);
            tvOption4 = itemView.findViewById(R.id.tvOption4);
        }
    }
}
