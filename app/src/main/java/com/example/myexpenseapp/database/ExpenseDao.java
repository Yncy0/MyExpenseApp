package com.example.myexpenseapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExpenseList expenseList);

    @Update
    void update(ExpenseList expenseList);

    @Delete
    void delete(ExpenseList expenseList);

    @Query("SELECT * FROM expense_table")
    List<ExpenseList> getAll();
}
