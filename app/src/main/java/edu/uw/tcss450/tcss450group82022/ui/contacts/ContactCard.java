package edu.uw.tcss450.tcss450group82022.ui.contacts;

import java.io.Serializable;

public class ContactCard implements Serializable{
    private final String mContactName;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder{
        private final String mContactName;

        /**
         * Constructs a new Builder.
         *
         * @param contactName the chatId for the chat
         */
        public Builder(String contactName) {
            this.mContactName = contactName;
        }


        public ContactCard build() {
            return new ContactCard(this);
        }

    }

    private ContactCard(final Builder builder) {
        this.mContactName = builder.mContactName;
    }

    public String getContactName() {
        return mContactName;
    }


}
