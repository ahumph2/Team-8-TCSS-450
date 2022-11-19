package edu.uw.tcss450.tcss450group82022.ui.blog;

import java.io.Serializable;

/**
 * Class to encapsulate a Phish.net Blog Post. Building an Object requires a publish date and title.
 *
 * Optional fields include URL, teaser, and Author.
 *
 *
 * @author Charles Bryan
 * @version 14 September 2018
 */
public class BlogPost implements Serializable {

    private final String mPubDate;
    private final String mTitle;
    private final String mUrl;
    private final String mTeaser;
    private final String mAuthor;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mPubDate;
        private final String mTitle;
        private  String mUrl = "";
        private  String mTeaser = "";
        private  String mAuthor = "";


        /**
         * Constructs a new Builder.
         *
         * @param pubDate the published date of the blog post
         * @param title the title of the blog post
         */
        public Builder(String pubDate, String title) {
            this.mPubDate = pubDate;
            this.mTitle = title;
        }

        /**
         * Add an optional url for the full blog post.
         * @param val an optional url for the full blog post
         * @return the Builder of this BlogPost
         */
        public Builder addUrl(final String val) {
            mUrl = val;
            return this;
        }

        /**
         * Add an optional teaser for the full blog post.
         * @param val an optional url teaser for the full blog post.
         * @return the Builder of this BlogPost
         */
        public Builder addTeaser(final String val) {
            mTeaser = val;
            return this;
        }

        /**
         * Add an optional author of the blog post.
         * @param val an optional author of the blog post.
         * @return the Builder of this BlogPost
         */
        public Builder addAuthor(final String val) {
            mAuthor = val;
            return this;
        }

        public BlogPost build() {
            return new BlogPost(this);
        }

    }

    private BlogPost(final Builder builder) {
        this.mPubDate = builder.mPubDate;
        this.mTitle = builder.mTitle;
        this.mUrl = builder.mUrl;
        this.mTeaser = builder.mTeaser;
        this.mAuthor = builder.mAuthor;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTeaser() {
        return mTeaser;
    }

    public String getAuthor() {
        return mAuthor;
    }


}
