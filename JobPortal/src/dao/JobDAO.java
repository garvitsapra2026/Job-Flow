package dao;

import db.DBConnection;
import model.Job;
import model.Application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class JobDAO {
    public ArrayList<Job> getJobs(String search) {
        ArrayList<Job> jobs = new ArrayList<>();
        String sql = "SELECT job_id,recruiter_id,job_title,company_name,location,salary,description "
                + "FROM jobs WHERE job_title LIKE ? OR company_name LIKE ? OR location LIKE ? ORDER BY created_at DESC";
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return jobs;
            PreparedStatement ps = con.prepareStatement(sql);
            String q = "%" + (search == null ? "" : search) + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) jobs.add(new Job(rs.getInt("job_id"), rs.getInt("recruiter_id"),
                    rs.getString("job_title"), rs.getString("company_name"), rs.getString("location"),
                    rs.getString("salary"), rs.getString("description")));
            rs.close(); ps.close(); con.close();
        } catch (Exception e) { System.out.println("Could not load jobs: " + e.getMessage()); }
        return jobs;
    }


    public int getJobCountForRecruiter(int recruiterId) {
        String sql = "SELECT COUNT(*) FROM jobs WHERE recruiter_id=?";
        try {
            Connection con = DBConnection.getConnection(); if (con == null) return 0;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, recruiterId);
            ResultSet rs = ps.executeQuery();
            int count = rs.next() ? rs.getInt(1) : 0;
            rs.close(); ps.close(); con.close(); return count;
        } catch (Exception e) { System.out.println("Could not count recruiter jobs: " + e.getMessage()); return 0; }
    }

    public int addJob(int recruiterId, String title, String company, String location,
                          String salary, String description) {
        String sql = "INSERT INTO jobs(recruiter_id,job_title,company_name,location,salary,description) VALUES(?,?,?,?,?,?)";
        try {
            Connection con = DBConnection.getConnection(); if (con == null) return -1;
            PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, recruiterId); ps.setString(2, title); ps.setString(3, company);
            ps.setString(4, location); ps.setString(5, salary); ps.setString(6, description);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys(); int id = keys.next() ? keys.getInt(1) : -1;
            keys.close(); ps.close(); con.close(); return id;
        } catch (Exception e) { System.out.println("Could not post job: " + e.getMessage()); return -1; }
    }

    public boolean updateJob(int jobId, int recruiterId, boolean admin, String title,
                             String company, String location, String salary, String description) {
        String sql = admin
                ? "UPDATE jobs SET job_title=?,company_name=?,location=?,salary=?,description=? WHERE job_id=?"
                : "UPDATE jobs SET job_title=?,company_name=?,location=?,salary=?,description=? WHERE job_id=? AND recruiter_id=?";
        try {
            Connection con = DBConnection.getConnection(); if (con == null) return false;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,title); ps.setString(2,company); ps.setString(3,location);
            ps.setString(4,salary); ps.setString(5,description); ps.setInt(6,jobId);
            if (!admin) ps.setInt(7,recruiterId);
            int rows = ps.executeUpdate(); ps.close(); con.close(); return rows > 0;
        } catch (Exception e) { System.out.println("Could not update job: " + e.getMessage()); return false; }
    }

    public boolean deleteJob(int jobId, int recruiterId, boolean admin) {
        String sql = admin ? "DELETE FROM jobs WHERE job_id=?" : "DELETE FROM jobs WHERE job_id=? AND recruiter_id=?";
        try {
            Connection con = DBConnection.getConnection(); if (con == null) return false;
            PreparedStatement ps = con.prepareStatement(sql); ps.setInt(1, jobId);
            if (!admin) ps.setInt(2, recruiterId);
            int rows = ps.executeUpdate(); ps.close(); con.close(); return rows > 0;
        } catch (Exception e) { System.out.println("Could not delete job: " + e.getMessage()); return false; }
    }

    public ArrayList<Application> getApplicationsForRecruiter(int recruiterId) {
        String sql = "SELECT a.application_id,j.job_title,u.name AS applicant,u.email,a.application_date,a.status "
                + "FROM applications a JOIN jobs j ON a.job_id=j.job_id JOIN users u ON a.user_id=u.user_id "
                + "WHERE j.recruiter_id=? ORDER BY a.application_date DESC";
        return getApplicationList(sql, recruiterId);
    }

    public ArrayList<Application> getAllApplications() {
        String sql = "SELECT a.application_id,j.job_title,u.name AS applicant,u.email,a.application_date,a.status "
                + "FROM applications a JOIN jobs j ON a.job_id=j.job_id JOIN users u ON a.user_id=u.user_id "
                + "ORDER BY a.application_date DESC";
        return getApplicationList(sql, null);
    }

    private ArrayList<Application> getApplicationList(String sql, Integer recruiterId) {
        ArrayList<Application> applications = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection(); if (con == null) return applications;
            PreparedStatement ps = con.prepareStatement(sql);
            if (recruiterId != null) ps.setInt(1, recruiterId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) applications.add(new Application(rs.getInt("application_id"),
                    rs.getString("job_title"), rs.getString("applicant"), rs.getString("email"),
                    rs.getTimestamp("application_date").toString(), rs.getString("status")));
            rs.close(); ps.close(); con.close();
        } catch (Exception e) { System.out.println("Could not load applications: " + e.getMessage()); }
        return applications;
    }

    public boolean updateApplicationStatus(int applicationId, String status, int recruiterId, boolean admin) {
        String sql = admin
                ? "UPDATE applications SET status=? WHERE application_id=?"
                : "UPDATE applications a JOIN jobs j ON a.job_id=j.job_id SET a.status=? WHERE a.application_id=? AND j.recruiter_id=?";
        try {
            Connection con = DBConnection.getConnection(); if (con == null) return false;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            if (!admin) ps.setInt(3, recruiterId);
            int rows = ps.executeUpdate(); ps.close(); con.close(); return rows > 0;
        } catch (Exception e) { System.out.println("Could not update status: " + e.getMessage()); return false; }
    }

}
