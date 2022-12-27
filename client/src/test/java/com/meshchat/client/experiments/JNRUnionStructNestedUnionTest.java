package com.meshchat.client.experiments;

import com.meshchat.client.experiments.libs.TypeMappingLib;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JNRUnionStructNestedUnionTest {

    private TypeMappingLib libC;
    private Runtime runtime;

    @BeforeEach
    void setup() {
         libC = LibraryLoader
                .create(TypeMappingLib.class)
                .failImmediately()
                .search("/home/kryo/Desktop/meshchat/client/src/main/resources")
                .load("typemapping");

         runtime = Runtime.getRuntime(libC);
    }


    @Test
    void test_create_common_t () {
        ComUnionJNR comUnionJNR = libC.create_com_union_t_common_t();

//        comUnionJNR.show();
        libC.print_com_union_t_common_t(comUnionJNR, "common");
//        libC.print_common_t(comUnionJNR.common);

        System.out.println(libC.com_union_t_get_common_t(comUnionJNR));
//        System.out.println(comUnionJNR.common.c);

    }
}
