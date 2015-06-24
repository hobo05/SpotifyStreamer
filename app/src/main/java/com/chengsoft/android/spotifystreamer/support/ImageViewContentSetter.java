package com.chengsoft.android.spotifystreamer.support;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Convenience subclass that uses a bean property to set a {@link android.widget.ImageView}
 * @param <T> The bean type
 */
public class ImageViewContentSetter<T> implements ViewContentSetter<T> {

    private String imageUrlBeanProperty;
    private int defaultImageResource;
    private PropertyUtils propertyUtils = PropertyUtils.getInstance();

    /**
     * Uses the image url bean property to load the image and
     * set it in the {@link android.widget.ImageView}
     *
     * @param imageUrlBeanProperty the bean property name that contains the image url
     * @param defaultImageResource the resource id for the image resource when the image fails to load or is null
     */
    public ImageViewContentSetter(String imageUrlBeanProperty, int defaultImageResource) {
        this.imageUrlBeanProperty = imageUrlBeanProperty;
        this.defaultImageResource = defaultImageResource;
    }

    @Override
    public void setViewContent(Object view, T object) {
        // retrieve the property containing the url and
        // use picasso to set the image
        ImageView imageView = (ImageView) view;

        // Only set thumbnail if the url was set
        Object imageUrlProperty = propertyUtils.readProperty(object, imageUrlBeanProperty);
        if (imageUrlProperty != null) {
            Picasso.with(imageView.getContext()).load(imageUrlProperty.toString()).into(imageView);
        } else {
            // Set the default if nothing was found
            imageView.setImageResource(defaultImageResource);
        }
    }
}
