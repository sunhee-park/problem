package org.example.problem3;

import java.sql.*;

public class EmployInfo {

    // MariaDB 접속 정보
    private static final String URL = "jdbc:mariadb://codingtest.brique.kr:3306/employees";
    private static final String USER = "codingtest";
    private static final String PASSWORD = "12brique!@";

    // SQL 쿼리
    private static final String SQL_QUERY = """
        SELECT 
            e.emp_no,
            e.first_name,
            e.last_name,
            e.gender,
            e.hire_date,
            d.dept_name,
            t.title,
            MAX(s.salary) AS max_salary
        FROM 
            employees e
        LEFT JOIN 
            dept_emp de ON e.emp_no = de.emp_no
            AND de.from_date <= CURRENT_DATE
            AND de.to_date >= CURRENT_DATE
        LEFT JOIN 
            departments d ON de.dept_no = d.dept_no
        LEFT JOIN 
            titles t ON e.emp_no = t.emp_no
            AND t.from_date <= CURRENT_DATE
            AND t.to_date >= CURRENT_DATE
        LEFT JOIN 
            salaries s ON e.emp_no = s.emp_no
        WHERE 
            e.hire_date >= '2000-01-01'
        GROUP BY 
            e.emp_no, e.first_name, e.last_name, e.gender, e.hire_date, d.dept_name, t.title
        ORDER BY 
            max_salary DESC;
        """;


    public static void main(String[] args) {
        // DB 연결
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // SQL 실행
            PreparedStatement stmt = conn.prepareStatement(SQL_QUERY);
            ResultSet rs = stmt.executeQuery();

            // 결과 출력
            System.out.printf("%-10s %-15s %-15s %-6s %-12s %-20s %-25s %-10s%n",
                    "emp_no", "first_name", "last_name", "gender", "hire_date", "dept_name", "title", "max_salary");

            while (rs.next()) {
                int empNo = rs.getInt("emp_no");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String gender = rs.getString("gender");
                String hireDate = rs.getString("hire_date");
                String deptName = rs.getString("dept_name");
                String title = rs.getString("title");
                int maxSalary = rs.getInt("max_salary");

                // 결과 출력
                System.out.printf("%-10d %-15s %-15s %-6s %-12s %-20s %-25s %-10d%n",
                        empNo, firstName, lastName, gender, hireDate, deptName, title, maxSalary);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
