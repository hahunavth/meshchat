/**
 * @deprecated
 */
//package com.meshchat.client.experiments;
//
////typedef struct common_t
////        {
////        char c;
////        float f;
////        double d;
////        int8_t int8;
////        int16_t int16;
////        int32_t int32;
////        int64_t int64;
////        u_int8_t u_int8;
////        u_int16_t u_int16;
////        u_int32_t u_int32;
////        u_int64_t u_int64;
////        char str1[8192];
////        char str2[8192];
////        char *str3;
////        char *str4;
////        } common_t;
//
//import com.sun.jna.FromNativeContext;
//import com.sun.jna.NativeMapped;
//import com.sun.jna.Structure;
//@Structure.FieldOrder({
//        "c", "f", "d",
//        "int8", "int16", "int32", "int64",
//        "u_int8", "u_int16", "u_int32", "u_int64",
//        "str1", "str2", "str3", "str4"
//})
//public class CommonT extends Structure {
//    public byte c;
//    public float f;
//    public double d;
//
//    public byte int8;
//    public short int16;
//    public char int32;
//    public long int64;
//
//    public byte u_int8;
//    public short u_int16;
//    public char u_int32;
//    public long u_int64;
//
//    public String str1;
//    public String str2;
//    public String str3;
//    public String str4;
//
//    public CommonT() {}
//
//    public static class ByReference extends CommonT implements Structure.ByReference {
//    };
//
//    public static class ByValue extends CommonT implements Structure.ByValue {
//    };
//
//
//}
