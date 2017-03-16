package com.codebreaker.chavam;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishek on 3/12/17.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>{

    private Context context;

    List<String> items;

    public Adapter(Context context){
        this.context = context;
        items = new ArrayList<>();
    }

    public void setItems(List<String> items){
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv;
        FrameLayout container;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            container = (FrameLayout) itemView.findViewById(R.id.container);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(getAdapterPosition())));
                    intent.setDataAndType(Uri.parse(items.get(getAdapterPosition())), "video/*");
                    context.startActivity(intent);

                }
            });
        }



    }
}
