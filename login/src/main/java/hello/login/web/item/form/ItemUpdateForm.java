package hello.login.web.item.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ItemUpdateForm {

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    // 수정할 경우, 수량은 자유롭게 수정이 가능하다.
    private Integer quantity;
}
