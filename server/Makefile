config:
ifdef DEBUG
	cmake -DCMAKE_BUILD_TYPE=Debug -S . -B debug
else
	cmake -S . -B build
endif

build:
ifdef DEBUG
	cmake --build debug/
else
	cmake --build build/
endif

test:
ifdef TEST
	ctest -VV --test-dir build -R $(TEST)
else
	ctest -VV --test-dir build
endif

clean:
	rm -rf ./build/* server sqlite

# migration
migrateup: sqlite db/schema.up.sql
	./sqlite db/meshserver.db < db/schema.up.sql

migratedown: sqlite db/schema.down.sql
	./sqlite db/meshserver.db < db/schema.down.sql

.PHONY: config build test clean migrateup migratedown
