package org.example.spring.boot.spin1;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProcessApplication {
	private final static String processKey1 = "spin-synchronous";
	private final static String processKey2 = "spin-asynchronous";
	private RuntimeService runtimeService;

	public ProcessApplication(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	@EventListener
	private void processPostDeploy(PostDeployEvent event) {
		if (log.isDebugEnabled()) log.debug("-----> processPostDeploy: Enter");

		for (int pi = 1; pi <= 1; pi++) {
			ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey(processKey1, processKey1 + "bk " + pi);
			ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey(processKey2, processKey2 + "bk " + pi);
			if (log.isDebugEnabled()) log.debug("-----> processPostDeploy: created {} process instance with ID: {}", processKey1, processInstance1.getId());
			if (log.isDebugEnabled()) log.debug("-----> processPostDeploy: created {} process instance with ID: {}", processKey2, processInstance2.getId());

			if ((pi % 1000) == 0) {
				if (log.isDebugEnabled()) log.debug("-----> processPostDeploy created: {} process instances", pi);
			}
		}

		if (log.isDebugEnabled()) log.debug("-----> processPostDeploy: Exit");
	}
	
	public static void main(String... args) {
		SpringApplication.run(ProcessApplication.class, args);
	}
}