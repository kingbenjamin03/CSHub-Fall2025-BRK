package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.UserMessage;
import models.rest.RESTResponse;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserMessageService;
import utils.Common;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class UserMessageController extends Controller {

    private final UserMessageService userMessageService;

    @Inject
    public UserMessageController(UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    public Result addMessage() {
        JsonNode json = request().body().asJson();
        if (json == null) return badRequest("Expecting Json data");
        
        Long authorId = json.findPath("authorId").asLong();
        String content = json.findPath("content").asText();
        
        if (content == null || content.isEmpty()) {
            return badRequest("Content cannot be empty");
        }

        UserMessage message = userMessageService.createMessage(authorId, content);
        return ok(Json.toJson(message));
    }

    public Result updateMessage() {
        JsonNode json = request().body().asJson();
        if (json == null) return badRequest("Expecting Json data");

        Long id = json.findPath("id").asLong();
        String content = json.findPath("content").asText();

        UserMessage message = userMessageService.updateMessage(id, content);
        if (message == null) return notFound("Message not found");
        
        return ok(Json.toJson(message));
    }

    public Result messageDetail(Long id) {
        UserMessage message = userMessageService.findById(id);
        if (message == null) return notFound("Message not found");
        return ok(Json.toJson(message));
    }

    public Result messageList(Optional<Integer> pageLimit, Optional<Integer> offset, Optional<String> sortCriteria) {
        List<UserMessage> messages = UserMessage.find.query().where().eq("isActive", "True").orderBy("createTime desc").findList();
        RESTResponse response = userMessageService.paginateResults(messages, offset, pageLimit, sortCriteria.orElse("createTime"));
        return ok(response.response());
    }

    public Result deleteMessage() {
        JsonNode json = request().body().asJson();
        if (json == null) return badRequest("Expecting Json data");
        
        Long id = json.findPath("id").asLong();
        userMessageService.deleteMessage(id);
        return ok("Message deleted");
    }

    public Result searchMessages() {
        JsonNode json = request().body().asJson();
        if (json == null) return badRequest("Expecting Json data");
        
        String keyword = json.findPath("keyword").asText();
        List<UserMessage> messages = UserMessage.find.query()
            .where()
            .eq("isActive", "True")
            .icontains("content", keyword)
            .orderBy("createTime desc")
            .findList();
            
        return ok(userMessageService.messageList2JsonArray(messages));
    }

    public Result messageListByAuthor(Long authorId, Optional<Integer> pageLimit, Optional<Integer> offset, Optional<String> sortCriteria) {
        List<UserMessage> messages = UserMessage.find.query()
            .where()
            .eq("authorId", authorId)
            .eq("isActive", "True")
            .orderBy("createTime desc")
            .findList();
        RESTResponse response = userMessageService.paginateResults(messages, offset, pageLimit, sortCriteria.orElse("createTime"));
        return ok(response.response());
    }
}
