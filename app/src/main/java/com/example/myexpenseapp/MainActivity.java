package com.example.myexpenseapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myexpenseapp.adapter.ExpenseListAdapter;
import com.example.myexpenseapp.database.ExpenseDatabase;
import com.example.myexpenseapp.database.ExpenseList;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myexpenseapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    RecyclerView recyclerView;
    ExpenseListAdapter listAdapter;
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


        //FAB onClick
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.hide();
            }
        });
    }

}