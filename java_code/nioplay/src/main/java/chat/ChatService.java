package chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ChatService {

    ServerSocketChannel serverSocketChannel;

    Selector selector;

    private int timeout = 2000;

    public ChatService(){

        try {
            serverSocketChannel = ServerSocketChannel.open();

            selector = Selector.open();

            serverSocketChannel.bind(new InetSocketAddress(9090));

            //设置accept非阻塞
            serverSocketChannel.configureBlocking(false);

            //只接受accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server is ready");

            start();
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void rebuildSelector() throws IOException {
        Selector newSelector=Selector.open();
        Selector oldSelect=selector;
        for (SelectionKey selectionKey : oldSelect.keys()) {
            int i = selectionKey.interestOps();
            selectionKey.cancel();
            selectionKey.channel().register(newSelector,i);
        }
        selector=newSelector;
        oldSelect.close();
    }

    public void start() throws IOException {
        int count=0;
        long start=System.nanoTime();
        while (true){
            //如果不设置timeout就阻塞了
            int select = selector.select(timeout);
            System.out.println("轮询结果：" + select);
            // 为了解决JDK空轮询的bug  Start
            long end=System.nanoTime();
            if(end-start>= TimeUnit.MILLISECONDS.toNanos(timeout)){
                count=1;
            }else{
                count++;
            }

            if(count>=10){
                System.out.println("有可能发生空轮询"+count+"次");
                rebuildSelector();
                count=0;
                selector.selectNow();
                continue;
            }
            // 为了解决JDK空轮询的bug  End

            Set<SelectionKey> selectionKeySet = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectionKeySet.iterator();

            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();

                if (selectionKey.isAcceptable()){
                    //获取网络通道
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //设置read方法非阻塞模式
                    socketChannel.configureBlocking(false);
                    //注册读取事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println(socketChannel.getRemoteAddress().toString() + " connected");

                }

                if (selectionKey.isReadable()){
                    //读取客户端发来的数据
                    readClientData(selectionKey);
                }

                //手动从当前集合将本次运行完的对象删除
                iterator.remove();
            }


        }
    }

    public void readClientData(SelectionKey selectionKey){
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        try {
            int read = socketChannel.read(byteBuffer);
            byteBuffer.flip();
            if (read > 0){
                byte[] bytes = new byte[read];
                byteBuffer.get(bytes, 0, read);
                String s = new String(bytes, "utf-8");
                //转发到其他client
                writeClientData(socketChannel, s);
            }else {
                // 如果连接断开是read方法，但是读出来是-1，这里关闭这个channel
                socketChannel.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeClientData(SocketChannel socketChannel, String s) throws IOException {
        //全部的
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key: keys){
            //因为key可能以及取消了
            if (key.isValid()){
                SelectableChannel channel = key.channel();
                //去除ServerSocketChannel
                if (channel instanceof SocketChannel){
                    SocketChannel socketChannel1 = (SocketChannel) channel;
                    //去除自己
                    if (channel != socketChannel){
                        //从byte数组生产buffer
                        ByteBuffer wrap = ByteBuffer.wrap(s.getBytes());
                        socketChannel1.write(wrap);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatService().start();
    }

}
