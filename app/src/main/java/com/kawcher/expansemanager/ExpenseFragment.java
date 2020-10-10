package com.kawcher.expansemanager;

import android.app.AlertDialog;
import android.graphics.ColorSpace;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kawcher.expansemanager.Model.Data;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference mExpenseDB;

    private RecyclerView expenseRecyclerview;


    private TextView totalExpenseTV;
    String stTotalExpense;

    private EditText ammountET,noteET,typeET;
   private String post_key;

   private String note,type;

   private int ammount;
   private Button updateBtn,deleteBtn;


    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_expense, container, false);

       firebaseAuth=FirebaseAuth.getInstance();
       String  uid=firebaseAuth.getCurrentUser().getUid();
       mExpenseDB=FirebaseDatabase.getInstance().getReference().child("expenseData").child(uid);

       expenseRecyclerview=view.findViewById(R.id.recycler_id_expense);
        totalExpenseTV=view.findViewById(R.id.expense_txt_result);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        expenseRecyclerview.setHasFixedSize(true);
        expenseRecyclerview.setLayoutManager(layoutManager);

        mExpenseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpense = 0;
                for(DataSnapshot snapshot1:snapshot.getChildren()){

                  Data data=snapshot1.getValue(Data.class);

                    totalExpense=totalExpense+data.getAmmount();

                }

                stTotalExpense=String.valueOf(totalExpense);
                totalExpenseTV.setText(stTotalExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



  return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data>options=new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDB,Data.class).build();

        FirebaseRecyclerAdapter<Data,MyExpenseViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyExpenseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyExpenseViewHolder holder, int position, @NonNull final Data model) {

                holder.setType(model.getType());
                holder.setDate(model.getDate());
                holder.setNote(model.getNote());
                holder.setAmmount(model.getAmmount());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key=mExpenseDB.getRef().getKey();
                        type=model.getType();
                        note=model.getNote();
                        ammount=model.getAmmount();

                        updateExpenseData();

                    }
                });

            }

            @NonNull
            @Override
            public MyExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data,parent,false);

                return new MyExpenseViewHolder(view);
            }
        };
        adapter.startListening();
        expenseRecyclerview.setAdapter(adapter);
    }

    private static class MyExpenseViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public MyExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type){

            TextView typeTV=mView.findViewById(R.id.type_txt_expense);
            typeTV.setText(type);
        }
        private void setNote(String note){

            TextView noteTV=mView.findViewById(R.id.note_txt_expense);
           noteTV.setText(note);
        }
        private void setAmmount(int ammount){

            TextView ammountTV=mView.findViewById(R.id.ammount_txt_expense);
            String stAmmount=String.valueOf(ammount);
            ammountTV.setText(stAmmount);
        }
        private void setDate(String date){

            TextView dateTV=mView.findViewById(R.id.date_txt_expense);
            dateTV.setText(date);
        }
    }

    public void updateExpenseData(){

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.update_data_item,null);

        builder.setView(view);

        final AlertDialog dialog=builder.create();
        dialog.show();

        ammountET=view.findViewById(R.id.ammount_edt);
        noteET=view.findViewById(R.id.note_edt);
        typeET=view.findViewById(R.id.type_edt);

        ammountET.setText(String.valueOf(ammount));
        ammountET.setSelection(String.valueOf(ammount).length());
        noteET.setText(note);
        noteET.setSelection(note.length());
        typeET.setText(type);
        typeET.setSelection(type.length());

        updateBtn=view.findViewById(R.id.btn_update);
        deleteBtn=view.findViewById(R.id.btn_delete);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=typeET.getText().toString().trim();
                note=noteET.getText().toString().trim();
                ammount=Integer.parseInt(ammountET.getText().toString().trim());

                String date= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(ammount,type,note,post_key,date);

                mExpenseDB.child(post_key).setValue(data);
                dialog.dismiss();

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });




    }
}