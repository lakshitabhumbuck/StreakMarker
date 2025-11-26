package com.example.myapp;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HabitManager {
    private static final String PREFS_NAME = "StreakMarkerPrefs";
    private static final String KEY_HABITS = "habits";
    private SharedPreferences prefs;
    private Gson gson;

    public HabitManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveHabits(List<Habit> habits) {
        String json = gson.toJson(habits);
        prefs.edit().putString(KEY_HABITS, json).apply();
    }

    public List<Habit> loadHabits() {
        String json = prefs.getString(KEY_HABITS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Habit>>(){}.getType();
        List<Habit> habits = gson.fromJson(json, type);
        if (habits == null) {
            return new ArrayList<>();
        }
        // Recalculate streaks after loading
        for (Habit habit : habits) {
            habit.calculateStreaks();
        }
        return habits;
    }

    public void addHabit(Habit habit) {
        List<Habit> habits = loadHabits();
        habits.add(habit);
        saveHabits(habits);
    }

    public void updateHabit(Habit updatedHabit) {
        List<Habit> habits = loadHabits();
        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).getId().equals(updatedHabit.getId())) {
                habits.set(i, updatedHabit);
                break;
            }
        }
        saveHabits(habits);
    }

    public void deleteHabit(String habitId) {
        List<Habit> habits = loadHabits();
        habits.removeIf(habit -> habit.getId().equals(habitId));
        saveHabits(habits);
    }
}

