package cleverton.heusner.adapter.input.resource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class MovieRatingRepositoryProxy implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
                .dynamicPort()
        );
        wireMockServer.start();
        System.setProperty("wiremock.port", String.valueOf(wireMockServer.port()));

        String mockResponse = """
                {
                    "Ratings": [
                        {"Source": "Internet Movie Database Mock", "Value": "9.1/10"},
                        {"Source": "Rotten Tomatoes Mock", "Value": "81%"},
                        {"Source": "Metacritic Mock", "Value": "81/100"}
                    ],
                    "Metascore": "81",
                    "imdbRating": "9.1"
                }
                """;
        wireMockServer.stubFor(get(urlPathEqualTo("/"))
                .withHeader("Accept", equalTo("application/json;charset=UTF-8"))
                .withQueryParam("t", equalTo("TheShawshankRedemption"))
                .withQueryParam("apikey", equalTo("apiKeyMock"))
                .withQueryParam("type", equalTo("movie"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBody(mockResponse)));

        return Collections.singletonMap("quarkus.rest-client.movie-api.uri", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
