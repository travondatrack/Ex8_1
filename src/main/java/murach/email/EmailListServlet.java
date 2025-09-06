package murach.email;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import murach.business.User;
import murach.data.UserDB;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(EmailListServlet.class.getName());
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Store the current date as a request attribute
        getServletContext().setAttribute("currentDate", new Date());
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Received POST request");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String url = "/index.html";

        String action = request.getParameter("action");
        if (action == null) {
            action = "join";
        }

        if (action.equals("join")) {
            url = "/index.html";
        } else if (action.equals("add")) {
            String firstName = trimOrEmpty(request.getParameter("firstName"));
            String lastName  = trimOrEmpty(request.getParameter("lastName"));
            String email     = trimOrEmpty(request.getParameter("email"));

            String dob = trimOrEmpty(request.getParameter("dob"));
            String heardFrom = trimOrEmpty(request.getParameter("heardFrom"));
            String wantsUpdates = request.getParameter("wantsUpdates");
            String emailOK = request.getParameter("emailOK");
            String contact = trimOrEmpty(request.getParameter("contact"));

            logger.log(Level.INFO, "Action: {0}", action);
            logger.log(Level.INFO, "First Name: {0}", firstName);
            logger.log(Level.INFO, "Last Name: {0}", lastName);
            logger.log(Level.INFO, "Email: {0}", email);
            logger.log(Level.INFO, "DOB: {0}", dob);
            logger.log(Level.INFO, "Heard From: {0}", heardFrom);
            logger.log(Level.INFO, "Wants Updates: {0}", wantsUpdates);
            logger.log(Level.INFO, "Email OK: {0}", emailOK);
            logger.log(Level.INFO, "Contact: {0}", contact);

            String message = null;
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                message = "Vui lòng nhập đầy đủ họ, tên và email.";
            } else if (!isValidEmail(email)) {
                message = "Định dạng email không hợp lệ.";
            }

            if (message != null) {
                request.setAttribute("user", new User(firstName, lastName, email));
                request.setAttribute("message", message);
                url = "/index.html";
            } else {
                // Create a user object with all the data
                User user = new User(firstName, lastName, email);
                user.setDob(dob);
                user.setHeardFrom(heardFrom);
                user.setWantsUpdates(wantsUpdates != null);
                user.setEmailOK(emailOK != null);
                user.setContact(contact);
                
                // Save user to database
                UserDB.insert(user);
                
                // Get the session and add the user to the users list
                HttpSession session = request.getSession();
                @SuppressWarnings("unchecked")
                java.util.ArrayList<User> users = (java.util.ArrayList<User>) session.getAttribute("users");
                if (users == null) {
                    users = new java.util.ArrayList<User>();
                }
                users.add(user);
                session.setAttribute("users", users);
                
                // Set the user and other attributes
                request.setAttribute("user", user);
                request.setAttribute("dob", dob);
                request.setAttribute("heardFrom", heardFrom);
                request.setAttribute("wantsUpdates", wantsUpdates != null);
                request.setAttribute("emailOK", emailOK != null);
                request.setAttribute("contact", contact);
                
                // Store the current date as a request attribute
                request.setAttribute("currentDate", new Date());
                
                url = "/thanks.jsp";
            }
        }

        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        getServletContext()
                .getRequestDispatcher("/index.html")
                .forward(request, response);
    }

    private static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
