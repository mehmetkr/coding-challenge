A. Installation

    1- Download the project from github.

    2- run "mvn install" in the coding_challenge_code and the tax_service folders.

B. Configuration

    1- Have a relational database connection URL that provides access to a database that supports two concurrent connections.

    2- Put the relational database connection URL and the relevant database connection configurations in the spring.datasource parameters in the application.properties file of both modules.

    3- Set up a MongoDB server for NoSQL storage of the logs of the tax service and puts its connection url in the spring.data.mongodb.uri parameter in the application.properties file of the tax_service module.

    4- Manually set a server port for the tax_service module in its application.properties file to prevent port conflicts. For example: 

        server.port=8090

    5- Manually put the tax calculation API of the tax_service module in the application.properties file of the coding_challenge_code in tax.service.server.url. For example:

        tax.service.server.url=http://localhost:8090/api/tax/calculate

        This will let the coding_challenge_code module make HTTP requests to the tax calculation API.

C. Running the APIs

    1- Run "mvn spring-boot:run" from the command line of the path of each API to start the application

D. Using the APIs

    1- Create User: Send a POST request to the URL {server_url}:8080/api/user/create with the following sample body to create a user:

        {
            "username": "testuser2",
            "password": "testp1234",
            "confirmPassword": "testp1234"
        }

    2- Login: Send a POST request to the URL {server_url}:8080/login with the following sample body to have the user log in:

        {
            "username": "testuser2",
            "password": "testp1234"
        }

        The header of the response to this request will contain an "Authorization" field with the value of a JWT. The user should use this JWT
        in the "Authorization" field of the headers of subsequent requests for authorization

    3- Create product: Send a POST request to the URL {server_url}:8080/login with the following sample body for the user to create a new product:

        {
            "name": "product 54",
            "price": "46.77",
            "description": "First persons second product 54"
        }

        The API will determine the username from the JWT put in the header of the request.

    4- Get all the products: Send a GET request to the URL {server_url}:8080/api/product. Note that the user is able to view all the products created by all the users.

    5- Update a product: Updating uses the same URL as creating a product but the user has to provide a product ID as well. For example:

        {
            "id": 1,
            "name": "product 54",
            "price": "46.77",
            "description": "First persons second product 54"
        }

        Note that the API will return a 401 Unauthorized error if the user attempts to update a product created by another user.

    6- Calculate tax: Send a Send a POST request to the URL {server_url}:8080/api/product/calculateTax with the following sample body to calculate the tax of a product:

        {
            "id": "2",
            "taxtype": "VAT"
        }

        The API will use this body to make an HTTP request to the tax_service module.

        No authentication is performed by the tax_service as it is assumed to be deployed in a private server accessible only by the coding_challenge_code. In other words, all authentication will be done by the coding_challenge_code module. 

