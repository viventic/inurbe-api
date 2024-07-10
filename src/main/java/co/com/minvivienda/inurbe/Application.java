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

import java.util.ArrayList;
import java.util.List;

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
                .contextPath("/api").apiContextPath("/api-doc")
                    .apiProperty("api.title", "INURBE REST API")
                    .apiProperty("api.version", "1.0")
                    .apiProperty("cors", "false")
                    .apiProperty("api.specification.contentType.json", "application/vnd.oai.openapi+json;version=2.0")
                    .apiProperty("api.specification.contentType.yaml", "application/vnd.oai.openapi;version=2.0")
                    .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json);
            
            rest("/expedientes").description("Detalle de un expediente")
                .get("/{id}").description("Detalle de un expediente por Id")
                    .route().routeId("inurbe-detalle-api")
                    .to("sql:SELECT A.* FROM NOTARIADO.VW_EXPEDIENTES A WHERE EXPEDIENTE = :#${header.id}?" +
                        "dataSource=dataSource&outputType=SelectOne&" + 
                        "outputClass=co.com.minvivienda.inurbe.Expediente")
                    .endRest()
            	.post("/list").description("Filtro de expedientes")
	        		.produces("application/json")
	        		.type(InputData.class)
	        		.route().routeId("inurbe-list-api")
	        		.to("direct:filterQuery")
	        		.endRest();
            
            from("direct:filterQuery")
            .log("Received POST request with body: ${body}")
            .process(exchange -> {
            	InputData input = exchange.getIn().getBody(InputData.class);
                String query = "SELECT * FROM (SELECT A.*, ROWNUM rnum FROM (SELECT * FROM NOTARIADO.VW_EXPEDIENTES";
            	List<String> params = new ArrayList<String>();
            	
            	if(input.getIdentificacion() != null) {
            		params.add("CEDULA = '" + input.getIdentificacion() + "'");
            	}
            	if(input.getNombreSolicitante() != null) {
            		params.add("UPPER(PETICIONARIO) LIKE '%" + input.getNombreSolicitante().toUpperCase() + "%'");
            	}
            	if(input.getExpediente() != null) {
            		params.add("EXPEDIENTE = " + input.getExpediente());
            	}
            	if(input.getMatricula() != null) {
            		params.add("UPPER(MATRICULA) LIKE '%" + input.getMatricula().toUpperCase() + "%'");
            	}
            	
            	String criterios = "";
            	if(!params.isEmpty()) {
            		criterios = " WHERE (" + String.join(" OR ", params) + ") ";
            	}
            	
        		query += criterios + " ORDER BY EXPEDIENTE DESC) A WHERE ROWNUM <= " + input.getFilaFin() + ") WHERE rnum >= " + input.getFilaIni();
            	exchange.getOut().setHeader("SqlQuery", query);
            })
            .log("${header.SqlQuery}")
            .to("direct:sqlRoute");
            
            
            // Route to use the SQL query from header
            from("direct:sqlRoute")
                .setHeader("CamelSqlQuery", header("SqlQuery"))
                .to("sql:dummy?" + 
                    "dataSource=dataSource&outputType=SelectList&" + 
                    "outputClass=co.com.minvivienda.inurbe.Expediente");
            
        }
    }

}