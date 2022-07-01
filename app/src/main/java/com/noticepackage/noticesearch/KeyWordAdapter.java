package com.noticepackage.noticesearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noticesearch.R;

import java.security.Key;
import java.util.ArrayList;

public class KeyWordAdapter extends RecyclerView.Adapter<KeyWordAdapter.ViewHolder>{

    Context context;
    ArrayList<KeyWord> list= new ArrayList<KeyWord>();
    KeyWordAdapter.OnItemClickListener listener;





    public static interface OnItemClickListener{
        public void onItemClick(KeyWordAdapter.ViewHolder holder, View view, int position);
    }



    public KeyWordAdapter(ArrayList<KeyWord> list, Context context){
        this.list = list;
        this.context = context;
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    @NonNull
    @Override
    public KeyWordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dataView = inflater.inflate(R.layout.keyword_list, parent, false);

        return new KeyWordAdapter.ViewHolder(dataView);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyWordAdapter.ViewHolder holder, int position) {
        KeyWord word = list.get(position);
        holder.setList(word);

        holder.setOnItemClickListner(listener);
    }

    public void addKeyWord(KeyWord word){
        list.add(word);
    }

    public void setList(ArrayList<KeyWord> list){
        this.list = list;
    }
    public KeyWord getData(int position){
        return list.get(position);
    }

    public void removeData(int position){
        list.remove(position);
    }

    public void setOnItemClickListener(KeyWordAdapter.OnItemClickListener listener){
        this.listener = listener;
    }







    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView key_word;



        KeyWordAdapter.OnItemClickListener listener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            key_word = (TextView) itemView.findViewById(R.id.key_word);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(listener!=null){
                        listener.onItemClick(KeyWordAdapter.ViewHolder.this, v, position);
                    }
                }
            });



        }

        public void setList(KeyWord word){
            key_word.setText(word.getWord());


        }

        public void setOnItemClickListner(KeyWordAdapter.OnItemClickListener listener){
            this.listener = listener;
        }


    }
}
