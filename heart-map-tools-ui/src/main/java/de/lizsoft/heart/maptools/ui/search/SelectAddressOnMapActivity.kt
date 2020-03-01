package de.lizsoft.heart.maptools.ui.search

import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.button.MaterialButton
import de.lizsoft.heart.interfaces.navigator.retrieveArgument
import de.lizsoft.heart.common.extension.dp2px
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.common.ui.extension.bindView
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import de.lizsoft.heart.interfaces.map.model.SelectAddressOnMapLaunchModel
import de.lizsoft.heart.maptools.mapbuilder.MapProvider
import de.lizsoft.heart.maptools.ui.R
import org.koin.androidx.scope.currentScope

class SelectAddressOnMapActivity : ActivityWithPresenter() {

    private var launchArgument: SelectAddressOnMapLaunchModel? = null
    private lateinit var mapProvider: MapProvider
    private val pinTextView: TextView by bindView(R.id.select_address_on_map_pin_textview)
    private val fetchedAddressTextView: TextView by bindView(R.id.select_address_on_map_address_textview)
    private val confirmButton: MaterialButton by bindView(R.id.select_address_on_map_confirm_button)

    override fun getCurrentScreenBucket(): ScreenBucket<*> {
        return object : ScreenBucket<SelectAddressOnMapPresenter.View>(
              ScreenBucketModel(
                    scope = currentScope,
                    viewLayout = R.layout.activity_select_address_on_map,
                    enableDisplayHomeAsUp = true
              )
        ) {
            override fun getPresenterView(): SelectAddressOnMapPresenter.View {
                return object : SelectAddressOnMapPresenter.View {
                    override fun updateConfirmButtonState(isEnabled: Boolean) {
                        runOnUiThread {
                            confirmButton.isEnabled = isEnabled
                        }
                    }

                    override fun updateFetchedAddressText(address: String?) {
                        runOnUiThread {
                            fetchedAddressTextView.text = address
                        }
                    }
                }
            }
        }
    }

    override fun initializeBeforePresenter() {
        supportPostponeEnterTransition()
        launchArgument = retrieveArgument()
    }

    override fun initializeViewListeners() {
        mapProvider = MapProvider.builder()
              .makeEverythingVisible()
              .addPadding()
              .animateCamera()
              .setDefaultLocationCurrentLocation(10.0f)
              .disposeBy(disposables)
              .build(supportFragmentManager)

        mapProvider.setPadding(
              0,
              dp2px(75).toInt(),
              0,
              dp2px(75).toInt()
        )

        mapProvider.setIdleListener(GoogleMap.OnCameraIdleListener {
            pinTextView.text = launchArgument?.addressTypeName
            actions.onNext(
                  SelectAddressOnMapPresenter.Action.SelectedLocationChanged(mapProvider.cameraTargetAddress())
            )
        })

        mapProvider.setStartMovingListener(GoogleMap.OnCameraMoveStartedListener {
            pinTextView.text = ""
            fetchedAddressTextView.text =
                  textUtils.getString(R.string.activity_select_address_on_map_fetching_placeholder)
            confirmButton.isEnabled = false
            actions.onNext(SelectAddressOnMapPresenter.Action.MapStartedMoving)
        })
        mapProvider.startWith(R.id.select_address_on_map_map_fragment)

        confirmButton.setOnClickListener {
            actions.onNext(SelectAddressOnMapPresenter.Action.ConfirmButtonClicked)
        }
    }
}
