/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 *
 * @author Kerolos Raouf
 */
public class JSONParser {
    
    private static ObjectMapper mapper;

    static
    {
        mapper = new ObjectMapper();
    }

    public static PlayerMessageBody convertFromJSONToPlayerMessageBody(String JSONString) throws JsonProcessingException
    {
        return mapper.readValue(JSONString,PlayerMessageBody.class);
    }
    

    public static String convertFromPlayerMessageBodyToJSON(PlayerMessageBody playerMessage) throws JsonProcessingException
    {
        return mapper.writeValueAsString(playerMessage);
    }

}
