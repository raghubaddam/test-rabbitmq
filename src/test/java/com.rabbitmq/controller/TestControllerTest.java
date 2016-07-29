package com.rabbitmq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.TestRabbitmqApplication;
import com.rabbitmq.domain.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestRabbitmqApplication.class})
@PropertySource("classpath:test.properties")
public class TestControllerTest {


	@Autowired
	private WebApplicationContext context;

	private ObjectMapper objectMapper;
	private MockMvc mockMvc;

	List<Person> planReceiptList = new ArrayList<>();


	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
		objectMapper= new ObjectMapper();
		planReceiptList.clear();
	}


	@RabbitListener(queues = "rrQueue")
    public void handleTNRMessages(Person planReceipt) {
		System.out.println(planReceipt);
		planReceiptList.add(planReceipt);
    }

	@Test
	public void shouldSendMessageAndListen() throws Exception {

		User user = new User("raghu","baddam", AuthorityUtils.createAuthorityList("USER"));
		TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);
		SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
		Person person = new Person();
		person.setFirstName("firstName");
		person.setLastName("lastName");

		MockHttpServletResponse response = mockMvc.perform(post("/message").principal(testingAuthenticationToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(person))).andReturn().getResponse();

		assertThat(response.getStatus(), is(HttpStatus.OK.value()));

	}




}