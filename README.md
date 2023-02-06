# meshchat
Chat application using TCP protocol. 

## Folder structure
- client: application java client
- server: application c server
- client-lib: c library build for client

## Setup
- Server: 
  ```shell
  cd ./server
  make build
  ```

- Client: 
  - Build client-lib
    ```shell
    cd ./client
    make build
    make start_server 
    ```
  - Run java client using intellji idea

