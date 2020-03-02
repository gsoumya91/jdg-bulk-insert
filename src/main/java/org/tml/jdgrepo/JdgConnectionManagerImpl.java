package org.tml.jdgrepo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tml.config.JdbcConnection;
import org.tml.config.JdgConstants;
import org.tml.esb.AccountExtractResponse;

/**
 * @author Soumya Gupta
 *
 */
public class JdgConnectionManagerImpl implements JdgConnectionManager {

	private static Logger logger = LoggerFactory.getLogger(JdgConnectionManagerImpl.class);
	private RemoteCache<String, Object> cache;
	private Map<String, Date> indexMasterDataMap;

	@Autowired
	JdbcConnection jdbcConnection;

	public JdgConnectionManagerImpl() {
		try {
			this.cache = JdgConnection.getCache(JdgConstants.CACHE_NAME);
		} catch (Exception e) {
			logger.error(
					"Exception occured while creating bean of class JdgConnectionManagerImpl with cache name {} and exception {}",
					JdgConstants.CACHE_NAME, e.toString());
		}
	}

	@Override
	public boolean put(String key, Exchange value) {
		logger.info("Entering Method  JdgConnectionManagerImpl.put with parameters :{}", key);

		if (value != null) {
			logger.info("Entering for getting cache to put data :");
			try {
				System.out.println("Body :::::" + value.getIn().getBody(String.class));
				this.cache.put("1", value.getIn().getBody(String.class));

			} catch (Exception e) {
				logger.error("Exception occured while putting data in cache {}", e.toString());
				return false;
			}
		}
		logger.info("Exiting Method  JdgConnectionManagerImpl.put with parameters");
		return true;
	}

	@Override
	public int putAll(Date startDate, Date endDate) {

		return 0;
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	@Override
	public void getData(String key, Exchange ex) throws Exception {
		System.out.println("gETTT" + cache.get("1"));
		System.out.println("Cache name :::::" + cache);

		QueryFactory factory = Search.getQueryFactory(cache);
		// Build up a query
		Query query = factory.from(AccountExtractResponse.class).having("vcNo").eq("27708642000R").toBuilder().build();
		// Execute the query
		List<AccountExtractResponse> list = query.list();
		System.out.println("List entries :::::" + list.get(0));
		// List the data
		list.forEach(person -> System.out.printf("Match: %s", person));

		/*
		 * QueryFactory queryFactory = Search.getQueryFactory(cache);
		 * 
		 * Query q = queryFactory.from(AccountExtractResponse.class).build();
		 * 
		 * List<String> list = q.list();
		 * 
		 * System.out.println("Keys" + list); System.out.println("List Size :::::::" +
		 * list.size());
		 */
		/*
		 * BeanInfo info = Introspector.getBeanInfo(AccountExtractResponse.class,
		 * Object.class);
		 * 
		 * QueryFactory qf = Search.getQueryFactory(cache); QueryBuilder qb =
		 * qf.from(AccountExtractResponse.class); Query q =
		 * qf.from(AccountExtractResponse.class).having("invcNum").eq("IBEESO1920000005"
		 * ).build(); List<AccountExtractResponse> list = q.list();
		 * System.out.println("List size :::::" + list.size());
		 * System.out.println("Parameters ::::" + q.getParameters());
		 * System.out.println("List entries :::::" + list.get(0));
		 * 
		 * FilterConditionContext ctx = null;
		 * 
		 * for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
		 * 
		 * System.out.println("Property Descriptors ::: " + pd);
		 * 
		 * Object searchValue = ex.getIn().getHeaders().get(pd.getName());
		 * 
		 * if (searchValue != null) {
		 * 
		 * System.out.println("searchValue :::::" + searchValue); if
		 * (pd.getPropertyType().equals(Date.class)) { searchValue = new
		 * Date(Long.parseLong((String) searchValue)); }
		 * 
		 * if (ctx == null) { ctx = qb.having(pd.getName()).eq(searchValue);
		 * 
		 * System.out.println("ctx ::::::::::" + ctx); } else {
		 * ctx.and().having(pd.getName()).eq(searchValue); }
		 * 
		 * } }
		 * 
		 * Query q = ctx.toBuilder().build(); List<AccountExtractResponse> list =
		 * q.list(); System.out.println("List entries :::::" + list.size());
		 * 
		 * ex.getIn().setBody(q.list());
		 */

		/*
		 * return qb.build();
		 * 
		 * Query q =qf.from(AccountExtractResponse.class).having("invcNum").eq(
		 * "IBEESO1920000005").build();
		 * 
		 * List<AccountExtractResponse> list = q.list();
		 * 
		 * System.out.println("List size :::::"+list.size());
		 * System.out.println("Parameters ::::"+q.getParameters());
		 * System.out.println("List entries :::::"+list.get(0));
		 */

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean updateOrAddIndex(Date startDate, Date endDate) throws Exception {
		logger.info("Entering Method JdgConnectionManagerImpl.updateOrAddIndex with parameters :{},{} ",
				startDate.toString(), endDate.toString());
		if (startDate != null && endDate != null) {
			this.indexMasterDataMap = (Map<String, Date>) ((Map<String, Object>) this.cache
					.get(JdgConstants.CACHE_NAME)).get(JdgConstants.INDEX_DATA_MAP);

			if (this.indexMasterDataMap != null) {
				logger.info("Index Cache is not empty and value is :{}", this.indexMasterDataMap.toString());
				this.indexMasterDataMap.put(JdgConstants.START_DATE, startDate);
				this.indexMasterDataMap.put(JdgConstants.END_DATE, endDate);
				this.cache.put(JdgConstants.INDEX_DATA_MAP, this.indexMasterDataMap);
				logger.info("Index Cache of master is added to cache");
			} else {
				logger.info("Index Cache is  empty , creating new object to store data");
				this.indexMasterDataMap = new HashMap<>();
				this.indexMasterDataMap.put(JdgConstants.START_DATE, startDate);
				this.indexMasterDataMap.put(JdgConstants.END_DATE, endDate);
				this.cache.put(JdgConstants.INDEX_DATA_MAP, this.indexMasterDataMap);
				logger.info("Index Cache of master is added to cache");
			}
		} else {
			logger.error("Start Date and End Date cannot be null , start date is {} and end date is {}", startDate,
					endDate);
			throw new Exception("Start Date and End Date cannot be null");
		}
		logger.info("Exiting from method JdgConnectionManagerImpl.updateOrAddIndex");
		return true;
	}

}
