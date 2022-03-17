package org.example.spring.boot.spin1.delegate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.example.spring.boot.spin1.model.Customer;
import org.camunda.spin.json.SpinJsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class ShowVariables implements JavaDelegate {
    private RepositoryService repositoryService;

    public ShowVariables(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String processKey = repositoryService.getProcessDefinition(delegateExecution.getProcessDefinitionId()).getKey();

        if (log.isDebugEnabled()) log.debug("-----> execute: Enter {}", processKey);

        SpinJsonNode taskNumbers = (SpinJsonNode) delegateExecution.getVariable("taskNumbers");
        if (log.isDebugEnabled()) log.debug("-----> {}: taskNumbers = {}", processKey, taskNumbers.toString());

        ObjectValue accountCustomersObject = delegateExecution.getVariableTyped("accountCustomersJava");
        processAccountCustomersObject(accountCustomersObject, processKey, "Java");

        accountCustomersObject = delegateExecution.getVariableTyped("accountCustomersJson");
        processAccountCustomersObject(accountCustomersObject, processKey, "Json");

        try {
            accountCustomersObject = delegateExecution.getVariableTyped("accountCustomersGsonToJson");
            processAccountCustomersObject(accountCustomersObject, processKey, "GsonToJson");
        }
        catch (ClassCastException e) {
            if (e.getMessage().contains("PrimitiveTypeValueImpl$StringValueImpl cannot be cast to class")) {
                String accountCustomersString = delegateExecution.getVariable("accountCustomersGsonToJson").toString();
                TypeReference<HashMap<String, List<Customer>>> typeReference = new TypeReference<HashMap<String, List<Customer>>>() {};
                Map<String, List<Customer>> accountCustomers = new ObjectMapper().readValue(accountCustomersString, typeReference);
                for (Map.Entry<String, List<Customer>> arrayEntry : accountCustomers.entrySet()) {
                    for (Customer customer : arrayEntry.getValue()) {
                        if (log.isDebugEnabled()) log.debug("-----> {}: Deserialized {}: Account Name = {} - Customer Name = {} {}",
                                processKey, "GsonToJson String", customer.getAccount(), customer.getFirstName(), customer.getLastName());
                        // Do something with Customer.
                    }
                }
            }
        }

        if (log.isDebugEnabled()) log.debug("-----> execute: Exit {}", processKey);
    }

    private void processAccountCustomersObject(ObjectValue accountCustomersObject, String processKey, String type) {
        Map<String, List<Customer>> accountCustomers = (HashMap<String, List<Customer>>) accountCustomersObject.getValue();
        Iterator<Map.Entry<String, List<Customer>>> mapIterator = accountCustomers.entrySet().iterator();
        for (Map.Entry<String, List<Customer>> arrayEntry : accountCustomers.entrySet()) {
            for (Customer customer : arrayEntry.getValue()) {
                if (log.isDebugEnabled()) log.debug("-----> {}: Deserialized {}: Account Name = {} - Customer Name = {} {}",
                        processKey, type, customer.getAccount(), customer.getFirstName(), customer.getLastName());
                // Do something with Customer.
            }
        }
    }
}
