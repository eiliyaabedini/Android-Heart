package de.lizsoft.travelcheck.travelcheck.renderer

import android.widget.TextView
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewFinder
import de.lizsoft.travelcheck.R
import de.lizsoft.travelcheck.model.common.RegionTravelModel

class TravelCheckContentRegionRenderer : ViewBinder.Binder<RegionTravelModel> {

    override fun bindView(regionTravelModel: RegionTravelModel, finder: ViewFinder, payloads: MutableList<Any>) {
        finder.find<TextView>(R.id.travel_check_regions_region_name).text = regionTravelModel.name
    }
}