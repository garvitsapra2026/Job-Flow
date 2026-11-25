import dao.AuthDAO;
import dao.JobDAO;
import dao.ApplicationDAO;
import dao.SkillDAO;
import model.User;
import model.Job;
import model.Application;

import java.util.ArrayList;
import java.util.Scanner;

public class App {
    private static final Scanner sc = new Scanner(System.in);
    private static final AuthDAO authDAO = new AuthDAO();
    private static final JobDAO jobDAO = new JobDAO();
    private static final ApplicationDAO applicationDAO = new ApplicationDAO();
    private static final SkillDAO skillDAO = new SkillDAO();
    private static User activeUser;

    public static void main(String[] args) {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("             WELCOME TO JOBFLOW");
        System.out.println("             JOB PORTAL SYSTEM");
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            System.out.println("\n============== MAIN MENU ==============");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: login(); break;
                case 2: register(); break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using JobFlow!");
                    break;
                default: System.out.println("Please enter a valid option.");
            }
        }
        sc.close();
    }

    private static void register() {
        System.out.println("\n============== CREATE ACCOUNT ==============");
        System.out.print("Enter full name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();
        System.out.print("Enter password (minimum 8 characters): ");
        String password = sc.nextLine();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required."); return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println("Please enter a valid email address."); return;
        }
        if (password.length() < 8) {
            System.out.println("Password must contain at least 8 characters."); return;
        }

        System.out.println("\nSelect account type:");
        System.out.println("1. JOB SEEKER");
        System.out.println("2. RECRUITER");
        int roleChoice = readInt("Enter choice: ");
        String role;
        if (roleChoice == 1) role = "JOB_SEEKER";
        else if (roleChoice == 2) role = "RECRUITER";
        else { System.out.println("Invalid account type."); return; }

        if (authDAO.register(name, email, password, role))
            System.out.println("Registration successful! You can now log in.");
        else System.out.println("Registration was not completed. Check whether this email already exists.");
    }

    private static void login() {
        System.out.println("\n============== LOGIN ==============");
        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        User user = authDAO.login(email, password);
        if (user == null) {
            System.out.println("Invalid email or password. Please try again.");
            return;
        }
        System.out.println("\nLogin successful! Welcome, " + user.getName() + ".");
        userMenu(user);
    }

    private static void userMenu(User user) {
        activeUser = user;
        boolean loggedIn = true;
        while (loggedIn) {
            showDashboard(user);
            System.out.println("\n============== JOB PORTAL MENU ==============");
            System.out.println("1. View / Search Jobs");
            if (!user.getRole().equals("JOB_SEEKER")) System.out.println("2. Post a Job");
            if (user.getRole().equals("JOB_SEEKER")) {
                System.out.println("3. Apply for a Job");
                System.out.println("4. My Applications");
                System.out.println("10. Set / Update My Skills");
                System.out.println("11. Jobs Matching My Skills");
            } else {
                System.out.println("5. Edit a Job");
                System.out.println("6. Delete a Job");
                System.out.println("7. View Applications");
                System.out.println("8. Update Application Status");
            }
            System.out.println("9. My Profile");
            System.out.println("0. Logout");
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: searchJobs(); break;
                case 2:
                    if (!user.getRole().equals("JOB_SEEKER")) postJob(user);
                    else System.out.println("Invalid option.");
                    break;
                case 3:
                    if (user.getRole().equals("JOB_SEEKER")) applyForJob(user);
                    else System.out.println("Invalid option.");
                    break;
                case 4:
                    if (user.getRole().equals("JOB_SEEKER")) showMyApplications(user);
                    else System.out.println("Invalid option.");
                    break;
                case 5:
                    if (!user.getRole().equals("JOB_SEEKER")) editJob(user);
                    else System.out.println("Invalid option.");
                    break;
                case 6:
                    if (!user.getRole().equals("JOB_SEEKER")) deleteJob(user);
                    else System.out.println("Invalid option.");
                    break;
                case 7:
                    if (!user.getRole().equals("JOB_SEEKER")) showRecruiterApplications(user);
                    else System.out.println("Invalid option.");
                    break;
                case 8:
                    if (!user.getRole().equals("JOB_SEEKER")) updateApplicationStatus();
                    else System.out.println("Invalid option.");
                    break;
                case 9: updateProfile(user); break;
                case 10:
                    if (user.getRole().equals("JOB_SEEKER")) setMySkills(user);
                    else System.out.println("Invalid option.");
                    break;
                case 11:
                    if (user.getRole().equals("JOB_SEEKER")) showMatchingJobs(user);
                    else System.out.println("Invalid option.");
                    break;
                case 0:
                    loggedIn = false;
                    activeUser = null;
                    System.out.println("You have been logged out.");
                    break;
                default: System.out.println("Please enter a valid option.");
            }
        }
    }

    private static void showDashboard(User user) {
        ArrayList<Job> jobs = jobDAO.getJobs("");
        int totalJobs = user.getRole().equals("RECRUITER")
                ? jobDAO.getJobCountForRecruiter(user.getUserId()) : jobs.size();
        ArrayList<Application> applications;
        if (user.getRole().equals("JOB_SEEKER"))
            applications = applicationDAO.getMyApplications(user.getUserId());
        else if (user.getRole().equals("RECRUITER"))
            applications = jobDAO.getApplicationsForRecruiter(user.getUserId());
        else applications = jobDAO.getAllApplications();

        int pending = 0;
        for (Application application : applications)
            if (application.getStatus().equalsIgnoreCase("PENDING")) pending++;

        System.out.println("\n==============================================");
        System.out.println("              JOBFLOW DASHBOARD");
        System.out.println("==============================================");
        System.out.println("Welcome       : " + user.getName());
        System.out.println("Account type  : " + user.getRole().replace('_', ' '));
        System.out.println("Total jobs    : " + totalJobs);
        System.out.println("Applications  : " + applications.size());
        System.out.println("Pending       : " + pending);
        System.out.println("==============================================");
    }

    private static void searchJobs() {
        System.out.print("Enter job title, company or location to search (leave blank for all): ");
        String query = sc.nextLine().trim();
        displayJobs(jobDAO.getJobs(query));
    }

    private static void displayJobs(ArrayList<Job> jobs) {
        System.out.println("\n============== AVAILABLE JOBS ==============");
        if (jobs.isEmpty()) { System.out.println("No jobs found."); return; }
        for (Job job : jobs) {
            System.out.println("\nJob ID      : " + job.getJobId());
            System.out.println("Job title   : " + job.getTitle());
            System.out.println("Company     : " + job.getCompany());
            System.out.println("Location    : " + job.getLocation());
            System.out.println("Salary      : " + valueOrNA(job.getSalary()));
            System.out.println("Description : " + valueOrNA(job.getDescription()));
            ArrayList<String> required = skillDAO.getJobSkills(job.getJobId());
            System.out.println("Required skills: " + (required.isEmpty() ? "Not specified" : String.join(", ", required)));
            if (activeUser != null && activeUser.getRole().equals("JOB_SEEKER") && !required.isEmpty()) {
                ArrayList<String> mine = skillDAO.getUserSkills(activeUser.getUserId());
                int matched = 0;
                for (String skill : required) if (mine.contains(skill)) matched++;
                int percent = mine.isEmpty() ? 0 : matched * 100 / required.size();
                System.out.println("Your skill match: " + percent + "% (" + matched + "/" + required.size() + " skills)");
            }
            System.out.println("--------------------------------------------");
        }
    }

    private static void postJob(User user) {
        System.out.println("\n============== POST A JOB ==============");
        System.out.print("Job title: "); String title = sc.nextLine().trim();
        System.out.print("Company name: "); String company = sc.nextLine().trim();
        System.out.print("Location: "); String location = sc.nextLine().trim();
        System.out.print("Salary: "); String salary = sc.nextLine().trim();
        System.out.print("Job description: "); String description = sc.nextLine().trim();
        System.out.println("Choose required skills for this job from the skill list:");
        showSkillCatalog();
        System.out.print("Enter skill names separated by commas (example: Java, SQL): ");
        ArrayList<String> requiredSkills = parseSkills(sc.nextLine());
        if (title.isEmpty() || company.isEmpty() || location.isEmpty()) {
            System.out.println("Job title, company and location are required."); return;
        }
        int newJobId = jobDAO.addJob(user.getUserId(), title, company, location, salary, description);
        if (newJobId > 0) {
            skillDAO.setJobSkills(newJobId, requiredSkills);
            System.out.println("Job posted successfully! Required skills saved.");
        }
    }

    private static void showSkillCatalog() {
        ArrayList<String> skills = skillDAO.getAllSkills();
        for (int i = 0; i < skills.size(); i++) System.out.println((i + 1) + ". " + skills.get(i));
    }

    private static ArrayList<String> parseSkills(String input) {
        ArrayList<String> selected = new ArrayList<>();
        ArrayList<String> available = skillDAO.getAllSkills();
        if (input == null || input.trim().isEmpty()) return selected;
        for (String item : input.split(",")) {
            String wanted = item.trim();
            for (String skill : available) {
                if (skill.equalsIgnoreCase(wanted) && !selected.contains(skill)) selected.add(skill);
            }
        }
        return selected;
    }

    private static void setMySkills(User user) {
        System.out.println("\n============== MY SKILLS ==============");
        System.out.println("Available skills:");
        showSkillCatalog();
        System.out.println("Current skills: " + String.join(", ", skillDAO.getUserSkills(user.getUserId())));
        System.out.print("Enter your skills separated by commas (example: Java, SQL, HTML): ");
        ArrayList<String> selected = parseSkills(sc.nextLine());
        if (selected.isEmpty()) { System.out.println("No valid skills selected. Use names from the list above."); return; }
        if (skillDAO.setUserSkills(user.getUserId(), selected))
            System.out.println("Your skills have been saved. Job matches will now use this list.");
    }

    private static void showMatchingJobs(User user) {
        ArrayList<String> mine = skillDAO.getUserSkills(user.getUserId());
        if (mine.isEmpty()) {
            System.out.println("First choose your skills using option 10. Then we can match jobs for you.");
            return;
        }
        ArrayList<Job> jobs = jobDAO.getJobs("");
        ArrayList<Job> matchedJobs = new ArrayList<>();
        for (Job job : jobs) {
            ArrayList<String> required = skillDAO.getJobSkills(job.getJobId());
            if (!required.isEmpty()) {
                int matched = 0;
                for (String skill : required) if (mine.contains(skill)) matched++;
                if (matched > 0) matchedJobs.add(job);
            }
        }
        System.out.println("\n============== JOBS MATCHED TO YOUR SKILLS ==============");
        if (matchedJobs.isEmpty()) { System.out.println("No matching jobs yet. Add more skills or check again later."); return; }
        displayJobs(matchedJobs);
        System.out.println("Tip: A higher skill match means more of the job's required skills match your profile.");
    }

    private static void applyForJob(User user) {
        displayJobs(jobDAO.getJobs(""));
        int jobId = readInt("Enter the Job ID you want to apply for: ");
        if (applicationDAO.apply(jobId, user.getUserId()))
            System.out.println("Your application has been submitted successfully!");
    }

    private static void showMyApplications(User user) {
        ArrayList<Application> applications = applicationDAO.getMyApplications(user.getUserId());
        System.out.println("\n============== MY APPLICATIONS ==============");
        if (applications.isEmpty()) { System.out.println("You have not applied for any jobs yet."); return; }
        for (Application a : applications) printApplication(a);
    }

    private static void showRecruiterApplications(User user) {
        ArrayList<Application> applications = user.getRole().equals("ADMIN")
                ? jobDAO.getAllApplications() : jobDAO.getApplicationsForRecruiter(user.getUserId());
        System.out.println("\n============== APPLICANT MANAGEMENT ==============");
        if (applications.isEmpty()) { System.out.println("No applications found."); return; }
        for (Application a : applications) printApplication(a);
    }

    private static void printApplication(Application a) {
        System.out.println("\nApplication ID : " + a.getApplicationId());
        System.out.println("Job             : " + a.getJobTitle());
        System.out.println("Applicant       : " + a.getApplicantName());
        System.out.println("Email           : " + a.getApplicantEmail());
        System.out.println("Applied on      : " + a.getApplicationDate());
        System.out.println("Status          : " + a.getStatus());
        System.out.println("-----------------------------------------------");
    }

    private static void editJob(User user) {
        displayJobs(jobDAO.getJobs(""));
        int id = readInt("Enter Job ID to edit: ");
        System.out.println("Enter the new job details:");
        System.out.print("Job title: "); String title = sc.nextLine().trim();
        System.out.print("Company: "); String company = sc.nextLine().trim();
        System.out.print("Location: "); String location = sc.nextLine().trim();
        System.out.print("Salary: "); String salary = sc.nextLine().trim();
        System.out.print("Description: "); String description = sc.nextLine().trim();
        if (title.isEmpty() || company.isEmpty() || location.isEmpty()) {
            System.out.println("Job title, company and location are required."); return;
        }
        boolean admin = user.getRole().equals("ADMIN");
        if (jobDAO.updateJob(id, user.getUserId(), admin, title, company, location, salary, description))
            System.out.println("Job updated successfully.");
        else System.out.println("Job not updated. Recruiters can edit only their own jobs.");
    }

    private static void deleteJob(User user) {
        displayJobs(jobDAO.getJobs(""));
        int id = readInt("Enter Job ID to delete: ");
        System.out.print("Are you sure you want to delete this job? (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) { System.out.println("Deletion cancelled."); return; }
        boolean admin = user.getRole().equals("ADMIN");
        if (jobDAO.deleteJob(id, user.getUserId(), admin))
            System.out.println("Job deleted successfully.");
        else System.out.println("Job not deleted. Recruiters can delete only their own jobs.");
    }

    private static void updateApplicationStatus() {
        int id = readInt("Enter Application ID: ");
        System.out.println("1. PENDING");
        System.out.println("2. ACCEPTED");
        System.out.println("3. REJECTED");
        int choice = readInt("Select new status: ");
        String status;
        if (choice == 1) status = "PENDING";
        else if (choice == 2) status = "ACCEPTED";
        else if (choice == 3) status = "REJECTED";
        else { System.out.println("Invalid status."); return; }
        User user = activeUser;
        boolean admin = user != null && user.getRole().equals("ADMIN");
        int recruiterId = user == null ? -1 : user.getUserId();
        if (jobDAO.updateApplicationStatus(id, status, recruiterId, admin))
            System.out.println("Application status updated successfully.");
        else System.out.println("Application was not found or it does not belong to your jobs.");
    }

    private static void updateProfile(User user) {
        System.out.println("\n============== MY PROFILE ==============");
        System.out.println("Current name : " + user.getName());
        System.out.println("Email        : " + user.getEmail());
        System.out.print("Enter new name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter new password (leave blank to keep current password): ");
        String password = sc.nextLine();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }
        if (!password.isEmpty() && password.length() < 8) {
            System.out.println("Password must contain at least 8 characters."); return;
        }
        if (authDAO.updateProfile(user.getUserId(), name, password)) {
            user.setName(name);
            System.out.println("Profile updated successfully.");
        }
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();
            try { return Integer.parseInt(input); }
            catch (NumberFormatException e) { System.out.println("Please enter a number."); }
        }
    }

    private static String valueOrNA(String value) {
        return value == null || value.trim().isEmpty() ? "Not provided" : value;
    }
}
