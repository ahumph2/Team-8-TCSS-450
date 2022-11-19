package edu.uw.tcss450.tcss450group82022.ui.incrementor;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class IncrementorViewModel extends ViewModel{
    private MutableLiveData<Integer> mCount;
    /**
     * No-arg constructor that initializes this object. Do not
     * Explicitly call this.
     */
    public IncrementorViewModel() {
        mCount = new MutableLiveData<Integer>();
        mCount.setValue(0);
    }
    /**
     * Add an observer to this live data. This is a pass through method for
     * this class’s MutableLiveData field mCount.
     *
     * See LiveData.observe for more implementation details.
     * @param owner the LifecycleOwner which controls the observer
     * @param observer the observer that will receive the events
     */
    public void addCountObserver(@NonNull LifecycleOwner owner,
                                 @NonNull Observer<? super Integer> observer) {
        mCount.observe(owner, observer);
    }
    // You may want to consider pass through methods for all observe and remove methods
    // found in LiveData.
    /**
     * Return the current count.
     * @return the current count.
     */
    public int getCount() {
        return mCount.getValue();
    }
    /**
     * Increment the current count by 1.
     */
    public void increment() {
        mCount.setValue(mCount.getValue() + 1);
    }
}
