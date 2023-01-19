package com.meshchat.client.net.client;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.cnative.CAPIServiceLib;
import com.meshchat.client.cnative.req.RequestAuth;
import com.meshchat.client.cnative.res.ResponseUser;
import com.meshchat.client.model.Conv;
import javafx.util.Pair;
import jnr.ffi.NativeLong;
import jnr.ffi.Runtime;
import jnr.ffi.byref.NativeLongByReference;

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
//        if (stt == 201) {
//            ResponseUser user = new ResponseUser(rt);
//            stt = this.lib._get_user_info(this.sockfd, this.lib._get_uid(), user);
//            System.out.println(user.uname);
//            return true;
//        }
        System.out.println(stt);
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
        System.out.println(stt);
        return stt == 200;
    }

    public boolean _create_conv(String gname){
        NativeLongByReference gid = new NativeLongByReference();
        int stt = this.lib._create_conv(this.lib.get_sockfd(), gname, gid);
        switch (stt){
            case 201:
                /* Update dataStore */
                Conv newConv = new Conv();
                /* FIXME what should newConv instance be modified before addConv() */
                ModelSingleton.getInstance().dataStore.addConv(gid.longValue(), newConv);
                return true;
            case 500:
            default:
                return false;
        }
    }

    public boolean _quit_conv(long gid){
        int stt;
//        NativeLongByReference gid = new NativeLongByReference();
        stt = this.lib._quit_conv(this.lib.get_sockfd(), gid);
        return stt == 200;
    }

}
