package com.goit.dao;

import com.goit.model.Developer;
import com.goit.model.Project;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.logging.log4j.*;

public class ProjectDao extends AbstractDao<Project> {

  private static final Logger LOGGER = LogManager.getLogger(ProjectDao.class);
  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

  private static ProjectDao instance;

  private ProjectDao() {
  }

  public static ProjectDao getInstance() {
    if (instance == null) {
      instance = new ProjectDao();
    }
    return instance;
  }

  @Override
  String getTableName() {
    return "projects";
  }

  @Override
  Project mapToEntity(ResultSet rs) throws SQLException {
    Project project = new Project();
    project.setId(rs.getLong("id"));
    project.setName(rs.getString("project_name"));
    project.setCreated(format.format(rs.getDate("created")));
    project.setCost(rs.getDouble("cost"));
    return project;
  }

  @Override
  public Optional<Project> create(Project project) {
    String sql = "insert into projects(project_name, created, cost)"
        + " values(?, ?, ?)";
    DbHelper.executeWithPreparedStatement(sql, ps -> {
      System.out.println("ps: " + ps);
      ps.setString(1, project.getName());
      if (project.getCreated() != null) {
        ps.setDate(2, new Date(Date.valueOf(project.getCreated()).getTime()));
      } else { ps.setDate(2, null); }
      ps.setDouble(3, project.getCost());
    });
    LOGGER.info("Record was created");
    return Optional.empty();
  }

  @Override
  public void update(Project project) {
    String sql = "update projects set project_name = ?, created = ?, cost = ?"
        + " where id = ?";
    DbHelper.executeWithPreparedStatement(sql, ps -> {
      ps.setString(1, project.getName());
      if (project.getCreated() != null) {
        ps.setDate(2, new Date(Date.valueOf(project.getCreated()).getTime()));
      } else { ps.setDate(2, null); }
      ps.setDouble(3, project.getCost());
      ps.setLong(4, project.getId());
    });
    LOGGER.info("Record was updated");
  }

  public String getProjectName(Project project) {
    return project.getName();
  }

  public Map<String, BigDecimal> getSumProjectSalary(String name) {
    Map<String, BigDecimal> resultList = new HashMap<>();
    ResultSet resultSet = null;
    String sql = "select p.project_name, sum(d.salary) as sum_salary " +
            "from developer_project dp " +
            "join developers d on d.id = dp.developer_id " +
            "join projects p on p.id = dp.project_id " +
            "where p.project_name = ? " +
            "group by p.project_name";
    try {
      resultSet = DbHelper.getWithPreparedStatement(
              sql, ps -> {
                ps.setString(1, name);
              });
      while (resultSet.next()) {
        resultList.put(resultSet.getObject(1, String.class),
                resultSet.getObject(2, BigDecimal.class));
      }
    } catch (SQLException e) {
      LOGGER.error("Get sumProjectSalary exception", e);
    }
    return resultList;
  }

  public List<String> getProjectInfo() {
    List<String> resultList = new ArrayList<>();
    ResultSet resultSet = null;
    String sql = "select p.created || ' - ' || p.project_name || ' - ' || count(d.id) as project_info  " +
            " from projects p " +
            " join developer_project dp on dp.project_id = p.id " +
            " join developers d on d.id = dp.developer_id " +
            " group by p.created, p.project_name ";
    try {
      resultSet = DbHelper.getWithPreparedStatement(
              sql, ps -> {
              });
      while (resultSet.next()) {
        resultList.add(resultSet.getObject(1, String.class));
      }
    } catch (SQLException e) {
      LOGGER.error("Get projectInfo exception", e);
    }
    return resultList;
  }
}