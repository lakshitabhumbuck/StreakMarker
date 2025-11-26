package com.example.myapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements HabitAdapter.OnHabitClickListener {

    private EditText habitInput;
    private Button addHabitButton;
    private RecyclerView habitsRecyclerView;
    private LinearLayout emptyState;
    private TextView totalStreaksText;
    
    private HabitAdapter adapter;
    private List<Habit> habits;
    private HabitManager habitManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        habitInput = findViewById(R.id.habitInput);
        addHabitButton = findViewById(R.id.addHabitButton);
        habitsRecyclerView = findViewById(R.id.habitsRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        totalStreaksText = findViewById(R.id.totalStreaksText);

        // Initialize HabitManager
        habitManager = new HabitManager(this);

        // Load habits from storage
        habits = habitManager.loadHabits();
        if (habits == null) {
            habits = new ArrayList<>();
        }

        // Setup RecyclerView
        adapter = new HabitAdapter(habits, this);
        habitsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        habitsRecyclerView.setAdapter(adapter);

        // Add habit button click listener
        addHabitButton.setOnClickListener(v -> addHabit());

        updateUI();
    }

    private void addHabit() {
        String habitName = habitInput.getText().toString().trim();
        if (TextUtils.isEmpty(habitName)) {
            Toast.makeText(this, "Please enter a habit name!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if habit already exists
        for (Habit habit : habits) {
            if (habit.getName().equalsIgnoreCase(habitName)) {
                Toast.makeText(this, "This habit already exists!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create new habit
        String habitId = UUID.randomUUID().toString();
        Habit newHabit = new Habit(habitId, habitName);
        habits.add(newHabit);
        habitManager.saveHabits(habits);

        // Clear input
        habitInput.setText("");

        // Update UI
        adapter.updateHabits(habits);
        updateUI();

        Toast.makeText(this, "Habit added: " + habitName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkComplete(Habit habit) {
        if (habit.isCompletedToday()) {
            habit.unmarkCompleted();
            Toast.makeText(this, habit.getName() + " unmarked", Toast.LENGTH_SHORT).show();
        } else {
            habit.markCompleted();
            Toast.makeText(this, "Great! Keep the streak going! 🔥", Toast.LENGTH_SHORT).show();
            
            // Check for milestones
            checkMilestones(habit);
        }

        habitManager.updateHabit(habit);
        adapter.updateHabits(habits);
        updateUI();
    }

    @Override
    public void onViewDetails(Habit habit) {
        showHabitDetails(habit);
    }

    private void showHabitDetails(Habit habit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(habit.getName());

        String details = "Current Streak: " + habit.getCurrentStreak() + " days\n" +
                "Longest Streak: " + habit.getLongestStreak() + " days\n" +
                "Total Completions: " + habit.getCompletedDates().size() + " days";

        builder.setMessage(details);
        builder.setPositiveButton("Delete Habit", (dialog, which) -> {
            deleteHabit(habit);
            dialog.dismiss();
        });
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteHabit(Habit habit) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete \"" + habit.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    habits.remove(habit);
                    habitManager.deleteHabit(habit.getId());
                    adapter.updateHabits(habits);
                    updateUI();
                    Toast.makeText(this, "Habit deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkMilestones(Habit habit) {
        int streak = habit.getCurrentStreak();
        String message = null;

        if (streak == 7) {
            message = "🎉 7 Day Milestone! You're building a strong habit!";
        } else if (streak == 30) {
            message = "🏆 30 Day Milestone! You're a streak champion!";
        } else if (streak == 100) {
            message = "🌟 100 Day Milestone! Incredible dedication!";
        }

        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI() {
        // Update empty state visibility
        if (habits.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            habitsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            habitsRecyclerView.setVisibility(View.VISIBLE);
        }

        // Calculate total active streaks
        int totalActiveStreaks = 0;
        for (Habit habit : habits) {
            if (habit.getCurrentStreak() > 0) {
                totalActiveStreaks++;
            }
        }

        if (habits.size() > 0) {
            totalStreaksText.setText(totalActiveStreaks + " active streak" + 
                (totalActiveStreaks != 1 ? "s" : "") + " | " + habits.size() + " total habit" + 
                (habits.size() != 1 ? "s" : ""));
        } else {
            totalStreaksText.setText("Track your habits, build your streaks!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recalculate streaks when app resumes (in case day changed)
        for (Habit habit : habits) {
            habit.calculateStreaks();
        }
        habitManager.saveHabits(habits);
        adapter.updateHabits(habits);
        updateUI();
    }
}
