#include "test_common.h"

int main()
{

  CONNECT_SERVER();
  before_each();

  LOGIN_AS_USER_X(1);

  LOGOUT();

  CLOSE_CONN();
  return 0;
}
