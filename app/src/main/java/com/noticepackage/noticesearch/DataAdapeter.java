package com.noticepackage.noticesearch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noticesearch.R;

import java.util.ArrayList;

public class DataAdapeter extends RecyclerView.Adapter<DataAdapeter.ViewHolder> {
    Context context;
    ArrayList<SearchData> datas= new ArrayList<SearchData>();
    OnDataClickListener listener;



    public void Deduplication(){//내가 위에꺼 수정해서 만든거//정렬후에 실행해야함
        for(int i=0; i<datas.size()-1;i++){
            int iplus=i+1;
            while( datas.get(i).time.equals(datas.get(iplus).time) ){
                if ( datas.get(i).title.equals(datas.get(iplus).title) ) {
                    datas.remove(iplus);
                }else {
                    iplus++;
                }
                if(iplus>=datas.size())break;
            }
        }
    }


    public void removeAll(){
        for(int i=datas.size()-1;i>-1;i--)
            datas.remove(i);
    }





    public static interface OnDataClickListener{
        public void onItemClick(ViewHolder holder, View view,int position);

        public void onStarClick(ViewHolder holder, View view,int position);
    }



    public DataAdapeter(ArrayList<SearchData> list, Context context){
        this.datas = list;
        this.context = context;
    }



   @Override
   public int getItemCount() {
       return datas.size();
   }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dataView = inflater.inflate(R.layout.search_data, parent, false);



        return new ViewHolder(dataView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchData data = datas.get(position);
        holder.onBindData(data);

        holder.setOnDataClickListner(listener);




    }

    public void addData(SearchData data){
        datas.add(data);
    }

    public void addDatas(ArrayList<SearchData> datas){
        this.datas = datas;
    }
    public SearchData getData(int position){
        return datas.get(position);
    }

    public void setOnDataClickListener(OnDataClickListener listener){
        this.listener = listener;
    }







    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView time;
        TextView site;
        TextView views;
        ImageView farovites;


        OnDataClickListener listener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.key_word);
            time = (TextView) itemView.findViewById(R.id.search_time);
            site = (TextView) itemView.findViewById(R.id.search_site);
            views = (TextView) itemView.findViewById(R.id.search_views);
            farovites = (ImageView) itemView.findViewById(R.id.imageViewFarovites);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
            farovites.setOnClickListener(new View.OnClickListener() {//삭제
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    int imgTag=(Integer) farovites.getTag();
                    switch (imgTag){
                        case R.drawable.star:
                            farovites.setImageResource(R.drawable.star2);
                            farovites.setTag(R.drawable.star2);
                            break;
                        case R.drawable.star2:
                            farovites.setImageResource(R.drawable.star);
                            farovites.setTag(R.drawable.star);
                            break;
                    }
                    if(listener!=null){
                        listener.onStarClick(ViewHolder.this, v, position);
                    }
                }
            });


        }

        public void onBindData(SearchData data){
            title.setText(data.getTitle());
            time.setText(data.getTime());
            site.setText(data.getSite());
            views.setText(data.getViews());
            farovites.setImageResource(data.getImageResid());
            farovites.setTag(data.getImageResid());
        }

        public void setOnDataClickListner(OnDataClickListener listener){
            this.listener = listener;
        }


    }










}
