package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class UserMessage {
    private Long id;
    private Long authorId;
    private String content;
    private String createTime;
    private String updateTime;
    private String isActive;
    private String authorName;
    private String authorAvatar;

    public UserMessage() {}

    public static UserMessage deserialize(JsonNode node) {
        UserMessage message = new UserMessage();
        if (node.has("id")) message.setId(node.get("id").asLong());
        if (node.has("authorId")) message.setAuthorId(node.get("authorId").asLong());
        if (node.has("content")) message.setContent(node.get("content").asText());
        if (node.has("createTime")) message.setCreateTime(node.get("createTime").asText());
        if (node.has("updateTime")) message.setUpdateTime(node.get("updateTime").asText());
        if (node.has("isActive")) message.setIsActive(node.get("isActive").asText());
        if (node.has("authorName")) message.setAuthorName(node.get("authorName").asText());
        if (node.has("authorAvatar")) message.setAuthorAvatar(node.get("authorAvatar").asText());
        return message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }
}
