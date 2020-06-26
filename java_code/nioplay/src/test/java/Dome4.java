import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Dome4 {
    public static void main(String[] args) throws Exception {
        FileOutputStream fileOutputStream=new FileOutputStream("/Users/Jack/Documents/Play_Ground/NIOPlay/java_code/nioplay/dome4write.txt");
        FileInputStream fileInputStream=new FileInputStream("/Users/Jack/Documents/Play_Ground/NIOPlay/java_code/nioplay/dome4read.txt");

        FileChannel channelRead = fileInputStream.getChannel();
        FileChannel channelWrite = fileOutputStream.getChannel();
        ByteBuffer byteBuffer=ByteBuffer.allocate(100);
       while (true){
           // 把position和limit重制
           /**
            * position = 0;
            * limit = capacity;
            * mark = -1;
            */
           byteBuffer.clear();
           System.out.println(byteBuffer.position());
           int readNumber = channelRead.read(byteBuffer);
           System.out.println(readNumber);
           if(-1==readNumber){
               break;
           }
           byteBuffer.flip();
           channelWrite.write(byteBuffer);
       }
       fileOutputStream.close();
       fileInputStream.close();
    }
}
