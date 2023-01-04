package com.meshchat.client.experiments.libs;

import com.meshchat.client.experiments.ComUnionJNR;
import com.meshchat.client.experiments.CommonJNR;
import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;

/**
 * Interface để load thư viện C
 * <br>
 * Using jnr-ffi:
 * <br>
 *  SIGNED:
 *  char:             byte or Byte	                    8-bit integer
 *  short:            short or Short	                16-bit integer
 *  int:              int or Integer	                32-bit integer
 *  long:             long or Long or NativeLong	    natural long, 32-bit integer on 32-bit systems, 64-bit integer on 64-bit systems
 *  long :            long long or Long	                64-bit integer
 *  float:            float or Float	                32 bit floating point
 *  double:           double or Double	                64 bit floating point
 * <br>
 *  UNSIGNED:
 *  - Nếu để cùng số bit -> overflow
 *  - Với (char *):
 *      + Param: byte[] || char[]
 *      + ReturnType: String
 *  - Hàm printf:
 *      + Print ko in ra ngay mà khi end mới in (chưa fix)
 */
public interface TypeMappingLib {
        default TypeMappingLib load() {
                return  LibraryLoader
                        .create(TypeMappingLib.class)
                        .failImmediately()
                        .search("/home/kryo/Desktop/meshchat/client/src/main/resources")
                        .load("typemapping");
        }

        /**
         * Primitive type
         */
        void in_char(String str);
        byte tm_int8_t(byte e);
        byte tm_int8_t(long e);         // T: overload
        byte tm_u_int8_t(byte e);

        byte tm_u_int16_t(long e);      // T: Expect int but use byte
        long tm_u_int64_t(long e);

        /**
         * Type: common_t -> CommonJNR
         */
        CommonJNR create_common_t(
                byte c,
                float f,
                double d,

                byte int8,
                short int16,
                int int32,
                long int64,

                byte u_int8,
                short u_int16,
                int u_int32,
                long u_int64,

                String str1,
                String str2,
                String str3,
                String str4
        );

        // obj -> buffer
        String c_common_t_to_buffer(@In CommonJNR in, @Out int len_out);
        int common_t_to_buffer(@In CommonJNR in, @Out byte[] buff, @In int buff_len, @Out int len_out);

        // buffer -> obj
        CommonJNR decode_common_t( @In  byte[] buff, @Out CommonJNR out);
        CommonJNR c_decode_common_t(@In byte[] buff);
        void print_common_t(CommonJNR in);        // print


        /**
         * Type: com_union_t -> ComUnionJNR
         * FIXME: com_union_t map nested struct pointer failed
         */
        ComUnionJNR create_com_union_t_common_t();
        ComUnionJNR create_com_union_t_int();

        ComUnionJNR create_com_union_t_str();
        void set_com_union_t_common_t(ComUnionJNR u, CommonJNR s);
        void print_com_union_t_common_t(ComUnionJNR t, String type);
        CommonJNR com_union_t_get_common_t(ComUnionJNR in);

        /**
         * Socket
         */
        int run_echo_tcp_client();

        /**
         * client test
         */
        int connect_server(String str, int port);
        int simple_send(String str);
        String simple_recv();
        int get_sockfd();
        void close_conn();
}
