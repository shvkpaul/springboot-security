package com.shvk.security.controller;

import com.shvk.security.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreateStudent_Authorized_with_admin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin"); // Basic authentication

        Student student = new Student(3, "John", "Doe");

        HttpEntity<Student> request = new HttpEntity<>(student, headers);

        ResponseEntity<Student> responseEntity = restTemplate.exchange(
            createURL("/v1/students"),
            HttpMethod.POST,
            request,
            Student.class
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Student responseStudent = responseEntity.getBody();
        assertEquals(student.id(), responseStudent.id());
        assertEquals(student.firstName(), responseStudent.firstName());
        assertEquals(student.lastName(), responseStudent.lastName());
    }

    @Test
    public void testCreateStudent_Forbidden_with_user() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "user"); // Basic authentication with user role

        Student student = new Student(3, "John", "Doe");

        HttpEntity<Student> request = new HttpEntity<>(student, headers);

        ResponseEntity<Void> responseEntity = restTemplate.exchange(
            createURL("/v1/students"),
            HttpMethod.POST,
            request,
            Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testGetStudents_Authorized_with_user() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "user"); // Basic authentication with user role

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Student[]> responseEntity = restTemplate.exchange(
            createURL("/v1/students"),
            HttpMethod.GET,
            request,
            Student[].class
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Student[] students = responseEntity.getBody();
        assertNotNull(students);
        assertEquals(4, students.length);
    }

    @Test
    public void testGetStudents_Authorized_with_admin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin"); // Basic authentication with user role

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Student[]> responseEntity = restTemplate.exchange(
            createURL("/v1/students"),
            HttpMethod.GET,
            request,
            Student[].class
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Student[] students = responseEntity.getBody();
        assertNotNull(students);
        assertEquals(2, students.length); // Assuming there are 2 initial students
    }

    private String createURL(String uri) {
        return "http://localhost:" + port + uri;
    }
}