package com.kawcher.expansemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kawcher.expansemanager.Model.Data;

import java.text.DateFormat;
import java.util.Date;


public class IncomeFragment extends Fragment {

    //Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    private RecyclerView recyclerView;
    private TextView incomeTotalSumTV;
    String stTotalValue;

    //update edit text

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate, btnDelete;

    //edit textvalue

    private String type,note;
    private int ammount;

    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        recyclerView = myView.findViewById(R.id.recycler_id_income);

        incomeTotalSumTV = myView.findViewById(R.id.income_txt_result);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalValue = 0;
                for (DataSnapshot ss : snapshot.getChildren()) {

                    Data data = ss.getValue(Data.class);


                    totalValue = totalValue + data.getAmmount();
                    stTotalValue = String.valueOf(totalValue);


                }

                incomeTotalSumTV.setText(stTotalValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query1 = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query1, Data.class)
                .build();


        FirebaseRecyclerAdapter<Data, MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, final int position, @NonNull final Data model) {


                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmmount(model.getAmmount());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key=getRef(position).getKey();
                        type=model.getType();
                        note=model.getNote();
                        ammount=model.getAmmount();
                        updateDataItem();
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);

                return new MyViewHolder(view);
            }

        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setType(String type) {

            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote(String note) {

            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date) {

            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmmount(int ammount) {

            TextView mAmmouunt = mView.findViewById(R.id.ammount_txt_income);


            String stAmmount = String.valueOf(ammount);

            mAmmouunt.setText(stAmmount);
        }

    }


    private void updateDataItem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.update_data_item, null);

        builder.setView(view);

        edtAmmount = view.findViewById(R.id.ammount_edt);
        edtNote = view.findViewById(R.id.note_edt);
        edtType = view.findViewById(R.id.type_edt);

        edtType.setText(type);
        edtType.setSelection(type.length());
        edtNote.setText(note);
        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());


        btnUpdate = view.findViewById(R.id.btn_update);
        btnDelete = view.findViewById(R.id.btn_delete);

        final AlertDialog dialog = builder.create();
        dialog.show();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                type=edtType.getText().toString();
                note=edtNote.getText().toString();

              //  String mdammount=String.valueOf(ammount);

              String  mdammount=edtAmmount.getText().toString().trim();

                int myAmmount=Integer.parseInt(mdammount);
                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(myAmmount,type,note,post_key,mDate);


                mIncomeDatabase.child(post_key).setValue(data);


                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });


    }

}