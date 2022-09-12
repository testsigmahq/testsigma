package com.testsigma.specification;

import com.testsigma.model.DryTestPlan;
import lombok.extern.log4j.Log4j2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Log4j2
public class DryTestPlanSpecification extends BaseSpecification<DryTestPlan> {
  public DryTestPlanSpecification(SearchCriteria searchCriteria) {
    super(searchCriteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation operation) {
    switch (key){
      case "createdDate":
        return parseDate(value, operation);
      case "updatedDate":
        return parseDate(value, operation);
      default:
        return super.getEnumValueIfEnum(key, value, operation);
    }
  }

  private Object parseDate(Object value, SearchOperation op) {
    String valueStr = value.toString();
    if (op.equals(SearchOperation.LESS_THAN))
      valueStr = valueStr + " 23:59:59";
    if (op.equals(SearchOperation.GREATER_THAN))
      valueStr = valueStr + " 00:00:00";
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    try {
      return format.parse(valueStr);
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

}
