package com.meshchat.client.cnative.res;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

/**
 * typedef struct
 * {
 * 	uint32_t *idls;
 * 	uint32_t msg_id;
 * 	uint32_t conv_id, chat_id;
 * 	uint32_t from_uid;
 * 	uint32_t reply_to;
 * 	uint32_t created_at;
 * 	uint8_t msg_type;
 * 	uint8_t content_type;
 * 	uint32_t content_length;
 * 	char *msg_content;
 * } response_msg;
 */
public class ResponseMsg extends Struct {

	/**
	 * Giữ attribute này để đúng với struct trong C,
	 * Tránh ảnh hưởng đến việc map type
	 */
	@Deprecated
	private Struct.u_int32_t idls = new u_int32_t();
	public Struct.u_int32_t msg_id = new u_int32_t();
	// NOTE: chat_id before conv_id in here
	public Struct.u_int32_t chat_id = new u_int32_t();
	public Struct.u_int32_t conv_id = new u_int32_t();
	public Struct.u_int32_t from_uid = new u_int32_t();
	public Struct.u_int32_t reply_to = new u_int32_t();
	public Struct.u_int32_t created_at = new u_int32_t();
	public Struct.Unsigned8 msg_type = new Unsigned8();
	public Struct.Unsigned8 content_type = new Unsigned8();
	public Struct.u_int32_t content_length = new u_int32_t();
    public Struct.String msg_content = new Struct.AsciiStringRef();

    public ResponseMsg(Runtime runtime) {
        super(runtime);
    }
}
