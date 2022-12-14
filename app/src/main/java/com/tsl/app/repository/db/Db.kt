package com.tsl.app.repository.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tsl.app.repository.db.dao.WordDao
import com.tsl.app.repository.db.tables.Word


@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class Db : RoomDatabase() {
    //添加Dao
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: Db? = null

        @Synchronized
        fun getDb(context: Context): Db {
            if (INSTANCE == null) {
                //名字不要加_下划线（callback导致无法正常回调）
                INSTANCE = Room
                    .databaseBuilder(context.applicationContext, Db::class.java, "CheeseDatabase")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            Log.i("###", "数据库创建")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            Log.i("###", "打开成功")
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {

                        }
                    }).build()
            }

            return INSTANCE!!
        }
    }
}