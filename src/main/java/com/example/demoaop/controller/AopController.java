package com.example.demoaop.controller;

import com.example.demoaop.annotation.LogExecutionTime;
import com.example.demoaop.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AopController {

  @RequestMapping(method = RequestMethod.GET, path = "/hello")
  @LogExecutionTime
  public ResponseEntity<?> hello() {
    return ResponseEntity.ok("Hello there");
  }

  @RequestMapping(method = RequestMethod.POST, path = "/student")
  @LogExecutionTime
  public ResponseEntity<?> student(@RequestBody Student newStudent) {
    Student student = new Student(newStudent.getId(), newStudent.getName());
    return new ResponseEntity<>(student, HttpStatus.CREATED);
  }
}
