package ru.netology.andhw_08_maps.dto

data class Point(
    val id: Long = 0L,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
)
