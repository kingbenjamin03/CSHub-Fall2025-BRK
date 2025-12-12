package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.UserMessage;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserMessageService;
import views.html.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static controllers.Application.checkLoginStatus;

public class UserMessageController extends Controller {

    private final UserMessageService userMessageService;
    private final FormFactory formFactory;

    @Inject
    public UserMessageController(UserMessageService userMessageService, FormFactory formFactory) {
        this.userMessageService = userMessageService;
        this.formFactory = formFactory;
    }

    public Result listPage(Integer page) {
        checkLoginStatus();
        try {
            JsonNode response = userMessageService.getMessageList(page, 10);
            List<UserMessage> messages = new ArrayList<>();
            if (response != null && response.has("items")) {
                for (JsonNode node : response.get("items")) {
                    messages.add(UserMessage.deserialize(node));
                }
            }
            int total = response != null && response.has("total") ? response.get("total").asInt() : 0;
            return ok(userMessageList.render(messages, page, total));
        } catch (Exception e) {
            Logger.error("Error in listPage: " + e.getMessage());
            return internalServerError("Error fetching messages");
        }
    }

    public Result detail(Long id) {
        checkLoginStatus();
        try {
            JsonNode response = userMessageService.getMessageDetail(id);
            if (response == null || response.has("error")) {
                return notFound("Message not found");
            }
            UserMessage message = UserMessage.deserialize(response);
            return ok(userMessageDetail.render(message));
        } catch (Exception e) {
            Logger.error("Error in detail: " + e.getMessage());
            return internalServerError("Error fetching message detail");
        }
    }

    public Result newPage() {
        checkLoginStatus();
        return ok(userMessageNew.render());
    }

    public Result createFromForm() {
        checkLoginStatus();
        Form<UserMessage> form = formFactory.form(UserMessage.class).bindFromRequest();
        String content = form.field("content").value();
        Long authorId = Long.parseLong(session("id"));
        
        userMessageService.addMessage(authorId, content);
        return redirect(routes.UserMessageController.listPage(1));
    }

    public Result editPage(Long id) {
        checkLoginStatus();
        JsonNode response = userMessageService.getMessageDetail(id);
        UserMessage message = UserMessage.deserialize(response);
        return ok(userMessageEdit.render(message));
    }

    public Result updateFromForm(Long id) {
        checkLoginStatus();
        Form<UserMessage> form = formFactory.form(UserMessage.class).bindFromRequest();
        String content = form.field("content").value();
        
        userMessageService.updateMessage(id, content);
        return redirect(routes.UserMessageController.detail(id));
    }

    public Result deleteFromForm(Long id) {
        checkLoginStatus();
        userMessageService.deleteMessage(id);
        return redirect(routes.UserMessageController.listPage(1));
    }

    public Result searchPage() {
        checkLoginStatus();
        return ok(userMessageSearch.render(new ArrayList<>()));
    }

    public Result searchPOST() {
        checkLoginStatus();
        Form<Object> form = formFactory.form(Object.class).bindFromRequest();
        String keyword = form.field("keyword").value();
        
        JsonNode response = userMessageService.searchMessages(keyword);
        List<UserMessage> messages = new ArrayList<>();
        if (response != null && response.isArray()) {
            for (JsonNode node : response) {
                messages.add(UserMessage.deserialize(node));
            }
        }
        return ok(userMessageSearch.render(messages));
    }

    public Result myMessagesPage(Integer page) {
        checkLoginStatus();
        try {
            Long authorId = Long.parseLong(session("id"));
            JsonNode response = userMessageService.getMessageListByAuthor(authorId, page, 10);
            List<UserMessage> messages = new ArrayList<>();
            if (response != null && response.has("items")) {
                for (JsonNode node : response.get("items")) {
                    messages.add(UserMessage.deserialize(node));
                }
            }
            int total = response != null && response.has("total") ? response.get("total").asInt() : 0;
            return ok(userMessageList.render(messages, page, total));
        } catch (Exception e) {
            Logger.error("Error in myMessagesPage: " + e.getMessage());
            return internalServerError("Error fetching your messages");
        }
    }
}
