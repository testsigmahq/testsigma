package com.testsigma.hibernate.query.function;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class SqlFunctionsMetadataBuilderContributor
  implements MetadataBuilderContributor {

  @Override
  public void contribute(MetadataBuilder metadataBuilder) {
    metadataBuilder.applySqlFunction(
      "JSON_EXTRACT",
      new StandardSQLFunction(
        "JSON_EXTRACT",
        StandardBasicTypes.STRING
      )
    );
  }
}
