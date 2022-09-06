package hr.dtakac.prognoza.domain.usecases

import hr.dtakac.prognoza.domain.repository.PlaceRepository
import hr.dtakac.prognoza.entities.Place

class SelectPlace(
    private val placeRepository: PlaceRepository
) {
    suspend operator fun invoke(place: Place) {
        placeRepository.save(place)
        TODO("persist to user settings as selected")
    }
}