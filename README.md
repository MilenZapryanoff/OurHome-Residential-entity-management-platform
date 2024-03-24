<h1> OurHome - Residential entities management platform</h1>

<h3>Web application build with Java and Spring Boot</h3>

<img src="https://i.ibb.co/DWyzPPN/01.jpg">


<h2> About the project </h2>
<ul>
<li>The idea behind this project is for residential entities management platform. 
A platform that allows the registration of both home owners and residential entity managers. The platform provides an opportunity to receive messages (notifications) for various events, different user roles, validation of data and user registration verification by a moderator/manager, data on amounts due, monthly taxes, statements, news and etc.</li>
</ul>

<h2>Some of the features </h2>
<ul>
  <li>User can register as Resident or Manager</li>
  <li>As a first step the manager have to register in the platform and register a Residential entity</li>
  <li>When the entity is registered an 8-digit residential entity code (ID) is generated. Together, with an access code
  created by the manager, an user (resident) can register in the platform. The resident cannot register withour at least one active residential entity</li>
  <li>After the registration, the resident can register his apartment (property) in the existing residential entity. Every request for registration has to be approved by the manager</li>
  <li>The manager has the option to reject registration or accept it. If the request is rejected, the resident can edit it and resend it agan.</li>
  <li>If the manager approve the request, the resident is now free to see available data for his property. The manager can perform changes to properties and taxes whenever he needs to.</li>
  <li>There are integrated some automatic messages (notifications) for some events, and on next stage there will be also an option for messages from person to person.</li>
  <li>There are no limits of apartments (properties) that one user can have, even in different residential entities. 
  There is also a possibility, one manager to manage different residential entities.</li>
  <li>Manager can change residential entity data whenever he needs. Also, can delete an residential entity if there are no registered residents in it.</li>
  <li>Messages (notifications) can be archived or deleted.</li>
  <li>Only Residential entity manager can edit a part of property data as number of adults living in apartment, number of children, number of pets, property number and property floor. Also the amount of monthly taxes</li>
  <li>Only owner can view and edin personal property data.</li>
  <li>Manager and Moderator receive emails for every new property registration request</li>
  <li>Moderator can see all registered users, and have limited access to available modifications and actions</li>
  <li>User-to-user messages</li>
  <li>Sending email via contact form</li>
  <li>Reset forgotten password functionality</li>
  <li>On next stage admin will be able to update and delete all managers in the platform via user interface and a news feed will be created.</li>
</ul>
<h2>Security</h2>
<ul>
  <li>Due to the nature of the platform, security settings require additional than standard security checks. The variety of options for property registrations in different       Residential entities requires multiple additional checks to ensure that a user or moderator cannot manipulate someone else's data.</li>
  <li> Not logged in users have access to login page, register page and contact page.</li>
  <li> Users with Moderator role can access Messages and Administration sections</li>
  <li> USers with Resident role can access Messages and Property section</li>
  <li> A moderator is able to manage his own residential entities. All Get and PostRequests are prohibited in case of another user with moderator rights tries to execute a request.</li>
  <li>User with role Resident is able to manage his own properties. All Get and PostRequests are prohibited in case of another resident tries to execute a request for another user property.</li>
  <li>User with role Resident is able to manage his own messages (notifications). All Get and PostRequests are prohibited in case of another resident tries to execute a request for another user messages (notifications).</li>
</ul>

<h2> How to start the app</h2>
  <li>First clone the repository to your local machine</li>
  <li>Configure your MySQL database by updating the application.yml file...</li>
  <li>...or configure the following environment variables in IntelliJ Idea  
  <li>  1) db_password=<your db password> [db username]  </li>
  <li>  2) db_username=<your db username> [db password] </li>
  <li>  3) admin_pass=[default admin user in the DB] </li>
  <li> (Optional) if using contact email feature : </li>
  <li>  4) mail_username[email username] </li>
  <li>  5) mail_password[email password] </li>
  <li>Run the project in IntelliJ Idea</li>
  <li>Access the web application by visiting http://localhost:8080 in your web browser.</li>
  <li>Create a manager account, then create a Residential Entity. Now you can create a resident and new property.</li>
  <li>Follow the steps and Enjoy</li>

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

<h4>v.1.24.0306</h4>
  <ul>
  <li> Bugs and problems fixed</li>
  <li> Some pages design changed and added new layouts.</li>
  </ul>

<h2>Updates</h2>
 <h4>v.1.24.0324</h4>
  <ul>
  <li>Added property categories.</li>
  </ul>


  <h4>v.1.24.0303</h4>
  <ul>
  <li>Show/hide password input toggle switch added in some password fields.</li>
  <li>Added information for property owners how their monthly fee is calculated. If a fee is component of the monthly fee an indicator is added for this. This will help property owner to better understand his fee.</li>
  <li>Added new component of monthly fee - individual property fee. Now manager can set such a fee individually for every single property. If set, the amount is added to       the pediodical monthly fee. All together form the total monthly fee.</li>
  <li>Some field types have been changed.</li>
  <li>Improved integration tests.</li>
  </ul>

  <h4>v.1.24.0302</h4>
  <ul>
  <li> New sections added for active/pending and rejected property registrations.</li>
  <li> General code and logic improvements.</li>
  </ul>

  <h4>v.1.24.0301</h4>
  <ul>
  <li> ON/OFF Switch for automatic monthly fee creation added for every property. </li>
  <li> Overpayment amount is now accessible for modification for every property. </li>
  <li> Additional information for overpaid amounts added to property monthly fees. This allows easier tracking and understanding of applied fees history.</li>
  <li> Pop-up confirmation prompts added when performing DELETE operations. This will prevent accidential deletions of records.</li>
  <li> Pop-up Alerts added for some user actions.</li>
  <li> Some additional fields validations have been added.</li>
  <li> Some link redirection improvements were done.</li>
  <li> Style and code improvements were done.</li>
  </ul>
  <h4>v.1.24.0227</h5>
  <ul>
  <li> Additional menus added. </li>
  <li> Global fee functionality added - now manager can manually create a monthly fee for all the entities in a residential entity. </li>
  <li>No need to do that one by one anymore. If fee is added manually there will be additional indicator in the list of property fees</li>
  </ul>  
  <h4>v.1.24.0225</h4>
  <ul>
  <li> Some functionality improvemets and code rafactoring have been done with the newest version of the app.</li>
  <li> All registered users can now upload their own pictures/avatars (max 3MB) and also edit personal data and password.</li>
  <li> Monthly fees section added. </li>
  </ul>
  <h4>Older versions</h4> 
  <ul>
  <li> Every new property monthly fee is calculated and set automatically with registration.</li>
  <li> Property monthly fee is automatically re-calculated when a change of the living in the property people/pets occures.</li>
  <li> When a manager applies a change to monthly fees, all properties monthly fees are automatically re-calculated.</li>
  <li> Scheduled monthly fees apply for every Residential entity property.</li>
  <li> Residential entity Expenses section added. </li>
  <li> Now Residential entity manager can add expenses and upload documents (invoices and etc. in picture formats)</li>
  <li> Residential entity manager can now get data about residential entity expenses for a specific period (added filter).</li>
  </ul>

<h2> More pictures: </h2>
<img src="https://i.ibb.co/JyGLFYq/index.jpg">
<img src="https://i.ibb.co/4K9fTK6/login.jpg">
<img src="https://i.ibb.co/ZgJp81c/messages.jpg">
<img src="https://i.ibb.co/vVtqbYn/profile.jpg">
<img src="https://i.ibb.co/qDQgVQq/properties.jpg">
<img src="https://i.ibb.co/MhQhWQ9/property-fees.jpg">
<img src="https://i.ibb.co/phf02yw/administration-fees.jpg">
<img src="https://i.ibb.co/JKfHM2L/administration-fees-expenses.jpg">
<img src="https://i.ibb.co/54m3c3T/administration-fees-property.jpg">
<img src="https://i.ibb.co/v4dxSMS/administration-fees-RE.jpg">
<img src="https://i.ibb.co/c3gvDyx/administration-owners.jpg">
<img src="https://i.ibb.co/pKg3fwz/administration-properties.jpg">
<img src="https://i.ibb.co/PNPKCj5/administration-summary.jpg">
Database architecture :
<img src="https://i.ibb.co/rpC6mF3/Our-Home-database-architecture.jpg">


