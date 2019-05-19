package com.example.acronyms;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LongFormAdapter adapter;
    SearchView searchView;
    LongFormViewModel viewModel;
    TextView emptyLabel;
    ViewModelProvider.Factory factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        searchView = findViewById(R.id.searchview);
        emptyLabel = findViewById(R.id.emptyLabel);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        factory = new DataViewModelFactory();
        viewModel = ViewModelProviders.of(MainActivity.this, factory).get(LongFormViewModel.class);

        viewModel.longFormLiveData.observe(MainActivity.this, new Observer<List<LongForm>>() {
            @Override
            public void onChanged(@Nullable List<LongForm> longForms) {
                if(longForms.size() > 0) {
                    adapter = new LongFormAdapter(MainActivity.this, longForms.get(0).getLfs());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyLabel.setVisibility(View.GONE);
                }
            }
        });

        viewModel.longFormErrorData.observe(MainActivity.this, new Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {
                emptyLabel.setVisibility(View.VISIBLE);
                emptyLabel.setText("Something went wrong, please try again");
                recyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.longFormEmptyData.observe(MainActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean empty) {
                if(empty) {
                    emptyLabel.setVisibility(View.VISIBLE);
                    emptyLabel.setText("Nothing to show");
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String acronym) {
                viewModel.loadLongFormData(acronym);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String acronym) {
                return false;
            }
        });
    }

    class DataViewModelFactory implements ViewModelProvider.Factory {
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if(modelClass.isAssignableFrom(LongFormViewModel.class)) {
                return (T)(new LongFormViewModel());
            }
            return null;
        }
    }
}
