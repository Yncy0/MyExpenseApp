package com.example.myexpenseapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "expense_table")
public class ExpenseList implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id = 0;

    @ColumnInfo(name = "amount")
    int amount = 0;

    @ColumnInfo(name = "category")
    String category;

    @ColumnInfo(name = "description")
    String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
