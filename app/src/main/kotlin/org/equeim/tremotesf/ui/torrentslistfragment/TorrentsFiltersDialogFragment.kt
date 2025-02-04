package org.equeim.tremotesf.ui.torrentslistfragment

import android.os.Bundle
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.navGraphViewModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import org.equeim.tremotesf.R
import org.equeim.tremotesf.databinding.TorrentsFiltersDialogFragmentBinding
import org.equeim.tremotesf.rpc.GlobalRpc
import org.equeim.tremotesf.ui.NavigationBottomSheetDialogFragment
import org.equeim.tremotesf.ui.utils.ArrayDropdownAdapter
import org.equeim.tremotesf.ui.utils.collectWhenStarted
import org.equeim.tremotesf.ui.utils.viewBinding

private const val RESET_BUTTON_HIDE_DELAY_MS = 100L

class TorrentsFiltersDialogFragment : NavigationBottomSheetDialogFragment(R.layout.torrents_filters_dialog_fragment) {
    private val model by navGraphViewModels<TorrentsListFragmentViewModel>(R.id.torrents_list_fragment)
    private val binding by viewBinding(TorrentsFiltersDialogFragmentBinding::bind)

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        with (binding) {
            sortView.apply {
                setAdapter(ArrayDropdownAdapter(resources.getStringArray(R.array.sort_spinner_items)))
                setOnItemClickListener { _, _, position, _ ->
                    model.sortMode.value = TorrentsListFragmentViewModel.SortMode.values()[position]
                }
            }

            sortViewLayout.setStartIconOnClickListener {
                with(model) {
                    sortOrder.value = sortOrder.value.inverted()
                }
            }

            val statusFilterViewAdapter = StatusFilterViewAdapter(requireContext(), statusView)
            statusView.apply {
                setAdapter(statusFilterViewAdapter)
                setOnItemClickListener { _, _, position, _ ->
                    model.statusFilterMode.value =
                        TorrentsListFragmentViewModel.StatusFilterMode.values()[position]
                }
            }

            val trackersViewAdapter = TrackersViewAdapter(requireContext(), trackersView)
            trackersView.apply {
                setAdapter(trackersViewAdapter)
                setOnItemClickListener { _, _, position, _ ->
                    model.trackerFilter.value = trackersViewAdapter.getTrackerFilter(position)
                }
            }

            val directoriesViewAdapter = DirectoriesViewAdapter(requireContext(), model, directoriesView)
            directoriesView.apply {
                setAdapter(directoriesViewAdapter)
                setOnItemClickListener { _, _, position, _ ->
                    model.directoryFilter.value = directoriesViewAdapter.getDirectoryFilter(position)
                }
            }

            combine(model.sortOrder, model.sortMode, ::Pair)
                .collectWhenStarted(viewLifecycleOwner) { (sortOrder, sortMode) ->
                    updateSortView(sortOrder, sortMode)
                }

            combine(GlobalRpc.torrents, model.statusFilterMode, ::Pair)
                .collectWhenStarted(viewLifecycleOwner) { (torrents, statusFilterMode) ->
                    statusFilterViewAdapter.update(torrents, statusFilterMode)
                }

            combine(GlobalRpc.torrents, model.trackerFilter, ::Pair)
                .collectWhenStarted(viewLifecycleOwner) { (torrents, trackerFilter) ->
                    trackersViewAdapter.update(torrents, trackerFilter)
                }

            combine(GlobalRpc.torrents, model.directoryFilter, ::Pair)
                .collectWhenStarted(viewLifecycleOwner) { (torrents, directoryFilter) ->
                    directoriesViewAdapter.update(torrents, directoryFilter)
                }

            TooltipCompat.setTooltipText(resetButton, resetButton.contentDescription)
            resetButton.setOnClickListener { model.resetSortAndFilters() }
            resetButton.isInvisible = true
            model.sortOrFiltersEnabled.collectWhenStarted(viewLifecycleOwner) {
                if (it) {
                    resetButton.isInvisible = false
                } else {
                    if (resetButton.isVisible) {
                        delay(RESET_BUTTON_HIDE_DELAY_MS)
                    }
                    resetButton.isInvisible = true
                }
            }
        }

        GlobalRpc.isConnected.filter { !it }.collectWhenStarted(viewLifecycleOwner) {
            navController.popBackStack()
        }
    }

    private fun updateSortView(sortOrder: TorrentsListFragmentViewModel.SortOrder, sortMode: TorrentsListFragmentViewModel.SortMode) {
        with(binding) {
            val resId = if (sortOrder == TorrentsListFragmentViewModel.SortOrder.Descending) {
                R.drawable.sort_descending
            } else {
                R.drawable.sort_ascending
            }
            sortViewLayout.setStartIconDrawable(resId)

            sortView.apply {
                setText(adapter.getItem(sortMode.ordinal) as String)
            }
        }
    }
}
