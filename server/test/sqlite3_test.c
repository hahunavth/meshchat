#include <assert.h>
#include <stdio.h>
#include <sqlite3/sqlite3.h>

int main(int argc, char **argv)
{
	(void)argc;
	(void)argv;

	printf("thread mode = %d\n", sqlite3_threadsafe());	
	
	struct sqlite3 *db;

	assert(sqlite3_open("test.db", &db) == SQLITE_OK);

	printf("Opened database successfully\n");

	assert(sqlite3_close(db) == SQLITE_OK);

	return 0;
}