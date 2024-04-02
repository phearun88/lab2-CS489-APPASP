package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) {

            // Creating sample data
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1, "Daniel", "Agar", LocalDate.of(2018, 1, 17), 105945.50));
        employees.add(new Employee(2, "Benard", "Shaw", LocalDate.of(2019, 4, 3), 197750.00 ));
        employees.add(new Employee(3, "Carly", "Agar", LocalDate.of(2014, 5, 16), 842000.75 ));
        employees.add(new Employee(4, "Wesley", "Schneider", LocalDate.of(2019, 5, 2), 74500.00  ));

        List<PensionPlan> pensionPlans = new ArrayList<>();
        pensionPlans.add(new PensionPlan("EX1089", LocalDate.of(2023, 1, 17), 100.00));
        pensionPlans.add(new PensionPlan("SM2307", LocalDate.of(2019, 11, 4), 1555.50));

        System.out.println("List of all employees:");
        printEmployeesJSON(employees, pensionPlans);

        System.out.println("\nMonthly Upcoming Enrollees report:");
        printMonthlyUpcomingEnrolleesJSON(employees, pensionPlans);
    }

    private static void printEmployeesJSON(List<Employee> employees, List<PensionPlan> pensionPlans) {
        List<String> employeeJsonList = employees.stream()
                .map(employee -> {
                    PensionPlan pensionPlan = findPensionPlan(pensionPlans, employee.getEmployeeId());
                    return buildEmployeeJson(employee, pensionPlan);
                })
                .collect(Collectors.toList());

        for (String employeeJson : employeeJsonList) {
            System.out.println(employeeJson);
        }
    }

    private static void printMonthlyUpcomingEnrolleesJSON(List<Employee> employees, List<PensionPlan> pensionPlans) {
        LocalDate nextMonthFirstDay = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        LocalDate nextMonthLastDay = nextMonthFirstDay.withDayOfMonth(nextMonthFirstDay.lengthOfMonth());

        List<Employee> upcomingEnrollees = employees.stream()
                .filter(employee -> employee.getEmploymentDate().plusYears(5).isBefore(nextMonthLastDay) || employee.getEmploymentDate().plusYears(5).isEqual(nextMonthLastDay))
                .filter(employee -> findPensionPlan(pensionPlans, employee.getEmployeeId()) == null)
                .sorted((e1, e2) -> e1.getEmploymentDate().compareTo(e2.getEmploymentDate()))
                .collect(Collectors.toList());

        List<String> upcomingEnrolleesJsonList = upcomingEnrollees.stream()
                .map(Main::buildEmployeeJson)
                .collect(Collectors.toList());

        for (String employeeJson : upcomingEnrolleesJsonList) {
            System.out.println(employeeJson);
        }
    }


    private static PensionPlan findPensionPlan(List<PensionPlan> pensionPlans, long employeeId) {
        return pensionPlans.stream()
                .filter(plan -> plan.getEmployeeId() == employeeId)
                .findFirst()
                .orElse(null);
    }


    private static String buildEmployeeJson(Employee employee, PensionPlan pensionPlan) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"id\":").append(employee.getEmployeeId()).append(",");
        jsonBuilder.append("\"firstName\":\"").append(employee.getFirstName()).append("\",");
        jsonBuilder.append("\"lastName\":\"").append(employee.getLastName()).append("\",");
        jsonBuilder.append("\"employmentDate\":\"").append(employee.getEmploymentDate()).append("\",");
        jsonBuilder.append("\"yearlySalary\":").append(employee.getYearlySalary());

        if (pensionPlan != null) {
            jsonBuilder.append(",\"pensionPlan\":{");
            jsonBuilder.append("\"planReferenceNumber\":\"").append(pensionPlan.getPlanReferenceNumber()).append("\",");
            jsonBuilder.append("\"enrollmentDate\":\"").append(pensionPlan.getEnrollmentDate()).append("\",");
            jsonBuilder.append("\"monthlyContribution\":").append(pensionPlan.getMonthlyContribution());
            jsonBuilder.append("}");
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    private static String buildEmployeeJson(Employee employee) {
        return buildEmployeeJson(employee, null);
    }

}