/**
 * 前端认证处理器
 * 处理auth包
 */
@Component
open class MysqlAuthHander : MysqlPacketHander {
    private fun handle0(auth: AuthPacket, source: OConnection) {
        source.schema = auth.database
        // check password
        if (!checkPassword(auth.password!!, auth.user!!)) {
            if (config.audit) {
            LoginLog(auth.user ?: "null", source.host, false).sendesServer()
            }
            failure(ErrorCode.ER_ACCESS_DENIED_ERROR,
			"Access denied for user '" + auth.user + "', 
			because password is error ", source)
        } else {
            success(auth, source)
        }
    }
}