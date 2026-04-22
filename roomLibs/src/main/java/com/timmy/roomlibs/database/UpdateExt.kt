package com.timmy.roomlibs.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.timmy.roomlibs.database.UpdateExt.availableMigration
import com.timmy.roomlibs.database.UpdateExt.databaseVersion
import com.timmymike.logtool.loge

object UpdateExt {

//
//    /** DB 範例：版本升級 20230811 */
//    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//        override fun migrate(db: SupportSQLiteDatabase) {
//            // DB 版本升級
//            val map = mutableMapOf<String, String>()
//            map["id"] = "INTEGER"
//            map["name"] = "TEXT"
//            map["client_id"] = "TEXT"
//            map["updateTime"] = "INTEGER"
//            createTable(
//                db,
//                "PersonEntity",
//                map.keys.toTypedArray(), // 欄位名稱陣列
//                map.values.toTypedArray(), // 欄位型別陣列
//                arrayOf("id") // 主鍵
//            )
//        }
//    }
//
//    /** DB 範例：版本升級 20230811 */
//    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
//        override fun migrate(db: SupportSQLiteDatabase) {
//            // DB 版本升級
//            deleteColumn(db, "SampleEntity", "client_id")
//        }
//    }
////
//    /** DB 範例：版本升級 20230811 */
//    val MIGRATION_3_4: Migration = object : Migration(3, 4) {
//        override fun migrate(db: SupportSQLiteDatabase) {
//            // DB 版本升級
//            addColumn(db, "SampleEntity", "client_id", "TEXT")
//        }
//    }

    /**
     * 修改資料庫欄位步驟：
     * 0. 於對應 package 的 Entity 依需求調整；若新增欄位須可為 null（舊資料無該欄）。
     * 1. 新增 Migration 常數，並確認 Migration(oldVersion, newVersion) 版本號正確。
     * 2. 將該 Migration 加入 [availableMigration]。
     * 3. 將 [databaseVersion] 改為新版本號。
     */

    val availableMigration = arrayOf<Migration>(/*MIGRATION_1_2, MIGRATION_2_3,  MIGRATION_3_4, MIGRATION_4_5*/)
    const val databaseVersion = 3

    /**
     * 新增資料表步驟：
     * 0. 於對應 package 新增 Entity、DAO。
     * 1. 於 [com.timmy.roomlibs.database.AppDataBase] 註冊 Dao，並將 Entity 加入 `entities = [...]`。
     * 2. 新增 Migration 常數，並確認 old／new 版本號正確。
     * 3. 將 Migration 加入 [availableMigration]。
     * 4. 將 [databaseVersion] 改為新版本號。
     *
     * 後續使用：
     * 1. 於 [com.timmy.roomlibs.di.RoomModule] 新增提供 Dao 的 @Provides 方法。
     * 2. 於 [com.timmy.roomlibs.repo] 新增 Repository（或其他類別）並注入該 Dao。
     */

    /** 新增欄位 */
    private fun addColumn(database: SupportSQLiteDatabase, table: String, col: String, type: String = "TEXT") {
        database.execSQL("ALTER TABLE $table ADD COLUMN $col $type;")
    }

    /** 刪除欄位 */
    private fun deleteColumn(database: SupportSQLiteDatabase, table: String, col: String) {
        getColumnNamesAndTypes(database, table).let { columnsMap ->
            if (columnsMap.containsKey(col)) {
                val tempTableName = "temp_$table"
                val createTempTableStatement = buildCreateTableStatement(tempTableName, columnsMap, col)

                database.execSQL(createTempTableStatement)

                val columnNamesWithoutExcluded = columnsMap.keys.filter { it != col }
                val insertColumns = columnNamesWithoutExcluded.sortedByDescending { it }.joinToString(", ")
                database.execSQL("INSERT INTO $tempTableName ($insertColumns) SELECT $insertColumns FROM $table;")

                database.execSQL("DROP TABLE $table;")

                val createTableStatement = buildCreateTableStatement(table, columnsMap, col)
                database.execSQL(createTableStatement)

                database.execSQL("INSERT INTO $table ($insertColumns) SELECT $insertColumns FROM $tempTableName;")

                database.execSQL("DROP TABLE $tempTableName;")
            } else {
                loge("欄位 ${col} 於 資料表 ${table} 中不存在!")
            }
        }
    }

    private fun buildCreateTableStatement(tableName: String, columnsMap: Map<String, Triple<String, Boolean, Int>>, excludedColumn: String): String {
        val columnDefinitions = columnsMap.filterKeys { it != excludedColumn }.map { (name, triple) ->
            val (type, notNull, _) = triple
            "$name $type${if (notNull) " NOT NULL" else ""}"
        }
        return "CREATE TABLE $tableName (${columnDefinitions.joinToString(", ")},PRIMARY KEY (${columnsMap.filter { it.value.third != 0 }.keys.joinToString(", ")}));"
    }

    // 查詢指定資料表資訊，回傳欄位名稱與型別的 Map。deleteColumn 內可呼叫 getColumnNamesAndTypes 取得待刪欄位資訊後再執行刪除。
    private fun getColumnNamesAndTypes(database: SupportSQLiteDatabase, tableName: String): MutableMap<String, Triple<String, Boolean, Int>> {

        val columnsMap = mutableMapOf<String, Triple<String, Boolean, Int>>()

        val cursor = database.query("PRAGMA table_info($tableName);")
        cursor.use { it ->
            while (it.moveToNext()) {
                val columnName = it.getString(1)
                val columnType = it.getString(2)
                val notNull = it.getInt(3) == 1 // 索引 3：NOT NULL 旗標
                val keyPosition = it.getInt(cursor.getColumnIndex("pk").takeIf { index -> index != -1 } ?: 0) // 主鍵順位（pk）
                columnsMap[columnName] = Triple(columnType, notNull, keyPosition)
            }
        }

        return columnsMap

    }


    /**
     * 新增 Table
     * 產生 SQL 字串如下
     * CREATE TABLE IF NOT EXISTS ShoeMinuteRecord (
     *     date INTEGER NOT NULL DEFAULT 0,
     *     mac TEXT NOT NULL,
     *     steps TEXT NOT NULL,
     *     updateTime INTEGER NOT NULL DEFAULT 0,
     *     apiUpdateTime INTEGER NOT NULL DEFAULT 0,
     *     PRIMARY KEY (date, mac)
     * )
     * */
    private fun createTable(database: SupportSQLiteDatabase, table: String, col: Array<String>, type: Array<String>, pk: Array<String>) {
        var sql = ""
        sql += "CREATE TABLE IF NOT EXISTS $table ("
        for (i in col.indices) {
            sql += when {
                type[i] == "INTEGER" -> "${col[i]} INTEGER NOT NULL DEFAULT 0"
                type[i] == "REAL" -> "${col[i]} REAL NOT NULL DEFAULT 0"
                type[i] == "TEXT" -> "${col[i]} TEXT NOT NULL "
                else -> ""
            }
            if (i < col.size - 1) {
                sql += ","
            }
        }
        if (pk.isNotEmpty()) {
            sql += ",PRIMARY KEY ("
            for (i in pk.indices) {
                sql += pk[i]
                if (i < pk.size - 1) {
                    sql += ","
                }
            }
            sql += ")"
        }
        sql += ")"
//            loge("TAG", "sql = $sql")
        database.execSQL(sql)
    }
}