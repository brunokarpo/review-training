package nom.brunokarpo.review.core.repository

import nom.brunokarpo.review.core.model.Pageable
import nom.brunokarpo.review.core.model.ReviewSummary
import java.util.*

interface ReviewSummaryRepository {

    fun getByRestaurantId(restaurantId: UUID): ReviewSummary?
    fun list(size: Int, page: Int): Pageable<ReviewSummary>

}
