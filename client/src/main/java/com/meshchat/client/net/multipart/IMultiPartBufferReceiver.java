package com.meshchat.client.net.multipart;

/**
 * Join multi-part buffer
 * @deprecated
 */
public interface IMultiPartBufferReceiver {
    boolean isFirstPart(char[] buff);
    boolean isFinallyPart(char[] buff);
    char[] splitBody (char[] buff);
    void joinBodyBuff(char[] bodyBuffer);
    String getBody();
}
