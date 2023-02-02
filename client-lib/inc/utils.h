#ifndef __CLIENT_LIB_INC_UTILS_H__
#define __CLIENT_LIB_INC_UTILS_H__

#include <pthread.h>

// synchronized
static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

#define LOCK pthread_mutex_lock(&mutex);
#define UNLOCK pthread_mutex_unlock(&mutex);

#endif
