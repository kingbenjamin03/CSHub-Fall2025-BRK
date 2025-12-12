package services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.UserMessage;
import models.User;
import models.rest.RESTResponse;
import play.libs.Json;
import utils.Common;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.text.SimpleDateFormat;

@Singleton
public class UserMessageService {

    public UserMessage createMessage(Long authorId, String content) {
        UserMessage message = new UserMessage();
        message.setAuthorId(authorId);
        message.setContent(content);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        message.setCreateTime(now);
        message.setUpdateTime(now);
        message.setIsActive("True");
        message.save();
        return message;
    }

    public UserMessage updateMessage(Long id, String content) {
        UserMessage message = UserMessage.find.byId(id);
        if (message != null) {
            message.setContent(content);
            message.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            message.save();
        }
        return message;
    }

    public UserMessage findById(Long id) {
        return UserMessage.find.byId(id);
    }

    public void deleteMessage(Long id) {
        UserMessage message = UserMessage.find.byId(id);
        if (message != null) {
            message.setIsActive("False");
            message.save();
        }
    }

    public RESTResponse paginateResults(List<UserMessage> messages, Optional<Integer> offset, Optional<Integer> pageLimit, String sortCriteria) {
        RESTResponse response = new RESTResponse();
        int maxRows = messages.size();
        if (pageLimit.isPresent()) {
            maxRows = pageLimit.get();
        }
        int startIndex = 0;
        if (offset.isPresent()) {
            startIndex = offset.get();
        }
        if (startIndex >= messages.size() && messages.size() > 0)
            startIndex = pageLimit.get() * ((messages.size() - 1) / pageLimit.get());
        
        List<UserMessage> paginatedMessages = Common.paginate(startIndex, maxRows, messages);
        response.setTotal(messages.size());
        response.setSort(sortCriteria);
        response.setOffset(startIndex);
        response.setResponse(messageList2JsonArray(paginatedMessages));
        return response;
    }

    public ArrayNode messageList2JsonArray(List<UserMessage> messages) {
        ArrayNode arrayNode = Json.newArray();
        for (UserMessage message : messages) {
            ObjectNode node = Json.newObject();
            node.put("id", message.getId());
            node.put("authorId", message.getAuthorId());
            node.put("content", message.getContent());
            node.put("createTime", message.getCreateTime());
            node.put("updateTime", message.getUpdateTime());
            node.put("isActive", message.getIsActive());
            
            User author = User.find.byId(message.getAuthorId());
            if (author != null) {
                node.put("authorName", author.getUserName());
                node.put("authorAvatar", author.getAvatar());
            }
            arrayNode.add(node);
        }
        return arrayNode;
    }
}
