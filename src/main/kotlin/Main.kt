import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.event.Level


fun main() {
    embeddedServer(Netty, 8080) {
        install(CallLogging) {
            level = Level.INFO
        }

        SleepQueryService.sleepSync(0.1) // Warm up connection pool

        routing {
            get("/sleep-sync/{seconds}") {
                val seconds = call.parameters["seconds"]!!.toDouble()
                val result = SleepQueryService.sleepSync(seconds)
                call.respond(result.toString())
            }

            get("/sleep-async/{seconds}") {
                val seconds = call.parameters["seconds"]!!.toDouble()
                val result = SleepQueryService.sleepAsync(seconds)
                call.respond(result.toString())
            }
        }
    }.start(wait = true)
}
