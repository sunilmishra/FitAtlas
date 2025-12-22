package com.codewithmisu.fitatlas

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.codewithmisu.fitatlas.body.data.BodyEntity
import com.codewithmisu.fitatlas.body.data.BodyLocalSource
import com.codewithmisu.fitatlas.exercise.data.ExerciseDetailEntity
import com.codewithmisu.fitatlas.exercise.data.ExerciseEntity
import com.codewithmisu.fitatlas.exercise.data.ExerciseLocalSource
import kotlinx.serialization.json.Json

@Database(
    entities = [BodyEntity::class, ExerciseEntity::class, ExerciseDetailEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bodyLocalSource(): BodyLocalSource
    abstract fun exerciseLocalSource(): ExerciseLocalSource
}

/// Construct App Database
fun constructDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "FitAtlas_App.db"
    ).build()
}

/// Convert List to String
class StringListConverter {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }
}
