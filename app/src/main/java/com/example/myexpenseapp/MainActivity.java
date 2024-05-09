package com.example.myexpenseapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myexpenseapp.adapter.ExpenseListAdapter;
import com.example.myexpenseapp.database.ExpenseDatabase;
import com.example.myexpenseapp.database.ExpenseList;
import com.example.myexpenseapp.listener.ExpenseListListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
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

    RecyclerView recyclerView;
    ExpenseListAdapter listAdapter;
    ExpenseList myList;
    ExpenseList selectedList;
    List<ExpenseList> expenseLists = new ArrayList<>();
    ExpenseDatabase database;

    EditText txtAmount, txtCategory, txtDescription;
    MaterialButton btnAdd, btnEdit, btnCancel;
    FloatingActionButton fabAdd;

    private String amount, category, description;
    private Boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        com.example.myexpenseapp.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
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
                showDialogAdd();
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
                Snackbar.make((View) recyclerView, "Item is deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expenseLists.add(position, selectedList);
                        database.expenseDao().insert(selectedList);
                        //expenseLists.addAll(position, database.expenseDao().getAll());
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
            showDialogEdit();
        }
    };

    private void showDialogAdd() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_add, null);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        txtAmount = bottomSheetDialog.findViewById(R.id.txt_add_amount);
        txtCategory = bottomSheetDialog.findViewById(R.id.txt_add_category);
        txtDescription = bottomSheetDialog.findViewById(R.id.txt_add_description);
        btnAdd = bottomSheetDialog.findViewById(R.id.btn_add);
        btnCancel = bottomSheetDialog.findViewById(R.id.btn_cancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = String.valueOf(txtAmount.getText());
                category = txtCategory.getText().toString();
                description = txtDescription.getText().toString();

                if(amount.isEmpty() || category.isEmpty() || description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill out the blanks", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isOldNote) {
                    myList = new ExpenseList();
                }

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

    private void showDialogEdit() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_edit, null);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        txtAmount = bottomSheetDialog.findViewById(R.id.txt_edit_amount);
        txtCategory = bottomSheetDialog.findViewById(R.id.txt_edit_amount);
        txtDescription = bottomSheetDialog.findViewById(R.id.txt_edit_amount);
        btnEdit = bottomSheetDialog.findViewById(R.id.btn_edit);
        btnCancel = bottomSheetDialog.findViewById(R.id.btn_cancel);

        myList = new ExpenseList();
        txtAmount.setText(String.valueOf(myList.getAmount()));
        txtCategory.setText(myList.getCategory());
        txtDescription.setText(myList.getDescription());
        isOldNote = true;

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.expenseDao().update(myList);
                expenseLists.clear();
                expenseLists.addAll(database.expenseDao().getAll());
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