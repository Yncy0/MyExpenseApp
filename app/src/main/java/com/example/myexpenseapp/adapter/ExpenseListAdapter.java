package com.example.myexpenseapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myexpenseapp.R;
import com.example.myexpenseapp.database.ExpenseList;
import com.example.myexpenseapp.listener.ExpenseListListener;

import java.util.List;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder> {

    Context context;
    List<ExpenseList> expenseLists;
    ExpenseListListener listener;

    public ExpenseListAdapter(Context context, List<ExpenseList> expenseLists, ExpenseListListener listener) {
        this.context = context;
        this.expenseLists = expenseLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpenseViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_expense_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.lblAmount.setText(expenseLists.get(position).getAmount());
        holder.lblAmount.setSelected(true);
        holder.lblCategory.setText(expenseLists.get(position).getCategory());
        holder.lblDescription.setText(expenseLists.get(position).getDescription());

        holder.expenseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(expenseLists.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseLists.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder{

        CardView expenseContainer;
        TextView lblAmount, lblCategory, lblDescription;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseContainer = itemView.findViewById(R.id.expense_container);
            lblAmount = itemView.findViewById(R.id.lbl_amount);
            lblCategory = itemView.findViewById(R.id.lbl_category);
            lblDescription = itemView.findViewById(R.id.lbl_description);

        }
    }
}
