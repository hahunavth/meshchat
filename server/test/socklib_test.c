#include "socklib/tcp.h"
#include "socklib/udp.h"

#include <assert.h>
#include <signal.h>
#include <sys/select.h>

int main(int argc, char **argv)
{
	socklib_init();

	socket_t servsock = tcp4_create_listener("127.0.0.1", 5000, 100);
	print_sock_list();

	socklib_close_socket(servsock);
	print_sock_list();

	servsock = udp4_create_bound_socket("127.0.0.1", 5000);
	print_sock_list();

	socklib_destroy();
	print_sock_list();

	return 0;
}