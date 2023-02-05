#include "sql.h"
#include "utils/random.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

#include <openssl/md5.h>

#define HASHED_LEN 16

static struct sqlite3 *db;

static int hash_str(const char *str, char *res)
{
	MD5_CTX md5_ctx;
	char hashed[HASHED_LEN];

	if (MD5_Init(&md5_ctx) == 0)
		return -1;

	if (MD5_Update(&md5_ctx, str, strlen(str)) == 0)
		return -1;

	if (MD5_Final((unsigned char *)hashed, &md5_ctx) == 0)
		return -1;

	/* output a hexa string */
	for (int i = 0; i < HASHED_LEN; i++)
	{
		uint8_t b = hashed[i];
		sprintf(res + (i << 1), "%02x", b);
	}
	res[(HASHED_LEN << 1)] = '\0';

	return 0;
}

void gen_users(int n_users)
{
	int rc;
	char uname[8];
	const char *password = "password";
	char hashed_password[HASHED_LEN<<1];
	hash_str(password, hashed_password);
	const char *phone = "123456789";
	const char *email = "abc@mail.com";
	user_schema user = {
		.uname = uname, .password = hashed_password, .phone = phone, .email = email
	};

	for(int i=0; i<n_users; i++)
	{
		snprintf(uname, 8, "user%d", i);
		user_create(db, &user, &rc);
		assert(sql_is_ok(rc));
	}
}

void gen_conv(int n_convs_per_user, int n_users_per_conv, int n_users)
{
	int rc;
	int n_conv = n_convs_per_user * n_users;
	char conv_name[8];
	char selected[n_users+1];
	int k = 0;

	for(int i=1; i<=n_users; i++)
	{
		for(int j=0; j<n_convs_per_user; j++)
		{
			memset(selected, 0, n_users+1);
			selected[i] = 1;
			snprintf(conv_name, 8, "conv%d", k++);
			uint32_t conv_id = conv_create(db, (uint32_t)i, conv_name, &rc);
			int user = i+1;
			for(int joined=0; joined<n_users_per_conv; joined++)
			{
				if(selected[user]) continue;
				selected[user] = 1;
				conv_join(db, (uint32_t)user, conv_id, &rc);
				if(++user > n_users) user=1;
			}
		}
	}
}

void gen_chat(int n_users)
{
	int rc;

	for(uint32_t i=1; i<=n_users; i++)
	{
		for(int j=i+1; j<=n_users; j+=2)
		{
			chat_create(db, i, j, &rc);
		}
	}
}

int main(int argc, char **argv)
{
	const char *db_path = "db/meshserver.db";
	if(argc > 1)
	{
		db_path = argv[1];
	}

	assert(sqlite3_open(db_path, &db) == SQLITE_OK);

	gen_users(20);
	gen_conv(10, 10, 20);
	gen_chat(20);

	assert(sqlite3_close(db) == SQLITE_OK);
}