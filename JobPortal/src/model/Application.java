package model;

public class Application {
    private int applicationId;
    private String jobTitle;
    private String applicantName;
    private String applicantEmail;
    private String applicationDate;
    private String status;

    public Application(int applicationId, String jobTitle, String applicantName,
                       String applicantEmail, String applicationDate, String status) {
        this.applicationId = applicationId;
        this.jobTitle = jobTitle;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    public int getApplicationId() { return applicationId; }
    public String getJobTitle() { return jobTitle; }
    public String getApplicantName() { return applicantName; }
    public String getApplicantEmail() { return applicantEmail; }
    public String getApplicationDate() { return applicationDate; }
    public String getStatus() { return status; }
}
