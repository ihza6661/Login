package com.ryan.login;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder> {

    private ArrayList<MahasiswaModel> mahasiswaList;

    public MahasiswaAdapter(ArrayList<MahasiswaModel> mahasiswaList) {
        this.mahasiswaList = mahasiswaList;
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

        holder.tvNomor.setText((position + 1) + ".");
        holder.tvNama.setText(mhs.getNama());
        holder.tvNim.setText(mhs.getNim());
        holder.tvGenderTeks.setText(mhs.getJenisKelamin());
        holder.tvBadgeJurusan.setText(mhs.getJurusan());

        // Logika Warna Kotak Jurusan (TI = Biru, SI = Merah)
        if (mhs.getJurusan().equalsIgnoreCase("TI")) {
            holder.tvBadgeJurusan.setBackgroundColor(Color.parseColor("#0000FF"));
        } else {
            holder.tvBadgeJurusan.setBackgroundColor(Color.parseColor("#FF0000"));
        }

        // MENGGUNAKAN GAMBAR ASLI: boy.png & girl.png
        if (mhs.getJenisKelamin().trim().equalsIgnoreCase("Laki-Laki")) {
            holder.imgGender.setImageResource(R.drawable.boy);
        } else if (mhs.getJenisKelamin().trim().equalsIgnoreCase("Perempuan")) {
            holder.imgGender.setImageResource(R.drawable.girl);
        } else {
            holder.imgGender.setImageResource(R.drawable.student); // Cadangan jika ada data jender kosong
        }
    }

    @Override
    public int getItemCount() {
        return mahasiswaList.size();
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