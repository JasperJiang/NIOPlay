package com.demo.myjedis;

public class MyJedis {

    private String ip;

    private int port;

    MySocket socket;

    public MyJedis(String ip, int port) {
        this.ip = ip;
        this.port = port;
        socket = new MySocket(ip, port);
    }

    public String set(String key, String value){
        socket.send(commandUtil(Resp.command.SET, key, value));
        return socket.read();
    }

    public String get(String key){
        socket.send(commandUtil(Resp.command.GET, key));
        return socket.read();
    }

    public String incr(String key){
        socket.send(commandUtil(Resp.command.INCR, key));
        return socket.read();
    }


    /**
     * *3
     * $3
     * SET
     * $4
     * key1
     * $4
     * val1
     *
     * @param command
     * @param values
     * @return
     */
    public static String commandUtil(Resp.command command, String... values){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Resp.START).append(values.length+1).append(Resp.LINE);
        stringBuilder.append(Resp.STRING_LENGTH).append(command.name().length()).append(Resp.LINE);
        stringBuilder.append(command.name()).append(Resp.LINE);
        for (String value : values) {
            stringBuilder.append(Resp.STRING_LENGTH).append(value.length()).append(Resp.LINE);
            stringBuilder.append(value).append(Resp.LINE);
        }
        return stringBuilder.toString();
    }


    public static void main(String[] args) {
//        System.out.println(commandUtil(Resp.command.SET, "key1", "val1"));
        MyJedis myJedis = new MyJedis("127.0.0.1", 6379);
        System.out.println("==== set ====");
        System.out.println(myJedis.set("key1", "val1"));
        System.out.println(myJedis.set("key2", "1"));
        System.out.println("==== get ====");
        System.out.println(myJedis.get("key1"));
        System.out.println("==== incr ====");
        System.out.println(myJedis.incr("key2"));
    }
}
