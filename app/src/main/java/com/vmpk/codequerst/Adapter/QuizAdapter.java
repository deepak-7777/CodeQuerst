package com.vmpk.codequerst.Adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.vmpk.codequerst.Activity.QuizDetailActivity;
import com.vmpk.codequerst.Model.BCreateQuiz;
import com.vmpk.codequerst.R;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<BCreateQuiz> quizList;
    private List<String> quizIds; // Firestore document IDs

    // ✅ Constructor with both lists
    public QuizAdapter(List<BCreateQuiz> quizList, List<String> quizIds) {
        this.quizList = quizList;
        this.quizIds = quizIds;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_showquiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        BCreateQuiz quiz = quizList.get(position);
        holder.tvQuizItem.setText("Quiz " + (position + 1) + ": " + quiz.getTitle());

        // 🔹 Short click → Open details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), QuizDetailActivity.class);
            intent.putExtra("quizData", quiz);
            intent.putExtra("quizId", quizIds.get(position)); // Firestore doc ID
            v.getContext().startActivity(intent);
        });

        // 🔹 Long click → Delete
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Quiz")
                    .setMessage("Are you sure you want to delete this quiz?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("quizzes")
                                .document(quizIds.get(position)) // Correct Firestore ID
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    quizList.remove(position);
                                    quizIds.remove(position);
                                    notifyItemRemoved(position);
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true; // consume long press
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizItem;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizItem = itemView.findViewById(R.id.tvQuizItem);
        }
    }
}
