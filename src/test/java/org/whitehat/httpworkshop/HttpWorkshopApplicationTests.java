package org.whitehat.httpworkshop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest
class HttpWorkshopApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void shouldReturnFirstInstruction() throws Exception {
		this.mockMvc
			.perform(get("/"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Welcome")));
	}

	@Test
	public void shouldAcceptPostRequestToApprentices() throws Exception {
		this.mockMvc
			.perform(
				post("/apprentices")
					.contentType(MediaType.APPLICATION_JSON)
					.content(asJsonString(new Apprentice("Test")))
			)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString("Test")))
			.andExpect(header().exists("Your-Id"));
	}

	@Test
	public void shouldNotAcceptGetRequestToApprentices() throws Exception {
		this.mockMvc
			.perform(get("/apprentices"))
			.andExpect(status().isMethodNotAllowed());
	}
}
