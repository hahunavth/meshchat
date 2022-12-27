package com.meshchat.client.experiments;

import com.meshchat.client.experiments.libs.TypeMappingLib;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;

import java.util.Arrays;

/**
 * @deprecated
 */
public class JNRTypeConverter {

    private TypeMappingLib libC
    = LibraryLoader
            .create(TypeMappingLib.class)
            .failImmediately()
            .search("/home/kryo/Desktop/meshchat/client/src/main/resources")
            .load("typemapping");

    public TypeMappingLib getLibC () {
        return libC;
    }

    public static void main(String[] args) {

        String providerName = System.getProperty("jnr.ffi.provider");
        System.out.print("RB: providerName " + providerName + "\n");

        JNRTypeConverter jnrTypeConverter = new JNRTypeConverter();
        Runtime runtime = Runtime.getRuntime(jnrTypeConverter.getLibC());

//
        CommonJNR c = jnrTypeConverter.libC.create_common_t(
                (byte) 97, 1.2f, 3.3,
                (byte) 4, (short) 5, (char) 6, 7L,
                (byte) 8, (short) 9, (char) 10, 11,
                "AAA", "baskdflf", "abcd", "absadfkjsfl"
            );
//        jnrTypeConverter.libC.print_common_t(c);


        CommonJNR c2 = new CommonJNR(runtime);
        int out_len = 0;
        byte[] buff = new byte[100000];

        if (jnrTypeConverter.getLibC().common_t_to_buffer(c, buff, 1000000, out_len) == 0) {
            System.out.println("Errr");
        } else {
            System.out.println(buff);
        }

        System.out.println(Arrays.toString(buff));

//
        jnrTypeConverter.getLibC().decode_common_t(buff, c2);
        jnrTypeConverter.getLibC().print_common_t(c2);

//        System.out.println(jnrTypeConverter.getLibC().tm_u_int8_t((byte) ((byte) Math.pow(2, 7) + 1)));
    }
}
