package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Entity
@DiscriminatorValue("Organizer")
@EqualsAndHashCode(callSuper = true)
@Data
public class Organizer extends User {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "organizer_id")
    private List<Event> myEvents;

    public boolean hasFutureEvents() {
        return false;
    }

    public void updateFromObject(Map<String, Object> updatedFields) {
        if (updatedFields == null) return;

        // Обновляем поля из суперкласса User
        updateUserFields(updatedFields);

        // Обновляем поля из текущего класса Organizer
        for (Map.Entry<String, Object> entry : updatedFields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Поле myEvents пропускаем, так как оно сложно обновляется
            if ("myEvents".equals(key)) {
                continue;
            }

            try {
                // Обновляем поля класса Organizer
                Field field = this.getClass().getDeclaredField(key);
                field.setAccessible(true);
                field.set(this, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.out.println("cannot update field: " + key);
            }
        }
    }

    private void updateUserFields(Map<String, Object> updatedFields) {
        for (Map.Entry<String, Object> entry : updatedFields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            try {
                // Обновляем поля суперкласса User
                Field field = User.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(this, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Поле отсутствует в User - пропускаем
            }
        }
    }
}

