package edu.staybalanced.staybalanced;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

// extends adapter, but also implement filterable for search bar
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseRowHolder> implements Filterable {

    // two array, 1 contains all exercises, the other contains the filtered for the filter search
    ArrayList<ExerciseItem> exerciseData;
    Context context;
    ArrayList<ExerciseItem> allExercises;
    String host;

    // History and Select need different things to happen when a card in this RecyclerView is clicked
    // This is accomplished by assigning an appropriate ClickListener based on the value of hostFragment
    // in ExerciseRowHolder's constructor
    public ExerciseAdapter(ArrayList<ExerciseItem> exerciseDataIn, Context contextIn, String hostFragment){
        this.context = contextIn;
        this.exerciseData = exerciseDataIn;
        this.allExercises = new ArrayList<>(this.exerciseData);
        this.host = hostFragment;
    }


    @NonNull
    @Override
    public ExerciseRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_row,parent,false);
        return new ExerciseRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseRowHolder holder, int position) {
        holder.cardExerciseName.setText(exerciseData.get(position).getName());
        holder.cardExerciseImage.setImageResource(exerciseData.get(position).getImage());
        holder.cardExerciseId.setText(String.valueOf(exerciseData.get(position).getId()));
        holder.secondPerSetView.setText(String.valueOf(exerciseData.get(position).getSecondsToExercise()));
    }

    @Override
    public int getItemCount() {
        return exerciseData.size();
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
            // array containing filtered exercises
            ArrayList<ExerciseItem> filteredExercises = new ArrayList<>();

            if (charSequence.toString().isEmpty()){
                filteredExercises.addAll(allExercises);
            }
            else {
                for (ExerciseItem e: allExercises) {
                    if (e.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredExercises.add(e);
                    }
                }
            }
            // create filtered results object that will contain all exercises that match the search
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredExercises;
            return filterResults;
        }

        // runs on ui thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            // change the list of displayed exercise data
            exerciseData.clear();
            exerciseData.addAll((Collection<? extends ExerciseItem>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    class ExerciseRowHolder extends RecyclerView.ViewHolder {
        TextView cardExerciseName, cardExerciseId;
        ImageView cardExerciseImage;
        EditText secondPerSetView;

        public ExerciseRowHolder(@NonNull View itemView) {
            super(itemView);
            //TODO THIS IS WHERE Info is added
            cardExerciseName = itemView.findViewById(R.id.txt_exercise_name);
            cardExerciseImage = itemView.findViewById(R.id.image_exercise);
            cardExerciseId = itemView.findViewById(R.id.txt_exercise_id);
            secondPerSetView = itemView.findViewById(R.id.editTextNumber2);
            // Depending upon the host Fragment, set a specific onClickListener for each RecyclerView
            // item
            if (host.equals("Select")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView idView = view.findViewById(R.id.txt_exercise_id);
                        int exerciseId = Integer.parseInt(idView.getText().toString());
                        DatabaseHelper update = new DatabaseHelper(context.getApplicationContext());
                        int userSeconds = Integer.parseInt(secondPerSetView.getText().toString());
                        update.updateSeconds(exerciseId, userSeconds);
                        Intent startExercise = new Intent (view.getContext(), ExerciseDo.class);
                        startExercise.putExtra("EXERCISE_ID", exerciseId);
                        view.getContext().startActivity(startExercise);
                    }
                });
            } else if (host.equals("History")) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View view) {
                        TextView idView = view.findViewById(R.id.txt_exercise_id);
                        int exerciseId = Integer.parseInt(idView.getText().toString());

                        Intent intent = new Intent("clicked_exercise_id");
                        intent.putExtra("EXERCISE_ID", exerciseId);
                        LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);

                        // textViewHistory.setText(exerciseId);
                    }
                });
            }
        }
    }

}
