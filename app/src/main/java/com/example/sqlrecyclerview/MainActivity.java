package com.example.sqlrecyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ConnectionClass connectionClass;
    private Connection connection;

    private ArrayList<RowItemList> arrayList;
    private CustomAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private boolean check = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        connectionClass = new ConnectionClass();
        connection = connectionClass.connection();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>();
        myTask();
        progressDialog = ProgressDialog.show(this,"","Loading",true);
    }
    public void myTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(connection == null){
                        check = false;
                    }else{
                        String query = "select * from table1";
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(query);
                        if(resultSet != null){
                            while (resultSet.next()){
                                arrayList.add(new RowItemList(resultSet.getString("name"),resultSet.getString("url"),resultSet.getInt("id")));
                            }
                            check = true;
                        }else{
                            check = false;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    check = false;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if(check == false){
                            System.out.println("An error occurred while retrieving data");
                        }
                        else {
                            try {
                                adapter = new CustomAdapter(arrayList);
                                recyclerView.setAdapter(adapter);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
        private List<RowItemList> rowItemLists;
        public class ViewHolder extends RecyclerView.ViewHolder{
            private final TextView textView;
            private final ImageView imageView;
            public ViewHolder(View view){
                super(view);
                textView = view.findViewById(R.id.textView);
                imageView = view.findViewById(R.id.imageView);
            }
        }
        public CustomAdapter(List<RowItemList> itemLists){
            rowItemLists = itemLists;
        }
        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
            final RowItemList rowItemList = rowItemLists.get(position);
            holder.textView.setText(rowItemList.getName());
            Picasso.get().load(rowItemList.getUrl()).into(holder.imageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this,"id = "+rowItemList.getId(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return rowItemLists.size();
        }
    }
}