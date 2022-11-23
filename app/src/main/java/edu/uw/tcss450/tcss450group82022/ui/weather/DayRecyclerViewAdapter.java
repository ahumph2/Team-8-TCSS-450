package edu.uw.tcss450.uiandnavigationlab.ui.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import edu.uw.tcss450.uiandnavigationlab.R;

import edu.uw.tcss450.uiandnavigationlab.databinding.FragmentDayPostBinding;

public class DayRecyclerViewAdapter extends RecyclerView.Adapter<DayRecyclerViewAdapter.DayViewHolder> {

    private final List<DayPost> mDays;

    public DayRecyclerViewAdapter(List<DayPost> items){
        this.mDays = items;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //mDays.clear();
        return new DayViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_day_post, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.setDay(mDays.get(position));
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public void removeAllContents(){
        mDays.clear();
    }

    public class DayViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentDayPostBinding binding;
        private DayPost mDay;

        public DayViewHolder(View view) {
            super(view);
            mView = view;
            binding = edu.uw.tcss450.uiandnavigationlab.databinding.FragmentDayPostBinding.bind(view);

        }

        void setDay(final DayPost day){
            mDay = day;
            binding.textViewDay.setText(day.getDay());
            binding.textViewHiTemp.setText(day.getHiTemp());
            binding.textViewLoTemp.setText(day.getLoTemp());
            binding.textViewDayCond.setText(day.getDayCondition());
            binding.textViewNightCond.setText(day.getNightCondition());

        }
    }

}