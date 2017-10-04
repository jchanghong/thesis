class CommandPacket : MySQLPacket() {
    var command: Byte = 0
    var arg: ByteArray? = null
    override fun read(data: ByteArray) {
        val mm = MySQLMessage(data)
        packetLength = mm.readUB3()
        packetId = mm.read()
        command = mm.read()
        arg = mm.readBytes()
    }
    @Throws(IOException::class)
    fun write(out: OutputStream) {
        StreamUtil.writeUB3(out, calcPacketSize())
        StreamUtil.write(out, packetId)
        StreamUtil.write(out, command)
        out.write(arg)
    }
}