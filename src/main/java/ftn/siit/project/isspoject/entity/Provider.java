package ftn.siit.project.isspoject.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Entity
@DiscriminatorValue("Provider")
@EqualsAndHashCode(callSuper = true)
@Data
public class Provider extends User {

    private String companyEmail;

    private String companyName;

    private String companyAddress;
    private String description;

    @ElementCollection
    @CollectionTable(name = "provider_company_photos", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "photo_url")
    private List<String> companyPhotos;

    private String openingTime;
    private String closingTime;

    public boolean hasActiveServices() {
        return false;
    }
    public void updateFromObject(Map<String, Object> updatedFields) {
        if (updatedFields == null) return;

        // Обновляем поля из суперкласса User
        updateUserFields(updatedFields);

        // Обновляем поля из текущего класса Provider
        for (Map.Entry<String, Object> entry : updatedFields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Пропускаем companyName и companyEmail
            if ("companyName".equals(key) || "companyEmail".equals(key)) {
                continue;
            }

            try {
                // Обновляем поля класса Provider
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

