package edu.uw.tcss450.tcss450group82022.ui.contacts;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class ContactCard implements Serializable{

    private final int memberID;

    private final String mContactFirstName;

    private final String mContactLastName;

    private final String mContactEmail;

    // Means we are memberIdB, meaning we have a request, can respond
    private boolean isMemberIdA;

    private boolean pending;

    public ContactCard(String mContactFirstName, String mContactLastName, String mContactEmail, int memberID, boolean pending, boolean isMemberIdA) {
        this.mContactFirstName = mContactFirstName;
        this.mContactLastName = mContactLastName;
        this.mContactEmail = mContactEmail;
        this.memberID = memberID;
        this.pending = pending;
        this.isMemberIdA = isMemberIdA;
    }

    public String getmContactFirstName() {
        return mContactFirstName;
    }

    public String getmContactLastName() {
        return mContactLastName;
    }

    public String getmContactEmail(){
        return mContactEmail;
    }

    public String getContactFullName(){
        return getmContactFirstName() + " " + getmContactLastName();
    }

    public int getMemberID(){
        return memberID;
    }


    public boolean getIsMemberIdA() {return isMemberIdA;}


    public boolean getPending() {return pending;}

    /**
     * Provides equality solely based on memberId.
     * @param other the other object to check for equality
     * @return true if other contact's email matches this message's email, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if (other instanceof ContactCard) {
            result = memberID == ((ContactCard) other).memberID;
        }
        return result;
    }


}
