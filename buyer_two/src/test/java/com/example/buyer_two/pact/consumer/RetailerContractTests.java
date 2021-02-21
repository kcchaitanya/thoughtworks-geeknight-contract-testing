package com.example.buyer_two.pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.example.buyer_two.core.BuyerTwoService;
import com.example.buyer_two.core.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RetailerContractTests {

  private static final String HOST_NAME = "localhost";
  private static final int PORT = 8088;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BuyerTwoService buyerTwoService;

  @Rule
  public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("retailer",
      HOST_NAME, PORT, this);

  @Pact(consumer = "buyer_two")
  public RequestResponsePact createPactForGetLastUpdatedTimestamp(PactDslWithProvider builder)
      throws JsonProcessingException {

    Order order = new Order("John", 2000.0, 3, new Date());
    String orderDetailsString = objectMapper.writeValueAsString(order);

    PactDslRootValue pactDslResponse = new PactDslRootValue();
    pactDslResponse.setValue(orderDetailsString);

    Map<String,String> headers = new HashMap();
    headers.put("Content-Type","application/json");

    return builder
        .given("Get order details")
        .uponReceiving("Get order details by order id")
        .path("/order/79")
        .method(HttpMethod.GET.name())
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .headers(headers)
        .body(pactDslResponse)
        .toPact();
  }

  @Test
  @PactVerification(value = "retailer", fragment = "createPactForGetLastUpdatedTimestamp")
  public void testConsumerGetRequestToOffsetService() {
    Order order = buyerTwoService.getOrderDetails();
    assertEquals(order.getCustomer(), "John");
  }

}
