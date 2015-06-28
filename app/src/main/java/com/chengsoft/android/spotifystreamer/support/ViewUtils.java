package com.chengsoft.android.spotifystreamer.support;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Utility class for switching/crossfading views.
 *
 * @see <a href="http://developer.android.com/training/animation/crossfade.html">Android Crossfade</a>
 */
public class ViewUtils {

    private static final String LOG_TAG = ViewUtils.class.getSimpleName();

    /**
     * Hide the loading view and hide the content view
     *
     * @param mContentView the view to show
     * @param mLoadingView the view to hide
     */
    public static void swap(View mContentView, View mLoadingView) {
        // Reset the alpha for both views
        mLoadingView.setAlpha(1f);
        mContentView.setAlpha(1f);

        // Hide the loading view
        mLoadingView.setVisibility(View.GONE);
        // Show the content view
        mContentView.setVisibility(View.VISIBLE);
    }

    /**
     * Crossfade views
     *
     * @param mContentView The view to crossfade into
     * @param mLoadingView The view to crossfade out of
     * @param animationDuration  The duration of the animation
     */
    public static void crossfade(final View mContentView, final View mLoadingView, Integer animationDuration) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mContentView.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }
}
