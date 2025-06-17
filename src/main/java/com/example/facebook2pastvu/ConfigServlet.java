package com.example.facebook2pastvu;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

@WebServlet("/config")
public class ConfigServlet extends HttpServlet {
    private static final String PASS = System.getenv().getOrDefault("ADMIN_PASS", "admin");

    private boolean checkAuth(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            resp.setHeader("WWW-Authenticate", "Basic realm=\"Config\"");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String decoded = new String(Base64.getDecoder().decode(header.substring(6)));
        // expected format user:pass, user is ignored
        int idx = decoded.indexOf(':');
        if (idx == -1) return false;
        String pass = decoded.substring(idx + 1);
        if (!PASS.equals(pass)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAuth(req, resp)) return;
        resp.setContentType("text/html;charset=UTF-8");
        try (Database db = new Database()) {
            PrintWriter w = resp.getWriter();
            w.println("<html><body><h1>Configuration</h1>");
            w.println("<form method='post'>");
            w.printf("Facebook token: <input name='FB_TOKEN' value='%s'><br>", safe(db.getConfig("FB_TOKEN")));
            w.printf("Facebook group id: <input name='FB_GROUP_ID' value='%s'><br>", safe(db.getConfig("FB_GROUP_ID")));
            w.printf("PastVu city: <input name='PASTVU_CITY' value='%s'><br>", safe(db.getConfig("PASTVU_CITY")));
            w.printf("Schedule minutes: <input name='SCHEDULE_MINUTES' value='%s'><br>", safe(db.getConfig("SCHEDULE_MINUTES")));
            w.println("<input type='submit' value='Save'></form></body></html>");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAuth(req, resp)) return;
        try (Database db = new Database()) {
            req.getParameterMap().forEach((k, v) -> db.setConfig(k, v[0]));
        } catch (Exception e) {
            throw new ServletException(e);
        }
        resp.sendRedirect("config");
    }

    private String safe(String val) {
        return val == null ? "" : val.replace("\"", "&quot;");
    }
}
