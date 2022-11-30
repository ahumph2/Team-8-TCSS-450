package edu.uw.tcss450.tcss450group82022.ui.weather;

public class HourPost {

    private final String mTemp;
    private final String mCondition;
    private final String mHour;

    public static class Builder{
        private final String mTemp;
        private final String mCondtiion;
        private final String mHour;

        public Builder(String hour, String temp, String condition){
            this.mHour = hour;
            this.mTemp = temp;
            this.mCondtiion = condition;
        }

        public HourPost build(){
            return new HourPost(this);
        }
    }

    private HourPost(final Builder builder){
        this.mHour = builder.mHour;
        this.mTemp = builder.mTemp;
        this.mCondition = builder.mCondtiion;
    }

    public String getHour(){
        return mHour;
    }

    public String getTemp(){
        return mTemp;
    }

    public String getCondition(){
        return mCondition;
    }

}
