package chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ChatClient implements Runnable {


    private SocketChannel socketChannel;

    private Selector selector;


    public ChatClient() {
        try {
            socketChannel = SocketChannel.open();
            selector = Selector.open();

            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doCon(){
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 9090);
        try {
            if (socketChannel.connect(new InetSocketAddress("127.0.0.1", 9090))){
                socketChannel.register(selector, SelectionKey.OP_READ);
                writeData(socketChannel);
            }else {
                //如果连接不上
                //如果连上了触发事件
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeData(final SocketChannel socketChannel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        System.out.println("input: ");
                        Scanner scanner = new Scanner(System.in);
                        String str = scanner.nextLine();
                        if (str.equalsIgnoreCase("exit")){
                            socketChannel.close();
                            return;
                        }
                        ByteBuffer byteBuffer = ByteBuffer.wrap((socketChannel.getLocalAddress().toString()+ " says: " + str).getBytes());
                        socketChannel.write(byteBuffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void run() {
        doCon();
        try {
            while (true){
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isValid()){
                        if (selectionKey.isConnectable()){
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            if (channel.finishConnect()){
                                channel.register(selector, SelectionKey.OP_READ);
                                System.out.println("connected");
                                writeData(channel);
                            }else {
                                System.exit(1);
                            }
                        }
                        if (selectionKey.isReadable()){
                            readData();
                        }
                    }

                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readData() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int read = socketChannel.read(byteBuffer);
        if (read>0){
            byteBuffer.flip();
            byte[] bytes = new byte[read];
            byteBuffer.get(bytes, 0, read);
            System.out.println(new String(bytes, "utf-8"));
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new ChatClient()).start();
    }
}
