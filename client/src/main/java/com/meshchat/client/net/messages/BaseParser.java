package com.meshchat.client.net.messages;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public abstract class BaseParser {
    private final Charset charset = StandardCharsets.US_ASCII;
    private final CharsetEncoder encoder = StandardCharsets.US_ASCII.newEncoder();

    public byte[] stringToBytes(String s) {
        return charset.encode(s).array();
    }

    public String bytesToString(byte[] bytes) {
        Charset charset = StandardCharsets.UTF_8;
        return new String(bytes, charset);
    }

    abstract byte[] encode(BaseHeader header, BaseBody body);
    abstract BaseInfo parse(byte[] buffer);
}
