package com.example.myapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Habit {
    private String id;
    private String name;
    private List<Long> completedDates; // Store dates as timestamps
    private int currentStreak;
    private int longestStreak;
    private long createdAt;

    public Habit() {
        this.completedDates = new ArrayList<>();
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.createdAt = System.currentTimeMillis();
    }

    public Habit(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getCompletedDates() {
        return completedDates;
    }

    public void setCompletedDates(List<Long> completedDates) {
        this.completedDates = completedDates;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Check if habit was completed today
     */
    public boolean isCompletedToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long todayTimestamp = today.getTimeInMillis();

        for (Long date : completedDates) {
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTimeInMillis(date);
            dateCal.set(Calendar.HOUR_OF_DAY, 0);
            dateCal.set(Calendar.MINUTE, 0);
            dateCal.set(Calendar.SECOND, 0);
            dateCal.set(Calendar.MILLISECOND, 0);
            
            if (dateCal.getTimeInMillis() == todayTimestamp) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mark habit as completed for today
     */
    public void markCompleted() {
        if (!isCompletedToday()) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            completedDates.add(today.getTimeInMillis());
            calculateStreaks();
        }
    }

    /**
     * Unmark habit for today
     */
    public void unmarkCompleted() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long todayTimestamp = today.getTimeInMillis();

        completedDates.removeIf(date -> {
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTimeInMillis(date);
            dateCal.set(Calendar.HOUR_OF_DAY, 0);
            dateCal.set(Calendar.MINUTE, 0);
            dateCal.set(Calendar.SECOND, 0);
            dateCal.set(Calendar.MILLISECOND, 0);
            return dateCal.getTimeInMillis() == todayTimestamp;
        });
        calculateStreaks();
    }

    /**
     * Calculate current and longest streaks
     */
    public void calculateStreaks() {
        if (completedDates.isEmpty()) {
            currentStreak = 0;
            longestStreak = 0;
            return;
        }

        // Normalize all dates to midnight
        List<Long> normalizedDates = new ArrayList<>();
        for (Long date : completedDates) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            normalizedDates.add(cal.getTimeInMillis());
        }

        // Remove duplicates and sort
        List<Long> sortedDates = new ArrayList<>();
        for (Long date : normalizedDates) {
            if (!sortedDates.contains(date)) {
                sortedDates.add(date);
            }
        }
        sortedDates.sort(Long::compareTo);

        // Calculate longest streak
        int maxStreak = 1;
        int currentMaxStreak = 1;
        for (int i = 1; i < sortedDates.size(); i++) {
            long daysDiff = (sortedDates.get(i) - sortedDates.get(i - 1)) / (1000 * 60 * 60 * 24);
            if (daysDiff == 1) {
                currentMaxStreak++;
            } else {
                maxStreak = Math.max(maxStreak, currentMaxStreak);
                currentMaxStreak = 1;
            }
        }
        longestStreak = Math.max(maxStreak, currentMaxStreak);

        // Calculate current streak from today backwards
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long checkDate = today.getTimeInMillis();

        currentStreak = 0;
        boolean allowYesterday = true; // Allow checking yesterday if today not completed
        
        while (true) {
            boolean found = false;
            for (Long date : sortedDates) {
                if (date == checkDate) {
                    currentStreak++;
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                if (allowYesterday && currentStreak == 0) {
                    // If today not completed, check yesterday
                    allowYesterday = false;
                    checkDate -= (1000 * 60 * 60 * 24);
                    continue;
                }
                break;
            }
            
            checkDate -= (1000 * 60 * 60 * 24);
            allowYesterday = false; // After first match, don't allow gaps
        }
    }
}

