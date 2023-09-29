package ru.netology.andhw_08_maps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.andhw_08_maps.entity.PointEntity

@Dao
interface PointDao {
    @Query("SELECT * FROM PointEntity ORDER BY id ASC")
    fun getAll(): Flow<List<PointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: PointEntity)

    @Query("DELETE FROM PointEntity WHERE id = :id")
    suspend fun removeById(id: Long)

}