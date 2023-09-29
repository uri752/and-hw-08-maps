package ru.netology.andhw_08_maps.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.andhw_08_maps.dao.PointDao
import ru.netology.andhw_08_maps.entity.PointEntity

@Database(entities = [PointEntity::class], version = 1)
abstract class PointDb: RoomDatabase(){
    abstract fun pointDao(): PointDao

    companion object {
        @Volatile
        private var instance: PointDb? = null

        fun getInstance(context: Context): PointDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, PointDb::class.java, "point.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}