package de.lizsoft.heart.maptools.ui

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.subjects.PublishSubject
import de.lizsoft.heart.common.extension.toResponseResult
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonView
import de.lizsoft.heart.interfaces.map.Location
import de.lizsoft.heart.interfaces.map.service.AddressService
import de.lizsoft.heart.interfaces.map.service.CurrentLocation
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.heart.maptools.ui.search.SearchAddressPresenter
import de.lizsoft.heart.maptools.ui.search.renderer.model.SearchItemModel
import de.lizsoft.heart.testhelper.TestReactiveTransformer
import de.lizsoft.heart.testhelper.thenJust
import org.junit.Test

class SearchAddressPresenterTest {

    private val actions: PublishSubject<PresenterAction> = PublishSubject.create()
    private val actionsCommon: PublishSubject<PresenterCommonAction> = PublishSubject.create()
    private val reactiveTransformer: ReactiveTransformer = TestReactiveTransformer()

    private val mockCurrentLocation: CurrentLocation = mock()
    private val mockHeartNavigator: HeartNavigator = mock()
    private val mockAddressService: AddressService = mock {

        on { getSuggestionsResult(any(), any()) } doReturn mock()
    }

    private val mockView: SearchAddressPresenter.View = mock {
        on { getLaunchModelArgument() } doReturn null
    }

    private val mockCommonView: PresenterCommonView = mock {
        on { actions() } doReturn actions.mergeWith(actionsCommon)
        on { getCurrentScope() } doReturn mock()
    }

    val prediction1: Location = mock()
    val prediction2: Location = mock()
    val prediction3: Location = mock()
    val prediction4: Location = mock()

    private val presenter: SearchAddressPresenter = SearchAddressPresenter(
          currentLocation = mockCurrentLocation,
          addressService = mockAddressService,
          heartNavigator = mockHeartNavigator,
          reactiveTransformer = reactiveTransformer
    )

    @Test
    fun `when retry in error screen clicked then show contents again`() {
        presenter.attachView(mockView, mockCommonView)
        presenter.initialise()

        actions.onNext(PresenterCommonAction.ErrorRetryClicked)

        verify(mockCommonView).showContent()
    }

    @Test
    fun `when text changed then show loading and try to fetch suggestions`() {
        whenever(mockAddressService.getSuggestionsResult(any(), any())).thenJust(
              listOf(
                    prediction1, prediction2, prediction3, prediction4
              ).toResponseResult()
        )

        presenter.attachView(mockView, mockCommonView)
        presenter.initialise()

        val changedText = "new_changed_text"

        actions.onNext(SearchAddressPresenter.Action.SearchTextChanged(changedText))

        verify(mockView).showLoading()
        verify(mockAddressService).getSuggestionsResult(eq(changedText), any())
    }

    @Test
    fun `when text changed then prediction received then hide loading`() {
        whenever(mockAddressService.getSuggestionsResult(any(), any())).thenJust(
              listOf(
                    prediction1, prediction2, prediction3, prediction4
              ).toResponseResult()
        )

        presenter.attachView(mockView, mockCommonView)
        presenter.initialise()

        val changedText = "new_changed_text"

        actions.onNext(SearchAddressPresenter.Action.SearchTextChanged(changedText))

        verify(mockView).hideLoading()
    }

    @Test
    fun `when received predictions then convert and set items and hide empty view`() {
        val predictions = listOf(
              prediction1, prediction2, prediction3, prediction4
        )

        whenever(mockAddressService.getSuggestionsResult(any(), any())).thenJust(
              predictions.toResponseResult()
        )

        presenter.attachView(mockView, mockCommonView)
        presenter.initialise()

        val changedText = "new_changed_text"

        actions.onNext(SearchAddressPresenter.Action.SearchTextChanged(changedText))

        verify(mockView).hideEmptyView()
        verify(mockView).setItems(
              predictions.map { prediction ->
                  SearchItemModel.SearchItemModelPrediction(
                        location = prediction
                  )
              }
        )
    }

    @Test
    fun `when received empty predictions then set empty items and show empty view`() {
        whenever(mockAddressService.getSuggestionsResult(any(), any())).thenJust(
              emptyList<Location>().toResponseResult()
        )

        presenter.attachView(mockView, mockCommonView)
        presenter.initialise()

        val changedText = "new_changed_text"

        actions.onNext(SearchAddressPresenter.Action.SearchTextChanged(changedText))

        verify(mockView).showEmptyView()
        verify(mockView, times(2)).setItems(emptyList())
    }
}