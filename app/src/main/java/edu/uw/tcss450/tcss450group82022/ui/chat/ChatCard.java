package edu.uw.tcss450.tcss450group82022.ui.chat;

import java.io.Serializable;

public class ChatCard implements Serializable{
    private final String mChatId;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder{
        private final String mChatId;

        /**
         * Constructs a new Builder.
         *
         * @param chatId the chatId for the chat
         */
        public Builder(String chatId) {
            this.mChatId = chatId;
        }


        public ChatCard build() {
            return new ChatCard(this);
        }

    }

    private ChatCard(final Builder builder) {
        this.mChatId = builder.mChatId;
    }

    public String getChatId() {
        return mChatId;
    }


}
