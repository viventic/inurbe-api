/*
 * Copyright 2016 Red Hat, Inc.
 * <p>
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package co.com.minvivienda.inurbe;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@SpringBootApplication
// load regular Spring XML file from the classpath that contains the Camel XML DSL
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application {

    /**
     * A main method to start this application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    
    @Component
    class RestApi extends RouteBuilder {

        @Override
        public void configure() {
            restConfiguration()
                .contextPath("/inurbe").apiContextPath("/api-doc")
                    .apiProperty("api.title", "Camel REST API")
                    .apiProperty("api.version", "1.0")
                    .apiProperty("cors", "true")
                    .apiProperty("api.specification.contentType.json", "application/vnd.oai.openapi+json;version=2.0")
                    .apiProperty("api.specification.contentType.yaml", "application/vnd.oai.openapi;version=2.0")
                    .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json);
            
            rest("/expedientes").description("Books REST service")
                .get("/").description("The list of all the books")
                    .route().routeId("books-api")
                    .to("sql:SELECT * FROM NOTARIADO.VW_EXPEDIENTES?" +
                        "dataSource=dataSource&" +
                        "outputClass=co.com.minvivienda.inurbe.Expediente")
                    .endRest()
                .get("/expediente/{id}").description("Details of an order by id")
                    .route().routeId("order-api")
                    .to("sql:SELECT * FROM NOTARIADO.VW_EXPEDIENTES WHERE EXPEDIENTE = :#${header.id}?" +
                        "dataSource=dataSource&outputType=SelectOne&" + 
                        "outputClass=co.com.minvivienda.inurbe.Expediente");
        }
    }

    /*@Component
    class Backend extends RouteBuilder {

        @Override
        public void configure() {
            // A first route generates some orders and queue them in DB
            from("timer:new-order?delay=1s&period={{quickstart.generateOrderPeriod:2s}}")
                .routeId("generate-order")
                .bean("orderService", "generateOrder")
                .to("sql:insert into orders (id, item, amount, description, processed) values " +
                    "(:#${body.id} , :#${body.item}, :#${body.amount}, :#${body.description}, false)?" +
                    "dataSource=dataSource")
                .log("Inserted new order ${body.id}");

            // A second route polls the DB for new orders and processes them
            from("sql:select * from orders where processed = false?" +
                "consumer.onConsume=update orders set processed = true where id = :#id&" +
                "consumer.delay={{quickstart.processOrderPeriod:5s}}&" +
                "dataSource=dataSource")
                .routeId("process-order")
                .bean("orderService", "rowToOrder")
                .log("Processed order #id ${body.id} with ${body.amount} copies of the «${body.description}» book");
        }
    }*/

}