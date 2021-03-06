/**
 * 前端命令处理器
 * 处理命令包
 */
@Component
class MysqlCommandHandler : MysqlPacketHander {
    private fun handle0(data: CommandPacket, source: OConnection) {
        logger.debug(data.toString())
        logger.info("command info")
        when (data.command) {
            MySQLPacket.COM_INIT_DB -> initDB(data, source)
            MySQLPacket.COM_QUERY -> query(data, source)
            MySQLPacket.COM_PING -> ping(source)
            MySQLPacket.COM_QUIT -> close("quit cmd", source)
            MySQLPacket.COM_PROCESS_KILL -> kill(data, source)
            MySQLPacket.COM_STMT_PREPARE -> stmtPrepare(data, source)
            MySQLPacket.COM_STMT_SEND_LONG_DATA->stmtSendLongData(data,source)
            MySQLPacket.COM_STMT_RESET -> stmtReset(data, source)
            MySQLPacket.COM_STMT_EXECUTE -> stmtExecute(data, source)
            MySQLPacket.COM_STMT_CLOSE -> stmtClose(data, source)
            MySQLPacket.COM_HEARTBEAT -> heartbeat(data, source)
            else -> source.writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR,
                    "Unknown command")
        }
    }
    private fun query(data: CommandPacket, source: OConnection) {
        val mm = MySQLMessage(data.arg!!)
        mm.position(0)
        try {
            val sql = mm.readString(source.charset)
            source.sqlHander.handle(sql!!, source)
        } catch (e: UnsupportedEncodingException) {
            source.writeErrMessage(ErrorCode.ER_UNKNOWN_CHARACTER_SET, "Unknown charset '" +
			source.charset + "'")
            e.printStackTrace()
        }
    }
}