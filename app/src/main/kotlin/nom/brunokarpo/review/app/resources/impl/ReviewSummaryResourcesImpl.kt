package nom.brunokarpo.review.app.resources.impl

import nom.brunokarpo.review.app.resources.ReviewSummaryResources
import nom.brunokarpo.review.app.resources.dtos.PageDTOResource
import nom.brunokarpo.review.app.resources.dtos.ReviewSummaryDTOResource
import nom.brunokarpo.review.app.resources.dtos.converter.ReviewSummaryDTOToReviewSummaryDTOResourceConverter
import nom.brunokarpo.review.core.controllers.ReviewSummaryController
import nom.brunokarpo.review.core.controllers.dtos.ReviewSummaryDTO
import nom.brunokarpo.review.core.controllers.dtos.converters.ReviewSummaryToReviewSummaryDTO
import nom.brunokarpo.review.core.model.ReviewSummary
import nom.brunokarpo.review.core.usercases.RetrieveSummaryReviewByRestaurantIdUseCase
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReviewSummaryResourcesImpl(
        private val reviewSummaryController: ReviewSummaryController
): ReviewSummaryResources {

    override fun retrieveSummaryByRestaurantId(restaurantId: UUID)
            = ResponseEntity.ok(ReviewSummaryDTOResource(reviewSummaryController.retrieveByRestaurantId(restaurantId)))


    override fun retrieveSummaryList(size: Int, page: Int) =
        PageDTOResource(reviewSummaryController.retrieveList(size, page)) {
            it.map {  element -> ReviewSummaryDTOToReviewSummaryDTOResourceConverter().convert(element as ReviewSummaryDTO) }
        }
}