package ftn.siit.project.isspoject.dto.chat;

public class ChatDTO {
    private int id;
    private String name;
    private String photoUrl;

    public ChatDTO() {
    }
    public ChatDTO(int id, String name, String lastName, String photoUrl) {
        this.id = id;
        this.name = name + " " + lastName;
        this.photoUrl = photoUrl;
    }
}
