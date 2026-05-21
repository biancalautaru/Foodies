package interfaces;

import models.Review;

public interface Reviewable {
    void addReview(Review review);
    double getAverageRating();
    int getReviewCount();
}