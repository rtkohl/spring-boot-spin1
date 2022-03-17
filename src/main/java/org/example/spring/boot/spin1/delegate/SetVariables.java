package org.example.spring.boot.spin1.delegate;

import camundajar.impl.com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.example.spring.boot.spin1.model.Customer;
import org.camunda.spin.Spin;
import org.camunda.spin.json.SpinJsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SetVariables implements JavaDelegate {
    private RepositoryService repositoryService;

    public SetVariables(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String processKey = repositoryService.getProcessDefinition(delegateExecution.getProcessDefinitionId()).getKey();

        if (log.isDebugEnabled()) log.debug("-----> execute: Enter {}", processKey);

        String taskNumbers = "{ \"taskNumber\" : [ \"A\", \"B\", \"C\" ] }";
        SpinJsonNode spinJson = Spin.JSON(taskNumbers);
        SpinJsonNode spinList = spinJson.prop("taskNumber");
        delegateExecution.setVariable("taskNumbers", spinList);

        Map<String, List<Customer>> accountCustomers = new HashMap<String, List<Customer>>();
        List<Customer> customersOfAccount1 = new ArrayList<Customer>();
        customersOfAccount1.add(new Customer("Account1", "First1", "Last1"));
        customersOfAccount1.add(new Customer("Account1", "First2", "Last2"));
        List<Customer> customersOfAccount2 = new ArrayList<Customer>();
        customersOfAccount2.add(new Customer("Account2", "First3", "Last3"));
        customersOfAccount2.add(new Customer("Account2", "First4", "Last4"));
        accountCustomers.put("Account1", customersOfAccount1);
        accountCustomers.put("Account2", customersOfAccount2);

        ObjectValue typedObjectVariableJava = Variables.objectValue(accountCustomers).serializationDataFormat(Variables.SerializationDataFormats.JAVA).create();
        delegateExecution.setVariable("accountCustomersJava", typedObjectVariableJava);

        ObjectValue typedObjectVariableJson = Variables.objectValue(accountCustomers).serializationDataFormat(Variables.SerializationDataFormats.JSON).create();
        delegateExecution.setVariable("accountCustomersJson", typedObjectVariableJson);

        delegateExecution.setVariable("accountCustomersGsonToJson", new Gson().toJson(accountCustomers));

        if (log.isDebugEnabled()) log.debug("-----> execute: Exit {}", processKey);
    }
}
