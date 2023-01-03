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
        Map<LibraryOption, Object> libraryOptions = new HashMap<>();
        libraryOptions.put(LibraryOption.LoadNow, true); // load immediately instead of lazily (ie on first use)
        libraryOptions.put(LibraryOption.IgnoreError, true); // calls shouldn't save last errno after call
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
}
