ko# JOB PORTAL — VIVA QUESTIONS AND ANSWERS

1. **What is your project?** JobFlow is a Java console-based recruitment system. Job seekers create skill profiles and find matching jobs, recruiters post jobs with required skills and manage applications, and admin monitors the system.
2. **Why did you choose Java?** Java supports object-oriented programming, portability and a large standard library.
3. **How is the user interface made?** It is a console-based interface. Scanner takes input and System.out.println displays menus and results.
4. **What is JDBC?** Java Database Connectivity, an API used by Java applications to communicate with databases.
5. **Does this project use Maven?** No. It uses core Java and the MySQL Connector/J JAR is added manually in the lib folder, like the RailSync setup.
6. **What is a DAO?** Data Access Object. It separates database operations from the user interface.
7. **What is PreparedStatement?** A parameterized SQL statement that safely passes values and helps prevent SQL injection.
8. **What is a primary key?** A column that uniquely identifies each row in a table.
9. **What is a foreign key?** A constraint that links a column to a key in another table.
10. **Why do you have multiple tables?** USERS stores accounts, JOBS stores vacancies, APPLICATIONS stores applications, and the skill tables connect users/jobs with their skills.
11. **How do you identify user roles?** The role column stores JOB_SEEKER, RECRUITER or ADMIN.
12. **How do you prevent duplicate applications?** A unique database constraint on job_id and user_id prevents duplicate applications.
13. **What is CRUD?** Create, Read, Update and Delete.
14. **What is an SQL JOIN?** It combines related rows from tables, for example applications with job and user details.
15. **What is ON DELETE CASCADE?** It automatically deletes child rows when the referenced parent row is deleted.
16. **What is exception handling?** Handling runtime errors using try/catch so the application can respond instead of terminating unexpectedly.
17. **What does DBConnection do?** It creates a JDBC connection using the MySQL URL, username and password.
18. **What does AuthDAO do?** It handles user registration and login.
19. **What does JobDAO do?** It handles job operations and recruiter/admin application queries.
20. **What does ApplicationDAO do?** It inserts job applications and retrieves a job seeker's application history.
21. **What is the role of MySQL?** It persistently stores users, jobs and applications.
22. **How does skill matching work?** The program compares the skills selected by a job seeker with the skills required by a job and calculates the percentage of required skills that match.

**What is application status history?** A MySQL trigger records the old and new status whenever an application status changes.

**What are the limitations?** It is a classroom console prototype and does not include resume uploads, email notifications or production-grade deployment.
23. **What future enhancements are possible?** Resume upload, email notifications, interview scheduling, advanced search and a Spring Boot web version.

24. **What is a Model class?** It represents the data of an entity, such as User, Job or Application.
25. **What is a DAO class?** It contains methods that perform database operations for a particular module.
26. **Why is the MySQL Connector JAR required?** It provides the JDBC driver that allows Java to communicate with MySQL.
27. **What is Scanner used for?** It reads user input from the console.


## Recruiter ownership
- A recruiter registers with their own email address. There is no shared demo recruiter login.
- The first registered recruiter receives ownership of the four starter jobs.
- Recruiters can view and update applications only for their own jobs. Admin can view and manage all jobs and applications.
