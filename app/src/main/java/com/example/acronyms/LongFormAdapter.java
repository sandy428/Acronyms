package com.example.acronyms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LongFormAdapter extends RecyclerView.Adapter<LongFormAdapter.LongFormViewHolder> {

    Context mCtx;
    List<LongFormItem> longFormList;

    public LongFormAdapter(Context mCtx, List<LongFormItem> longFormList) {
        this.mCtx = mCtx;
        this.longFormList = longFormList;
    }

    @NonNull
    @Override
    public LongFormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_layout, parent, false);
        return new LongFormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LongFormViewHolder holder, int position) {
        LongFormItem longFormItem = longFormList.get(position);
        holder.textView.setText(longFormItem.getLf());
    }

    @Override
    public int getItemCount() {
        return longFormList.size();
    }

    class LongFormViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public LongFormViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}

