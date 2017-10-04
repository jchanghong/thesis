/**
 * 
 * 服务器
 */
@Service
class NettyServer {
    fun start() {
        val group = DefaultEventExecutorGroup(8)
        val bossGroup = NioEventLoopGroup(1)
        val workerGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                   .channel(NioServerSocketChannel::class.java)
                   .option(ChannelOption.SO_BACKLOG, 100)
                   .childHandler(object : ChannelInitializer<SocketChannel>(){
                       @Throws(Exception::class)
                       public override fun initChannel(ch: SocketChannel) {
                           val p = ch.pipeline()
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast("idle", IdleStateHandler(10, 5, 0))
                            p.addLast("decoder", byteToMysqlDecoder)
                            p.addLast("packet", bytetomysql)
                            p.addLast(group, "hander", mysqlPacketHander)
                        }
                    })
            val f = b.bind(PORT).sync()
            // Wait until the server socket is closed.
            logger.info("server start  complete.................... ")
            Minformation_schama.init_if_notexits()
            if (config.distributed) {
                myHazelcast.inits()
            }
            f.channel().closeFuture().sync()
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}
