package edu.staybalanced.staybalanced;

import android.content.Context;
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
import java.util.Collection;

// extends adapter, but also implement filterable for search bar
public class ExcerciseAdapter extends RecyclerView.Adapter<ExcerciseAdapter.ExcerciseRowHolder> implements Filterable {

    // two array, 1 contains all excercises, the other contains the filtered for the filter search
    ArrayList<Excercise> excerciseData;
    Context context;
    ArrayList<Excercise> allExcercises;

    public ExcerciseAdapter(ArrayList<Excercise> excerciseDataIn, Context contextIn){
        this.context = contextIn;
        this.excerciseData = excerciseDataIn;
        this.allExcercises = new ArrayList<>(this.excerciseData);

    }


    @NonNull
    @Override
    public ExcerciseRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.excercise_row,parent,false);

        return new ExcerciseRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcerciseRowHolder holder, int position) {

        holder.cardExcerciseName.setText(excerciseData.get(position).getName());
        holder.cardExcerciseImage.setImageResource(excerciseData.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return excerciseData.size();
    }

    // THIS METHOD FOR SEARCHBAR
    // NEED TO IMPLEMENT due to implement of Filterable
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        // run on background thread automatically
        protected FilterResults performFiltering(CharSequence charSequence) {
            // array containing filtered excercises
            ArrayList<Excercise> filteredExcercises = new ArrayList<>();

            if (charSequence.toString().isEmpty()){
                filteredExcercises.addAll(allExcercises);
            }
            else {
                for (Excercise e: allExcercises) {
                    if (e.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredExcercises.add(e);
                    }
                }
            }
            // create filtered results object that will contain all excercises that match the search
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredExcercises;
            return filterResults;
        }

        // runs on ui thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            // change the list of displayed excercise data
            excerciseData.clear();
            excerciseData.addAll((Collection<? extends Excercise>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    class ExcerciseRowHolder extends RecyclerView.ViewHolder {
        TextView cardExcerciseName;
        ImageView cardExcerciseImage;

        public ExcerciseRowHolder(@NonNull View itemView) {
            super(itemView);

            cardExcerciseName = itemView.findViewById(R.id.txt_excercise_name);
            cardExcerciseImage = itemView.findViewById(R.id.image_excercise);
        }
    }
}
