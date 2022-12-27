package com.meshchat.client.experiments;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.Union;

/**
 * Test jnr:
 * - Sử dụng: struct com_union_t
 * FIXME: Chưa xử lý đc con trỏ lồng trong union
 */
public class ComUnionJNR extends Union {

    /**
     * Tham trị: common_t common;
     * Expected: common_t *common;  // trong code c
     */
    public final CommonJNR common = (new CommonJNR(getRuntime(), this));
    public final Union.int8_t i = new Struct.int8_t();
    public final Union.String str = new Struct.AsciiStringRef();

    void show() {
        System.out.println(common);
        System.out.println(i);
        System.out.println(str);
    }

    public ComUnionJNR(Runtime runtime) {
        super(runtime);
    }

}
