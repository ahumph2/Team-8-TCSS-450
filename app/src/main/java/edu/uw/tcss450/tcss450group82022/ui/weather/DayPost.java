package edu.uw.tcss450.tcss450group82022.ui.weather;

public class DayPost {

    private final String mHiTemp;
    private final String mLoTemp;
    private final String mDayCondition;
    private final String mDay;
    private final String mNightCondition;

    public static class Builder{

        private final String mHiTemp;
        private final String mLoTemp;
        private final String mDayCondition;
        private final String mDay;
        private final String mNightCondition;

        public Builder(String day, String hiTemp, String loTemp,
                       String dayCond, String nightCond){
            this.mDay = day;
            this.mHiTemp = hiTemp;
            this.mLoTemp = loTemp;
            this.mDayCondition = dayCond;
            this.mNightCondition= nightCond;
        }

        public DayPost build(){
            return new DayPost(this);
        }
    }

    private DayPost(final Builder builder){
        this.mDay = builder.mDay;
        this.mHiTemp = builder.mHiTemp;
        this.mLoTemp = builder.mLoTemp;
        this.mDayCondition = builder.mDayCondition;
        this.mNightCondition= builder.mNightCondition;
    }

    public String getDay(){
        return mDay;
    }

    public String getHiTemp(){
        return mHiTemp;
    }

    public String getLoTemp(){
        return mLoTemp;
    }

    public String getDayCondition(){

        return mDayCondition;
    }

    public String getNightCondition(){
        return mNightCondition;
    }

}
