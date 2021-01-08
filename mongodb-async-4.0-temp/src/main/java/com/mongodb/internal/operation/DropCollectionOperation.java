package com.mongodb.internal.operation;

import com.mongodb.MongoNamespace;
import com.mongodb.ServerAddress;
import com.mongodb.internal.async.SingleResultCallback;
import com.mongodb.internal.binding.AsyncWriteBinding;
import com.mongodb.internal.binding.WriteBinding;
import com.newrelic.agent.bridge.datastore.DatastoreVendor;
import com.newrelic.api.agent.DatastoreParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.agent.mongo.MongoUtil;

@Weave
public abstract class DropCollectionOperation {

	private final MongoNamespace namespace = Weaver.callOriginal();

	@Trace
	public Void execute(WriteBinding binding) {
		if(!MongoUtil.initialized) {
			MongoUtil.init();
		}
		ServerAddress address = binding.getWriteConnectionSource().getServerDescription().getAddress();
		DatastoreParameters params = DatastoreParameters
				.product(DatastoreVendor.MongoDB.name())
				.collection(namespace.getCollectionName())
				.operation(MongoUtil.OP_DROP_COLLECTION)
				.instance(address.getHost(), address.getPort())
				.databaseName(namespace.getDatabaseName())
				.build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		return Weaver.callOriginal();
	}

	@Trace
	public void executeAsync(AsyncWriteBinding binding, SingleResultCallback<Void> callback) {
		if(!MongoUtil.initialized) {
			MongoUtil.init();
		}
		if(binding.operationName == null) {
			binding.operationName = MongoUtil.OP_DROP_COLLECTION;
		}
		if(binding.namespace == null) {
			binding.namespace = namespace;
		}
		if(callback.token == null) {
			callback.token = NewRelic.getAgent().getTransaction().getToken();
		}
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","AsyncWriteOperation",getClass().getSimpleName(),"executeAsync"});
		Weaver.callOriginal();

	}
}
