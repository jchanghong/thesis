   fun test() {
        //sql执行次数
        val sqlnumber = intArrayOf(10, 100, 1000, 3000, 5000, 10000)
        val mysql = "jdbc:mysql://localhost:3306/changhong?" + "user=root&" +
                "password=0000&useUnicode=" + "true&characterEncoding=UTF8"
        var connection = DriverManager.getConnection(mysql)
        var statement = connection.createStatement()
        println("mysql sqlnumber to times:")
        for (number in sqlnumber) {
            val start = System.currentTimeMillis()
            for (i in 1..number) {
                statement.executeQuery("select * from test")
            }
            val end = System.currentTimeMillis()
            println("$number      ${end - start}")
        }
        connection.close()
        val jsql = "jdbc:mysql://localhost:9999/changhong?" + "user=root&" +
                "password=0000&useUnicode=" + "true&characterEncoding=UTF8"
        connection = DriverManager.getConnection(jsql)
        statement = connection.createStatement()
        println("jsql sqlnumber to times:")
        for (number in sqlnumber) {
            val start = System.currentTimeMillis()
            for (i in 1..number) {
                statement.executeQuery("select * from test")
            }
            val end = System.currentTimeMillis()
            println("$number      ${end - start}")
        }
        connection.close()
    }
}
