package com.example.myapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
    private List<Habit> habits;
    private OnHabitClickListener listener;

    public interface OnHabitClickListener {
        void onMarkComplete(Habit habit);
        void onViewDetails(Habit habit);
    }

    public HabitAdapter(List<Habit> habits, OnHabitClickListener listener) {
        this.habits = habits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.bind(habit);
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public void updateHabits(List<Habit> newHabits) {
        this.habits = newHabits;
        notifyDataSetChanged();
    }

    class HabitViewHolder extends RecyclerView.ViewHolder {
        private TextView habitName;
        private TextView streakInfo;
        private Button markButton;
        private View streakIndicator;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habitName);
            streakInfo = itemView.findViewById(R.id.streakInfo);
            markButton = itemView.findViewById(R.id.markButton);
            streakIndicator = itemView.findViewById(R.id.streakIndicator);
        }

        public void bind(Habit habit) {
            habitName.setText(habit.getName());
            
            String streakText = "🔥 " + habit.getCurrentStreak() + " day streak";
            if (habit.getLongestStreak() > 0) {
                streakText += " | Best: " + habit.getLongestStreak();
            }
            streakInfo.setText(streakText);

            boolean completed = habit.isCompletedToday();
            if (completed) {
                markButton.setText("✓ Completed");
                markButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                streakIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
            } else {
                markButton.setText("Mark Complete");
                markButton.setBackgroundColor(Color.parseColor("#6200EE"));
                streakIndicator.setBackgroundColor(Color.parseColor("#E0E0E0"));
            }

            markButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMarkComplete(habit);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetails(habit);
                }
            });
        }
    }
}

