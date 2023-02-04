package com.meshchat.client.cnative;

import com.meshchat.client.cnative.req.RequestAuth;
import com.meshchat.client.cnative.res.ResponseMsg;
import com.meshchat.client.cnative.res.ResponseUser;
import com.meshchat.client.db.entities.MsgEntity;
import com.meshchat.client.utils.Config;
import jnr.ffi.NativeLong;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.byref.NativeLongByReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CAPIServiceLibTest {

    CAPIServiceLib lib;
    Runtime rt;

    int fd;
    String buff = new String();

    @BeforeEach
    void setUp() {
        lib = CAPIServiceLib.load();
        rt = Runtime.getRuntime(lib);
    }

    @AfterAll
    public static void cleanUp(){
        System.out.println("After All cleanUp() method called");
    }

    @Test
    void testGetParams() {
        assertEquals(this.lib.get_sockfd(), -1);
    }

    @Test
    void testConnectServer() {
        int socketfd = this.lib.connect_server("127.0.0.1", 9000);
        System.out.println(
                socketfd
        );

        assertTrue(socketfd > 0);
        assertEquals(0, this.lib._get_uid());
        System.out.println(this.lib._get_token());
        assertEquals(0, this.lib.is_authenticated());

//        CharSequence token = new StringBuffer(Config.TOKEN_LEN);
//        long uid = -1;
//        this.lib.get_auth(token, uid);
//        System.out.println(uid);

        this.lib.close_conn();
    }

    @Test
    void testRegister() {
       int fd =  this.lib.connect_server("0.0.0.0", 9000);
        assertTrue(this.lib.get_sockfd() > 0);

        RequestAuth auth = new RequestAuth(rt);
        auth.email.set("abc@def.ghi");
        auth.phone.set("123456789");
        auth.password.set("123456789");
        auth.uname.set("user test");

        int stt = this.lib._register(fd, auth);
        System.out.println("_register: " + stt);

        stt = this.lib._login(fd, "user test", "123456789");
        System.out.println("_login: " + stt);

        ResponseUser responseUser = new ResponseUser(rt);
        stt = this.lib._get_user_info(fd, this.lib._get_uid(), responseUser);
        System.out.println("_get_user_info: " + responseUser.toString());
        System.out.println("_get_user_info: " + stt);
//
        NativeLongByReference gid = new NativeLongByReference(-100);
        stt = this.lib._create_conv(fd, "My first conv", gid);
        System.out.println("_create_conv: " + stt);
        System.out.println("_create_conv: " + gid.intValue());
        assertTrue(gid.intValue() > 0);
        assertEquals(201, stt);
//
        stt = this.lib._logout(fd);
        System.out.println("_logout: " + stt);

        this.lib.close_conn();
    }

    @Test
    void TestReturnListApi () {
        int fd =  this.lib.connect_server("0.0.0.0", 9000);
        assertTrue(this.lib.get_sockfd() > 0);

        int stt;
        stt = this.lib._login(fd, "user test", "123456789");
        System.out.println("_login: " + stt);
        assertEquals(200, stt);

        long[] idls = new long[1000];
        NativeLongByReference len = new NativeLongByReference(0);
        this.lib._get_conv_list(fd, 10, 0, idls, len);
        for(int i = 0; i < len.getValue().intValue(); i++) {
            System.out.print(idls[i]);
            System.out.print(", ");
        }

        this.lib.close_conn();
    }

    @Test
    void TestMsgGetDetail() {
        int socketfd = this.lib.connect_server("127.0.0.1", 9000);
        assertTrue(socketfd > 0);
        int uid = this.lib._login(this.lib.get_sockfd(), "user_1", "pass");
        assertTrue(uid > 0);
//        ResponseMsg msg = new ResponseMsg(rt);
//        this.lib._get_msg_detail(this.lib.get_sockfd(), 3, msg);
////        msg.msg_type.set(64 + msg.msg_type.byteValue());
////        msg.msg_id.set(3);
//        System.out.println("msg_id: " + msg.msg_id.intValue());
//        System.out.println("from_uid: " + msg.from_uid.intValue());
//        System.out.println("reply_to: " + msg.reply_to.intValue());
//        System.out.println("created_at: " + msg.created_at.intValue());
//        System.out.println("msg_content: " + msg.msg_content.get());
//        System.out.println("content_length: " + msg.content_length.get());
//        System.out.println("chat_id: " + msg.chat_id.intValue());
//        System.out.println("conv_id: " + msg.conv_id.intValue());
//        System.out.println("msg_type: " + msg.msg_type.byteValue());
//        MsgEntity msgEntity = new MsgEntity(msg);
//        System.out.println(msgEntity);
    }

    @Test
    void testGetConvInfo() {
//        this.lib._login(this.lib.get_sockfd(), "user_1", "pass");
//        NativeLongByReference admin_id = new NativeLongByReference();
//        CharSequence gname = new StringBuffer();
//        int stt = this.lib._get_conv_info(this.lib.get_sockfd(), 1, admin_id, gname);
    }
}