/**
 * 所有的sql语句处理器必须是这个类的子类.
 * 比如Mupdate。Mselect
 */
abstract class SqlStatementHander {
   
      //返回值只有4种可能，不然报错！！！
      //一种是long类型， 一种是MyResultSet 一种是null ,
	  // 一种是string表示错误的消息
      //返回其他对面都是错误的
    fun handle(sqlStatement: SQLStatement, connection: OConnection) {
        try {
            val result = handle0(sqlStatement, connection)
            when (result) {
                null -> connection.writeok()
                is MyResultSet -> onsuccess(result.data, 
				result.columns, connection)
                is Long -> onsuccess(result, connection)
                is String -> connection.writeErrMessage(result)
                else -> connection.writeok()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onerror(e, connection)
        }

    }
}
