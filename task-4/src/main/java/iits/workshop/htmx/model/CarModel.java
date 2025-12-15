package iits.workshop.htmx.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Model name is required")
    private String name;

    @ManyToOne
    @JoinColumn(name = "make_id", nullable = false)
    @NotNull(message = "Make is required")
    private Make make;

    public CarModel(String name, Make make) {
        this.name = name;
        this.make = make;
    }
}
