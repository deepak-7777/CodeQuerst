package com.vmpk.codequerst.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Model.ACreateQuiz;
import com.vmpk.codequerst.R;

import java.util.List;

public class CreateQuizAdapter extends RecyclerView.Adapter<CreateQuizAdapter.QuestionViewHolder> {

    private final List<ACreateQuiz> questionList;

    public CreateQuizAdapter(List<ACreateQuiz> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_createquiz, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        ACreateQuiz q = questionList.get(position);
        holder.tvQuestion.setText((position + 1) + ". " + q.getQuestion());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
        }
    }
}
