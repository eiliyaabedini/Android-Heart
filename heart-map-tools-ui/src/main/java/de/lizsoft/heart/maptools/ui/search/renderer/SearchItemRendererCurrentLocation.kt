package de.lizsoft.heart.maptools.ui.search.renderer

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewFinder
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.maptools.ui.R
import de.lizsoft.heart.maptools.ui.search.SearchAddressPresenter
import de.lizsoft.heart.maptools.ui.search.renderer.model.SearchItemModel
import io.reactivex.subjects.Subject

class SearchItemRendererCurrentLocation(
    private val actions: Subject<PresenterAction>
) : ViewBinder.Binder<SearchItemModel.SearchItemModelCurrentLocation> {

    override fun bindView(
          model: SearchItemModel.SearchItemModelCurrentLocation,
          finder: ViewFinder,
          payloads: MutableList<Any>
    ) {
        val parent: View = finder.find(R.id.search_item_parent)
        val icon: ImageView = finder.find(R.id.search_item_icon)
        val name: TextView = finder.find(R.id.search_item_name)
        val address: TextView = finder.find(R.id.search_item_address)

        parent.setOnClickListener {
            actions.onNext(SearchAddressPresenter.Action.SearchItemClicked(model))
        }

        icon.setImageResource(R.drawable.ic_near_me_black_24dp)
        name.setText(R.string.address_search_screen_current_location)
        address.isGone = true

    }
}