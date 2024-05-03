package com.example.myexpenseapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myexpenseapp.adapter.ExpenseListAdapter;
import com.example.myexpenseapp.database.ExpenseDao;
import com.example.myexpenseapp.database.ExpenseDatabase;
import com.example.myexpenseapp.database.ExpenseList;
import com.example.myexpenseapp.listener.ExpenseListListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myexpenseapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    RecyclerView recyclerView;
    ExpenseListAdapter listAdapter;
    ExpenseList myList;
    ExpenseList selectedList;
    List<ExpenseList> expenseLists = new ArrayList<>();
    ExpenseDatabase database;

    EditText txtAmount, txtCategory, txtDescription;
    MaterialButton btnAdd, btnCancel;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;

        });

        //Init Components
        recyclerView = findViewById(R.id.recyclerview);
        fabAdd = findViewById(R.id.fab_add);

        database = ExpenseDatabase.getInstance(this);
        expenseLists = (List<ExpenseList>) database.expenseDao().getAll();

        //Recycler Function
        updateRecycle(expenseLists);

        //FAB onClick
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        //ItemTouchHelper
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                selectedList = new ExpenseList();

                selectedList = expenseLists.remove(position);

                database.expenseDao().delete(selectedList);

                Snackbar.make((View) recyclerView, "Item is deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expenseLists.add(position, selectedList);
                        listAdapter.notifyDataSetChanged();
                    }
                }).show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);



        //Bottom Nav
        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
    }

    private void updateRecycle(List<ExpenseList> list) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ExpenseListAdapter(this, list, expenseListListener);
        recyclerView.setAdapter(listAdapter);
    }

    private final ExpenseListListener expenseListListener = new ExpenseListListener() {
        @Override
        public void onClick(ExpenseList expenseList) {
            showDialog();
        }
    };

    private void showDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view= LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet, null);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        txtAmount = bottomSheetDialog.findViewById(R.id.txt_amount);
        txtCategory = bottomSheetDialog.findViewById(R.id.txt_category);
        txtDescription = bottomSheetDialog.findViewById(R.id.txt_description);
        btnAdd = bottomSheetDialog.findViewById(R.id.btn_add);
        btnCancel = bottomSheetDialog.findViewById(R.id.btn_cancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = String.valueOf(txtAmount.getText());
                String category = txtCategory.getText().toString();
                String description = txtDescription.getText().toString();

                if(amount.isEmpty() || category.isEmpty() || description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill out the blanks", Toast.LENGTH_LONG).show();
                    return;
                }

                myList = new ExpenseList();
                myList.setAmount(Integer.parseInt(amount));
                myList.setCategory(category);
                myList.setDescription(description);

                database.expenseDao().insert(myList);
                expenseLists.clear();
                expenseLists.addAll(database.expenseDao().getAll());



                //Toast.makeText(MainActivity.this, "List has been added", Toast.LENGTH_LONG). show();
                bottomSheetDialog.hide();
                listAdapter.notifyDataSetChanged();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.hide();
            }
        });
    }

}