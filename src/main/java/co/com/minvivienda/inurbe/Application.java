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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@SpringBootApplication
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
    	
    	Map<String, String> fieldsMap = new HashMap<String, String>(){{
    		put("identificacion", "CEDULA");
    		put("nombresolicitante", "PETICIONARIO");
    		put("expediente", "EXPEDIENTE");
    		put("matricula", "MATRICULA");
    	}};
    	

        @Override
        public void configure() {
            restConfiguration()
	    		.enableCORS(true)
	    		.corsAllowCredentials(true)
	    		.corsHeaderProperty("Access-Control-Allow-Origin", "*")
	    		.corsHeaderProperty("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization")
	    		.contextPath("/api")
                .component("servlet")
                .bindingMode(RestBindingMode.json);
            
            
            rest("/expedientes").description("Detalle de un expediente")
                .get("/{id}").description("Detalle de un expediente por numero de expediente")
                .produces("application/json")
                    .route().routeId("inurbe-detalle-api")
                    .to("sql:SELECT A.* FROM NOTARIADO.VW_EXPEDIENTES A WHERE EXPEDIENTE = :#${header.id}?" +
                        "dataSource=dataSource&outputType=SelectOne&" + 
                        "outputClass=co.com.minvivienda.inurbe.Expediente")
                    
                    .choice()
                    .when(simple("${body} == null"))
                        .log("No existe el expediente ${header.id}.")
                        .setBody(constant("{\"response\": \"error\", \"message\": \"No existe el expediente especificado\"}"))
                        .setHeader("Content-Type", constant("application/json"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
	                    .setHeader("Access-Control-Allow-Origin", constant("*"))
	                    .setHeader("Access-Control-Allow-Headers", constant("Authorization"))
                    .endRest()
            	
                .post("/list").description("Listado de expedientes paginados y con filtros")
	        		.produces("application/json")
	        		.type(InputData.class)
	        		.route().routeId("inurbe-list-api")
	        		.multicast(new ExpedientesAggregator())
	        		.to("direct:filterQuery", "direct:totalCount")
	        		.setHeader("Access-Control-Allow-Origin", constant("*"))
	        		.setHeader("Access-Control-Allow-Headers", constant("Authorization"))
	        		.endRest();
            
            //Consulta de expedientes paginados y con filtros
            from("direct:filterQuery")
	            .process(exchange -> {
	            	InputData input = exchange.getIn().getBody(InputData.class);
	                String query = "SELECT * FROM (SELECT A.*, ROWNUM rnum FROM (SELECT * FROM NOTARIADO.VW_EXPEDIENTES";
	            	
	                String ordenarPor = fieldsMap.get(input.getOrdenarPor().trim().toLowerCase());
	                if(ordenarPor == null) {
	                	ordenarPor =  "EXPEDIENTE"; 
	                }
	                
	                
	                String orden = input.getOrden() == null ||  input.getOrden().trim().isEmpty() ? "ASC" : input.getOrden().toUpperCase();
	                orden = orden.contains("ASC") || orden.contains("DESC") ? orden: "ASC";
	                
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
	            	
	            	long rowNumIni = input.getFilas() * input.getPagina() - input.getFilas();
	            	long rowNumFin = input.getPagina() * input.getFilas();
	            	query += criterios + " ORDER BY " + ordenarPor + " " + orden + ") A WHERE ROWNUM <= " + rowNumFin + ") WHERE rnum > " + rowNumIni;
	        		exchange.getOut().setHeader("SqlQuery", query);
	            })
	            .log("QUERY: ${header.SqlQuery}")
	            .to("direct:sqlRoute");
            
            
            //Se ejecuta el query construido en la ruta direct:filterQuery
            from("direct:sqlRoute")
                .setHeader("CamelSqlQuery", header("SqlQuery"))
                .to("sql:dummy?" + 
                    "dataSource=dataSource&outputType=SelectList&" + 
                    "outputClass=co.com.minvivienda.inurbe.Expediente")
                .process(exchange -> {
                	List<Expediente> expedientes = new ArrayList<Expediente>();
                	expedientes = exchange.getIn().getBody(expedientes.getClass());
                	exchange.getIn().setHeader("Access-Control-Allow-Origin", constant("*"));
                	exchange.getIn().setHeader("Access-Control-Allow-Headers", constant("Authorization"));
                	exchange.getIn().setBody(expedientes);
                });
            
            
            //Se consulta el total de registro sin paginar para enviar en el json de respuesta
            from("direct:totalCount")
            .process(exchange -> {
            	InputData input = exchange.getIn().getBody(InputData.class);
                String queryCount = "SELECT COUNT(1) AS totalRows FROM NOTARIADO.VW_EXPEDIENTES ";
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
            	
        		queryCount += criterios;
            	exchange.getOut().setHeader("SqlQuery", queryCount);
            })
            .setHeader("CamelSqlQuery", header("SqlQuery"))
            .to("sql:dummy?" + 
                "dataSource=dataSource&outputType=SelectList&" + 
                "outputClass=co.com.minvivienda.inurbe.TotalRows")
            .process(exchange -> {
            	List<TotalRows> rows = exchange.getIn().getBody(List.class);
            	Long totalRows = 0L;
            	if(rows != null && !rows.isEmpty()) {
            		totalRows = rows.get(0).getTotalRows();
            	}
            	
            	exchange.getIn().setHeader("Access-Control-Allow-Origin", constant("*"));
            	exchange.getIn().setHeader("Access-Control-Allow-Headers", constant("Authorization"));
            	exchange.getIn().setBody(totalRows);
            });   
        }
    }
}