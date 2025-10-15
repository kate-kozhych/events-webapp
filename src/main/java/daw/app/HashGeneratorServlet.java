package daw.app;

import daw.app.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/generateHash")
public class HashGeneratorServlet extends HttpServlet {

    @Inject
    private AuthService authService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        String[] passwords = {"password123", "secret2", "adminPass", "userPass", "student123"};

        out.println("-- Generated password hashes for the SQL script:");
        for (String password : passwords) {
            String hashedPassword = authService.encryptPassword(password);
            out.println("-- Password: " + password);
            out.println("-- Hash: " + hashedPassword);
            out.println("INSERT INTO users (id, name, surname, password, role, esnCard, ujaEmail)");
            out.println("VALUES (ID, 'Name', 'Surname', '" + hashedPassword + "', 'role', 'ESNXXX', 'email@example.com');");
            out.println();
        }
    }
}