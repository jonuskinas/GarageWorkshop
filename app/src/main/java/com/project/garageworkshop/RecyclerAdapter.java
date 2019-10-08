package com.project.garageworkshop;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    private RecyclerClickListener mListener;
    private ArrayList<CarListItem> data;
    private ArrayList<CarListItem> dataIsFull;

   public RecyclerAdapter(ArrayList<CarListItem> data) {
        this.data = data;
        dataIsFull = new ArrayList<>(this.data);
    }

    public void added() {
       dataIsFull.clear();
       dataIsFull.addAll(data);
    }

    public interface RecyclerClickListener {
        void onOnItemClick(int position);
    }

    public void setOnItemClickListener(RecyclerClickListener listener) {
        mListener = listener;

    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView make;
        TextView model;
        TextView carNumb;
        ImageView carImage;


        ViewHolder(View itemView, final RecyclerClickListener listener) {
            super(itemView);
            make = itemView.findViewById(R.id.make);
            model = itemView.findViewById(R.id.model);
            carNumb = itemView.findViewById(R.id.carNumb);
            carImage = itemView.findViewById(R.id.carimage);
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.carslistitem, viewGroup, false);
        ViewHolder rvh = new ViewHolder(view, mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarListItem car = data.get(position);
        holder.make.setText(car.getMake());
        holder.model.setText(car.getModel());
        holder.carNumb.setText(car.getCarNumb());
        holder.carImage.setImageResource(car.getImageId());
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

    @Override
    public Filter getFilter() {
        return dataFilter;
    }
    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<CarListItem> filteredList =new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {

                filteredList.addAll(dataIsFull);
            }
            else {
                String filteredPattern =constraint.toString().toLowerCase().trim();

                for (CarListItem item: data) {
                    if (item.getCarNumb().toLowerCase().startsWith(filteredPattern) ) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults result = new FilterResults();
            result.values = filteredList;
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };




    CarListItem getItem(int id) {


        return data.get(id);
    }

}
