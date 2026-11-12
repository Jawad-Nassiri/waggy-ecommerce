package com.waggy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class WaggyBackendApplicationTests {

	@MockitoBean
	private JavaMailSender mailSender;

	@Test
	void contextLoads() {
	}

}
