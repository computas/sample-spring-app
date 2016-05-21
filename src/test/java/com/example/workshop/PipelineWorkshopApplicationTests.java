package com.example.workshop;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PipelineWorkshopApplication.class)
@WebAppConfiguration
public class PipelineWorkshopApplicationTests {

	@Test
	public void contextLoads() {
        String doFail = System.getProperty("pipelineFailOneTest");
        if ("true".equalsIgnoreCase(doFail)) {
            Assert.assertTrue("This test fails on purpose", false);
        }
    }

    @Test
    public void getNote() {
    }
}
