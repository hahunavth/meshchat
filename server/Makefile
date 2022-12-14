config:
	cmake -S . -B build

build:
	cmake --build build/
	
test:
ifdef TEST
	ctest -VV --test-dir build -R $(TEST)
else
	ctest -VV --test-dir build
endif

clean:
	rm -rf ./build/*

# migration
migrateup: sqlite db/schema.up.sql
	./sqlite db/meshserver.db < db/schema.up.sql

migratedown: sqlite db/schema.down.sql
	./sqlite db/meshserver.db < db/schema.down.sql

.PHONY: config build test clean migrateup migratedown
