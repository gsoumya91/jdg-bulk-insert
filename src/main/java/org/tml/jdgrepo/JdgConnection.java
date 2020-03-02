/*
 * created by Soumya Gupta
 */
package org.tml.jdgrepo;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.configuration.cache.Index;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tml.esb.AccountExtractResponse;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JdgConnection {

	private static String JDG_HOST;
	private static String HOTROD_PORT;

	@Value("${jdg.hostname}")
	public void setJDG_HOST(String jDG_HOST) {
		JDG_HOST = jDG_HOST;
	}

	@Value("${jdg.port}")
	public void setHOTROD_PORT(String hOTROD_PORT) {
		HOTROD_PORT = hOTROD_PORT;
	}

	/*
	 * Connection references
	 */
	private static ConfigurationBuilder builder = new ConfigurationBuilder();
	private static RemoteCacheManager cacheManager;
	private static RemoteCache<String, Object> cache;

	private static Logger logger = LoggerFactory.getLogger(JdgConnection.class);

	/*
	 * Method to get Cache from Cache Manager JDG
	 */
	public static RemoteCache<String, Object> getCache(String cacheName) throws Exception {
		logger.info("Entering method RemoteCache with cache Name:{}", cacheName);
		logger.info("Properties with values {} , {}", JDG_HOST, HOTROD_PORT);

		try {

			builder.addServer().host(JDG_HOST).port(Integer.parseInt(HOTROD_PORT))
					.marshaller(new ProtoStreamMarshaller());
			cacheManager = new RemoteCacheManager(builder.build());

			SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(cacheManager);

			String generatedSchema = null;
			try {
				ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
				generatedSchema = protoSchemaBuilder.fileName("accountExtractResponse.proto").packageName("org.tml.esb")
						.addClass(AccountExtractResponse.class).build(serCtx);

				// register the schemas with the server too
				RemoteCache<String, String> metadataCache = cacheManager
						.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);

				metadataCache.put("accountExtractResponse.proto", generatedSchema);

			} catch (Exception e1) {

				StringBuilder sb = new StringBuilder();
				sb.append("No schema generated because of Exception");
				logger.error(sb.toString(), e1);

			}

			logger.debug(generatedSchema);

			cache = cacheManager.getCache(cacheName);
		} catch (Exception e) {
			logger.error("Exception occured in method RemoteCache with exception :{}", e);
			throw new Exception("Exception occured in method RemoteCache with exception e");
		}

		logger.info("Entering method RemoteCache with cache Name:{}", cacheName);
		return cache;
	}

}
