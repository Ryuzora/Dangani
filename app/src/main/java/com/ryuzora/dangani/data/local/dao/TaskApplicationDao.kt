package com.ryuzora.dangani.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ryuzora.dangani.data.local.entity.TaskApplicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskApplicationDao {

    @Query("SELECT * FROM task_applications WHERE taskId = :taskId ORDER BY appliedAt DESC")
    fun getByTaskId(taskId: String): Flow<List<TaskApplicationEntity>>

    @Query("SELECT * FROM task_applications WHERE helperId = :helperId ORDER BY appliedAt DESC")
    fun getByHelperId(helperId: String): Flow<List<TaskApplicationEntity>>

    @Query("SELECT * FROM task_applications WHERE taskId = :taskId AND helperId = :helperId LIMIT 1")
    fun getByTaskIdAndHelperId(taskId: String, helperId: String): Flow<TaskApplicationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(application: TaskApplicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(applications: List<TaskApplicationEntity>)

    @Update
    suspend fun update(application: TaskApplicationEntity)

    @Query("DELETE FROM task_applications WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM task_applications WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: String)
}
