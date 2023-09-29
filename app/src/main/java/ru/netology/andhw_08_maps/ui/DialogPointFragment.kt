package ru.netology.andhw_08_maps.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ru.netology.andhw_08_maps.databinding.FragmentDialogPointBinding
import ru.netology.andhw_08_maps.dto.Point
import ru.netology.andhw_08_maps.util.AndroidUtils
import ru.netology.andhw_08_maps.viewmodel.MapViewModel

class DialogPointFragment : DialogFragment() {

    var name: String? = null
    var description: String? = null

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val LAT_KEY = "LAT_KEY"
        private const val LONG_KEY = "LONG_KEY"
        private const val NAME_KEY = "NAME_KEY"
        private const val DESC_KEY = "DESC_KEY"
        fun newInstance(lat: Double, long: Double, id: Long? = null, name: String? = null, description: String? = null) = DialogPointFragment().apply {
            arguments = bundleOf(LAT_KEY to lat, LONG_KEY to long, ID_KEY to id, NAME_KEY to name, DESC_KEY to description)
            this.name = name
            this.description = description
        }
    }

    private val mapViewModel: MapViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDialogPointBinding.inflate(inflater, container, false)

        binding.name.setText(name)
        binding.description.setText(description)
        binding.add.setText(if (name.isNullOrEmpty()) "add" else "update" )


        binding.add.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())

            if (binding.name.text.isNotBlank() && binding.description.text.isNotBlank()) {
                mapViewModel.save(
                    Point(
                        id = requireArguments().getLong("ID_KEY"),
                        name = binding.name.text.toString(),
                        description = binding.description.text.toString(),
                        latitude = requireArguments().getDouble("LAT_KEY"),
                        longitude = requireArguments().getDouble("LONG_KEY")
                    )
                )
                dismiss()
            }
        }


        return binding.root
    }

}