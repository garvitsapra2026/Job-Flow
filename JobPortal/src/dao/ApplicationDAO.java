package dao;

import db.DBConnection;
import model.Application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ApplicationDAO {
    public boolean apply(int jobId, int userId) {
        String sql = "INSERT INTO applications(job_id,user_id) VALUES(?,?)";
        try {
            Connection con = DBConnection.getConnection(); if (con == null) return false;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, jobId); ps.setInt(2, userId);
            int rows = ps.executeUpdate(); ps.close(); con.close(); return rows == 1;
        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().toLowerCase().contains("unique") || e.getMessage().toLowerCase().contains("duplicate")))
                System.out.println("You have already applied for this job.");
            else System.out.println("Application failed: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Application> getMyApplications(int userId) {
        ArrayList<Application> applications = new ArrayList<>();
        String sql = "SELECT a.application_id,j.job_title,u.name AS applicant,u.email,a.application_date,a.status "
                + "FROM applications a JOIN jobs j ON a.job_id=j.job_id JOIN users u ON a.user_id=u.user_id "
                + "WHERE a.user_id=? ORDER BY a.application_date DESC";
        try {
            Connection con = DBConnection.getConnection(); if (con == null) return applications;
            PreparedStatement ps = con.prepareStatement(sql); ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) applications.add(new Application(rs.getInt("application_id"),
                    rs.getString("job_title"), rs.getString("applicant"), rs.getString("email"),
                    rs.getTimestamp("application_date").toString(), rs.getString("status")));
            rs.close(); ps.close(); con.close();
        } catch (Exception e) { System.out.println("Could not load your applications: " + e.getMessage()); }
        return applications;
    }
}
