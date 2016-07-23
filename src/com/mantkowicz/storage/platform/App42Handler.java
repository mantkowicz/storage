package com.mantkowicz.storage.platform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mantkowicz.storage.callback.Callback;
import com.mantkowicz.storage.exception.TableNameException;
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
	
	private HashMap<Class<?>, String> tableNames = new HashMap<>();
	
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
	public <T extends AbstractRecord> void registerEntity(Class<T> type, String tableName) {
		if(!tableNames.containsKey(type)) {
			tableNames.put(type, tableName);
		}
	}
	
	@Override
	public <T extends AbstractRecord> String insert(T object, Callback callback) {
		Gson gson = new GsonBuilder().serializeNulls().create();
		String documentId = "";
		
		if(callback != null) {
			storageService.insertJSONDocument(this.getDatabaseName(), getTableName(object.getClass()), gson.toJson(object), callback);
		}
		else {
			Storage storage = storageService.insertJSONDocument(this.getDatabaseName(), getTableName(object.getClass()), gson.toJson(object));
			documentId = storage.getJsonDocList().get(0).getDocId();
		}
		
		return documentId;
	}

	@Override
	public <T extends AbstractRecord> List<T> select(Class<T> type, Integer maxRecordCount, List<Condition> conditions, Callback callback) {
		Gson gson = new GsonBuilder().serializeNulls().create();
		
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
		
		List<T> objects = new LinkedList<>();
		
		Storage storage = storageService.findDocumentsByQueryWithPaging(this.getDatabaseName(), getTableName(type), query, maxRecordCount, 0);
				
		for(Storage.JSONDocument jsonDocument : storage.getJsonDocList()) {
			objects.add(gson.fromJson(jsonDocument.getJsonDoc(), type));
		}
		
		return objects;
	}

	@Override
	public <T extends AbstractRecord> void update(String objectId, T object, Callback callback) {
		Gson gson = new GsonBuilder().create(); //Do not serialize null fields
		JSONObject jsonUpdateObject = null;
		
		System.out.println(gson.toJson(object));
		
		try {
			jsonUpdateObject = new JSONObject( gson.toJson(object) );
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
				
		try {
			jsonUpdateObject = new JSONObject(gson.toJson(object));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		storageService.addOrUpdateKeys(this.getDatabaseName(), getTableName(object.getClass()), objectId, jsonUpdateObject);
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
	
	private String getTableName(Class<?> type) {
		String tableName = tableNames.get(type);
		
		if(tableName == null) {
			throw new TableNameException("Entity " + type.getSimpleName() + " has not been registered yet. Use registerEntity() method firstly.");
		}
		else {
			return tableName;
		}
	}
}
