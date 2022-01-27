package org.camunda.bpm.spring.boot.spin1.delegate;

import camundajar.impl.com.google.gson.Gson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.util.xml.Parse;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.spring.boot.spin1.model.Customer;
import org.camunda.spin.json.SpinJsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ShowVariables implements JavaDelegate {
     private final static Logger LOGGER = LoggerFactory.getLogger(ShowVariables.class);

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String processKey = repositoryService.getProcessDefinition(delegateExecution.getProcessDefinitionId()).getKey();

        LOGGER.info("-----> execute: Enter {}", processKey);

        SpinJsonNode taskNumbers = (SpinJsonNode) delegateExecution.getVariable("taskNumbers");
        LOGGER.info("-----> {}: taskNumbers = {}", processKey, taskNumbers.toString());

        ObjectValue accountCustomersObject = delegateExecution.getVariableTyped("accountCustomersJava");
        Map<String, List<Customer>> accountCustomers = (HashMap<String, List<Customer>>) accountCustomersObject.getValue();
        ArrayList<Customer> customersOfAccount = (ArrayList<Customer>)accountCustomers.get("Account1");
        customersOfAccount.forEach( ( customer -> {
            LOGGER.info("-----> {}: Deserialized Java: Account Name = {} - Customer Name = {} {}", processKey, customer.getAccount(), customer.getFirstName(), customer.getLastName());
        }));
        customersOfAccount= (ArrayList<Customer>)accountCustomers.get("Account2");
        customersOfAccount.forEach( ( customer -> {
            LOGGER.info("-----> {}: Deserialized Java: Account Name = {} - Customer Name = {} {}", processKey, customer.getAccount(), customer.getFirstName(), customer.getLastName());
        }));

        accountCustomersObject = delegateExecution.getVariableTyped("accountCustomersJson");
        Map<String, List<LinkedHashMap>> accountCustomersFromJson = (HashMap<String, List<LinkedHashMap>>) accountCustomersObject.getValue();
        Iterator mapIterator = accountCustomersFromJson.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<String, List<Object>> arrayEntry = (Map.Entry<String, List<Object>>)mapIterator.next();
            if ((arrayEntry.getValue().size() > 0) && (arrayEntry.getValue().get(0) instanceof Customer)) {
                processArrayOfCustomer(processKey, arrayEntry.getValue());
            } else if ((arrayEntry.getValue().size() > 0) && (arrayEntry.getValue().get(0) instanceof LinkedHashMap)) {
                processArrayOfLinkedHasMap(processKey, arrayEntry.getValue());
            } else {
                LOGGER.info("Cannot deserialize unknown object type");
            }

        }

        try {
            accountCustomersObject = delegateExecution.getVariableTyped("accountCustomersGsonToJson");
            accountCustomers = (HashMap<String, List<Customer>>) accountCustomersObject.getValue();
            customersOfAccount = (ArrayList<Customer>) accountCustomers.get("Account1");
            customersOfAccount.forEach((customer -> {
                LOGGER.info("-----> {}: Deserialized GsonToJson: Account Name = {} - Customer Name = {} {}", processKey, customer.getAccount(), customer.getFirstName(), customer.getLastName());
            }));
            customersOfAccount = (ArrayList<Customer>) accountCustomers.get("Account2");
            customersOfAccount.forEach((customer -> {
                LOGGER.info("-----> {}: Deserialized GsonToJson: Account Name = {} - Customer Name = {} {}", processKey, customer.getAccount(), customer.getFirstName(), customer.getLastName());
            }));
        }
        catch (ClassCastException e) {
            if (e.getMessage().contains("PrimitiveTypeValueImpl$StringValueImpl cannot be cast to class")) {
                String accountCustomersString = delegateExecution.getVariable("accountCustomersGsonToJson").toString();
                TypeReference<HashMap<String, List<Customer>>> typeReference = new TypeReference<HashMap<String, List<Customer>>>() {};
                accountCustomers = new ObjectMapper().readValue(accountCustomersString, typeReference);
                customersOfAccount = (ArrayList<Customer>) accountCustomers.get("Account1");
                customersOfAccount.forEach((customer -> {
                    LOGGER.info("-----> {}: Deserialized GsonToJson String: Account Name = {} - Customer Name = {} {}", processKey, customer.getAccount(), customer.getFirstName(), customer.getLastName());
                }));
                customersOfAccount = (ArrayList<Customer>) accountCustomers.get("Account2");
                customersOfAccount.forEach((customer -> {
                    LOGGER.info("-----> {}: Deserialized GsonToJson String: Account Name = {} - Customer Name = {} {}", processKey, customer.getAccount(), customer.getFirstName(), customer.getLastName());
                }));
            }

        }

        LOGGER.info("-----> execute: Exit {}", processKey);
    }

    private void processArrayOfCustomer(String processKey, List<Object> arrayOfCustomers) {
        Iterator arrayIterator = arrayOfCustomers.listIterator();
        while (arrayIterator.hasNext()) {
            Customer customer = (Customer) arrayIterator.next();
            LOGGER.info("-----> {}: Deserialized Json: Account Name = {} - Customer Name = {} {}", processKey, customer.getAccount(), customer.getFirstName(), customer.getLastName());
            // Do something with Customer.
        }
    }

    private void processArrayOfLinkedHasMap(String processKey, List<Object> arrayOfCustomers) {
        ArrayList<Customer> convertedCustomersOfAccountFromJson = new ArrayList<Customer>();
        Gson gson = new Gson();
        Iterator arrayIterator = arrayOfCustomers.listIterator();
        while (arrayIterator.hasNext()) {
            Customer customer = gson.fromJson(arrayIterator.next().toString(), Customer.class);
            LOGGER.info("-----> {}: Gson Deserialized Json: Account Name = {} - Customer Name = {} {}", processKey, customer.getAccount(), customer.getFirstName(), customer.getLastName());
            // Do something with Customer.
        }
    }

//    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
//        Map<String, Object> retMap = new HashMap<String, Object>();
//
//        if(json != JSONObject.NULL) {
//            retMap = toMap(json);
//        }
//        return retMap;
//    }
//
//    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
//        Map<String, Object> map = new HashMap<String, Object>();
//
//        Iterator<String> keysItr = object.keys();
//        while(keysItr.hasNext()) {
//            String key = keysItr.next();
//            Object value = object.get(key);
//
//            if(value instanceof JSONArray) {
//                value = toList((JSONArray) value);
//            }
//
//            else if(value instanceof JSONObject) {
//                value = toMap((JSONObject) value);
//            }
//            map.put(key, value);
//        }
//        return map;
//    }
//
//    public static List<Object> toList(JSONArray array) throws JSONException {
//        List<Object> list = new ArrayList<Object>();
//        for(int i = 0; i < array.length(); i++) {
//            Object value = array.get(i);
//            if(value instanceof JSONArray) {
//                value = toList((JSONArray) value);
//            }
//
//            else if(value instanceof JSONObject) {
//                value = toMap((JSONObject) value);
//            }
//            list.add(value);
//        }
//        return list;
//    }
}
