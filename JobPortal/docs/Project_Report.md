# JOB PORTAL MANAGEMENT SYSTEM — PROJECT REPORT

## 1. Introduction
The Job Portal Management System is a Java console application that connects job seekers and recruiters. Job seekers can search for vacancies and submit applications. Recruiters can post vacancies and review applications. An administrator can monitor job postings and applications.

## 2. Objectives
- Provide a simple platform for job discovery and recruitment.
- Reduce manual handling of job advertisements and applications.
- Store user, job and application information in a relational database.
- Demonstrate Java OOP, console input/output, JDBC and MySQL integration.

## 3. Technology Stack
Java 17 or newer, core Java, Scanner, JDBC, MySQL, MySQL Connector/J (manually added JAR), VS Code / any Java IDE. No Maven or Swing is used.

## 4. Modules
**Admin:** Login, view all jobs, delete job postings, view applications.

**Recruiter:** Register with their own email/login, post jobs with required skill tags, edit/delete only their own jobs, view applicants for their jobs and update only those applications. The first registered recruiter receives the four starter jobs.

**Job Seeker:** Register/login, maintain a personal skill profile, view job requirements and skill-match percentage, discover jobs matching their skills, apply for jobs and view application status.

## 5. Database Design
- `users`: user_id (PK), name, email (unique), password, role.
- `jobs`: job_id (PK), recruiter_id (FK → users.user_id), job_title, company_name, location, salary, description, created_at.
- `applications`: application_id (PK), job_id (FK → jobs.job_id), user_id (FK → users.user_id), application_date, status.
- `skills`: master catalogue of skills.
- `user_skills`: many-to-many mapping between job seekers and their skills.
- `job_skills`: many-to-many mapping between job postings and required skills.
- `application_status_history`: stores initial application status and later status changes using MySQL triggers.
- A unique constraint on `(job_id, user_id)` prevents the same job seeker from applying to the same job more than once.

## 6. ER Diagram
```text
USERS (1) --------< (M) JOBS
  |                     |
  |                     |
  | (1)             (1) |
  +--------< APPLICATIONS >--------+
                  (M)              |
                                   |
                              JOBS (1)
```
A recruiter is represented by a USERS row with role `RECRUITER`. A job seeker is represented by a USERS row with role `JOB_SEEKER`. APPLICATIONS links a job seeker to a job.

## 7. Data Flow Diagram
```text
[Job Seeker] ---- search/apply ----> [JOB PORTAL] ---- read/write ----> [MySQL]
[Recruiter]  ---- post/manage -----> [JOB PORTAL] ---- read/write ----> [MySQL]
[Admin]      ---- monitor/manage --> [JOB PORTAL] ---- read/write ----> [MySQL]
```

Level 1 process:
1. Registration/Login validates account details using USERS.
2. Recruiter posts or removes vacancies; the system changes JOBS.
3. Job seeker searches JOBS and submits an application; the system inserts APPLICATIONS.
4. Recruiter/Admin reviews applications and changes status.
5. Job seeker views status from APPLICATIONS.
6. Skill matching compares the job seeker's selected skills with the required skills of each job and calculates a match percentage.
7. MySQL triggers record application status changes in APPLICATION_STATUS_HISTORY.

## 8. Use Cases
- Register and log in.
- Search and view job details.
- Apply for a job.
- View application status.
- Post and delete a job (Recruiter).
- View applicants and update application status (Recruiter).
- View and manage jobs/applications (Admin).

## 9. Validation and Security
The application uses required-field checks, email format checks, prepared SQL statements, database uniqueness constraints and role-based dashboard options. Recruiter status updates are restricted to applications for jobs owned by that recruiter; Admin can update any application. Newly registered passwords are stored using PBKDF2-HMAC-SHA256 hashing. The seeded demo admin password is upgraded to a hash on first successful login. Database credentials are local classroom settings and should not be used for a production deployment.

## 10. Test Cases
| Test | Expected Result |
|---|---|
| Register with valid details | Account is created |
| Register with an existing email | Duplicate-email error |
| Login with valid credentials | Correct dashboard opens |
| Login with invalid credentials | Error message |
| Recruiter posts a job | Job appears in the list |
| Job seeker searches by title | Matching jobs appear |
| Job seeker saves personal skills | Skills are stored in USER_SKILLS |
| Job seeker opens skill matches | Jobs with overlapping required skills appear with match percentage |
| Recruiter posts a job with required skills | Skills are stored in JOB_SKILLS |
| Application status changes | Trigger inserts a history row |
| Job seeker applies twice to same job | Second application is rejected |
| Recruiter updates application status | New status is saved |
| Job seeker opens My Applications | Application and status are displayed |
| Admin deletes a job | Job is removed |

## 11. Limitations and Future Scope
This is a console-based classroom prototype. Future improvements may include resume upload, email notifications, advanced filters, pagination, interview scheduling, a web interface and stronger production security.

## 12. Conclusion
The project demonstrates a basic recruitment workflow using a Java console interface, JDBC and MySQL. It applies object-oriented programming, database relationships, CRUD operations and role-specific functionality.
