package com.vmpk.codequerst.Adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.vmpk.codequerst.Model.ACreateQuiz;
import com.vmpk.codequerst.R;

import java.util.List;

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.QuestionViewHolder> {

    private List<ACreateQuiz> questions;
    private String quizId;

    public QuizQuestionAdapter(List<ACreateQuiz> questions, String quizId) {
        this.questions = questions;
        this.quizId = quizId;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        ACreateQuiz q = questions.get(position);
        holder.tvQuestion.setText((position + 1) + ". " + q.getQuestion());
        holder.tvOptions.setText(
                "1. " + q.getOptions().get(0) + "\n" +
                        "2. " + q.getOptions().get(1) + "\n" +
                        "3. " + q.getOptions().get(2) + "\n" +
                        "4. " + q.getOptions().get(3)
        );

        // 🔹 Long press → delete
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Question")
                    .setMessage("Are you sure you want to delete this question?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        questions.remove(pos);
                        notifyItemRemoved(pos);

                        // Firestore update
                        FirebaseFirestore.getInstance()
                                .collection("quizzes")
                                .document(quizId)
                                .update("questions", questions)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(v.getContext(), "Question deleted", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        // 🔹 Single tap → edit
        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_edit_question, null);
            EditText etQ = dialogView.findViewById(R.id.etQuestion);
            EditText et1 = dialogView.findViewById(R.id.etOption1);
            EditText et2 = dialogView.findViewById(R.id.etOption2);
            EditText et3 = dialogView.findViewById(R.id.etOption3);
            EditText et4 = dialogView.findViewById(R.id.etOption4);

            // Set current values
            etQ.setText(q.getQuestion());
            et1.setText(q.getOptions().get(0));
            et2.setText(q.getOptions().get(1));
            et3.setText(q.getOptions().get(2));
            et4.setText(q.getOptions().get(3));

            builder.setView(dialogView)
                    .setTitle("Edit Question")
                    .setPositiveButton("Save", (d, w) -> {
                        // Update question object
                        q.setQuestion(etQ.getText().toString().trim());
                        q.getOptions().set(0, et1.getText().toString().trim());
                        q.getOptions().set(1, et2.getText().toString().trim());
                        q.getOptions().set(2, et3.getText().toString().trim());
                        q.getOptions().set(3, et4.getText().toString().trim());

                        notifyItemChanged(holder.getAdapterPosition());

                        // Update Firestore
                        FirebaseFirestore.getInstance()
                                .collection("quizzes")
                                .document(quizId)
                                .update("questions", questions)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(v.getContext(), "Question updated", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvOptions;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvOptions = itemView.findViewById(R.id.tvOptions);
        }
    }
}
