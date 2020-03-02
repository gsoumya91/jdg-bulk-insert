package org.tml.esb;

import org.apache.camel.component.infinispan.InfinispanQueryBuilder;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

public class InfinispanQueryExample {
	public InfinispanQueryBuilder getBuilder() {
		return new InfinispanQueryBuilder() { 
	  
	@Override
	public Query build(QueryFactory queryFactory) {
		return queryFactory.from(AccountExtractResponse.class).having("invcNum").eq("IBEESO1920000005").build();
	} 
	
   };
}
}