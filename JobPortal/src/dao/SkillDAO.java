package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;

public class SkillDAO {
    public ArrayList<String> getAllSkills() {
        ArrayList<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM skills ORDER BY skill_name";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) skills.add(rs.getString("skill_name"));
        } catch (Exception e) { System.out.println("Could not load skills: " + e.getMessage()); }
        return skills;
    }

    public ArrayList<String> getUserSkills(int userId) {
        return getSkills("SELECT s.skill_name FROM user_skills us JOIN skills s ON us.skill_id=s.skill_id WHERE us.user_id=? ORDER BY s.skill_name", userId);
    }

    public ArrayList<String> getJobSkills(int jobId) {
        return getSkills("SELECT s.skill_name FROM job_skills js JOIN skills s ON js.skill_id=s.skill_id WHERE js.job_id=? ORDER BY s.skill_name", jobId);
    }

    private ArrayList<String> getSkills(String sql, int id) {
        ArrayList<String> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) result.add(rs.getString(1)); }
        } catch (Exception e) { System.out.println("Could not load skills: " + e.getMessage()); }
        return result;
    }

    public boolean setUserSkills(int userId, ArrayList<String> selected) {
        String delete = "DELETE FROM user_skills WHERE user_id=?";
        String insert = "INSERT INTO user_skills(user_id,skill_id) SELECT ?,skill_id FROM skills WHERE LOWER(skill_name)=LOWER(?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement d = con.prepareStatement(delete); PreparedStatement p = con.prepareStatement(insert)) {
                d.setInt(1, userId); d.executeUpdate();
                for (String skill : selected) { p.setInt(1, userId); p.setString(2, skill.trim()); p.addBatch(); }
                p.executeBatch(); con.commit(); return true;
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) { System.out.println("Could not save your skills: " + e.getMessage()); return false; }
    }

    public boolean setJobSkills(int jobId, ArrayList<String> selected) {
        String delete = "DELETE FROM job_skills WHERE job_id=?";
        String insert = "INSERT INTO job_skills(job_id,skill_id) SELECT ?,skill_id FROM skills WHERE LOWER(skill_name)=LOWER(?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement d = con.prepareStatement(delete); PreparedStatement p = con.prepareStatement(insert)) {
                d.setInt(1, jobId); d.executeUpdate();
                for (String skill : selected) { p.setInt(1, jobId); p.setString(2, skill.trim()); p.addBatch(); }
                p.executeBatch(); con.commit(); return true;
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) { System.out.println("Could not save job skills: " + e.getMessage()); return false; }
    }
}
