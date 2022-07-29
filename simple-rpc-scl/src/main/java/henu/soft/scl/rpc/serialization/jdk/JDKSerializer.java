package henu.soft.scl.rpc.serialization.jdk;

import java.io.*;

/**
 * JDK原生序列化方式
 */

public class JDKSerializer {

    public static Object deserialize(byte[] bytes){
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return inputStream.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] serialize(Object serializable){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(byteArrayOutputStream);
            stream.writeObject(serializable);
            stream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

}
