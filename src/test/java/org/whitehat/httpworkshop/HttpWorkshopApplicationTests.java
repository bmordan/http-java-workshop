package org.whitehat.httpworkshop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import net.minidev.json.JSONObject;

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

	public String getHashId() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(
			post("/apprentices")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(new Apprentice("Test2")))		
		).andReturn();
		Object hashId = mvcResult.getResponse().getHeaderValue("Your-Id");
		return hashId.toString();		
	}

	@Test
	public void shouldReturnFirstInstruction() throws Exception {
		this.mockMvc
			.perform(get("/"))
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

	@Test
	public void shouldBeAbleToPassQueryParams() throws Exception {
		this.mockMvc
			.perform(get("/apprentices/{hash}", this.getHashId()))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Nice work Test2,")));
	}

	@Test
	public void shouldNotBeAbleToPassWrongParams() throws Exception {
		this.mockMvc
			.perform(get("/apprentices/12345-wrong"))
			.andExpect(status().isNotFound());
	}

	@Test
	public void shouldBeAbleToMakeAPatch() throws Exception {
		this.mockMvc
			.perform(
				patch("/apprentices/{hash}", this.getHashId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.content("guests=Cheech%2C%20Chong")
			)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("I wonder what Cheech and Chong")));
	}

	@Test
	public void shouldNotBeAbleToMakeAPatchWithWrongMediaType() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("guests", "Cheech%2C%20Chong");
		this.mockMvc
			.perform(
				patch("/apprentices/{hash}", this.getHashId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(jsonObject))
			)
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void shouldNotBeAbleToMakeAPatchWithAPost() throws Exception {
		this.mockMvc
			.perform(
				post("/apprentices/{hash}", this.getHashId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.content("guests=Cheech%2C%20Chong")
			)
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void shouldUseQueryParams() throws Exception {
		String hashId = getHashId();
		this.mockMvc
			.perform(
				patch("/apprentices/{hash}", hashId)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.content("guests=Cheech%2C%20Chong")				
			)
			.andReturn();
		this.mockMvc	
			.perform(
				get("/apprentices/{hash}/menus", hashId)
				.queryParam("starter", "Soup")
				.queryParam("main", "Boiled beans and carrots")
				.queryParam("dessert", "cheesecake")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("enjoying Soup")));
	}

	@Test
	public void shouldDeleteOK() throws Exception {
		this.mockMvc
			.perform(delete("/apprentices/{hash}", this.getHashId()))
			.andExpect(status().isNoContent())
			.andExpect(content().string(containsString("https://applied.whitehat.org.uk")));
	}
}
