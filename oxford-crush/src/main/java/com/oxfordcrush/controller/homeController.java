package com.oxfordcrush.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class homeController {
	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 @PostMapping("/login/questions")
	 public ResponseEntity<List<Map<String, Object>>> submitAnswers(@RequestBody Map<String, Object> data) {

	     try {
	         List<String> answers = (List<String>) data.get("answers");
	         String fname = (String) data.get("fname");
	         String gender = (String) data.get("gender");
	         String department = (String) data.get("department");
	         String ipAddress = (String) data.get("ipAddress");
	         String createTableSql = "CREATE TABLE IF NOT EXISTS login_data ("
	        		    + "id INT AUTO_INCREMENT,"
	        		    + "fname VARCHAR(255),"
	        		    + "gender VARCHAR(255),"
	        		    + "department VARCHAR(255),"
	        		    + "ipAddress VARCHAR(255),"
	        		    + "question1 VARCHAR(255),"
	        		    + "question2 VARCHAR(255),"
	        		    + "question3 VARCHAR(255),"
	        		    + "question4 VARCHAR(255),"
	        		    + "question5 VARCHAR(255),"
	        		    + "question6 VARCHAR(255),"
	        		    + "question7 VARCHAR(255),"
	        		    + "question8 VARCHAR(255),"
	        		    + "question9 VARCHAR(255),"
	        		    + "question10 VARCHAR(255),"
	        		    + "PRIMARY KEY(id)"
	        		    + ");";
	        		jdbcTemplate.execute(createTableSql);

	        		String insertSql = "INSERT INTO login_data (fname, gender, department, ipAddress, question1, question2, question3, question4, question5, question6, question7, question8, question9, question10) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	        		jdbcTemplate.update(insertSql, fname, gender, department, ipAddress, answers.get(0), answers.get(1), answers.get(2), answers.get(3), answers.get(4), answers.get(5), answers.get(6), answers.get(7), answers.get(8), answers.get(9));

	         String sql1 = "SELECT fname, gender, department, question1, question2, question3, question4, question5, question6, question7, question8, question9, question10 FROM login_data";
	         List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql1);
	         List<Map<String, Object>> match = new ArrayList<>();
	         for (Map<String, Object> row : rows) {
	             if(!row.get("gender").equals(gender) && !row.get("fname").equals(fname)) {
	                 int j = 0;
	                 for(int i = 0; i < 10; i++) {
	                     if(answers.get(i).equals(row.get("question" + (i + 1)))) {
	                         j++;
	                     }
	                 }
	                 if(j > 6){
	                     Map<String, Object> matchedUser = new HashMap<>();
	                     matchedUser.put("fname", row.get("fname"));
	                     matchedUser.put("department", row.get("department"));
	                     matchedUser.put("matchScore", j*10);
	                     match.add(matchedUser);
	                 }
	             }
	         }

	         // Sort the match list by matchScore in descending order
	         match.sort((m1, m2) -> Integer.compare((int)m2.get("matchScore"), (int)m1.get("matchScore")));

	         // Get the first 10 elements of the sorted list
	         List<Map<String, Object>> topMatches = match.size() > 10 ? match.subList(0, 10) : match;

	         return new ResponseEntity<>(topMatches, HttpStatus.OK);

	         
	     } catch (Exception e) {
	         return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	 }



}
