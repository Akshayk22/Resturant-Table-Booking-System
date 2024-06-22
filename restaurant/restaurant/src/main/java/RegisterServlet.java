import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final static String queryInsert = "INSERT INTO user (name, email, mobile, date, gender, table_no) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String queryCheckAvailability = "SELECT * FROM user WHERE date = ? AND table_no = ?";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // get PrintWriter
        PrintWriter pw = res.getWriter();
        // set content type
        res.setContentType("text/html");
        // link the bootstrap
        pw.println("<link rel='stylesheet' href='css/bootstrap.css'></link>");
        // get the values
        String name = req.getParameter("userName");
        String email = req.getParameter("email");
        String mobile = req.getParameter("mobile");
        String dob = req.getParameter("date");
        String gender = req.getParameter("gender");
        String table = req.getParameter("table");
        
        // load the JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // generate the connection
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant", "root", "Abhi@1234");
                PreparedStatement psCheckAvailability = con.prepareStatement(queryCheckAvailability);
                PreparedStatement psInsert = con.prepareStatement(queryInsert);) {
            
            // Check if the selected table is already booked for the given date
            psCheckAvailability.setString(1, dob);
            psCheckAvailability.setString(2, table);
            ResultSet rs = psCheckAvailability.executeQuery();
            if (rs.next()) {
                // Table is already booked for the given date
                pw.println("<div class='card' style='margin:auto;width:300px;margin-top:100px'>");
                pw.println("<h2 class='bg-danger text-light text-center'>Sorry, the selected table is already booked for the selected date.</h2>");
                pw.println("<a href='home.html'><button class='btn btn-outline-success'>Home</button></a>");
                pw.println("</div>");
                return;
            }
            
            // If the table is available, insert the new booking
            psInsert.setString(1, name);
            psInsert.setString(2, email);
            psInsert.setString(3, mobile);
            psInsert.setString(4, dob);
            psInsert.setString(5, gender);
            psInsert.setString(6, table);
            
            // Execute the query
            int count = psInsert.executeUpdate();
            pw.println("<div class='card' style='margin:auto;width:300px;margin-top:100px'>");
            if (count == 1) {
                pw.println("<h2 class='bg-danger text-light text-center'>Record Registered Successfully</h2>");
                res.sendRedirect("items.html");
            } else {
                pw.println("<h2 class='bg-danger text-light text-center'>Record Not Registered</h2>");
            }
            pw.println("</div>");
            pw.println("<a href='home.html'><button class='btn btn-outline-success'>Home</button></a>");
        } catch (SQLException se) {
            pw.println("<h2 class='bg-danger text-light text-center'>" + se.getMessage() + "</h2>");
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // close the stream
        pw.close();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }
}
