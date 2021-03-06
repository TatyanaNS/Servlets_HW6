package com.goit.webapp.servlets.company;

import com.goit.model.Company;
import com.goit.service.*;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/companies")
public class CompanyServlet extends HttpServlet {

  private CompanyService service;

  @Override
  public void init() {
    this.service = (CompanyService) getServletContext().getAttribute("companyService");
  }

  @Override
  protected void doGet(
      HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String deleteId = req.getParameter("deleteId");
    if (deleteId != null) {
      Company company = new Company();
      company.setId(Long.parseLong(deleteId));
      service.delete(company);
      resp.sendRedirect("/companies");
    } else {
      List<Company> all = service.getAll();
      req.setAttribute("companies", all);
      req.getRequestDispatcher("/jsp/companies.jsp").forward(req, resp);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Optional<Company> modelFromStream = HandleBodyUtil.getModelFromStream(req.getInputStream(), Company.class);
    modelFromStream.ifPresent(company -> service.create(company));
    resp.sendRedirect("/companies");
  }
}