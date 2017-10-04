/**
 * 处理sql语句的入口
 */
@Component
class MysqlSQLhander : SQLHander {
    @Autowired
    lateinit private var allHanders: AllHanders
	//所有的sql处理器容器
    @PostConstruct
    override fun handle(sql: String, c: OConnection){
	//处理正常的sql语句，前端连接
        if (config.audit) {
            SqlLog(sql, c.user ?: "null", c.host).sentoELServer()
        }
        logger.info(sql)
        if (logger.isDebugEnabled) {
            logger.debug(sql)
        }
        val sqlStatement: SQLStatement
        try {
            sqlStatement = sql.tosql()
            val hander = allHanders.handerMap[sqlStatement.javaClass]
            if (hander != null) {
                hander.handle(sqlStatement, c)
            } else {
                sqlStatement.accept(MSQLvisitor(c))
            }
            if (isupdatesql(sql)) {
                if (config.distributed) {
                    myHazelcast.exeSql(sql, if (c.schema == null) ""
					else c.schema!!)
                } else {
                    myHazelcast.exesqlLocal(sql, if (c.schema == null)
					"" else c.schema!!)
                }
            }
            return
        } catch (e: Exception) {
		//如果不是合法的mysql语句，就报错
        //druid支持的语句就用上面的方法语句处理，如果不支持，就会有异常，
	    //就自己写代码解析sql语句，处理。
         //下面是drop event语句的例子，这个例子druid不支持，所以自己写
            handleotherStatement(sql, c, e)
        }
    }
    fun handle(sql: SqlUpdateLog, c: OConnection) {
	//处理来自其他服务器的sql语句，同步，不需要前端连接
        logger.info(sql.toString())
        if (logger.isDebugEnabled) {
            logger.debug(sql.toString())
        }
        val sqlStatement: SQLStatement
        try {
            val parser = MySqlStatementParser(sql.sql)
            sqlStatement = parser.parseStatement()
            val hander = allHanders.handerMap[sqlStatement.javaClass]
            if (hander != null) {
                hander.handle(sqlStatement, c)
            } else {
                sqlStatement.accept(MSQLvisitor(c))
            }
            return
        }
    }
}
