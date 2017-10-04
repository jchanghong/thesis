//sql队列 复制队列命令队列。2个锁。发布
@Component
class MyHazelcast : ItemListener<SqlUpdateLog> {
    private fun exeSqlforReplication() {
        Collections.sort(localqueneReplication)
        var log: SqlUpdateLog? = localqueneReplication.poll()
        var lastlsn: Long = 0
        while (log != null) {
            locals_maxlsn = log.LSN
            logger.info("exeSqlforReplication exe sql " + log)
            oNullConnection!!.schema = log.db
            sqLhander.handle(log, oNullConnection)
            logFile!!.write(log)
            lastlsn = log.LSN
            log = localqueneReplication.poll()
        }
        isreplicating = false
        log = localquene.poll()
        val remotel = iAtomic_remote_lsn!!.get()
        while (log != null) {
            locals_maxlsn = log.LSN
            logger.info("exe  Sql : " + log)
            oNullConnection!!.schema = log.db
            sqLhander.handle(log, oNullConnection)
            logFile!!.write(log)
            lastlsn = log.LSN
            if (lastlsn > remotel) {
                remotequene!!.offer(log)
            }
            log = localqueneReplication.poll()
        }
        if (lastlsn > remotel) {
            iAtomic_remote_lsn!!.set(lastlsn)
        }
    }
    /**
     *  本机发出的sql语句.记录到本地logfile。
	 同时发布到其他服务器*/
    fun exeSql(sql: String, db: String) {
        if (isreplicating) {
            val l = iAtomic_remote_lsn!!.get()
            val log = SqlUpdateLog(l + 1, sql, db)
            localquene.addLast(log)
        } else {
            locals_maxlsn++
            val l = iAtomic_remote_lsn!!.addAndGet(1)
            val log = SqlUpdateLog(l, sql, db)
            logger.info("exe sql " + log)
            logFile!!.write(log)
            remotequene!!.offer(log)
        }
    }
    private fun exesqlforremoteData() {
        Collections.sort(localquene)
        var log: SqlUpdateLog? = localquene.poll()
        var last: Long = 0
        while (log != null) {
            locals_maxlsn = log.LSN
            logger.info("exesqlforremoteData()  " + log)
            oNullConnection!!.schema = log.db
            sqLhander.handle(log, oNullConnection)
            logFile!!.write(log)
            last = log.LSN
            log = localquene.poll()
        }
        val l = iAtomic_remote_lsn!!.get()
        if (last > l) {
            iAtomic_remote_lsn!!.set(last)
        }

    }

}
