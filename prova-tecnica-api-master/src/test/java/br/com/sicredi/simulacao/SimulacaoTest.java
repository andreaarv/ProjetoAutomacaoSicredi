package br.com.sicredi.simulacao;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.*;

public class SimulacaoTest {
    @Test
    public void testCpfSemRestricao(){
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        String CPF = "16650249092";

        given()
                .pathParam("cpf", CPF)
        .when()
                .get("/v1/restricoes/{cpf}")
        .then()
                .assertThat()
                .statusCode(204);
    }
    @Test
    public void testCpfComRestricao() {
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        String CPF = "97093236014";

        String message = given()
                .pathParam("cpf", CPF)
        .when()
                .get("/v1/restricoes/{cpf}")
        .then()
                .assertThat()
                .statusCode(200)
                .log().all()
                .extract()
                .path("mensagem");
        Assert.assertEquals("O CPF " + CPF + " tem problema", message);
    }
    @Test
    public void testCriarUmaSimulacaoComSucesso(){
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        given()
                .body("{\n" +
                        "  \"nome\": \"Cliente Teste\",\n" +
                        "  \"cpf\": 35193083048,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 1200,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": true\n" +
                        "}")
                .contentType(ContentType.JSON)
        .when()
                .post("/v1/simulacoes")
        .then()
                .log().all()
                .assertThat()
                .statusCode(201);
    }
    @Test
    public void testCriarUmaSimulacaoComProblema(){
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        given()
                .body("{\n" +
                        "  \"nome\": \"Cliente Teste\",\n" +
                        "  \"cpf\": 501.255.380-22,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 500,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": true\n" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/v1/simulacoes")
                .then()
                .log().all()
                .assertThat()
                .statusCode(400);
    }
    @Test
    public void testCriarUmaSimulacaoComOMesmoCpf(){
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        String cpfDuplicado = given()
                .body("{\n" +
                        "  \"nome\": \"Cliente Teste\",\n" +
                        "  \"cpf\": 16650249092,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 500,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": true\n" +
                        "}")
                .contentType(ContentType.JSON)
        .when()
                .post("/v1/simulacoes")
        .then()
                .log().all()
                .assertThat()
                .statusCode(409)
                .extract()
                .path("mensagem");
        Assert.assertEquals("CPF já existente", cpfDuplicado);
    }

    @Test
    public void testAlterarUmaSimulacaoComCpfExistente() {
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        String CPF = "16650249092";

        Boolean seguro = given()
                .pathParam("cpf", CPF)
                .body("{\n" +
                        "  \"nome\": \"Cliente Teste\",\n" +
                        "  \"cpf\": 16650249092,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 500,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": false\n" +
                        "}")
                .contentType(ContentType.JSON)
        .when()
                .put("/v1/simulacoes/{cpf}")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("seguro");
        Assert.assertEquals(false, seguro);
    }

    @Test
    public void testAlterarUmaSimulacaoComCpfNaoExistente() {
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        String CPF = "46260353022";

        String CpfNaoExistente = given()
                .pathParam("cpf", CPF)
                .body("{\n" +
                        "  \"nome\": \"Cliente Teste\",\n" +
                        "  \"cpf\": 46260353022,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 500,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": false\n" +
                        "}")
                .contentType(ContentType.JSON)
        .when()
                .put("/v1/simulacoes/{cpf}")
        .then()
                .log().all()
                .assertThat()
                .statusCode(404)
                .extract()
                .path("mensagem");
        Assert.assertEquals("CPF " + CPF + " não encontrado", CpfNaoExistente);
    }
    @Test
    public void testConsultarTodasSimulacoesCadastradas(){
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        given()
        .when()
                .get("/v1/simulacoes")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void testConsultarUmaSimulacaoPorCpf(){
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        String CPF = "16650249092";

        String CPFRetornado = given()
                .pathParam("cpf", CPF)
        .when()
                .get("/v1/simulacoes/{cpf}")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("cpf");
        Assert.assertEquals(CPF, CPFRetornado);
    }

    @Test
    public void testRemoverUmaSimulacaoExistente(){
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        given()
                .pathParam("id", 9)
        .when()
                .delete("/v1/simulacoes/{id}")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void testRemoverUmaSimulacaoNaoExistente(){
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        String SimulacaoNaoExistente = given()
                .pathParam("id", 100)
        .when()
                .delete("/v1/simulacoes/{id}")
        .then()
                .log().all()
                .assertThat()
                .statusCode(404)
                .extract()
                .path("mensagem");
        Assert.assertEquals("Simulação não encontrada", SimulacaoNaoExistente);
    }
}
