package com.vmpk.codequerst.Adapter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        // Open Quiz Details on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), QuizDetailActivity.class);
            intent.putExtra("quizData", quiz);
            intent.putExtra("quizId", quizIds.get(position));
            v.getContext().startActivity(intent);
        });

        // Delete quiz on long click
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Quiz")
                    .setMessage("Are you sure you want to delete this quiz?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("quizzes")
                                .document(quizIds.get(position))
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    quizList.remove(position);
                                    quizIds.remove(position);
                                    notifyItemRemoved(position);
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        // Show quiz code dialog on eye button click
        holder.btnViewCode.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Quiz Code");

            String code = quizIds.get(position); // The quiz code (Firestore doc ID)
            builder.setMessage("Your Quiz Code: " + code);

            builder.setPositiveButton("Copy", (dialog, which) -> {
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Quiz Code", code);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), "Code copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizItem;
        ImageView btnViewCode;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizItem = itemView.findViewById(R.id.tvQuizItem);
            btnViewCode = itemView.findViewById(R.id.btnViewCode);
        }
    }
}
