package com.ryan.login;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder> implements Filterable {

    private ArrayList<MahasiswaModel> mahasiswaList;
    private ArrayList<MahasiswaModel> mahasiswaListFull;

    public MahasiswaAdapter(ArrayList<MahasiswaModel> mahasiswaList) {
        this.mahasiswaList = mahasiswaList;
        this.mahasiswaListFull = new ArrayList<>(mahasiswaList);
    }

    @NonNull
    @Override
    public MahasiswaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa, parent, false);
        return new MahasiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MahasiswaViewHolder holder, int position) {
        MahasiswaModel mhs = mahasiswaList.get(position);

        String nomorText = (position + 1) + ".";
        holder.tvNomor.setText(nomorText);
        holder.tvNama.setText(mhs.getNama());
        holder.tvNim.setText(mhs.getNim());
        holder.tvGenderTeks.setText(mhs.getJenisKelamin());
        holder.tvBadgeJurusan.setText(mhs.getJurusan());

        if (mhs.getJurusan().equalsIgnoreCase("TI")) {
            holder.tvBadgeJurusan.setBackgroundColor(Color.parseColor("#0000FF"));
        } else {
            holder.tvBadgeJurusan.setBackgroundColor(Color.parseColor("#FF0000"));
        }

        if (mhs.getJenisKelamin().trim().equalsIgnoreCase("Laki-Laki")) {
            holder.imgGender.setImageResource(R.drawable.boy);
        } else if (mhs.getJenisKelamin().trim().equalsIgnoreCase("Perempuan")) {
            holder.imgGender.setImageResource(R.drawable.girl);
        } else {
            holder.imgGender.setImageResource(R.drawable.student);
        }
    }

    @Override
    public int getItemCount() {
        return mahasiswaList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<MahasiswaModel> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mahasiswaListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (MahasiswaModel item : mahasiswaListFull) {
                        if (item.getNama().toLowerCase().contains(filterPattern) || 
                            item.getNim().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mahasiswaList.clear();
                mahasiswaList.addAll((List<MahasiswaModel>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void updateList(ArrayList<MahasiswaModel> newList) {
        mahasiswaListFull = new ArrayList<>(newList);
        mahasiswaList.clear();
        mahasiswaList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MahasiswaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomor, tvNama, tvNim, tvGenderTeks, tvBadgeJurusan;
        ImageView imgGender;

        public MahasiswaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomor = itemView.findViewById(R.id.itemTvNomor);
            imgGender = itemView.findViewById(R.id.itemImgGender);
            tvNama = itemView.findViewById(R.id.itemTvNama);
            tvNim = itemView.findViewById(R.id.itemTvNim);
            tvGenderTeks = itemView.findViewById(R.id.itemTvGenderTeks);
            tvBadgeJurusan = itemView.findViewById(R.id.itemTvBadgeJurusan);
        }
    }
}