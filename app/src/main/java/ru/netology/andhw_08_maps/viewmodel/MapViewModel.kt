package ru.netology.andhw_08_maps.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.andhw_08_maps.db.PointDb
import ru.netology.andhw_08_maps.dto.Point
import ru.netology.andhw_08_maps.repository.PointsRepository
import ru.netology.andhw_08_maps.repository.PointsRepositoryImpl


class MapViewModel (application: Application): AndroidViewModel(application) {
    private val repository: PointsRepository = PointsRepositoryImpl (PointDb.getInstance(application).pointDao())
    val data: Flow<List<Point>>
        get() = repository.data
    init {
        loadPoints()
    }

    fun loadPoints()= viewModelScope.launch{
        try {
            repository.data.map{ it.asReversed() }

        } catch (e: Exception){
            Toast
                .makeText(getApplication(), "Something went wrong: $e", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun save(point: Point) = viewModelScope.launch{
        try {
            repository.insert(point)
        } catch (e: Exception){
            Toast
                .makeText(getApplication(), "Something went wrong: $e", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun removeById(id: Long)= viewModelScope.launch{
        try {
            repository.removeById(id)
        } catch (e: Exception){
            Toast
                .makeText(getApplication(), "Something went wrong: $e", Toast.LENGTH_SHORT)
                .show()
        }
    }
}