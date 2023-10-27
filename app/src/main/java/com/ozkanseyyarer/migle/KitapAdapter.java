package com.ozkanseyyarer.migle;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class KitapAdapter extends RecyclerView.Adapter<KitapAdapter.kitapHolder> {

    private ArrayList<Kitap> kitapList;
    private Context context;
    private OnItemClickListener listener;


    public KitapAdapter(ArrayList<Kitap> kitapList, Context context) {
        this.kitapList = kitapList;
        this.context = context;
    }

    @NonNull
    @Override
    public kitapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.kitap_item, parent, false);
        return new kitapHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull kitapHolder holder, int position) {

        Kitap kitap = kitapList.get(position);
        holder.setData(kitap);



    }

    @Override
    public int getItemCount() {
        return kitapList.size();
    }

    class kitapHolder extends RecyclerView.ViewHolder {

        TextView txtKitapAdi, txtKitapYazari, txtKitapOzeti;
        ImageView imgKitapResim;

        public kitapHolder(@NonNull View itemView) {
            super(itemView);


            txtKitapAdi = itemView.findViewById(R.id.kitap_item_textViewKitapAdi);
            imgKitapResim = itemView.findViewById(R.id.kitap_item_imageViewKitapResim);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null &&  position != RecyclerView.NO_POSITION){
                        listener.onItemClick(kitapList.get(position));
                    }
                }
            });

        }


        public void setData(Kitap kitap) {
            this.txtKitapAdi.setText(kitap.getKitapAdi());
            this.imgKitapResim.setImageBitmap(kitap.getKitapResim());
        }


    }

    public interface OnItemClickListener{
        void onItemClick(Kitap kitap);
    }

    public void setOnItemClickListener(OnItemClickListener listener){

        this.listener = listener;
    }
}

