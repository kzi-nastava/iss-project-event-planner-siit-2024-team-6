package ftn.siit.project.isspoject.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Map;

@Data
@Entity
@DiscriminatorValue("Admin")
public class Admin extends User {
    public void updateFromObject(Map<String, Object> updatedFields) {
        if (updatedFields == null) return;

        // Обновляем поля из суперкласса User
        for (Map.Entry<String, Object> entry : updatedFields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            try {
                // Получаем поле из класса User
                Field field = User.class.getDeclaredField(key);
                field.setAccessible(true);

                // Присваиваем значение, если оно не null
                if (value != null) {
                    field.set(this, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.out.println("problem with field: " + key);
            }
        }
    }
}
