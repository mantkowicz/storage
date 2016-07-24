package com.mantkowicz.storage.platform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mantkowicz.storage.callback.Callback;
import com.mantkowicz.storage.exception.ConditionException;
import com.mantkowicz.storage.exception.TableNameException;
import com.mantkowicz.storage.handler.AbstractStorageHandler;
import com.mantkowicz.storage.operator.Condition;
import com.mantkowicz.storage.operator.ConditionAND;
import com.mantkowicz.storage.operator.ConditionOR;
import com.mantkowicz.storage.operator.Operators;
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
	
	Gson gson;
	
	private HashMap<Class<?>, String> tableNames = new HashMap<>();
	
	public App42Handler(String databaseName, String API_KEY, String SECRET_KEY) {
		super(databaseName);
		this.API_KEY = API_KEY;
		this.SECRET_KEY = SECRET_KEY;
		
		this.gson = new GsonBuilder().create();
	}

	@Override
	public void connect() {
		serviceAPI = new ServiceAPI(this.API_KEY, this.SECRET_KEY); 
    	storageService = serviceAPI.buildStorageService();
	}

	@Override
	public <T> void registerEntity(Class<T> type, String tableName) {
		if(!tableNames.containsKey(type)) {
			tableNames.put(type, tableName);
		}
	}
	
	@Override
	public <T> String insert(T object, Callback callback) {
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
	public <T> List<T> select(Class<T> type, Integer maxRecordCount, Condition condition, Callback callback) {		
		List<T> objects = new LinkedList<>();
		Query query = resolveQuery(condition);
			
		Storage storage = storageService.findDocumentsByQueryWithPaging(this.getDatabaseName(), getTableName(type), query, maxRecordCount, 0);
				
		for(Storage.JSONDocument jsonDocument : storage.getJsonDocList()) {
			objects.add(gson.fromJson(jsonDocument.getJsonDoc(), type));
		}
		
		return objects;
	}

	@Override
	public <T> void update(String objectId, T object, Callback callback) {
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
	
	@Override
	public <T> void remove(Class<T> type, String objectId, Callback callback) {
		storageService.deleteDocumentById(this.getDatabaseName(), getTableName(type), objectId);  
	}
	
	private Query resolveQuery(Condition condition) {
		if(condition instanceof ConditionAND) {
			Query queryA = resolveQuery(((ConditionAND) condition).conditionA);
			Query queryB = resolveQuery(((ConditionAND) condition).conditionB);
						
			return QueryBuilder.compoundOperator(queryA, Operator.AND, queryB);
		}
		else if(condition instanceof ConditionOR) {
			Query queryA = resolveQuery(((ConditionOR) condition).conditionA);
			Query queryB = resolveQuery(((ConditionOR) condition).conditionB);
			
			return QueryBuilder.compoundOperator(queryA, Operator.OR, queryB);
		}
		else {
			if(condition.key == null) {
				throw new ConditionException("Condition's key cannot be null. Did you initialize the condition properly?");
			}
			
			return QueryBuilder.build(condition.key, condition.value, parseOperator(condition.operator));
		}
	}
	
	private Operator parseOperator(Operators operator) {
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
