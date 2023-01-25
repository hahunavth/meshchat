//package com.meshchat.client.experiments;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JNRNumberConverterTest {
//
//    JNRTypeConverter converter;
//
//    @BeforeEach
//    void setUp() {
//        converter = new JNRTypeConverter();
//    }
//
//    @Test
//    void test_under_return() {
//        int in = (int) Math.pow(2, 7) + 1;
//        byte out = converter.getLibC().tm_u_int16_t(in);
//        assertEquals(in, out);
//        System.out.println("out: " + out);
//    }
//
//    @Test
//    void test_over_param() {
//        long in = (long) Math.pow(2, 16) + 10;
//        byte out = converter.getLibC().tm_int8_t(in);
//        assertEquals(in, out);
//        System.out.println("out: " + out);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "6,1,1",
//            "6,1,-1",
//            "7,1,1",
//            "7,1,-1",
//            "8,1,1",
//            "8,1,-1"
//    })
//    void test_u_int_8_vs_byte (long e, long r, long sign) {
//        byte in = (byte) (( Math.pow(2, e) + r) * sign);
//        byte out = converter.getLibC().tm_u_int8_t((byte) in);
//        assertEquals(in, out);
//        System.out.println("out: " + out);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "6,1,1",
//            "6,1,-1",
//            "7,1,1",
//            "7,1,-1",
//            "8,1,1",
//            "8,1,-1"
//    })
//    void test_tm_int_8 (long e, long r, long sign) {
//        byte in = (byte) (( Math.pow(2, e) + r) * sign);
//        byte out = converter.getLibC().tm_int8_t((byte) in);
//        assertEquals(in, out);
//        System.out.println("out: " + out);
//    }
//
//    @Test
//    void test_tm_u_int_64 () {
//        long in = ((long) Math.pow(2, 63) + 1);
//        long out = converter.getLibC().tm_u_int64_t(in);
//        System.out.println("test_tm_u_int_64");
//        assertEquals(in, out);
//        System.out.println("out: " + out);
//    }
//
//
//}