package com.testsigma.service;

import java.io.IOException;

public class GraphqlSchemaReaderService {
    public static String getSchemaFromFileName(final String filename) throws IOException {
        return new String(
                GraphqlSchemaReaderService.class.getClassLoader().getResourceAsStream("graphql/" + filename + ".graphql").readAllBytes());

    }
}
