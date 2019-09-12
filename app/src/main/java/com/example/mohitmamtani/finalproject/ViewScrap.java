package com.example.mohitmamtani.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mohitmamtani.finalproject.common.Utils;
import com.example.mohitmamtani.finalproject.db.DatabaseHelper;
import com.example.mohitmamtani.finalproject.model.Scrap;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewScrap extends Activity {

    RecyclerView recyclerScrap;
    Button btnBack;
    ArrayList<Scrap> arrList = new ArrayList<>();
    private DatabaseHelper db;
    ScrapAdapter mAdapter;
    int userId;
    TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_scrap);

        btnBack = findViewById(R.id.btnBack);
        recyclerScrap = findViewById(R.id.recyclerScrap);
        txtEmpty = findViewById(R.id.txtEmpty);

        userId = Utils.getLoggedInUser(this);
        db = new DatabaseHelper(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        populateData();
    }

    public void populateData() {
        arrList = db.getAllScrap(userId);

        if (arrList != null && arrList.size() > 0) {
            recyclerScrap.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);
            mAdapter = new ScrapAdapter(arrList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerScrap.setLayoutManager(mLayoutManager);
            recyclerScrap.setItemAnimator(new DefaultItemAnimator());
            recyclerScrap.setAdapter(mAdapter);
        } else {
            recyclerScrap.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            populateData();
        }
    }

    public class ScrapAdapter extends RecyclerView.Adapter<ScrapAdapter.MyViewHolder> {

        private List<Scrap> m_list;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, date;
            public ImageView imgScrap;
            public RelativeLayout rlMain;

            public MyViewHolder(View view) {
                super(view);
                rlMain = view.findViewById(R.id.rlMain);
                title = view.findViewById(R.id.txtTitle);
                date = view.findViewById(R.id.txtDate);
                imgScrap = view.findViewById(R.id.imgScrap);
            }
        }


        public ScrapAdapter(List<Scrap> moviesList) {
            this.m_list = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scrap_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Scrap scrap = m_list.get(position);
            holder.title.setText(scrap.getTitle());
            holder.date.setText(scrap.getTimestamp());
            Picasso.get().load(new File(scrap.getImagePath())).into(holder.imgScrap);
            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent next = new Intent(ViewScrap.this, DeleteScrap.class);
                    next.putExtra("scrap_id", scrap.getId());
                    startActivityForResult(next, 100);
                }
            });
//            holder.year.setText(scrap.getim());
        }

        @Override
        public int getItemCount() {
            return m_list.size();
        }
    }
}
