package ru.netology.andhw_08_maps.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.flow.collectLatest
import ru.netology.andhw_08_maps.BuildConfig
import ru.netology.andhw_08_maps.R
import ru.netology.andhw_08_maps.databinding.FragmentMapsBinding
import ru.netology.andhw_08_maps.databinding.FragmentPointBinding
import ru.netology.andhw_08_maps.viewmodel.MapViewModel


class MapsFragment : Fragment() {

    companion object {
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"
    }

    private var mapView: MapView? = null
    private lateinit var userLocation: UserLocationLayer
    private val listener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            DialogPointFragment.newInstance(point.latitude, point.longitude)
                .show(childFragmentManager, null)
        }
    }

    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) = Unit

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocation.cameraPosition()?.target?.let {
                mapView?.map?.move(CameraPosition(it, 15F, 0F, 0F))
            }
            userLocation.setObjectListener(null)
        }
    }

    private val viewModel by viewModels<MapViewModel>()

    private val pointTapListener = MapObjectTapListener { mapObject, _ ->
       findNavController().navigate(R.id.action_mapsFragment_to_pointsFragment)
        true
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    userLocation.isVisible = true
                    userLocation.isHeadingEnabled = false
                    userLocation.cameraPosition()?.target?.also {
                        val map = mapView?.map ?: return@registerForActivityResult
                        val cameraPosition = map.cameraPosition
                        map.move(
                            CameraPosition(
                                it,
                                cameraPosition.zoom,
                                cameraPosition.azimuth,
                                cameraPosition.tilt,
                            )
                        )
                    }
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.need_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapsBinding.inflate(inflater, container, false)

        mapView = binding.map.apply {
            userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            if (requireActivity()
                    .checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                userLocation.isVisible = true
                userLocation.isHeadingEnabled = false
            }

            map.addInputListener(listener)

            val collection = map.mapObjects.addCollection()
            viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
                viewModel.data.collectLatest { points ->
                    collection.clear()
                    points.forEach { point ->
                        val pointBinding = FragmentPointBinding.inflate(layoutInflater)
                        pointBinding.name.text = point.name
                        collection.addPlacemark(
                            Point(point.latitude, point.longitude),
                            ViewProvider(pointBinding.image)
                        ).apply {
                            userData = point.id
                        }.setText(point.name)

                    }
                }
            }
            collection.addTapListener(pointTapListener)

            val arguments = arguments
            if ((arguments != null) &&
                arguments.containsKey(LAT_KEY) &&
                arguments.containsKey(LONG_KEY)
            ) {
                val cameraPosition = map.cameraPosition
                map.move(
                    CameraPosition(
                        Point(arguments.getDouble(LAT_KEY), arguments.getDouble(LONG_KEY)),
                        10F,
                        cameraPosition.azimuth,
                        cameraPosition.tilt,
                    )
                )
                arguments.remove(LAT_KEY)
                arguments.remove(LONG_KEY)
            } else {
                userLocation.setObjectListener(locationObjectListener)
            }
        }

        binding.plus.setOnClickListener {
            binding.map.map.move(
                CameraPosition(
                    binding.map.map.cameraPosition.target,
                    binding.map.map.cameraPosition.zoom + 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null
            )
        }

        binding.minus.setOnClickListener {
            binding.map.map.move(
                CameraPosition(
                    binding.map.map.cameraPosition.target,
                    binding.map.map.cameraPosition.zoom - 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null,
            )
        }

        binding.location.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.map_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                if (menuItem.itemId == R.id.list) {
                    findNavController().navigate(R.id.action_mapsFragment_to_pointsFragment)
                    true
                } else {
                    false
                }

        }, viewLifecycleOwner)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView = null
    }
}