# Streak Marker App

A simple and intuitive Android app to track habits and maintain streaks. 

## Features

### Core Functionality
- **Habit Tracking**: Create and manage multiple habits
- **Streak Visualization**: See your current streak and longest streak for each habit
- **Daily Marking**: Mark habits as complete with a single tap
- **Color-Coded Indicators**: Visual feedback with green for completed, gray for incomplete
- **Streak Statistics**: Track current streak, longest streak, and total completions

### Gamification
- **Milestone Celebrations**: Get notified at 7, 30, and 100-day milestones
- **Progress Tracking**: See your consistency at a glance

### Data Persistence
- All habits and streaks are saved locally
- Data persists across app restarts
- Automatic streak recalculation

## How to Use

1. **Add a Habit**: Type a habit name in the input field and tap "Add"
2. **Mark Complete**: Tap "Mark Complete" button on any habit to mark it for today
3. **View Details**: Tap on a habit card to see detailed statistics
4. **Delete Habit**: View details and tap "Delete Habit" to remove a habit

## Project Structure

- `MainActivity.java` - Main activity with habit management logic
- `Habit.java` - Habit model class with streak calculation
- `HabitAdapter.java` - RecyclerView adapter for displaying habits
- `HabitManager.java` - Data persistence using SharedPreferences
- `activity_main.xml` - Main UI layout
- `item_habit.xml` - Individual habit card layout

## Requirements

- Android Studio
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34

## Future Enhancements

Potential features for future versions:
- Reminders and notifications
- Calendar view for streak visualization
- Social sharing and challenges
- Analytics and insights
- Badges and achievements
- Weekly/monthly summaries

