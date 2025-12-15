package iits.workshop.htmx.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Make implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Make name is required")
    private String name;

    @OneToMany(mappedBy = "make", cascade = CascadeType.ALL)
    private List<CarModel> models = new ArrayList<>();

    public Make(String name) {
        this.name = name;
    }
}
