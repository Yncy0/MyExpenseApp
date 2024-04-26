package com.example.myexpenseapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = ExpenseList.class, version = 1, exportSchema = false)
public abstract class ExpenseDatabase extends RoomDatabase {
    private static ExpenseDatabase expenseDatabase;
    private static String DATABASE_NAME = "ExpenseDatabase";

    public synchronized static ExpenseDatabase getInstance(Context context) {
        if(expenseDatabase == null) {
            expenseDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            ExpenseDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        } return expenseDatabase;
    }

    public abstract ExpenseDao expenseDao();
}
