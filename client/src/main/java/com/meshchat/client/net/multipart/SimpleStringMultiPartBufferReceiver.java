package com.meshchat.client.net.multipart;

public class SimpleStringMultiPartBufferReceiver implements IMultiPartBufferReceiver {

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public boolean isFirstPart(char[] buff) {
        return true;
    }

    @Override
    public boolean isFinallyPart(char[] buff) {
        for(int i = buff.length - 1; i >= 0; i--) {
            if(buff[i] == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public char[] splitBody(char[] buff) {
        return buff;
    }

    @Override
    public void joinBodyBuff(char[] bodyBuffer) {
        for (char c : bodyBuffer) {
            this.stringBuilder.append(c);
            if (c == 0) {
                return;
            }
        }
    }

    @Override
    public String getBody() {
        return stringBuilder.toString();
    }
}
