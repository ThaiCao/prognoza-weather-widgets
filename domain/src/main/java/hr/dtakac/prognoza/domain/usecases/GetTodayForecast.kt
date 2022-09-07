package hr.dtakac.prognoza.domain.usecases

import hr.dtakac.prognoza.domain.repository.ForecastRepository
import hr.dtakac.prognoza.entities.Place
import hr.dtakac.prognoza.entities.forecast.ForecastDatum
import hr.dtakac.prognoza.entities.forecast.DayForecast
import java.lang.Exception
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class GetTodayForecast(
    private val getSelectedPlace: GetSelectedPlace,
    private val forecastRepository: ForecastRepository
) {
    suspend operator fun invoke(): ForecastResult {
        val selectedPlace = getSelectedPlace() ?: return ForecastResult.NoSelectedPlace

        return try {
            val now = ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS)
            forecastRepository.getForecast(
                latitude = selectedPlace.latitude,
                longitude = selectedPlace.longitude,
                from = now,
                to = now.plusHours(23L)
            )
                .takeIf { it.isNotEmpty() }
                ?.let { mapToSuccess(selectedPlace, it) }
                ?: ForecastResult.Empty
        } catch (e: Exception) {
            ForecastResult.Empty
        }
    }

    private fun mapToSuccess(place: Place, data: List<ForecastDatum>): ForecastResult.Success =
        ForecastResult.Success(
            placeName = place.name,
            dayForecast = DayForecast(data)
        )
}

sealed interface ForecastResult {
    data class Success(
        val placeName: String,
        val dayForecast: DayForecast
    ) : ForecastResult

    object NoSelectedPlace : ForecastResult
    object Empty : ForecastResult
}