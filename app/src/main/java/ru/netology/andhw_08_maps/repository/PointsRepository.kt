package ru.netology.andhw_08_maps.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.andhw_08_maps.dto.Point

interface PointsRepository  {
    val data: Flow<List<Point>>
    suspend fun insert(point: Point)
    suspend fun removeById(id: Long)
}
