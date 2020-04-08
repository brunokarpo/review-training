package nom.brunokarpo.review.repository.jdbc

import nom.brunokarpo.review.core.model.Review
import org.apache.ibatis.jdbc.ScriptRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.FileReader
import java.sql.Types
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("Test JDBC implementation to ReviewRepository interface")
class ReviewRepositoryJdbcTest: DatabaseTestBase() {

    private lateinit var sut: ReviewRepositoryJdbc

    @BeforeEach
    internal fun setUp() {
        sut = ReviewRepositoryJdbc(DATA_SOURCE)

        val scriptRunner = ScriptRunner(DATA_SOURCE.connection)
        val reader = BufferedReader(FileReader(System.getProperty("user.dir") + "/src/test/resources/database/load-database.sql"))
        scriptRunner.runScript(reader)
    }

    @Test
    internal fun `should create new review on database`() {
        val restId = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val grade = 5

        val review = Review(restaurantId = restId, userId = userId, orderId = orderId, review = grade)

        sut.create(review)

        val ps = DATA_SOURCE.connection.prepareStatement("SELECT restaurant_id, order_id, user_id, review FROM review WHERE restaurant_id = ?")
        ps.setObject(1, restId, Types.OTHER)

        val rs = ps.executeQuery()
        assertTrue { rs.next() }
        assertEquals(restId, rs.getObject("restaurant_id", UUID::class.java))
        assertEquals(orderId, rs.getObject("order_id", UUID::class.java))
        assertEquals(userId, rs.getObject("user_id", UUID::class.java))
        assertEquals(grade, rs.getInt("review"))
    }

    @Test
    internal fun `should create review summary after create new review`() {
        val restId = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val grade = 5

        val review = Review(restaurantId = restId, userId = userId, orderId = orderId, review = grade)

        sut.create(review)

        val ps = DATA_SOURCE.connection.prepareStatement("SELECT restaurant_id, average, qtd_review FROM review_summary WHERE restaurant_id = ?")
        ps.setObject(1, restId, Types.OTHER)

        val rs = ps.executeQuery()
        assertTrue { rs.next() }
        assertEquals(restId, rs.getObject("restaurant_id", UUID::class.java))
        assertEquals(5.0, rs.getDouble("average"))
        assertEquals(1, rs.getInt("qtd_review"))
    }

    @Test
    internal fun `should recalculate review summary after create new review`() {
        val restId = UUID.fromString("5c5749a5-e0e3-42a0-9d26-a428eb2e5344")
        val orderId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val grade = 4

        val review = Review(restaurantId = restId, userId = userId, orderId = orderId, review = grade)

        sut.create(review)

        val ps = DATA_SOURCE.connection.prepareStatement("SELECT restaurant_id, average, qtd_review FROM review_summary WHERE restaurant_id = ?")
        ps.setObject(1, restId, Types.OTHER)

        val rs = ps.executeQuery()
        assertTrue { rs.next() }
        assertEquals(restId, rs.getObject("restaurant_id", UUID::class.java))
        assertEquals(4.4, rs.getDouble("average"))
        assertEquals(5, rs.getInt("qtd_review"))
    }

    @AfterEach
    internal fun tearDown() {
        val scriptRunner = ScriptRunner(DATA_SOURCE.connection)
        val reader = BufferedReader(FileReader(System.getProperty("user.dir") + "/src/test/resources/database/clean-database.sql"))
        scriptRunner.runScript(reader)
    }
}