.PHONY: config build test clean migrateup migratedown init_server build_native_lib

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
	rm -rf ./build/*

init_server:
	cd ../server/; make; cd build; make; cd .. ; make migratedown && make migrateup && ./server
