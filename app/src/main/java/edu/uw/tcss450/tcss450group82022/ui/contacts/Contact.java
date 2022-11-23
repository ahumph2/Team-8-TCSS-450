package edu.uw.tcss450.tcss450group82022.ui.contacts;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import edu.uw.tcss450.tcss450group82022.ui.chat.ChatMessage;

public class Contact implements Serializable {
    private final String mName;
    private final String mPhoneNumber;
    private final String mEmail;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     * @author Ryan MacLeod
     */
    public static class Builder {
        private final String mEmail;
        private  String mName = "";
        private  String mPhoneNumber = "";


        /**
         * Constructs a new Builder.
         *
         * @param email the email of the contact
         */
        public Builder(String email) {
            this.mEmail = email;
        }

        /**
         * Add an optional first name for the contact.
         * @param val an optional first name for the contact
         * @return the Builder of this Contact
         */
        public Builder addName(final String val) {
            mName = val;
            return this;
        }

        /**
         * Add an optional phone number of the Contact.
         * @param val an optional phone number of the Contact.
         * @return the Builder of this Contact
         */
        public Builder addPhoneNumber(final String val) {
            mPhoneNumber = val;
            return this;
        }

        public Contact build() {
            return new Contact(this);
        }

    }

    public Contact (final Builder builder) {
        this.mName = builder.mName;
        this.mEmail = builder.mEmail;
        this.mPhoneNumber = builder.mPhoneNumber;
    }

    public Contact (final String firstName, final String phoneNumber,
                    final String email) {
        mName = firstName;
        mPhoneNumber = phoneNumber;
        mEmail = email;
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
        return new Contact(msg.getString("name"),
                msg.getString("phoneNumber"),
                msg.getString("email"));
    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getEmail() {
        return mEmail;
    }

    /**
     * Provides equality solely based on email.
     * @param other the other object to check for equality
     * @return true if other contact's email matches this message's email, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if (other instanceof ChatMessage) {
            result = mEmail == ((Contact) other).mEmail;
        }
        return result;
    }
}
