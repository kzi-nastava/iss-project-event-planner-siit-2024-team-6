package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.dto.chat.ChatDTO;
import ftn.siit.project.isspoject.dto.message.MessageDTO;

import java.util.List;

public class ChatWithMessagesDTO {
    private ChatDTO chat;
    private List<MessageDTO> messages;

    public ChatWithMessagesDTO(ChatDTO chat, List<MessageDTO> messages) {
        this.chat = chat;
        this.messages = messages;
    }
    public ChatDTO getChat() {
        return chat;
    }

    public void setChat(ChatDTO chat) {
        this.chat = chat;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
