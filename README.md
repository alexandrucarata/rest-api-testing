# REST API Testing Training
*This is the project I developed as part of my REST API testing training to become a Java QA Automation Engineer within the company.*

## Technology Stack:
1. Programming language - `Java`
2. Build and project management tool - `Maven`
3. Testing framework - `JUnit 5`
4. Request handling - `Apache HTTP Client` & `Rest Assured`
5. Reporting framework - `Allure`
6. Integration - `Docker`

## Overview
During the training, will test the provided web-service from Docker container. All requirements for the web-service must work properly only for one-thread execution. Web-service handles storage of users and their information: `name`, `age`, `sex` and `zip code`.

Application provides:
- information about all stored users;
- possibility to create, update, delete users;
- information about available zip codes;
- possibility to add new zip codes.

## Task 1 - Authentication:
1. Go to `http://localhost:<port>/swagger-ui/` and read information about all endpoints.
2. Create Maven project with the dependencies needed.
3. Develop client code to get bearer tokens with read and write scopes separately.
4. No tests should be developed for this task
### Requirements:
### Scenario #1:
- when I send `POST request` to `/oauth/token`
- and I put parameters `grant_type=client_credentials` and `scope=write`
- and I set username and password for `basic auth`
- then I get `response` with `bearer token`, which works for any `POST, PUT, PATCH, DELETE` methods of web-service
### Scenario #2:
- when I send `POST request` to `/oauth/token`
- and I put parameters `grant_type=client_credentials` and `scope=read`
- and I set username and password for `basic auth`
- then I get `response` with `bearer token`, which works for any `GET` methods of web-service
## Task 2 - Zip Codes:
Write tests to cover requirements for zip codes functionality
### Requirements:
### Scenario #1:
- Given I am authorized user
- When I send `GET` request to `/zip-codes` endpoint
- Then I get `200` response code
- And I get all available zip codes in the application for now
### Scenario #2:
- Given I am authorized user
- When I send `POST` request to `/zip-codes/expand` endpoint
- And Request body contains list of zip codes
- Then I get `201` response code
- And zip codes from request body are added to available zip codes of application
### Scenario #3:
- Given I am authorized user
- When I send `POST` request to `/zip-codes/expand` endpoint
- And Request body contains list of zip codes
- And list of zip codes has duplications for available zip codes
- Then I get `201` response code
- And zip codes from request body are added to available zip codes of application without duplicates
### Scenario #4:
- Given I am authorized user
- When I send `POST` request to `/zip-codes/expand` endpoint
- And Request body contains list of zip codes
- And list of zip codes has duplications of already used zip codes
- Then I get `201` response code
- And zip codes from request body are added to available zip codes of application without duplicates
## Task 3 - Create Users:
Write tests to cover requirements for user creation functionality
### Requirements:
### Scenario #1:
- Given I am authorized user
- When I send `POST` request to `/users` endpoint
- And Request body contains user to add
- And all fields are filled in
- Then I get `201` response code
- And User is added to application
- And Zip code is removed from available zip codes of application
### Scenario #2:
- Given I am authorized user
- When I send `POST` request to `/users` endpoint
- And Request body contains user to add
- And only required fields are filled in
- Then I get `201` response code
- And User is added to application`
### Scenario #3:
- Given I am authorized user
- When I send `POST` request to `/users` endpoint
- And Request body contains user to add
- And all fields are filled in
- And Zip code is incorrect (unavailable)
- Then I get `424` response code
- And User is NOT added to application
### Scenario #4:
- Given I am authorized user
- When I send `POST` request to `/users` endpoint
- And Request body contains user to add with same name and sex as existing user
- Then I get `400` response code
- And User is NOT added to application
## Task 4 - Filter Users:
Write tests to cover requirements for user filtering
### Requirements:
### Scenario #1:
- Given I am authorized user
- When I send `GET` request to `/users` endpoint
- Then I get `200` response code
- And I get all users stored in the application for now
### Scenario #2:
- Given I am authorized user
- When I send `GET` request to `/users` endpoint
- And I add `olderThan` parameter
- Then I get `200` response code
- And I get all users older than parameter value
### Scenario #3:
- Given I am authorized user
- When I send `GET` request to `/users` endpoint
- And I add `youngerThan` parameter
- Then I get `200` response code
- And I get all users younger than parameter value
### Scenario #4:
- Given I am authorized user
- When I send `GET` request to `/users` endpoint
- And I add `sex` parameter
- Then I get `200` response code
- And I get all users with sex equal to parameter value
## Task 5 - Update Users:
Write tests to cover requirements for updating user functionality
### Requirements:
### Scenario #1:
- Given I am authorized user
- When I send `PUT/PATCH` request to `/users` endpoint
- And Request body contains user to update and new values
- Then I get `200` response code
- And User is updated
### Scenario #2:
- Given I am authorized user
- When I send `PUT/PATCH` request to `/users` endpoint
- And Request body contains user to update and new values
- And new zip code is incorrect (unavailable)
- Then I get `424` response code
- And User is NOT updated
### Scenario #3:
- Given I am authorized user
- When I send `PUT/PATCH` request to `/users` endpoint
- And Request body contains user to update and new values
- And Required fields are not filled in
- Then I get `409` response code
- And User is NOT updated

## Task 6 - Delete Users:
Write tests to cover requirements for deleting user functionality
### Requirements:
### Scenario #1:
- Given I am authorized user
- When I send `DELETE` request to `/users` endpoint
- And Request body contains user to delete
- Then I get `204` response code
- And User is deleted
- And its zip code is returned in list of available zip codes
### Scenario #2:
- Given I am authorized user
- When I send `DELETE` request to `/users` endpoint
- And Request body contains user to delete (required fields only)
- Then I get `204` response code
- And User is deleted
- And its zip code is returned in list of available zip codes
### Scenario #3:
- Given I am authorized user
- When I send `DELETE` request to `/users` endpoint
- And Request body contains user to delete (any required field not filled)
- Then I get `409` response code
- And User is deleted
## Task 7 - Upload Users:
Write tests to cover requirements for uploading user functionality
### Requirements:
### Scenario #1:
- Given I am authorized user
- When I send `POST` request to `/users/upload` endpoint
- And Request body contains `JSON file` with array of users to upload
- Then I get `201` response code
- And all users are replaced with users from file
- And Response contains number of uploaded users
### Scenario #2:
- Given I am authorized user
- When I send `POST` request to `/users/upload` endpoint
- And Request body contains `JSON file` with array of users to upload
- And at least 1 user has incorrect (unavailable) zip code
- Then I get `424` response code
- And Users are NOT uploaded
### Scenario #3:
- Given I am authorized user
- When I send `POST` request to `/users/upload` endpoint
- And Request body contains `JSON file` with array of users to upload
- And at least 1 user has a required field not filled
- Then I get `409` response code
- And Users are NOT uploaded
## Task 8 - Allure Reporting:
1. Add `Allure Framework` to the project
2. Add payload to tests in the report if required
3. Add `@Step` annotation for better readability of the report
4. Mark tests with bugs with corresponding Allure annotation
## Final Task - Rest Assured:
1. Pull one more docker image (containing improved web-service) and start the container
2. Execute all tests in project with `Apache HTTP Client`
3. Execute all tests in project with `Rest Assured Framework`
4. Make sure ALL tests are passed