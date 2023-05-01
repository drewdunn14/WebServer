# COMP 3700 - Project 3 Documentation #
## Preface ##
In this document, I will be providing documentation about all things related to my Project 3.
This includes but will not be limited to...
- Database Design
- Microservice Architecture
- Microservice Use-Cases
- Webserver Design & Implementation
- Testing Microservices & Client CLI
- Conclusion & Takeaways from COMP-3700

Lastly, when describing the microservices portion of the project, I will be taking a sequential approach to\
describe the coordination between the microservices, the registry, and the test clients. This is an integral\
part of this project that I feel deserves extra attention.

## IDE/Compiler Details ##
This entire project was developed in within the IntelliJ IDEA Community, and was compiled using the tools included in the IDE.
- Project SDK: Amazon Corretto Version 16.0.2
- JDBC Jar Version: sqlite-jdbc-3.16.1
- Gson Jar Version: gson-2.10.1

## Running Project 3 ##
Project 3 has a few detailed instructions that need to be followed properly in order for the microservices to run.
It is worth noting that the Webserver runs completely separate from the registry/microservices/clients, but nevertheless
they do utilize the same adapter (SQLiteDataAdapter.java) to pull information from the SQLite Database.

In order for the microservices to be properly published and discoverable by their respective test clients, the ServiceRegistry
run configuration needs to be invoked before any microservice configurations are invoked. If this step is not followed correctly,
the microservices will not be published to the in-memory database provided by the ServiceRegistry, and without the proper
publishing of the microservices, the test clients will not have the information to connect to the microservices.
Only after the ServiceRegistry is invoked can the microservices be published, and then after the microservices are published
the test client(s) can be invoked.

### General Architecture ###
The WebServer portion of this project incorporates a two-tier application layer, this can be verified by the fact that
the application-logic is not abstracted away whatsoever like it usually would be in the application/business-layer, but rather
appears right alongside the Client-Side, conforming the functionality into one layer, which communicates with the second-layer,
the database.

The Microservices portion on the other hand, operates much differently. It is a 3-layer architecture, as it has the
presentation-layer in the CLI (command-line interface), the application/business-layer in the microservice, and the
database-layer structured as our SQLiteDatabase.

## Database Design ##
The backend of this entire project utilizes an SQLite database, and uses the Java SQL package to communicate with this database.
### SQL Tables ###
This project utilizes four unique tables apart from the default "sqlite_sequence" table.
#### User ####
- Coordinates with the 'UserAuthenticationService' & 'OrderCancellationService' to verify and provide user details.
#### Product ####
- Coordinates with 'ProductInfoService', 'ProductPriceUpdateService', and 'ProductQuantityUpdateService' to retrieve/edit data about available items for purchase, their price, and the quantity of them available.
#### Orders ####
- Coordinates with 'OrderInfoService' & 'OrderCancellationService' to retrieve data related to users purchasing products, the date purchased, their total cost, and the total tax as well.
#### OrderLines ####
- It is worth mentioning that while this table was actively used in the other two projects, project three requires no interaction with this table whatsoever.

## Microservice Architecture ##
This project differs much differently from the previous two in that for this project we utilize microservices to handle
the heavy lifting for our business/application logic, while the other two employed a monolithic services approach.

#### Monolithic vs. Microservices - What is the difference? ####
The main difference between monolithic/microservices is that with monolithic applications, the burden of handling the request(s)
of client(s) is solely the responsibility of one client handler, which must continuously decide what to do with requests,
and process them correctly, and generate a response to the client. 

While it may at first seem that letting all requests be handled by one server is a great idea, this is simply not the case.
For example, when trying to add new functionality for the client, you must be very careful not to step on your own feet,
or you may very well end up bringing down that one server that is handling requests, essentially bricking your entire application.
This makes both development and maintenance of a respository very time-consuming and tricky, which is one of the key
negative traits of a monolithic architecture.

A microservices architecture addresses these shortcomings, by breaking up specific requests to be handled by different
service-handlers. While there may be a higher up-front cost to establish a registry to which the microservices can
make themselves available to a client, the long-term advantages of microservices greatly outweigh that aforementioned
up-front cost. This is especially true as an application starts to get bigger & bigger, and more functionality is
constantly added.


#### ServiceInfoModel & ServiceMessageModel ####
These two classes are very important, as foster communication/organization that is vital to establishing
an efficient/reliable registry. For example, the microservices use the ServiceMessageModel to request to publish
themselves to the registry, while the ServiceInfoModel holds the necessary info that will allow client(s) to find
the correct service they need, and the correct location the clients need to connect to.

To be more specific, clients will use the ServiceMessageModel to request the services they need, and the ServiceInfoModel
will be returned to them from the registry's in-memory database to tell the clients what location (address/port) they need
to connect to.


#### Common Traits of Microservices within Project 3 ####
If you decide to look at this codebase, you will quickly find the two common functions in every microservice class.
- register
- serve

The register method is what attempts to publish the microservice to the ServiceRegistry, and gives details to the
registry about what address it can be found at, what port it can be found at, and most importantly what services it offers.
This information is stored in a very useful object called 'ServiceInfoModel', and is stored in the aforementioned in-memory database,
so that when a client makes a request to a registry, it may find the service it is looking for.

The serve method is what invokes the services that the microservices offers. Each microservice has its own specified
functionality that aligns with the description the ServiceRegistry has. Below is the specific use-case/functionality
that each microservice offers, as well as its location that clients will connect to. The default address of every single
microservice in this application is "localhost", but the port will/must differ for each.

1) ProductInfoService (Port 5050)
- This microservice simply provides product information/details to a client when given a request containing a productID.

2) OrderInfoService (Port 5051)
- This microservice simply provides order information/details to a client when given a request containing an orderID.

3) ProductPriceUpdateService (Port 5052)
- When the client provides an existing productID, it allows the client to edit the current price of the product with the given ProductID.

4) ProductQuantityUpdateService (Port 5053)
- When the client provides an existing productID, it allows the client to edit the current quantity of the product with the given ProductID.

5) UserAuthenticationService (Port 5054)
- Simply authenticates user existence when given the username and password from the client.

6) OrderCancellationService (Port 5055)
- When an authenticated user gives an orderID, this checks details of that Order, if authenticated user is the one that input the orderID,
it allows deletion/removal of that order from the database.

## Webserver Design & Implementation ##
As previously mentioned in the 'General Architecture' section, the WebServer operates much differently,
as it simply establishes an HTTPServer that both provides a client-view and contains our application-logic
at the same level. It operates through a series of URIs and links that offer the client the opportunity
to view details of the tables/items in our SQLiteDatabase. The API endpoints are as follows.

1) /users/<username> - contains information related to the specified username if it exists within the database.

2) /products/<product_id> - contains information related to the specified productID if it exists within the database.

3) /orders/<order_id> - contains information related to the specified orderID if it exists within the database.

All of these URIs are readily available to access via links at either /users/all, /products/all, /orders/all.

## Testing Microservices & CLI ##
Testing definitely took a big chunk of time. The WebServer is pretty self-explanatory as the testing project
was trial-by-error after the SQLiteAdapter was reliably relaying information from the database. It was more often
than not a process of ensuring that each link lead to the correct endpoint and that the correct information was 
displayed at that endpoint.

The Microservices were handled quite a bit differently than the WebServer from a testing standpoint.
While I did not use a testing framework, I developed client-side files that would effectively
test the registry by ensuring that it provided the correct location for the correct service,
and then these client testing files would send an array of requests that I could vary upon 
each execution via a command-line-interface. While not the prettiest UI in the world, this still provided
me with all the functionality I needed to establish that my registry was reliable in providing microservice
information to my client, and that my microservices were robust and could handle the necessary scope of information
based on the service they provide.

## Conclusion & Takeaways from COMP-3700 ##
This class has been an interesting experience, and offers a much different pace/mode of delivery than any other class I have taken at Auburn.
While other classes spoon-feed the information and tightly specify what is expected for assignments
and projects, this class offers a much more realistic endeavor into what software development is like in the real-world.
This class is unique because its assignments/projects are more open-ended than possibly any other class
I have taken at Auburn. Because of this I have become much more familiar with many technologies, such as but not limited to:
1) Socket-Programming
2) Client/Server Architecture
2) Design Approaches
3) GUI Programming/Design
4) SQLite Databases

I would highly recommend any student that is up for a personal challenge to take this course, as it will force you to think/learn/work
in a different manner than you have for much of your college career, and will allow you to be more creative than many of your other courses.







