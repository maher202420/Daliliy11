package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedCategoryDao {
    @Query("SELECT * FROM cached_categories ORDER BY `order` ASC")
    fun getAllCategories(): Flow<List<CachedCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CachedCategory>)

    @Query("DELETE FROM cached_categories")
    suspend fun clearAll()
}

@Dao
interface CachedProviderDao {
    @Query("SELECT * FROM cached_providers WHERE categoryId = :catId")
    fun getProvidersByCategory(catId: String): Flow<List<CachedProvider>>

    @Query("SELECT * FROM cached_providers")
    fun getAllProviders(): Flow<List<CachedProvider>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<CachedProvider>)

    @Query("DELETE FROM cached_providers WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM cached_providers")
    suspend fun clearAll()
}

@Database(entities = [CachedCategory::class, CachedProvider::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun categoryDao(): CachedCategoryDao
    abstract fun providerDao(): CachedProviderDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "dalili_local_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
