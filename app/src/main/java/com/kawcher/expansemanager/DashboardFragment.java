package com.kawcher.expansemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class DashboardFragment extends Fragment {

    //floating button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //floating button textview

    private TextView fab_income_txt, fab_expense_txt;

    private TextView totalIncomeTV, totalExpenseTV;
    private String stTotalValue;
    private boolean isOpen = false;

    private Animation animOpen, animClose;


    private FirebaseAuth mAuth;
    private DatabaseReference expenseRef;
    private DatabaseReference incomeRef;

    //Recycler View

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        incomeRef = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        expenseRef = FirebaseDatabase.getInstance().getReference().child("expenseData").child(uid);

        totalIncomeTV = myView.findViewById(R.id.dashboard_total_income);
        totalExpenseTV = myView.findViewById(R.id.expense_set_result);

        fab_main_btn = myView.findViewById(R.id.fb_main_plsu_btn);
        fab_income_btn = myView.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myView.findViewById(R.id.expense_ft_btn);

        //connect floating text

        fab_income_txt = myView.findViewById(R.id.income_ft_text);
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text);

        mRecyclerIncome=myView.findViewById(R.id.recycler_income);
        mRecyclerExpense=myView.findViewById(R.id.recycler_expense);



        //animation connect

        animOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        animClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addData();

                if (isOpen) {

                    fab_income_btn.startAnimation(animClose);
                    fab_expense_btn.startAnimation(animClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(animClose);
                    fab_expense_txt.startAnimation(animClose);

                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                } else {


                    fab_income_btn.startAnimation(animOpen);
                    fab_expense_btn.startAnimation(animOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(animOpen);
                    fab_expense_txt.startAnimation(animOpen);

                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }
            }
        });


        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalValue = 0;
                for (DataSnapshot ss : snapshot.getChildren()) {

                    Data data = ss.getValue(Data.class);


                    totalValue = totalValue + data.getAmmount();
                    stTotalValue = String.valueOf(totalValue);


                }

                Log.e("TAG", "totalIncome: "+totalValue );

                totalIncomeTV.setText(stTotalValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalExpense=0;
                for(DataSnapshot ss:snapshot.getChildren()){

                    Data data=ss.getValue(Data.class);

                    totalExpense=totalExpense+data.getAmmount();


                }

                totalExpenseTV.setText(String.valueOf(totalExpense));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recyclerview





      /*  LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getActivity());

        linearLayoutManager1.setOrientation(RecyclerView.HORIZONTAL);

        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(linearLayoutManager1);
*/


        return myView;
    }

    //floating button animation

    private void ftAnimation() {
        if (isOpen) {

            fab_income_btn.startAnimation(animClose);
            fab_expense_btn.startAnimation(animClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(animClose);
            fab_expense_txt.startAnimation(animClose);

            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        } else {


            fab_income_btn.startAnimation(animOpen);
            fab_expense_btn.startAnimation(animOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(animOpen);
            fab_expense_txt.startAnimation(animOpen);

            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }


    private void addData() {

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                expenseDataInsert();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query=FirebaseDatabase.getInstance().getReference().child("IncomeData").child(FirebaseAuth.getInstance().getCurrentUser().getUid());;

        FirebaseRecyclerOptions<Data> options=new FirebaseRecyclerOptions.Builder<Data>().setQuery( query,Data.class).build();

        final FirebaseRecyclerAdapter<Data,MyIncomeViewHolder>incomeAdapter=new FirebaseRecyclerAdapter<Data, MyIncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyIncomeViewHolder holder, int position, @NonNull Data model) {

                holder.setType(model.getType());
                holder.setIncomeAmmount(model.getAmmount());
                holder.setIncomeDate(model.getDate());

            }

            @NonNull
            @Override
            public MyIncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


               View view=LayoutInflater.from(getActivity()).inflate(R.layout.dashboard_income,parent,false);

                return new MyIncomeViewHolder(view);
            }
        };

        incomeAdapter.startListening();
        mRecyclerIncome.setAdapter(incomeAdapter);

        Query query1=FirebaseDatabase.getInstance().getReference().child("expenseData").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseRecyclerOptions<Data> options1=new FirebaseRecyclerOptions.Builder<Data>().setQuery( query1,Data.class).build();

        FirebaseRecyclerAdapter<Data,MyExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, MyExpenseViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull MyExpenseViewHolder holder, int position, @NonNull Data model) {

                holder.setExpenseAmmount(model.getAmmount());
                holder.setExpenseDate(model.getDate());
                holder.setExpenseType(model.getType());
            }

            @NonNull
            @Override
            public MyExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(getActivity()).inflate(R.layout.dashboard_expense,parent,false);

                return new MyExpenseViewHolder(view);
            }
        };

        expenseAdapter.startListening();
        mRecyclerExpense.setAdapter(expenseAdapter);

    }

    private static class MyIncomeViewHolder extends RecyclerView.ViewHolder{

        View myView;
        public MyIncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            myView=itemView;
        }

        private void setType(String type){


            TextView mtype=myView.findViewById(R.id.type_income_ds);
            mtype.setText(type);
        }

        public void setIncomeAmmount(int ammount){

            TextView incomeAmmount=myView.findViewById(R.id.ammount_income_ds);
            incomeAmmount.setText(String.valueOf(ammount));
        }

        public void setIncomeDate(String date){

            TextView incomeDate=myView.findViewById(R.id.date_income_ds);
            incomeDate.setText(date);
        }


    }

    private static class MyExpenseViewHolder extends RecyclerView.ViewHolder{


        View expenseView;
        public MyExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            expenseView=itemView;
        }

        private void setExpenseType(String type){

            TextView expenseType=expenseView.findViewById(R.id.type_expense_ds);
            expenseType.setText(type);
        }

        private void  setExpenseDate(String date){

            TextView expenseDate=expenseView.findViewById(R.id.date_expense_ds);
            expenseDate.setText(date);
        }

        private void setExpenseAmmount(int ammount){

            TextView expenseAmmount=expenseView.findViewById(R.id.ammount_expense_ds);
            expenseAmmount.setText(String.valueOf(ammount));
        }
    }

    private void incomeDataInsert() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.custom_layout_for_insert_data, null);
        builder.setView(myView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        final EditText edtAmmount = myView.findViewById(R.id.ammount_edt);
        final EditText edtType = myView.findViewById(R.id.type_edt);
        final EditText edtNote = myView.findViewById(R.id.note_edt);


        Button btnSave = myView.findViewById(R.id.btn_Save);
        Button btnCancell = myView.findViewById(R.id.btn_Cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type = edtType.getText().toString().trim();
                String ammount = edtAmmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edtType.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(ammount)) {
                    edtAmmount.setError("Required Field..");
                    return;
                }

                int ourammount = Integer.parseInt(ammount);

                if (TextUtils.isEmpty(note)) {

                    edtNote.setError("Required Field..");
                    return;
                }


                String id = incomeRef.push().getKey();

                String mDate = DateFormat.getInstance().format(new Date());

                Data data = new Data(ourammount, type, note, id, mDate);

                incomeRef.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();


                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void expenseDataInsert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_layout_for_insert_data, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        dialog.setCancelable(false);

        final EditText edtAmmount = view.findViewById(R.id.ammount_edt);
        final EditText edtType = view.findViewById(R.id.type_edt);
        final EditText edtNote = view.findViewById(R.id.note_edt);


        Button btnSave = view.findViewById(R.id.btn_Save);
        Button btnCancell = view.findViewById(R.id.btn_Cancel);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type = edtType.getText().toString().trim();
                String ammount = edtAmmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edtType.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(ammount)) {
                    edtAmmount.setError("Required Field..");
                    return;
                }

                int ourammount = Integer.parseInt(ammount);

                if (TextUtils.isEmpty(note)) {

                    edtNote.setError("Required Field..");
                    return;
                }

                String id = expenseRef.push().getKey();
                String mDate = DateFormat.getInstance().format(new Date());

                Data data = new Data(ourammount, type, note, id, mDate);

                expenseRef.child(id).setValue(data);

                ftAnimation();
                dialog.dismiss();


            }
        });

        btnCancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}