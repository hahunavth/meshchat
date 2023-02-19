# Meshchat
Welcome to the meshchat repository! This repository contains the code for a secure chat application with TCP protocol.

## Structure
The repository contains three main folders:

- `client`: This folder contains the Java code for the client application.
- `client-lib`: This folder contains C code for building a library that can be called from Java using jnr-ffi.
- `server`: This folder contains the Java code for the server application.

## Getting started
To use the meshchat application, follow these steps:

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

## Contributing
We welcome contributions to the meshchat project! If you find a bug or have an idea for a new feature, please submit a pull request.

## License
The MeshChat Client code is licensed under the MIT license. See the [LICENSE](./LICENSE.md) file for more details.
