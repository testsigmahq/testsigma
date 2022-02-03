package com.testsigma.specification;

import com.testsigma.model.Attachment;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class AttachmentSpecificationsBuilder extends BaseSpecificationsBuilder {

  public AttachmentSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<Attachment> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new AttachmentSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new AttachmentSpecification(params.get(i)));
    }

    return result;
  }
}
