package ftn.siit.project.isspoject.dto.budget;

import lombok.Data;

import java.util.List;

@Data
public class NewBudgetDTO {
    private Integer id;
    private List<NewBudgetItemDTO> budgetItems;
}
