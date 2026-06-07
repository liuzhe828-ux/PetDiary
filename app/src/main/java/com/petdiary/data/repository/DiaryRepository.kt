package com.petdiary.data.repository

import com.petdiary.data.dao.DiaryDao
import com.petdiary.data.entity.DiaryEntry
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {

    fun getAllEntries(): Flow<List<DiaryEntry>> = diaryDao.getAllEntries()

    suspend fun getEntryById(id: Long): DiaryEntry? = diaryDao.getEntryById(id)

    suspend fun insert(entry: DiaryEntry): Long = diaryDao.insert(entry)

    suspend fun update(entry: DiaryEntry) = diaryDao.update(entry)

    suspend fun delete(entry: DiaryEntry) = diaryDao.delete(entry)

    suspend fun deleteById(id: Long) = diaryDao.deleteById(id)

    fun searchEntries(query: String): Flow<List<DiaryEntry>> = diaryDao.searchEntries(query)
}
