package com.example.facebook2pastvu;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try (Database db = new Database()) {
            List<Match> matches = db.getMatches();
            PrintWriter w = resp.getWriter();
            w.println("<html><head><title>Matches</title></head><body>");
            w.println("<h1>Facebook to PastVu Matches</h1>");
            w.println("<table border='1' cellpadding='5' cellspacing='0'>");
            w.println("<tr><th>Post</th><th>Facebook</th><th>PastVu</th><th>Method</th></tr>");
            for (Match m : matches) {
                String fbLink = "https://facebook.com/" + m.postId();
                String pvThumb = fetchPvThumb(m.pvUrl());
                w.println("<tr>");
                w.println("<td><a href='" + fbLink + "' target='_blank'>" + m.postId() + "</a></td>");
                w.println("<td><a href='" + m.fbUrl() + "' target='_blank'><img src='" + m.fbUrl() + "' style='max-height:150px'></a></td>");
                w.println("<td><a href='" + m.pvUrl() + "' target='_blank'><img src='" + pvThumb + "' style='max-height:150px'></a></td>");
                w.println("<td>" + m.method() + "</td>");
                w.println("</tr>");
            }
            w.println("</table></body></html>");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String fetchPvThumb(String pvUrl) {
        Request request = new Request.Builder().url(pvUrl).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return "";
            String html = response.body().string();
            int idx = html.indexOf("property=\"og:image\"");
            if (idx != -1) {
                int content = html.indexOf("content=\"", idx);
                if (content != -1) {
                    int start = content + 9;
                    int end = html.indexOf('"', start);
                    if (end > start) {
                        return html.substring(start, end);
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return "";
    }
}
