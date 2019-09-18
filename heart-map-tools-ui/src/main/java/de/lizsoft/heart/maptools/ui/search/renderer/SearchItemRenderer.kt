package de.lizsoft.heart.maptools.ui.search.renderer

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewFinder
import de.lizsoft.heart.common.ui.extension.setTextOrGone
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.maptools.ui.R
import de.lizsoft.heart.maptools.ui.search.SearchAddressPresenter
import de.lizsoft.heart.maptools.ui.search.renderer.model.SearchItemModel
import io.reactivex.subjects.Subject

class SearchItemRenderer(
    private val actions: Subject<PresenterAction>
) : ViewBinder.Binder<SearchItemModel.SearchItemModelPrediction> {

    override fun bindView(
          model: SearchItemModel.SearchItemModelPrediction,
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

        icon.setImageResource(R.drawable.ic_location_pin_black_24dp)
        name.text = model.location.name
        address.setTextOrGone(model.location.address)
    }
}