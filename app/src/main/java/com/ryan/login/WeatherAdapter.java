package com.ryan.login;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private List<WeatherItem> dataList;
    private Context context;

    public WeatherAdapter(Context context, List<WeatherItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cuaca, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeatherItem item = dataList.get(position);

        holder.tvMain.setText(item.main);
        holder.tvDeskripsi.setText(item.deskripsi);
        holder.tvWaktu.setText(item.waktu);
        holder.tvSuhu.setText(item.suhu);

        String kodeIcon = item.icon.replace(".png", "").trim();

        // UBAH KE http:// (bukan https://) untuk mem-bypass blokir SSL Emulator
        String iconUrl = "http://openweathermap.org/img/wn/" + kodeIcon + "@2x.png";

        Glide.with(context)
                .load(iconUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Jika masih gagal, error-nya akan muncul di tab Logcat bawah!
                        Log.e("GLIDE_ERROR", "Gagal memuat gambar dari URL: " + iconUrl, e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GLIDE_SUCCESS", "Berhasil memuat gambar: " + iconUrl);
                        return false;
                    }
                })
                .into(holder.ivIcon);
    }

    @Override
    public int getItemCount() { return dataList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMain, tvDeskripsi, tvWaktu, tvSuhu;
        ImageView ivIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            tvMain = itemView.findViewById(R.id.tvMain);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvSuhu = itemView.findViewById(R.id.tvSuhu);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}