package com.goit.webapp.servlets.developer;

import com.goit.model.Developer;
import com.goit.service.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/developers")
public class DeveloperServlet extends HttpServlet {

    private DeveloperService service;

    @Override
    public void init() {
        this.service = (DeveloperService) getServletContext().getAttribute("developerService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String deleteId = req.getParameter("deleteId");
        if (deleteId != null) {
            Developer developer = new Developer();
            developer.setId(Long.parseLong(deleteId));
            service.delete(developer);
            resp.sendRedirect("/developers");
        } else {
            List<Developer> all = service.getAll();
            req.setAttribute("developers", all);
            req.setCharacterEncoding("UTF-8");
            req.getRequestDispatcher("/jsp/developers.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<Developer> modelFromStream = HandleBodyUtil.
            getModelFromStream(req.getInputStream(), Developer.class);
        modelFromStream.ifPresent(developer -> service.create(developer));
        System.out.println("Created developer with status code:" + resp.getStatus());
        resp.sendRedirect("/developers");
    }
}