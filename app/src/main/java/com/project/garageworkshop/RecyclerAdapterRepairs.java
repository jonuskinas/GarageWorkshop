package com.project.garageworkshop;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapterRepairs extends RecyclerView.Adapter<RecyclerAdapterRepairs.ViewHolder> {

    private RepairsRecyclerClickListener mListener;
    private ArrayList<Repairs> data;


    public RecyclerAdapterRepairs(ArrayList<Repairs> data) {
        this.data = data;
    }



    public interface RepairsRecyclerClickListener {
        void onOnItemClick(int position);
        void onLongItemClick(int position);
    }

    public void setOnItemClickListener(RepairsRecyclerClickListener listener) {
        mListener = listener;

    }

    public void removeElement(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView carNumb;
        TextView repaired;
        TextView cost;


        ViewHolder(View itemView, final RecyclerAdapterRepairs.RepairsRecyclerClickListener listener) {
            super(itemView);
            carNumb = itemView.findViewById(R.id.carNumb);
            repaired = itemView.findViewById(R.id.repaired);
            cost = itemView.findViewById(R.id.cost);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onOnItemClick(position);
                        }
                    }
                }
            });

        }

    }

    @NonNull
    @Override
    public RecyclerAdapterRepairs.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.repairs_list_item, viewGroup, false);
        ViewHolder rvh = new ViewHolder(view, mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterRepairs.ViewHolder viewHolder, int i) {
        if (data.get(i) != null) {
        Repairs rep = data.get(i);
        viewHolder.carNumb.setText(rep.getCarNumb());
        viewHolder.repaired.setText(rep.getRepaired());
        viewHolder.cost.setText(Double.toString(rep.getCost())); }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        if (data != null ) {
            return data.size();
        }
        else {
            return 0;
        }
    }



}
