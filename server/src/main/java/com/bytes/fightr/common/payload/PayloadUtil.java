package com.bytes.fightr.common.payload;

import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.action.FightResult;
import com.bytes.fightr.common.model.event.ChatEvent;
import com.bytes.fightr.common.model.event.MatchEvent;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fmk.data.event.LoginEvent;
import com.bytes.fmk.data.event.UserEvent;
import com.bytes.fmk.data.model.Message;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.payload.Payload;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PayloadUtil {
	
	private static Logger logger = LoggerFactory.getLogger(PayloadUtil.class);
	private static Gson gson = new Gson();
	
	/**
	 * Convenience method to retrieve the list of strongly typed objects in the Payload 
	 * json-formatted-data
	 * 
	 * @param payload - the payload containing the data
	 * @param type - the data type
	 * @return the strongly typed list
	 */
	public static <D> List<D> getDataList(Payload<String> payload, DataType type) {
		return getDataList(payload.getData(), type);
	}
	
	/**
	 * Convenience method to retrieve the list of strongly typed objects in the Payload 
	 * json-formatted-data
	 * 
	 * @param dataJson - the data
	 * @param type - the data type
	 * @return the strongly typed list
	 */
	public static <D> List<D> getDataList(String dataJson, DataType type) {
		
		switch (type) {
		case User:
			return gson.fromJson(dataJson, new TypeToken<List<User>>(){}.getType());
			
		case Fighter:
			return gson.fromJson(dataJson, new TypeToken<List<Fighter>>(){}.getType());

		case Match:
			return gson.fromJson(dataJson, new TypeToken<List<Match>>(){}.getType());
		
		case String:
			return gson.fromJson(dataJson, new TypeToken<List<String>>(){}.getType());
		
		default:
			break;
		
		}
		
		logger.error("Unknown data type. Unable to convert to data list: " + type);
		return null;
	}

	/**
	 * Convenience method to retrieve the strongly typed objects in the Payload 
	 * json-formatted-data
	 * 
	 * @param payload - the payload containing the data
	 * @param type - the data type
	 * @return the strongly typed object
	 */
	public static <D> D getData(Payload<String> payload, DataType type) {
		return getData(payload.getData(), type);
	}
	
	/**
	 * Convenience method to retrieve the strongly typed objects in the Payload 
	 * json-formatted-data
	 * 
	 * @param dataJson - the data in json format, except when data type is String
	 * @param type - the data type
	 * @return the strongly typed object
	 */
	@SuppressWarnings("unchecked")
	public static <D> D getData(String dataJson, DataType type) {
		
		switch (type) {
		case Fighter:
			return gson.fromJson(dataJson, new TypeToken<Fighter>(){}.getType());

		case Match:
			return gson.fromJson(dataJson, new TypeToken<Match>(){}.getType());
			
		case MatchEvent:
			return gson.fromJson(dataJson, new TypeToken<MatchEvent>(){}.getType());

		case String:
			return (D) dataJson;
			
		case FightAction:
			return gson.fromJson(dataJson, new TypeToken<FightAction>(){}.getType());

		case FightResult:
			return gson.fromJson(dataJson, new TypeToken<FightResult>(){}.getType());
			
		case LoginEvent:
			return gson.fromJson(dataJson, new TypeToken<LoginEvent>(){}.getType());
		
		case User:
			return gson.fromJson(dataJson, new TypeToken<User>(){}.getType());
			
		case Message:
			return gson.fromJson(dataJson, new TypeToken<Message>(){}.getType());
			
		case UserEvent:
			return gson.fromJson(dataJson, new TypeToken<UserEvent>(){}.getType());
			
		case ChatEvent:
			return gson.fromJson(dataJson, new TypeToken<ChatEvent>(){}.getType());
		
		
		case ActionEvent:
		case LatencyCmd:
		case Null:
		case ResetCmd:
		default:
			break;
		
		}
		
		logger.error("Unknown data type. Unable to convert to data object: " + type);
		return null;
	}
	
	/**
	 * Returns the json representation of a {@code Payload<String>}
	 * @param payload - the payload to serialize
	 * @return the json value
	 */
	public static String toJson(Payload<String> payload) {
		return gson.toJson(payload);
	}

	public static Type getClass(String message) {
		return FighterPayload.class;
	}

	/**
	 * @return the gson
	 */
	public static Gson getGson() {
		return gson;
	}
}
