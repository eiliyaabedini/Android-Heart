package de.lizsoft.heart.maptools.ui

import de.lizsoft.heart.activitylauncher.ActivityLauncher
import de.lizsoft.heart.interfaces.map.model.SelectAddressOnMapLaunchModel
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.heart.interfaces.model.SearchAddressPrediction
import de.lizsoft.heart.maptools.ui.navigation.OpenSelectAddressOnMap
import de.lizsoft.heart.maptools.ui.search.SelectAddressOnMapActivity

object HeartMap {

    fun register(heartNavigator: HeartNavigator) {

        heartNavigator.registerMaybeNavigation(
              OpenSelectAddressOnMap::class.java
        ) { navigator, model ->
            val launchModel = SelectAddressOnMapLaunchModel(addressTypeName = model.addressTypeName)
            val builder = ActivityLauncher.with(navigator.getActivity()).open(SelectAddressOnMapActivity::class.java)
            builder.addArgument(launchModel)

            builder.startActivityForResult()
                  .map { it as SearchAddressPrediction }
        }
    }
}