package ru.netology.andhw_08_maps.app.ui

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import ru.netology.andhw_08_maps.R
import ru.netology.andhw_08_maps.adapter.OnInteractionListener
import ru.netology.andhw_08_maps.databinding.FragmentPointsBinding
import ru.netology.andhw_08_maps.dto.Point
import ru.netology.andhw_08_maps.viewmodel.MapViewModel
import ru.netology.andhw_08_maps.adapter.PointsAdapter
import ru.netology.andhw_08_maps.app.ui.MapsFragment


class PointsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPointsBinding.inflate(inflater, container, false)

        val viewModel by viewModels<MapViewModel>()

        val adapter = PointsAdapter(object : OnInteractionListener {

            override fun onClick(point: Point) {
                findNavController().navigate(
                    R.id.action_pointsFragment_to_mapsFragment, bundleOf(
                        MapsFragment.LAT_KEY to point.latitude,
                        MapsFragment.LONG_KEY to point.longitude
                    )
                )
            }

            override fun onRemove(point: Point) {
                viewModel.removeById(point.id)
            }

            override fun onEdit(point: Point) {
                DialogPointFragment.newInstance(lat = point.latitude, long = point.longitude, id = point.id, name = point.name, description = point.description)
                    .show(childFragmentManager, null)
            }
        })

        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.data.collectLatest { points ->
                adapter.submitList(points)
                binding.empty.isVisible = points.isEmpty()
            }
        }
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.map_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                if (menuItem.itemId == R.id.list) {
                    findNavController().navigate(R.id.action_pointsFragment_to_mapsFragment)
                    true
                } else {
                    false
                }

        }, viewLifecycleOwner)


        return binding.root
    }
}