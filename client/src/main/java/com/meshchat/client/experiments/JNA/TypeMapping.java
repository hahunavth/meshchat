/**
 * @deprecated
 */
//package com.meshchat.client.experiments;
//
//import com.sun.jna.Library;
//import com.sun.jna.Native;
//
//public interface TypeMapping extends Library {
//
//    TypeMapping INSTANCE =
//            (TypeMapping)
//                    Native.load(
//                            "../libtypemapping.so",
//                            TypeMapping.class);
//
//    byte tm_int8_t(int e);
//    int tm_int16_t(int e);
//    int tm_int32_t(int e);
//    long tm_int64_t(int e);
//
//    void in_char (byte[] buff);
//    String out_char ();
//    String io_char (String buff);
//
//    CommonT.ByValue create_common_t(
//            byte c,
//            float f,
//            double d,
//
//            byte int8,
//            short int16,
//            char int32,
//            long int64,
//
//            byte u_int8,
//            short u_int16,
//            char u_int32,
//            long u_int64,
//
//            String str1,
//            String str2,
//            String str3,
//            String str4
//    );
//
//}