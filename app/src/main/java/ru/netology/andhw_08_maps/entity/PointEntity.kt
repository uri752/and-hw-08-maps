package ru.netology.andhw_08_maps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.andhw_08_maps.dto.Point

@Entity
data class PointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
)
{
    fun toDto() = Point(
        id,
        name,
        latitude,
        longitude,
        description
    )

    companion object {
        fun fromDto(dto: Point) =
            PointEntity(
                dto.id,
                dto.name,
                dto.latitude,
                dto.longitude,
                dto.description,
            )
    }
}

fun List<PointEntity>.toDto(): List<Point> = map(PointEntity::toDto)
fun List<Point>.toEntity(): List<PointEntity> = map(PointEntity::fromDto)