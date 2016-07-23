package com.mantkowicz.storage.platform;

import java.util.LinkedList;
import java.util.List;

import com.mantkowicz.storage.callback.Callback;
import com.mantkowicz.storage.handler.AbstractStorageHandler;
import com.mantkowicz.storage.operator.Condition;
import com.mantkowicz.storage.operator.ConditionOperator;
import com.mantkowicz.storage.record.AbstractRecord;
import com.shephertz.app42.paas.sdk.java.ServiceAPI;
import com.shephertz.app42.paas.sdk.java.storage.Query;
import com.shephertz.app42.paas.sdk.java.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.java.storage.QueryBuilder.Operator;
import com.shephertz.app42.paas.sdk.java.storage.Storage;
import com.shephertz.app42.paas.sdk.java.storage.StorageService;

public class App42Handler extends AbstractStorageHandler {

	private ServiceAPI serviceAPI;
	private String API_KEY, SECRET_KEY;
	
	private StorageService storageService;	
	
	public App42Handler(String databaseName, String API_KEY, String SECRET_KEY) {
		super(databaseName);
		this.API_KEY = API_KEY;
		this.SECRET_KEY = SECRET_KEY;
	}

	@Override
	public void connect() {
		serviceAPI = new ServiceAPI(this.API_KEY, this.SECRET_KEY); 
    	storageService = serviceAPI.buildStorageService();
	}

	@Override
	public <T extends AbstractRecord> String insert(T object, Callback callback) {
		String documentId = "";
		
		if(callback != null) {
			storageService.insertJSONDocument(this.getDatabaseName(), object.getTableName(), object.toJson(), callback);
		}
		else {
			Storage storage = storageService.insertJSONDocument(this.getDatabaseName(), object.getTableName(), object.toJson());
			documentId = storage.getJsonDocList().get(0).getDocId();
		}
		
		return documentId;
	}

	@Override
	public <T extends AbstractRecord> List<T> select(List<T> objects, List<Condition> conditions, Callback callback) {
		List<Query> queries = new LinkedList<Query>();
		
		for(Condition condition : conditions) {
			queries.add(QueryBuilder.build(condition.key, condition.value, parseOperator(condition.operator)));
		}
		
		//FIXME to check whether the queries list is not null and to handle this properly
		if(queries.size() == 0) {
			return null;
		}
		
		Query query = queries.get(0);
		
		if(queries.size() > 1) {			
			for(int i = 1; i < queries.size(); i++) {
				query = QueryBuilder.compoundOperator(query, Operator.AND, queries.get(i));
			}
		}
		
		//FIXME to check whether the objects list is not null and to handle this properly
		if(objects.size() == 0) {
			return null;
		}
		
		int max = objects.size();
		
		Storage storage = storageService.findDocumentsByQueryWithPaging(this.getDatabaseName(), objects.get(0).getTableName(), query, max, 0);
		List<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();
		
		for(int i=0; i<jsonDocList.size(); i++) {
			String jsonString = jsonDocList.get(i).getJsonDoc();
			objects.get(i).loadJson(jsonString); 
		}
		
		return objects;
	}

	@Override
	public <T extends AbstractRecord> void update(Long objectId, T object, Callback callback) {
		// TODO Auto-generated method stub
		
	}
	
	private Operator parseOperator(ConditionOperator operator) {
		switch(operator) {
			case EQ:
				return Operator.EQUALS;
			case NE:
				return Operator.NOT_EQUALS;
			case GE:
				return Operator.GREATER_THAN_EQUALTO;
			case GT:
				return Operator.GREATER_THAN;
			case LE:
				return Operator.LESS_THAN_EQUALTO;
			case LT:
				return Operator.LESS_THAN;
			case LIKE:
				return Operator.LIKE;
			default:
				return Operator.EQUALS;
		}
	}
}
