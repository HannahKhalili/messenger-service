# Messenger Service

This is a simple REST message service. 

### Build
Following command to build and run tests:

    ./gradlew clean test build

### Usage
The server is listening on port `:8080` and will respond to the following http requests:
- POST /messages
    - query-params:
      - **receiver**={username}   _[mandatory]_
    - body: message text
  
          result:
          201   Sent Successfuly
          400   Bad Request due to empty body or receiver
- GET /messages
    - query-params:
        - **receiver**={username} _[mandatory]_
        - **only-unread**={true|false} _[optional]_
        - **from**={timestamp} _[optional]_
        - **to**={timestamp} _[optional]_
    - body: message text

          result:
          200   Success
          400   Bad Request due to empty receiver or invalid time

- DELETE /messages
    - query-params:
        - **messageIds**={comma-separated-ids} _[mandatory]_
    - body: message text

          result:
          200   Deleted Successfuly
          400   Bad Request due to empty or invalid id
          404   Not Found due to MessageIds contain a not-found id
