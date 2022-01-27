package org.camunda.bpm.spring.boot.spin1;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableProcessApplication
public class ProcessApplication {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProcessApplication.class);

	@Autowired
	private RuntimeService runtimeService;

	@EventListener
	private void processPostDeploy(PostDeployEvent event) {
		LOGGER.info("-----> processPostDeploy: Enter");

		for (int pi = 1; pi <= 1; pi++) {
			ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey("spin-synchronous", "Spin BK " + pi);
			ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("spin-asynchronous", "Spin BK " + pi);
			LOGGER.info("-----> processPostDeploy: created synchronous process instance with ID: {}", processInstance1.getId());
			LOGGER.info("-----> processPostDeploy: created asynchronous process instance with ID: {}", processInstance2.getId());

			if ((pi % 1000) == 0) {
				LOGGER.info("-----> processPostDeploy created: {} process instances", pi);
			}
		}

		LOGGER.info("-----> processPostDeploy: Exit");
	}
	
	public static void main(String... args) {
		SpringApplication.run(ProcessApplication.class, args);

	}
}