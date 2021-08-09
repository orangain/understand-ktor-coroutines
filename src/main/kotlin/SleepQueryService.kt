import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory

private val dataSource = HikariDataSource().apply {
    jdbcUrl = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password="
}
private val jdbi = Jdbi.create(dataSource).installPlugins()

object SleepQueryService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sleepSync(seconds: Double): Double {
        val sql = "SELECT pg_sleep(:seconds)"

        return jdbi.withHandle<Double, Exception> { handle ->
            logger.info("sleepSync($seconds)")
            handle.createQuery(sql).bind("seconds", seconds).mapToMap().list()
            seconds
        }
    }

    suspend fun sleepAsync(seconds: Double): Double {
        val sql = "SELECT pg_sleep(:seconds)"

        return withContext(Dispatchers.IO) {
            jdbi.withHandle<Double, Exception> { handle ->
                logger.info("sleepAsync($seconds)")
                handle.createQuery(sql).bind("seconds", seconds).mapToMap().list()
                seconds
            }
        }
    }
}
