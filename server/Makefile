config:
	cmake -S . -B build

build:
	cmake --build build/

clean:
	rm -rf ./build/*
	
test:
ifdef TEST
	ctest -VV --test-dir build -R $(TEST)
else
	ctest -VV --test-dir build
endif

# migration
migrateup: sqlite db/schema.up.sql
	./sqlite db/meshserver.db < db/migration/schema.up.sql

migratedown: sqlite db/schema.down.sql
	./sqlite db/meshserver.db < db/migration/schema.down.sql

.PHONY: config build test clean migrateup migratedown
