package ru.netology.andhw_08_maps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.andhw_08_maps.R
import ru.netology.andhw_08_maps.databinding.FragmentPointBinding
import ru.netology.andhw_08_maps.dto.Point


interface OnInteractionListener {
    fun onEdit(point: Point) {}
    fun onRemove(point: Point) {}
    fun onClick(point: Point) {}

}

class PointsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Point, PointViewHolder>(PointDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val binding =
            FragmentPointBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PointViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val point = getItem(position)
        holder.bind(point)

    }
}

class PointViewHolder(
    private val binding: FragmentPointBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(point: Point) {
        binding.apply {
            name.text = point.name
            description.text = point.description
            latitude.text = point.latitude.toString()
            longitude.text = point.longitude.toString()

            root.setOnClickListener {
                onInteractionListener.onClick(point)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.point_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(point)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(point)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

        }
    }
}

class PointDiffCallback : DiffUtil.ItemCallback<Point>() {
    override fun areItemsTheSame(oldPoint: Point, newPoint: Point): Boolean {
        return oldPoint.id == newPoint.id
    }

    override fun areContentsTheSame(oldPoint: Point, newPoint: Point): Boolean {
        return oldPoint == newPoint
    }
}
