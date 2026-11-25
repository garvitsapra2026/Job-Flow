package model;

public class Job {
    private int jobId;
    private int recruiterId;
    private String title;
    private String company;
    private String location;
    private String salary;
    private String description;

    public Job(int jobId, int recruiterId, String title, String company,
               String location, String salary, String description) {
        this.jobId = jobId;
        this.recruiterId = recruiterId;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.description = description;
    }

    public int getJobId() { return jobId; }
    public int getRecruiterId() { return recruiterId; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getSalary() { return salary; }
    public String getDescription() { return description; }
}
