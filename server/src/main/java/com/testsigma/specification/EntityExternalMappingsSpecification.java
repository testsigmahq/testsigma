package com.testsigma.specification;

import com.testsigma.model.EntityExternalMapping;
import com.testsigma.model.EntityType;
import com.testsigma.model.Integration;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityExternalMappingsSpecification extends BaseSpecification<EntityExternalMapping>{

    public EntityExternalMappingsSpecification(final SearchCriteria criteria){
        super(criteria);
    }

    @Override
    protected Expression<String> getPath(SearchCriteria criteria, Root<EntityExternalMapping> root) {
        if (criteria.getKey().equals("integration")) {
            Join s = root.join("application", JoinType.INNER);
            return s.get("workspace");
        }
        return root.get(criteria.getKey());
    }

    @Override
    protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
        if (key.equals("integration")) {
            if(op == SearchOperation.IN)
                return parseInQuery(value);
            return Integration.valueOf(value.toString());
        }
        if(key.equals("entityType"))
            return EntityType.valueOf(value.toString());
        return value;
    }

    public static List<Integration> parseInQuery(Object value) {
        List<Integration> integrations = new ArrayList<>();
        Arrays.asList(value.toString().split("#")).forEach(string -> {
            integrations.add(Integration.valueOf(string));
        });
        return integrations;
    }
}
