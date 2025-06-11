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
    // Add getters (and setters if needed)
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
