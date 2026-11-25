package dao;

import db.DBConnection;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthDAO {
    public boolean register(String name, String email, String password, String role) {
        String sql = "INSERT INTO users(name,email,password,role) VALUES(?,?,?,?)";
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(password));
            ps.setString(4, role);
            int rows = ps.executeUpdate();

            if (rows != 1) {
                ps.close();
                con.rollback();
                con.close();
                return false;
            }

            ResultSet keys = ps.getGeneratedKeys();
            int newUserId = keys.next() ? keys.getInt(1) : -1;
            keys.close();
            ps.close();

            // The first user-created recruiter takes ownership of the four starter jobs.
            // Their email is whatever they entered during registration; no fixed recruiter login is needed.
            if ("RECRUITER".equals(role) && newUserId > 0) {
                String transfer = "UPDATE jobs SET recruiter_id=? WHERE recruiter_id=(SELECT user_id FROM users WHERE email='admin@jobportal.com' LIMIT 1) "
                        + "AND ((job_title='Java Developer' AND company_name='ABC Technologies') "
                        + "OR (job_title='Frontend Developer' AND company_name='PixelWorks') "
                        + "OR (job_title='Backend Developer' AND company_name='CodeBridge') "
                        + "OR (job_title='Full Stack Trainee' AND company_name='NextGen Systems'))";
                PreparedStatement move = con.prepareStatement(transfer);
                move.setInt(1, newUserId);
                move.executeUpdate();
                move.close();
            }

            con.commit();
            con.close();
            return true;
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public User login(String email, String password) {
        String sql = "SELECT user_id,name,email,password,role FROM users WHERE email=?";
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return null;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { rs.close(); ps.close(); con.close(); return null; }

            String saved = rs.getString("password");
            boolean valid;
            if (saved != null && saved.startsWith("pbkdf2$")) {
                valid = PasswordUtil.verify(password, saved);
            } else {
                // Supports the original admin row created by jobportal.sql.
                valid = saved != null && saved.equals(password);
                if (valid) {
                    PreparedStatement update = con.prepareStatement("UPDATE users SET password=? WHERE user_id=?");
                    update.setString(1, PasswordUtil.hash(password));
                    update.setInt(2, rs.getInt("user_id"));
                    update.executeUpdate();
                    update.close();
                }
            }

            User user = null;
            if (valid) user = new User(rs.getInt("user_id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("role"));
            rs.close(); ps.close(); con.close();
            return user;
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    public boolean updateProfile(int userId, String name, String newPassword) {
        boolean changePassword = newPassword != null && !newPassword.isEmpty();
        String sql = changePassword
                ? "UPDATE users SET name=?,password=? WHERE user_id=?"
                : "UPDATE users SET name=? WHERE user_id=?";
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            if (changePassword) {
                ps.setString(2, PasswordUtil.hash(newPassword));
                ps.setInt(3, userId);
            } else ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            ps.close(); con.close();
            return rows > 0;
        } catch (Exception e) {
            System.out.println("Profile update failed: " + e.getMessage());
            return false;
        }
    }
}
