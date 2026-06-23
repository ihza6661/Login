package com.ryan.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ForexAdapter extends RecyclerView.Adapter<ForexAdapter.ForexViewHolder> {

    private ArrayList<ForexModel> forexList;

    public ForexAdapter(ArrayList<ForexModel> forexList) {
        this.forexList = forexList;
    }

    @NonNull
    @Override
    public ForexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forex, parent, false);
        return new ForexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForexViewHolder holder, int position) {
        ForexModel item = forexList.get(position);
        holder.tvCode.setText(item.getCode());
        holder.tvName.setText(item.getName());
        holder.tvRate.setText(item.getRate());
    }

    @Override
    public int getItemCount() {
        return forexList.size();
    }

    public static class ForexViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvName, tvRate;

        public ForexViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCurrencyCode);
            tvName = itemView.findViewById(R.id.tvCurrencyName);
            tvRate = itemView.findViewById(R.id.tvCurrencyRate);
        }
    }
}