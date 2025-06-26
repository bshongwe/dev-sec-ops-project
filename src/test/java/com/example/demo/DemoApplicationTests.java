package com.example.demo;

import com.example.demo.controller.DemoController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
		// Basic context loading test
	}

	@Test
	void testHomeEndpoint() throws Exception {
		mockMvc.perform(get("/api/v1/"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("Hello DevSecOps World!")))
				.andExpect(jsonPath("$.version", is("1.0.0")))
				.andExpect(jsonPath("$.status", is("healthy")))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void testHealthEndpoint() throws Exception {
		mockMvc.perform(get("/api/v1/health"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status", is("UP")))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void testEchoEndpointWithValidInput() throws Exception {
		DemoController.EchoRequest request = new DemoController.EchoRequest();
		request.setMessage("Hello DevSecOps!");

		mockMvc.perform(post("/api/v1/echo")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.originalMessage", is("Hello DevSecOps!")))
				.andExpect(jsonPath("$.echoMessage", is("Echo: Hello DevSecOps!")))
				.andExpect(jsonPath("$.messageLength", is(17)))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void testEchoEndpointWithInvalidInput() throws Exception {
		DemoController.EchoRequest request = new DemoController.EchoRequest();
		request.setMessage(""); // Empty message should fail validation

		mockMvc.perform(post("/api/v1/echo")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testEchoEndpointWithTooLongMessage() throws Exception {
		DemoController.EchoRequest request = new DemoController.EchoRequest();
		// Create a message longer than 1000 characters
		StringBuilder longMessage = new StringBuilder();
		for (int i = 0; i < 1001; i++) {
			longMessage.append("a");
		}
		request.setMessage(longMessage.toString());

		mockMvc.perform(post("/api/v1/echo")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testActuatorHealthEndpoint() throws Exception {
		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("UP")));
	}
}
