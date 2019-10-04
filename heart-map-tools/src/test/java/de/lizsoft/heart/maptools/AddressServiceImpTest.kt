package de.lizsoft.heart.maptools

import com.nhaarman.mockitokotlin2.*
import de.lizsoft.heart.common.extension.ToResponseResultMaybe
import de.lizsoft.heart.common.extension.toResponseResult
import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.map.Coordinate
import de.lizsoft.heart.interfaces.map.Location
import de.lizsoft.heart.interfaces.map.model.PlaceModel
import de.lizsoft.heart.interfaces.map.model.PredictionModel
import de.lizsoft.heart.interfaces.map.service.AddressService
import de.lizsoft.heart.interfaces.map.service.CurrentLocation
import de.lizsoft.heart.interfaces.map.service.PlacesService
import de.lizsoft.heart.maptools.services.AddressServiceImp
import de.lizsoft.heart.testhelper.thenEmpty
import de.lizsoft.heart.testhelper.thenError
import org.junit.Test
import org.koin.core.scope.Scope

class AddressServiceImpTest {

    private val mockLocation: Location = Location(
          name = "mockLocation Name",
          address = "mockLocation Address",
          coordinate = Coordinate(
                latitude = 10.0,
                longitude = 40.0
          )
    )

    private val mockError: Exception = mock()
    private val mockScope: Scope = mock()

    private val mockPlacesService: PlacesService = mock()
    private val mockCurrentLocation: CurrentLocation = mock {
        on { getCurrentLocation(any()) } doReturn mockLocation.ToResponseResultMaybe()
    }
    private val predictions: List<PredictionModel> = listOf(
          PredictionModel("1", "p1", "s1", "f1"),
          PredictionModel("2", "p2", "s2", "f2"),
          PredictionModel("3", "p3", "s3", "f3"),
          PredictionModel("4", "p4", "s4", "f4"),
          PredictionModel("5", "p5", "s5", "f5")
    )

    private val locations: List<Location> = listOf(
          Location(0.0, "p1", "f1", Coordinate(1.0, 1.0)),
          Location(0.0, "p2", "f2", Coordinate(1.0, 1.0)),
          Location(0.0, "p3", "f3", Coordinate(1.0, 1.0)),
          Location(0.0, "p4", "f4", Coordinate(1.0, 1.0)),
          Location(0.0, "p5", "f5", Coordinate(1.0, 1.0))
    )

    private val addressService: AddressService = AddressServiceImp(
          mockPlacesService, mockCurrentLocation
    )

    @Test
    fun `when receiving places and predictions then pass proper locations`() {
        whenever(mockPlacesService.getPredictionsResult(any(), any())).thenReturn(predictions.ToResponseResultMaybe())
        whenever(mockPlacesService.fetchPlace(any())).thenReturn(
              PlaceModel("999", Coordinate(1.0, 1.0)).ToResponseResultMaybe()
        )

        addressService.getSuggestionsResult("test", mockScope)
              .test()
              .assertValueCount(1)
              .assertValue(locations.toResponseResult())
              .assertNoErrors()
              .assertComplete()

        verify(mockPlacesService).getPredictionsResult("test", mockLocation.coordinate)
        verify(mockCurrentLocation).getCurrentLocation(mockScope)
    }

    @Test
    fun `when one of the places threw an error then pass error`() {
        whenever(mockPlacesService.getPredictionsResult(any(), any())).thenReturn(predictions.ToResponseResultMaybe())
        whenever(mockPlacesService.fetchPlace(any()))
              .thenReturn(PlaceModel("999", Coordinate(1.0, 1.0)).ToResponseResultMaybe())
              .thenReturn(PlaceModel("999", Coordinate(1.0, 1.0)).ToResponseResultMaybe())
              .thenError(mockError)

        addressService.getSuggestionsResult("test", mockScope)
              .test()
              .assertValueCount(1)
              .assertValue(ResponseResult.Failure(mockError))
              .assertNoErrors()
              .assertComplete()
    }

    @Test
    fun `when predictions threw an error then pass error`() {
        whenever(mockPlacesService.getPredictionsResult(any(), any())).thenError(mockError)
        whenever(mockPlacesService.fetchPlace(any()))
              .thenReturn(PlaceModel("999", Coordinate(1.0, 1.0)).ToResponseResultMaybe())

        addressService.getSuggestionsResult("test", mockScope)
              .test()
              .assertValueCount(1)
              .assertValue(ResponseResult.Failure(mockError))
              .assertNoErrors()
              .assertComplete()
    }

    @Test
    fun `when receiving empty predictions then pass empty list`() {
        whenever(
              mockPlacesService.getPredictionsResult(
                    any(),
                    any()
              )
        ).thenReturn(emptyList<PredictionModel>().ToResponseResultMaybe())
        whenever(mockPlacesService.fetchPlace(any())).thenReturn(
              PlaceModel("999", Coordinate(1.0, 1.0)).ToResponseResultMaybe()
        )

        addressService.getSuggestionsResult("test", mockScope)
              .test()
              .assertValueCount(1)
              .assertValue(emptyList<Location>().toResponseResult())
              .assertNoErrors()
              .assertComplete()
    }

    @Test
    fun `when current location is not provided then it should ask for predictions without providing current location`() {
        whenever(mockPlacesService.getPredictionsResult(any(), any())).thenReturn(predictions.ToResponseResultMaybe())
        whenever(mockPlacesService.fetchPlace(any())).thenReturn(
              PlaceModel("999", Coordinate(1.0, 1.0)).ToResponseResultMaybe()
        )

        whenever(mockCurrentLocation.getCurrentLocation(any())).thenEmpty()

        addressService.getSuggestionsResult("test", mockScope)
              .test()
              .assertValueCount(1)
              .assertValue(locations.toResponseResult())
              .assertNoErrors()
              .assertComplete()
    }
}