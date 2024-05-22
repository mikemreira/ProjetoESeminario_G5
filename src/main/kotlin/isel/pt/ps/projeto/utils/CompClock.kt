package isel.pt.ps.projeto.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component

@Component
object CompClock : Clock {
    // To only have second precision
    override fun now(): Instant = Instant.fromEpochSeconds(Clock.System.now().epochSeconds)
}
