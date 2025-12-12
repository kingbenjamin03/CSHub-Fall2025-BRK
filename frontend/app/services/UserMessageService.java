package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import models.UserMessage;
import play.Logger;
import play.libs.Json;
import utils.RESTfulCalls;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UserMessageService {
    @Inject
    Config config;

    public JsonNode addMessage(Long authorId, String content) {
        ObjectNode jsonData = Json.newObject();
        jsonData.put("authorId", authorId);
        jsonData.put("content", content);
        return RESTfulCalls.postAPI(RESTfulCalls.getBackendAPIUrl(config, "/message/addMessage"), jsonData);
    }

    public JsonNode updateMessage(Long id, String content) {
        ObjectNode jsonData = Json.newObject();
        jsonData.put("id", id);
        jsonData.put("content", content);
        return RESTfulCalls.postAPI(RESTfulCalls.getBackendAPIUrl(config, "/message/updateMessage"), jsonData);
    }

    public JsonNode getMessageDetail(Long id) {
        return RESTfulCalls.getAPI(RESTfulCalls.getBackendAPIUrl(config, "/message/messageDetail/" + id));
    }

    public JsonNode getMessageList(int page, int pageLimit) {
        int offset = (page - 1) * pageLimit;
        return RESTfulCalls.getAPI(RESTfulCalls.getBackendAPIUrl(config, "/message/messageList?offset=" + offset + "&pageLimit=" + pageLimit));
    }

    public JsonNode deleteMessage(Long id) {
        ObjectNode jsonData = Json.newObject();
        jsonData.put("id", id);
        return RESTfulCalls.postAPI(RESTfulCalls.getBackendAPIUrl(config, "/message/deleteMessage"), jsonData);
    }

    public JsonNode searchMessages(String keyword) {
        ObjectNode jsonData = Json.newObject();
        jsonData.put("keyword", keyword);
        return RESTfulCalls.postAPI(RESTfulCalls.getBackendAPIUrl(config, "/message/searchMessages"), jsonData);
    }
}
