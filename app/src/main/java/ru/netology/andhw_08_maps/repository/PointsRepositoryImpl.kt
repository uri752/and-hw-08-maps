package ru.netology.andhw_08_maps.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.andhw_08_maps.dao.PointDao
import ru.netology.andhw_08_maps.dto.Point
import ru.netology.andhw_08_maps.entity.PointEntity
import ru.netology.andhw_08_maps.entity.toDto


class PointsRepositoryImpl (private val dao: PointDao): PointsRepository {
    override val data: Flow<List<Point>>
        get() = dao.getAll().map(List<PointEntity>::toDto).flowOn(Dispatchers.Default)


    override suspend fun insert(point: Point) {
        try {
            dao.insert(PointEntity.fromDto(point))
        } catch (e: Exception){
            println(e.message.toString())
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
        } catch (e: Exception){
            println(e.message.toString())
        }
    }


}