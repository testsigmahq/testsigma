package com.testsigma.specification;

import com.testsigma.model.Agent;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class AgentSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<Agent> result;

  public AgentSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<Agent> build() {
    for (int i = 0; i < params.size(); i++) {
      result = Specification.where(result).and(new AgentSpecification(params.get(i)));
    }

    return result;
  }

  public Specification<Agent> buildAll() {
    if (params.size() == 0) {
      return null;
    }

    result = new AgentSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new AgentSpecification(searchCriteria)));
    return result;
  }

}
