package com.meshchat.client.experiments;

import com.meshchat.client.experiments.libs.TypeMappingLib;
import jnr.ffi.LibraryLoader;
import jnr.ffi.LibraryOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Test result: false
 * - fd=0
 */
class JNRCSocketTest {

    private TypeMappingLib lib;

    @BeforeEach
    void setUp () {
        lib = LibraryLoader
                .create(TypeMappingLib.class)
                .option(LibraryOption.LoadNow, true)
                .option(LibraryOption.SaveError, true)
                .failImmediately()
                .search("/home/kryo/Desktop/meshchat/client/src/main/resources")
                .load("typemapping");
    }

    @Test
    void test_run_echo_tcp_client() {
        int client_fd = lib.run_echo_tcp_client();

        assertTrue(client_fd > 0);
    }

    @Test
    void test_connect_server() throws InterruptedException {
        int fd = lib.connect_server("127.0.0.1", 5500);

        lib.simple_send("Hello");
        lib.simple_recv();
        lib.simple_send("Hello");


        for(int i = 0; i < 3; i++) Thread.sleep(1000);
        System.out.println("client_fd=" + fd);
    }
}
