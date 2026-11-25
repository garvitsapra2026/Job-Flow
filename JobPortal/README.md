# JobFlow — Console-Based Job Portal Management System

This version converts the original JobPortal Swing/Maven project into a **core Java + JDBC + MySQL console application**, following the same general setup used by the supplied RailSync project.

## Technology used
- Core Java (JDK 17 or newer)
- JDBC
- MySQL
- MySQL Connector/J (JAR added manually in `lib/`)
- `Scanner` and console input/output
- Separate `model`, `dao` and `db` packages
- No Maven and no Java Swing

## Project structure

```text
JobPortal/
├── database/
│   └── jobportal.sql
├── lib/
│   └── mysql-connector-j-26.10.0.jar
├── src/
│   ├── App.java
│   ├── dao/
│   │   ├── ApplicationDAO.java
│   │   ├── AuthDAO.java
│   │   ├── JobDAO.java
│   │   └── PasswordUtil.java
│   ├── db/
│   │   └── DBConnection.java
│   └── model/
│       ├── Application.java
│       ├── Job.java
│       └── User.java
└── README.md
```

## 1. Set up the database
1. Start MySQL Server and open MySQL Workbench.
2. Open `database/jobportal.sql`.
3. Execute the complete script.
4. Confirm that the `jobportal` database contains `users`, `jobs`, `applications`, `skills`, `user_skills`, `job_skills` and `application_status_history` tables. The SQL file is separate from Java code, just like the SQL file you run in Workbench for RailSync.

The SQL script creates the Admin account and four sample jobs. There is no hard-coded demo recruiter account.

Demo account:
- Admin: `admin@jobportal.com` / `admin123`

Register your own account and choose `RECRUITER`. The first registered recruiter automatically becomes the owner of the four starter jobs. Their own name and email are used; no special recruiter email or password is required. If a recruiter already exists when the SQL file is run, the starter jobs are assigned to that recruiter. The SQL script can be run again without duplicating the four sample jobs.

## 2. Configure MySQL connection
Open `src/db/DBConnection.java` and set these values to match your local MySQL installation:

```java
private static final String URL = "jdbc:mysql://localhost:3306/jobportal";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

The supplied RailSync project uses `root` / `root`; if your MySQL password is different, change `PASSWORD` here.

## 3. Compile and run on Windows (PowerShell)
Open the terminal in the `JobPortal` project folder (the folder containing `src`, `lib` and `database`). Run:

```powershell
mkdir bin
javac -cp "lib/*" -d bin src\App.java src\model\*.java src\dao\*.java src\db\*.java
java -cp "bin;lib/*" App
```

If the `bin` folder already exists, PowerShell may say it already exists; continue with the `javac` command.

## 4. Features preserved

### All account types
- Register as a Job Seeker or Recruiter.
- Login and logout.
- View dashboard counts (jobs, applications and pending applications).
- Update profile name and optionally change password.
- Search jobs by title, company or location.

### Job Seeker
- View all jobs and job details, including each job's required skills and your match percentage.
- Add/update your personal skills from the supplied skill catalogue.
- See a dedicated list of jobs matching your skills.
- Apply for a job.
- Prevent duplicate applications to the same job.
- View personal application history and application status.

### Recruiter
- Post jobs and tag the skills required for each role.
- View applicants for jobs posted by that recruiter.
- Edit and delete only their own job postings.
- Update application status to `PENDING`, `ACCEPTED` or `REJECTED`.
- Application status changes are automatically recorded in `application_status_history` by MySQL triggers.

### Admin
- View all jobs and all applications.
- Post jobs and tag the skills required for each role.
- Edit and delete any job posting.
- Update application status.

## 5. New skill-matching workflow
1. Run `database/jobportal.sql` in MySQL Workbench. It creates four sample jobs automatically.
2. Register or log in as a Job Seeker.
3. Choose menu option `10. Set / Update My Skills` and enter skills from the catalogue (for example: `Java, SQL, JDBC, OOP, HTML, CSS`).
4. Choose `11. Jobs Matching My Skills` to see matching sample jobs immediately. Each result shows required skills and your match percentage.
5. Apply using option `3`, then track the result using option `4`.

To manage the starter jobs, register or log in with your own Recruiter account. Recruiters see only their own job count and applications. The first registered recruiter receives the four starter jobs; additional recruiters can post and manage their own jobs.

The match percentage is a simple transparent calculation: (number of your skills that match the job requirements / total required skills) × 100. It is a rule-based Java feature, not an external AI service.

## Notes
- The original `users`, `jobs` and `applications` tables are retained. Additional skill and application-history tables power the new matching and tracking features.
- New account passwords are stored using PBKDF2-HMAC-SHA256 hashing. The seeded admin password is accepted on first login and upgraded to a hash.
- This is a local classroom project. Do not use real passwords. For production, use secure configuration for database credentials and add further validation and authorization checks.
