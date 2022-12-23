package com.meshchat.client.experiments;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class CommonJNR extends Struct {

    public Struct.BYTE c = new Struct.BYTE();
    public Struct.Float f = new Struct.Float();
    public Struct.Double d = new Struct.Double();

    public Struct.int8_t int8 = new Struct.int8_t();
    public Struct.int16_t int16 = new Struct.int16_t();
    public Struct.int32_t int32 = new Struct.int32_t();
    public Struct.int64_t int64 = new Struct.int64_t();

    public Struct.u_int8_t u_int8 = new Struct.u_int8_t();
    public Struct.u_int16_t u_int16 = new Struct.u_int16_t();
    public Struct.u_int32_t u_int32 = new Struct.u_int32_t();
    public Struct.u_int64_t u_int64 = new Struct.u_int64_t();

    public Struct.String str1 = new Struct.AsciiString(8192);
    public Struct.String str2 = new Struct.AsciiString(8192);
    public Struct.String str3 = new Struct.AsciiStringRef();
    public Struct.String str4 = new Struct.AsciiStringRef();

    public CommonJNR(Runtime runtime) {
        super(runtime);
    }
}
