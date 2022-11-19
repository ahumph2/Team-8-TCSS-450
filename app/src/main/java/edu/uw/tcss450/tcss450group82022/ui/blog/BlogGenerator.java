package edu.uw.tcss450.tcss450group82022.ui.blog;

import java.util.Arrays;
import java.util.List;

/**
 * This class is used to create Dummy Blogs. Use it for development.
 * In future labs, connect to an API to gain actual Blog Data.
 */
public final class BlogGenerator {

    private static final BlogPost[] BLOGS;
    public static final int COUNT = 20;


    static {
        BLOGS = new BlogPost[COUNT];
        for (int i = 0; i < BLOGS.length; i++) {
            BLOGS[i] = new BlogPost
                    .Builder("2020-04-" + (i + 1) + " 12:59 pm",
                        "Blog Title: Blog Post #" + (i + 43))
                    .addTeaser("<p>Phish got right down to business last night at Dick&rsquo;s&hellip; so we&rsquo;ll do the same. Roaring out of the gates with &ldquo;Ghost,&rdquo; the band offered only the second show-opening Ghost since the &lsquo;90s, the other also being at Dick&rsquo;s (<a href=\\\"http://phish.net/setlists/?d=2013-08-30&amp;highlight=222\\\">8/31/13</a>, the &ldquo;MOST SHOWS SPELL SOMETHING&rdquo; gig). Rounding out at a little over ten minutes, it was still too early to sense that this was a night where IT was happening.</p><p><img  src=\\\"http://smedia.pnet-static.com/img/herschel_1.png\\\" /><br /><small>Photo by Herschel Gelman.</small></p>")
                    .addUrl("http://phish.net/blog/1472930164/dicks1-when-mercury-comes-out-at-night")
                    .build();
        }
    }

    public static List<BlogPost> getBlogList() {
        return Arrays.asList(BLOGS);
    }

    public static BlogPost[] getBLOGS() {
        return Arrays.copyOf(BLOGS, BLOGS.length);
    }

    private BlogGenerator() { }


}
