package edu.uw.tcss450.tcss450group82022.ui.contacts;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Contact implements Serializable {
    private final int mPrimaryKey;
    private final int mMemberIdA;
    private final int mMemberIdB;
    private final int mVerified;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     * @author Ryan MacLeod
     */
    public static class Builder {
        private final int mPrimaryKey;
        private int mMemberIdA = 0;
        private int mMemberIdB = 1;
        private int mVerified = 0;


        /**
         * Constructs a new Builder.
         *
         * @param primaryKey the primaryKey of the contact
         */
        public Builder(int primaryKey) {
            this.mPrimaryKey= primaryKey;
        }

        /**
         * Add an optional memberID_A for the contact.
         * @param val an optional memberID_A for the contact
         * @return the Builder of this Contact
         */
        public Builder addMemberIdA(final int val) {
            mMemberIdA = val;
            return this;
        }

        /**
         * Add an optional memberID_B of the Contact.
         * @param val an optional memberID_B of the Contact.
         * @return the Builder of this Contact
         */
        public Builder addMemberIdB(final int val) {
            mMemberIdB = val;
            return this;
        }

        /**
         * Add an optional verified for the contact.
         * @param val an optional verified for the contact
         * @return the Builder of this Contact
         */
        public Builder addVerified(final int val) {
            mVerified = val;
            return this;
        }

        public Contact build() {
            return new Contact(this);
        }

    }

    public Contact (final Builder builder) {
        this.mPrimaryKey = builder.mPrimaryKey;
        this.mMemberIdA = builder.mMemberIdA;
        this.mMemberIdB = builder.mMemberIdB;
        this.mVerified = builder.mVerified;
    }

    public Contact (final int primaryKey, final int memberIdA, final int memberIdB,
                    final int verified) {
        mPrimaryKey = primaryKey;
        mMemberIdA = memberIdA;
        mMemberIdB = memberIdB;
        mVerified = verified;
    }

    /**
     * Static factory method to turn a properly formatted JSON String into a
     * Contact object.
     * @param cmAsJson the String to be parsed into a Contact Object.
     * @return a Contact Object with the details contained in the JSON String.
     * @throws JSONException when cmAsString cannot be parsed into a Contact.
     */
    public static Contact createFromJsonString(final String cmAsJson) throws JSONException {
        final JSONObject msg = new JSONObject(cmAsJson);
        return new Contact(msg.getInt("primaryKey"),
                msg.getInt("memberID_A"),
                msg.getInt("memberID_B"),
                msg.getInt("verified"));
    }

    public int getPrimaryKey() {
        return mPrimaryKey;
    }

    public int getMemberIdA() {
        return mMemberIdA;
    }

    public int getMemberIdB() {
        return mMemberIdB;
    }

    public int getVerified() {
        return mVerified;
    }

    /**
     * Provides equality solely based on memberId.
     * @param other the other object to check for equality
     * @return true if other contact's email matches this message's email, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if (other instanceof Contact) {
            result = mPrimaryKey == ((Contact) other).mPrimaryKey;
        }
        return result;
    }
}
