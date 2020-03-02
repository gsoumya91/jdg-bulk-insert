package org.tml.esb;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

public class RemoteCacheManagerFactory {

	ConfigurationBuilder clientBuilder;
	RemoteCacheManager cacheManager;

	public RemoteCacheManagerFactory(String hostname, int port) {
		clientBuilder = new ConfigurationBuilder();
		clientBuilder.addServer().host(hostname).port(port).marshaller(new ProtoStreamMarshaller());
		cacheManager = new RemoteCacheManager(clientBuilder.build());
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

		}
	}

	public RemoteCacheManager newRemoteCacheManager() {
		return cacheManager;
	}

}
