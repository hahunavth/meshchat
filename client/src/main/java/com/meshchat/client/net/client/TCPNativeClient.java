package com.meshchat.client.net.client;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.meshchat.client.cnative.CAPIServiceLib;
import com.meshchat.client.cnative.req.RequestAuth;
import com.meshchat.client.cnative.res.ResponseUser;
import com.meshchat.client.db.entities.MsgEntity;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.*;
import jnr.ffi.Runtime;
import jnr.ffi.byref.NativeLongByReference;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * TCPNativeClient <br>
 * - Wrap CAPIServiceLib: Tạo request và trả về response <br>
 * hoặc throw APICallException<br>
 *<br>
 * - Không sửa thay đổi DataStore (thay đổi ở ViewModel) <br>
 */
@Singleton
public class TCPNativeClient extends TCPBasedClient implements Runnable {

    private Logger logger;

    private final CAPIServiceLib lib;
    private final Runtime rt;

    private DataStore ds;

    private int sockfd = -1;

    @Inject
    public TCPNativeClient(DataStore ds, Logger logger) {
        super("127.0.0.1", 9000);
        this.ds = ds;
        this.logger = logger;

        this.lib = CAPIServiceLib.load();
        this.rt = Runtime.getRuntime(lib);
    }

    public TCPNativeClient(String host, int port) {
        super(host, port);

        this.lib = CAPIServiceLib.load();
        this.rt = Runtime.getRuntime(lib);
    }

    public void initClient(String host, int port) {

        System.out.println("Init client: " + host + "/" + port);
        this.setCloseFlag(false);
        this.setHost(host);
        this.setPort(port);
        Thread clientThread = new Thread(this);
        clientThread.start();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void connect() {
        do {
            if (this.isCloseFlag()) {
                return;
            }
            sockfd = this.lib.connect_server(this.host, this.port);
            if (sockfd == -1) {
                logger.info("Try reconnect in 3 second!");
                // wait 3 second and reconnect
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } while (sockfd == -1);
        this.setConnected(true);
        logger.info("Connect successfully!" + this.lib.get_sockfd());
    }

    @Override
    public void close() {
        if (this.lib != null) {
            this.lib.close_conn();
            sockfd = this.lib.get_sockfd();
            if(this.isConnected()) {
                this.setConnected(false);
                this.setCloseFlag(true);
            } else {
                this.setCloseFlag(true);
            }
        } else {
            logger.info("Lib is null");
        }
    }

    @Override
    public void run() {
        try {
            connect();
            if (isConnected()) {
                while(true) {
                    if(this.lib.get_sockfd() == -1) {
                        logger.info("reconnect");
                        this.setConnected(false);
                        if (!this.isCloseFlag())
                            connect();
                    }
                    Thread.sleep(3000);
                    this.sockfd = this.lib.get_sockfd();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void  _register(String uname, String pass, String phone, String email) throws APICallException {
        int stt;
        RequestAuth auth = new RequestAuth(rt);
        auth.uname.set(uname);
        auth.password.set(pass);
        auth.phone.set(phone);
        auth.email.set(email);

        stt = this.lib._register(this.lib.get_sockfd(), auth);

        switch (stt) {
            case 201:
                break;
            case 404:
            default:
                throw new APICallException(stt, "Cannot register");
        }
    }

    public synchronized boolean _login(String uname, String pass) {
        int stt;

        stt = this.lib._login(this.lib.get_sockfd(), uname, pass);
        System.out.println("Status: " + stt);

        return stt == 200;
    }

    public synchronized boolean _logout() {
        return this.lib._logout(this.lib.get_sockfd()) == 201;
    }

    public synchronized UserEntity _get_user_by_id(long uid){
        UserProfile up;
        ResponseUser ru = new ResponseUser(this.rt);
        int stt = this.lib._get_user_info(this.lib.get_sockfd(), uid, ru);
        switch (stt){
            case 200:
                up = new UserProfile(uid, ru.uname.get(), null, ru.phone.get(), ru.email.get());
                return up.getEntity();
            default:
                return null;
        }
    }

    public synchronized List<Long> _get_user_search(String searchTxt, int limit, int offset) throws APICallException{
        long[] ls = new long[limit];
        NativeLongByReference len = new NativeLongByReference();
        int stt = this.lib._get_user_search(this.lib.get_sockfd(), searchTxt, offset, limit, ls, len);
        switch (stt){
            case 200:
            case 404:
                break;
            default:
                throw new APICallException(stt, "Failed to search");
        }
        return Arrays.stream(ls).limit(len.intValue()).boxed().toList();
    }

    public synchronized long get_uid() {
        return this.lib._get_uid();
    }

    public synchronized long _create_chat(long u2id) throws APICallException {
        NativeLongByReference chat_id = new NativeLongByReference();
        int stt = this.lib._create_chat(this.lib.get_sockfd(), u2id, chat_id);
        switch (stt) {
            case 201:
                return chat_id.intValue();
            default:
                throw new APICallException(stt, "Cannot create chat");
        }
    }

    public synchronized List<Long> _get_chat_list() {
        long[] idls = new long[10];
        NativeLongByReference len = new NativeLongByReference(0);
        this.lib._get_chat_list(this.lib.get_sockfd(), 10, 0, idls, len);
        return Arrays.stream(idls).limit(len.intValue()).boxed().toList();
    }

    public synchronized Chat _get_chat_info(long chat_id) throws APICallException {
        Chat chat = new Chat();
        //
        NativeLongByReference mem1_id = new NativeLongByReference(-1);
        NativeLongByReference mem2_id = new NativeLongByReference(-1);
        int stt = this.lib._get_chat_info(this.lib.get_sockfd(), chat_id, mem1_id, mem2_id);
        if (stt != 200) {
            throw new APICallException(stt, "Cannot get api");
        }
        long u2Id = mem1_id.intValue();
        if(this.lib._get_uid() == mem1_id.intValue()) {
            u2Id = mem2_id.intValue();
        }

        ResponseUser responseUser = new ResponseUser(this.rt);
        stt = this.lib._get_user_info(this.lib.get_sockfd(), u2Id, responseUser);
        if (stt != 200) {
            throw new APICallException(stt, "Cannot get user info");
        }
        UserEntity user2 = new UserEntity(u2Id, responseUser.uname.get(), responseUser.phone.get(), responseUser.email.get());
        chat.setUser2(user2);
        chat.id = chat_id;

        return chat;
    }

    public synchronized long _create_conv(String gname) throws  APICallException {
        NativeLongByReference gid = new NativeLongByReference();
        Conv conv = new Conv();
        int stt = this.lib._create_conv(this.lib.get_sockfd(), gname, gid);
        switch (stt){
            case 201: case 408:
                return gid.intValue();
            default:
                throw new APICallException(stt, "Cannot create chat");
        }
    }

    public synchronized List<Long> _get_conv_list() {
        long[] idls = new long[10];
        NativeLongByReference len = new NativeLongByReference(0);
        this.lib._get_conv_list(this.lib.get_sockfd(), 10, 0, idls, len);
        return Arrays.stream(idls).limit(len.intValue()).boxed().toList();
    }

    public synchronized void _conv_join(long conv_id, long user_id) throws APICallException{
        int stt = this.lib._join_conv(this.lib.get_sockfd(), conv_id, user_id);
        // FIXME: api return 408 but success?
        if(stt != 200 && stt != 408){
            throw new APICallException(stt, "Failed to join user "+user_id+" to conv "+conv_id);
        }
    }

    public synchronized Conv _get_conv_info(long conv_id) throws APICallException {
        Conv conv;
        int stt;
        {
            NativeLongByReference admin_id = new NativeLongByReference();
            byte[] _gname = new byte[1000];

            stt = this.lib._get_conv_info(this.lib.get_sockfd(), conv_id, admin_id, _gname);
            switch (stt){
                case 200:
                    String gname = new String(_gname, StandardCharsets.UTF_8);
                    int nullIndex = gname.indexOf('\0');
                    if (nullIndex != -1) {
                        gname = gname.substring(0, nullIndex);
                    }
                    conv = new Conv();
                    conv.id = conv_id;
                    conv.setAdmin(_get_user_by_id(admin_id.intValue()));
                    conv.setName(gname);
                    break;
                default:
                    throw new APICallException(stt, "get conv info failed");
            }
        }
        {
            long[] idls = new long[2048];
            NativeLongByReference len = new NativeLongByReference();
            stt = this.lib._get_conv_members(this.lib.get_sockfd(), conv_id, idls, len);
            switch (stt) {
                case 200:
                    for(int i = 0; i < len.intValue(); i++) {
                        ResponseUser user = new ResponseUser(rt);
                        stt = this.lib._get_user_info(this.lib.get_sockfd(), idls[i], user);
                        if (stt != 200)
                            throw new APICallException(stt, "get user info failed");
                        conv.members.put(idls[i], new UserProfile(idls[i], user.uname.get(), "", user.phone.get(), user.email.get()));
                    }
            }
        }
        return conv;
    }

    public synchronized boolean _quit_conv(long gid, long user2_id){
//        NativeLongByReference gid = new NativeLongByReference();
        int stt = this.lib._quit_conv(this.lib.get_sockfd(), gid, user2_id);
        return stt == 200;
    }

    public synchronized Message _send_msg(ChatRoomType type, long room_id, long replyTo, String msg) throws Exception {
        long conv_id = 0;
        long chat_id = 0;
        switch (type) {
            case CHAT -> {
                chat_id = room_id;
            }
            case CONV -> {
                conv_id = room_id;
            }
            default -> throw new Exception("Invalid ChatRoomType");
        }

        NativeLongByReference msg_id = new NativeLongByReference(-1);
        int stt;
        stt = this.lib._send_msg_text(this.lib.get_sockfd(), conv_id, chat_id, replyTo, msg, msg_id);

        switch (stt) {
            case 201:
                int _msg_id = msg_id.intValue();
                return new Message(_msg_id, this.get_uid(), replyTo, msg, 0, false);
            default:
                throw new APICallException(stt, "Cannot send msg");
        }
    }

    public synchronized List<Long> _get_msg_all(ChatRoomType type, int roomId, int limit, int offset) throws APICallException {

        int conv_id = 0;
        int chat_id = 0;
        switch (type) {
            case CHAT -> {
                chat_id = roomId;
            }
            case CONV -> {
                conv_id = roomId;
            }
            default -> {
                throw new RuntimeException("Not implemented");
            }
        }

        int stt = 0;
        long[] idls = new long[limit];
        NativeLongByReference _len = new NativeLongByReference(-1); // FIXME: CAUSE COREDUMP -> disable set _len in service
        Arrays.fill(idls, -1);

        stt = this.lib._get_msg_all(
                this.lib.get_sockfd(),
                limit,
                offset,
                conv_id,
                chat_id,
                idls,
                _len
        );

        if (stt != 200) {
            throw new APICallException(stt, "Cannot get all msg");
        }

        return Arrays.stream(idls).limit(_len.intValue()).boxed().toList();
    }

    public synchronized MsgEntity _get_msg_detail(long msg_id) throws APICallException {
        NativeLongByReference chatId = new NativeLongByReference(-1);
        NativeLongByReference convId = new NativeLongByReference(-1);
        NativeLongByReference replyTo = new NativeLongByReference(-1);
        NativeLongByReference fromUid = new NativeLongByReference(-1);
        NativeLongByReference createdAt = new NativeLongByReference(-1);
        NativeLongByReference contentType = new NativeLongByReference(-1);
        NativeLongByReference contentLength = new NativeLongByReference(-1);
        byte[] msgContent = new byte[1000];
        int stt = this.lib._get_msg_detail_raw(this.lib.get_sockfd(),
                msg_id,
                chatId,
                convId,
                replyTo,
                fromUid,
                createdAt,
                contentType,
                contentLength,
                msgContent
                );
        switch (stt) {
            case 200:
                MsgEntity msgEntity = new MsgEntity();
                msgEntity.setId(msg_id);
                msgEntity.setChat_id(chatId.intValue());
                msgEntity.setConv_id(convId.intValue());
                msgEntity.setReply_to(replyTo.intValue());
                msgEntity.setFrom_user_id(fromUid.intValue());
                msgEntity.setCreated_at(createdAt.intValue());
                msgEntity.setType(contentType.byteValue());
                String _msgContent = new String(msgContent, StandardCharsets.UTF_8);
                _msgContent = _msgContent.substring(0, contentLength.intValue());
                msgEntity.setContent(_msgContent);
                return msgEntity;
            case 404:
            default:
                throw new APICallException(stt, "Cannot get msg detail");
        }
    }

    public synchronized void deleteMsg(long msg_id) throws APICallException {
        int stt = this.lib._delete_msg(this.lib.get_sockfd(), msg_id);

        switch (stt) {
            case 200:
                return;
            default:
                throw new APICallException(stt, "Notify new failed");
        }
    }

    public synchronized List<Long> notifyDeleteMsg(ChatRoomType type, long roomId) throws APICallException {
        long chatId = 0;
        long convId = 0;
        switch (type) {
            case CHAT -> {
                chatId = roomId;
            }
            case CONV -> {
                convId = roomId;
            }
            default -> {
                throw new RuntimeException("Not implemented");
            }
        }
        long[] idls = new long[2048];
        NativeLongByReference _len = new NativeLongByReference();
        int stt = this.lib._notify_del_msg(
                this.lib.get_sockfd(),
                convId, chatId,
                idls, _len
                );

        switch (stt) {
            case 200:
                return Arrays.stream(idls).limit(_len.intValue()).boxed().toList();
            default:
                throw new APICallException(stt, "Notify new failed");
        }
    }

    public synchronized List<Long> notifyNewMsg() throws APICallException {
        long[] idls = new long[2048];
        NativeLongByReference _len = new NativeLongByReference();
        int stt = this.lib._notify_new_msg(
                this.lib.get_sockfd(),
                idls, _len
        );

        switch (stt) {
            case 200:
                return Arrays.stream(idls).limit(_len.intValue()).boxed().toList();
            default:
                throw new APICallException(stt, "Notify new failed");
        }
    }
}
