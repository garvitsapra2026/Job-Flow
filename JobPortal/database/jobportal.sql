-- JOBFLOW DATABASE SETUP
-- Run this file in MySQL Workbench before starting the Java application.
-- No fixed recruiter email is required. The first registered recruiter receives the starter jobs.

CREATE DATABASE IF NOT EXISTS jobportal;
USE jobportal;

CREATE TABLE IF NOT EXISTS users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role ENUM('JOB_SEEKER','RECRUITER','ADMIN') NOT NULL
);

CREATE TABLE IF NOT EXISTS jobs (
  job_id INT AUTO_INCREMENT PRIMARY KEY,
  recruiter_id INT NOT NULL,
  job_title VARCHAR(120) NOT NULL,
  company_name VARCHAR(120) NOT NULL,
  location VARCHAR(120) NOT NULL,
  salary VARCHAR(60),
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_job_recruiter FOREIGN KEY (recruiter_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS applications (
  application_id INT AUTO_INCREMENT PRIMARY KEY,
  job_id INT NOT NULL,
  user_id INT NOT NULL,
  application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status ENUM('PENDING','ACCEPTED','REJECTED') DEFAULT 'PENDING',
  UNIQUE KEY unique_application (job_id, user_id),
  CONSTRAINT fk_application_job FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE,
  CONSTRAINT fk_application_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS skills (
  skill_id INT AUTO_INCREMENT PRIMARY KEY,
  skill_name VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_skills (
  user_id INT NOT NULL,
  skill_id INT NOT NULL,
  PRIMARY KEY (user_id, skill_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS job_skills (
  job_id INT NOT NULL,
  skill_id INT NOT NULL,
  PRIMARY KEY (job_id, skill_id),
  FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE,
  FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS application_status_history (
  history_id INT AUTO_INCREMENT PRIMARY KEY,
  application_id INT NOT NULL,
  old_status VARCHAR(20),
  new_status VARCHAR(20) NOT NULL,
  changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (application_id) REFERENCES applications(application_id) ON DELETE CASCADE
);

INSERT IGNORE INTO skills(skill_name) VALUES
('Java'),('Python'),('C'),('C++'),('JavaScript'),('HTML'),('CSS'),('React'),
('SQL'),('MySQL'),('JDBC'),('Spring Boot'),('Data Structures'),('Algorithms'),
('OOP'),('Git'),('GitHub'),('Problem Solving'),('Communication'),('Teamwork'),
('Node.js'),('Express.js'),('MongoDB'),('PHP'),('Bootstrap'),('Linux'),
('Excel'),('Power BI'),('Figma'),('UI/UX Design'),('Networking');

-- Admin is the temporary owner of sample jobs until a user registers as a recruiter.
-- The admin password is upgraded to a PBKDF2 hash on first successful login.
INSERT INTO users(name,email,password,role)
VALUES ('Administrator','admin@jobportal.com','admin123','ADMIN')
ON DUPLICATE KEY UPDATE email=email;

SET @admin_id = (SELECT user_id FROM users WHERE email='admin@jobportal.com' LIMIT 1);
SET @recruiter_owner_id = COALESCE(
  (SELECT user_id FROM users WHERE role='RECRUITER' AND email <> 'recruiter@jobflow.com' ORDER BY user_id LIMIT 1),
  @admin_id
);

-- Create sample jobs only if they do not already exist.
INSERT INTO jobs(recruiter_id,job_title,company_name,location,salary,description)
SELECT @recruiter_owner_id,'Java Developer','ABC Technologies','Chandigarh','500000 per year','Work on Java applications, JDBC connectivity and relational databases.'
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE job_title='Java Developer' AND company_name='ABC Technologies');

INSERT INTO jobs(recruiter_id,job_title,company_name,location,salary,description)
SELECT @recruiter_owner_id,'Frontend Developer','PixelWorks','Mohali','400000 per year','Build responsive user interfaces using HTML, CSS and React.'
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE job_title='Frontend Developer' AND company_name='PixelWorks');

INSERT INTO jobs(recruiter_id,job_title,company_name,location,salary,description)
SELECT @recruiter_owner_id,'Backend Developer','CodeBridge','Panchkula','550000 per year','Develop backend services using Java, JDBC and MySQL.'
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE job_title='Backend Developer' AND company_name='CodeBridge');

INSERT INTO jobs(recruiter_id,job_title,company_name,location,salary,description)
SELECT @recruiter_owner_id,'Full Stack Trainee','NextGen Systems','Chandigarh','350000 per year','Work across frontend and backend modules with HTML, CSS, Java and SQL.'
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE job_title='Full Stack Trainee' AND company_name='NextGen Systems');

-- On an existing installation, move the four starter jobs to the first real recruiter if one exists.
-- If no recruiter has registered yet, keep them with Admin; AuthDAO transfers them to the first recruiter later.
UPDATE jobs
SET recruiter_id = @recruiter_owner_id
WHERE (job_title='Java Developer' AND company_name='ABC Technologies')
   OR (job_title='Frontend Developer' AND company_name='PixelWorks')
   OR (job_title='Backend Developer' AND company_name='CodeBridge')
   OR (job_title='Full Stack Trainee' AND company_name='NextGen Systems');

-- Remove the old hard-coded demo recruiter after its sample jobs have been reassigned.
DELETE FROM users WHERE email='recruiter@jobflow.com' AND role='RECRUITER';

-- Attach required skills to sample jobs. Safe to run more than once.
INSERT IGNORE INTO job_skills(job_id,skill_id)
SELECT j.job_id,s.skill_id FROM jobs j JOIN skills s
WHERE (j.job_title='Java Developer' AND j.company_name='ABC Technologies' AND s.skill_name IN ('Java','SQL','JDBC','OOP'))
   OR (j.job_title='Frontend Developer' AND j.company_name='PixelWorks' AND s.skill_name IN ('HTML','CSS','React'))
   OR (j.job_title='Backend Developer' AND j.company_name='CodeBridge' AND s.skill_name IN ('Java','JDBC','MySQL','SQL'))
   OR (j.job_title='Full Stack Trainee' AND j.company_name='NextGen Systems' AND s.skill_name IN ('Java','HTML','CSS','SQL'));

DROP TRIGGER IF EXISTS trg_application_status_history_insert;
DROP TRIGGER IF EXISTS trg_application_status_history_update;
DELIMITER $$
CREATE TRIGGER trg_application_status_history_insert
AFTER INSERT ON applications
FOR EACH ROW
BEGIN
  INSERT INTO application_status_history(application_id, old_status, new_status)
  VALUES (NEW.application_id, NULL, NEW.status);
END$$

CREATE TRIGGER trg_application_status_history_update
AFTER UPDATE ON applications
FOR EACH ROW
BEGIN
  IF OLD.status <> NEW.status THEN
    INSERT INTO application_status_history(application_id, old_status, new_status)
    VALUES (NEW.application_id, OLD.status, NEW.status);
  END IF;
END$$
DELIMITER ;

-- Verification
SELECT user_id,name,email,role FROM users ORDER BY user_id;
SELECT job_id,recruiter_id,job_title,company_name FROM jobs ORDER BY job_id;
SELECT application_id,job_id,user_id,status FROM applications ORDER BY application_id;
