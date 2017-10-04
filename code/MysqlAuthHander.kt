/**
 * 前端认证处理器
 * 处理auth包
 */
@Component
open class MysqlAuthHander : MysqlPacketHander {
    @Autowired
    lateinit var config: MyConfig
    private fun handle0(auth: AuthPacket, source: OConnection) {
        source.schema = auth.database
        // check password
        if (!checkPassword(auth.password!!, auth.user!!)) {
            if (config.audit) {
                LoginLog(auth.user ?: "null", source.host, false).sendesServer()
            }
            failure(ErrorCode.ER_ACCESS_DENIED_ERROR, "Access denied for user '" + auth.user + "', because password is error ", source)
        } else {
            success(auth, source)
        }
    }
    private fun success(auth: AuthPacket, source: OConnection) {

        source.authenticated = true
        source.user = auth.user
        source.schema = auth.database
        source.charsetIndex = auth.charsetIndex
        source.charset = CharsetUtil.getCharset(source.charsetIndex)
        if (config.audit) {
            LoginLog(source.user ?: "null", source.host, true).sendesServer()
        }
        if (LOGGER.isInfoEnabled) {
            val s = StringBuilder()
            s.append(source).append('\'').append(auth.user).append("' login success")
            val extra = auth.extra
            if (extra != null && extra.size > 0) {
                s.append(",extra:").append(String(extra))
            }
            LOGGER.info(s.toString())
        }
        source.write(Unpooled.wrappedBuffer(AUTH_OK))
    }
    private fun failure(errno: Int, info: String, source: OConnection) {
        LOGGER.error(source.toString() + info)
        source.writeErrMessage(2.toByte(), errno, info)
    }
    override fun hander(mySQLPacket: MySQLPacket, source: OConnection) {
        println(mySQLPacket.toString())
        handle0(mySQLPacket as AuthPacket, source)
    }
}