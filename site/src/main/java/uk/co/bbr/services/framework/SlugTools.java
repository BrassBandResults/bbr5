package uk.co.bbr.services.framework;

import com.github.slugify.Slugify;

public interface SlugTools {
    /**
     * https://github.com/slugify/slugify
     */
    default String slugify(String input) {


        final Slugify slg = Slugify.builder().build();
        return slg.slugify(input);
    }
}
