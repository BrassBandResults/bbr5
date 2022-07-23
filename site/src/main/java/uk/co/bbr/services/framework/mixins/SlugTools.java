package uk.co.bbr.services.framework.mixins;

import com.github.slugify.Slugify;

public interface SlugTools {
    /**
     * https://github.com/slugify/slugify
     */
    default String slugify(String input) {
        final Slugify slg = Slugify.builder().build();
        String slug = slg.slugify(input);
        if (slug.length() > 50) {
            slug = slug.substring(0, 50);
        }
        return slug;
    }
}
