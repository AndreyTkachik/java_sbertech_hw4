package org.example;

import java.util.*;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class SocketTests {
    private static void configureServer() {
        ServerFactory serverFactory = new ServerFactoryImp();
        serverFactory.listen(8000, new ScoreServiceImp());
    }

    private static ScoreService createScoreClient() {
        ClientFactory factory = new ClientFactoryImp("127.0.0.1", 8000);
        return factory.newClient(ScoreService.class);
    }

    @Test
    void checkComputing() {
        List<Double> results = new ArrayList<>();
        List<Double> expect = List.of(60., 41.);
        configureServer();

        ScoreService scoreService = createScoreClient();
        double result = scoreService.score(new Person("John Doe", 30));
        results.add(result);
        result = scoreService.nextAge(new Person("Jane Doe", 40));
        results.add(result);

        assertThat(results).containsAll(expect);
    }
}