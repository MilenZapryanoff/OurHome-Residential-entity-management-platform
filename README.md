<h1> OurHome - Residential entities management platform</h1>

<h3>About the procect -  
  It is my first web application build with Java and Spring Boot</h3>

<img src="https://i.ibb.co/nrPpPyy/04.png">


<h2> About the project </h2>
- The idea behind this project is for residential entities management platform. 
The platform allows the registration of both home owners and residential entity managers. The platform provides an opportunity to receive notifications for various events, different user roles, validation of data, and verification by a moderator/manager after user registration, data on amounts due and etc.


<h2> Some of the functionalities </h2>
<ul>
  <li>User can register as Resident or Manager</li>
  <li>As a first step the manager have to register in the platform and register a Residential entity</li>
  <li>When the entity is registered an 8-digit residential entity code (ID) is generated. Together, with an access code
  created by the manager, an user (resident) can register in this residential entity.</li>
  <li>After the registration, the resident can register his apartment (property) in the existing residential entity. Every request for registration has to be approved by the manager</li>
  <li>The manager has the option to reject registration or accept it. If the request is rejected, the resident can edit it and resend it agan.</li>
  <li>If the manager approve the request, the resident is now free to see available data for his property. The manager can perform changes to properties and taxes whenever he needs to.</li>
  <li>There are integrated some automatic notifications (messages) for some events, and there will be also an option for messages from person to person.</li>
  <li>There are no limits of apartments (properties) that one user can have, even in different residential entities. 
  There is also a possibility, one manager to manage different residential entities.</li>
  <li>Manager can change residential entity data whenever he needs. Also, can delete an residential entity if there are no registered residents in it.</li>
  <li>Messages (notifications) can be archived or deleted.</li>
  <li>Manager or Moderator can add, edit and delete residential entities and residents</li>
  <li>Only Manager can edin number of people living in apartment and monthly taxes</li>
  <li>Manager and Moderator receive emails for every new property registration request</li>
  <li>Moderator can see all registered users, can change their roles and delete them</li>
  <li>On next stage admin will be able to update and delete all managers in the platform.</li>
</ul>

<h2> How to start the app</h2>
<ol>
  <li>First clone the repository to your local machine</li>
  <li>Configure your MySQL database by updating the application.yml file.</li>
  <li>Configure 2 environment variables for database as - db_password and db_username and 1 environment variable - admin_pass used for the default admin user in the DB</li>
  <li>Run the project in IntelliJ Idea</li>
  <li>Access the web application by visiting http://localhost:8080 in your web browser.</li>
  <li>Create a manager account, then create a Residential Entity. Now you can create a resident and new property.</li>
  <li>Follow the steps and Enjoy</li>
</ol>

<h2> Used technologies</h2>
<ul>
  <li>Java 18</li>
  <li>Spring Boot 3.1.3</li>
  <li>Spring Security</li>
  <li>Spring Data JPA</li>
  <li>Bootstrap</li>
  <li>HTML, CSS, JavaScript</li>
  <li>MySQL</li>
</ul>

<h2> License </h2>
<ul>
  <li>MIT License</li>
</ul>

<h2> More pictures: </h2>
<img src="https://i.ibb.co/nrPpPyy/04.png">
<img src="https://i.ibb.co/FmHrZxp/01.png">
<img src="https://i.ibb.co/x50JjCJ/03.png">
<img src="https://i.ibb.co/nLYtV89/05.png">
<img src="https://i.ibb.co/C0SHwRC/06.png">
<img src="https://i.ibb.co/hV9frBD/07.png">
<img src="https://i.ibb.co/kGqZkLL/08.png">
<img src="https://i.ibb.co/tX7KP79/09.png">



