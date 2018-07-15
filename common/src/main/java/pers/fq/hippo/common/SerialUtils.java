package pers.fq.hippo.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * 序列化Object，底层使用kryo
 *
 * @Description:
 * @author: fang
 * @date: Created by on 18/4/16
 */
public class SerialUtils {

    /**
     * 保证了kryo线程安全
     */
    private static ThreadLocal<Kryo> longLocal = new ThreadLocal<Kryo>();

    /**
     * 保证了kryo线程安全
     */
    private static Kryo getKryo() {
        Kryo kryo = longLocal.get();
        if (kryo == null) {
            Kryo k = new Kryo();
            longLocal.set(k);
            return k;
        } else {
            return kryo;
        }
    }

    /**
     * 将一个对象序列化
     */
    public static byte[] obj2Byte(Object obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Output output = new Output(os);
        getKryo().writeObject(output, obj);
        output.close();
        return os.toByteArray();
    }

    /**
     * 将一个对象序列化，并将前8位设置成时间戳
     */
    public static byte[] obj2ByteWithTimeStampInHead(Object obj, long time) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(ByteUtil.long2Byte(time));
            Output output = new Output(os);
            getKryo().writeObject(output, obj);
            output.close();
            return os.toByteArray();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 反序列化字节数组
     */
    public static <T> T byte2Obj(byte[] bytes, int offset, int count, Class<T> clazz) {
        Input input = new Input(bytes, offset, count);
        return getKryo().readObject(input, clazz);
    }

    /**
     * 反序列化字节数组
     */
    public static <T> T byte2Obj(byte[] bytes, Class<T> clazz) {
        Input input = new Input(bytes);
        return getKryo().readObject(input, clazz);
    }
}



