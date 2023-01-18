#ifndef __CLIENT_LIB_INC_COMMON_H__
#define __CLIENT_LIB_INC_COMMON_H__

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <assert.h>

#include "../../server/inc/response.h"
#include "../../server/inc/request.h"
#include "../../server/inc/utils/string.h"
#include "../../server/inc/utils/sll.h"
#include "../../server/lib/libfdr/jval.h"

#include "./color.h"

#define TOKEN_LEN 16

#endif
