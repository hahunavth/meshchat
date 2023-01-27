package com.meshchat.client.net.client;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.cnative.CAPIServiceLib;
import com.meshchat.client.cnative.req.RequestAuth;
import com.meshchat.client.cnative.res.ResponseUser;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import javafx.util.Pair;
import jnr.ffi.NativeLong;
import jnr.ffi.Runtime;
import jnr.ffi.byref.NativeLongByReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TCPNativeClient <br>
 * - Wrap CAPIServiceLib: Tạo request và trả về response <br>
 * hoặc throw APICallException<br>
 *<br>
 * - Không sửa thay đổi DataStore (thay đổi ở ViewModel) <br>
 */
public class TCPNativeClient extends TCPBasedClient implements Runnable {

    CAPIServiceLib lib;
    Runtime rt;

    private int sockfd = -1;

    public TCPNativeClient(String host, int port) {
        super(host, port);
        this.lib = CAPIServiceLib.load();
        this.rt = Runtime.getRuntime(lib);
    }

    @Override
    protected void connect() {
        do {
            if (this.isCloseFlag()) {
//                this.setCloseFlag(false);
                return;
            }
            sockfd = this.lib.connect_server(this.host, this.port);
            if (sockfd == -1) {
                System.out.println("Try reconnect in 3 second!");
                // wait 3 second and reconnect
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } while (sockfd == -1);
        this.setConnected(true);
        System.out.println("Connect successfully!");
    }

    @Override
    public void close() {
        this.lib.close_conn();
        sockfd = this.lib.get_sockfd();
        if(this.isConnected()) {
            this.setConnected(false);
            this.setCloseFlag(true);
        } else {
            this.setCloseFlag(true);
        }
    }

    @Override
    public void run() {
        try {
            connect();
            if (isConnected()) {
                while(true) {
                    System.out.println(this.lib.get_sockfd());
                    if(this.lib.get_sockfd() == -1) {
                        System.out.println("reconnect");
                        this.setConnected(false);
                        if (!this.isCloseFlag())
                            connect();
                    } else {
//                        System.out.println(receive());
                    }
                    Thread.sleep(3000);
                    this.sockfd = this.lib.get_sockfd();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean _register(String uname, String pass, String phone, String email) {
        int stt;
        RequestAuth auth = new RequestAuth(rt);
        auth.uname.set(uname);
        auth.password.set(pass);
        auth.phone.set(phone);
        auth.email.set(email);
        stt = this.lib._register(this.sockfd, auth);
        return stt == 201;
    }

    public boolean _login(String uname, String pass) {
        int stt;
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stt = this.lib._login(this.lib.get_sockfd(), uname, pass);

        return stt == 200;
    }

    public boolean _logout() {
        return this.lib._logout(this.lib.get_sockfd()) == 201;
    }

    public long get_uid() {
        return this.lib._get_uid();
    }

    public long _create_chat(long u2id) throws APICallException {
        NativeLongByReference chat_id = new NativeLongByReference();
        int stt = this.lib._create_chat(this.lib.get_sockfd(), u2id, chat_id);
        switch (stt) {
            case 201:
                return chat_id.intValue();
            default:
                throw new APICallException(stt, "Cannot create chat");
        }
    }

    public List<Long> _get_chat_list() {
        long[] idls = new long[10];
        NativeLongByReference len = new NativeLongByReference(0);
        System.out.println("_get_chat_list sockfd: " + this.lib.get_sockfd());
        this.lib._get_chat_list(this.lib.get_sockfd(), 10, 0, idls, len);
        return Arrays.stream(idls).limit(len.intValue()).boxed().toList();
    }

    public Chat _get_chat_info(long chat_id) {
        /* FIXME: Require api _get_chat_info in server */
        Chat chat = new Chat();
        // TODO: Call api and update chat
        this.lib._get_chat_info(this.lib.get_sockfd(), chat_id);
        UserEntity user2 = new UserEntity(1, "abc", "123456789", "a@b.c");
        chat.setUser2(user2);
        return chat;
    }

    public boolean _create_conv(String gname){
        NativeLongByReference gid = new NativeLongByReference();
        int stt = this.lib._create_conv(this.lib.get_sockfd(), gname, gid);
        switch (stt){
            case 201:
                /* Update dataStore */
                Conv newConv = new Conv();
                /* FIXME what should newConv instance be modified before addConv() */
                ModelSingleton.getInstance().dataStore.addConv(gid.intValue(), newConv);
                return true;
            case 500:
            default:
                return false;
        }
    }

    public boolean _quit_conv(long gid, long user2_id){
        int stt;
//        NativeLongByReference gid = new NativeLongByReference();
        stt = this.lib._quit_conv(this.lib.get_sockfd(), gid, user2_id);
        return stt == 200;
    }

}
