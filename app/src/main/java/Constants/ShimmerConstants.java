package Constants;

import com.facebook.shimmer.ShimmerDrawable;

public class ShimmerConstants {


    private static ShimmerDrawable shimmerDrawable = new ShimmerDrawable();

    public static ShimmerDrawable getShimmer() {
        com.facebook.shimmer.Shimmer shimmer = new com.facebook.shimmer.Shimmer.AlphaHighlightBuilder().setDuration(500)
                .setBaseAlpha(0.9f)
                .setHighlightAlpha(0.4f)
                .setDirection(com.facebook.shimmer.Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();

        shimmerDrawable.setShimmer(shimmer);
        return shimmerDrawable;
    }
}
