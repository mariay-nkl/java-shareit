package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {
    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String text;
}
