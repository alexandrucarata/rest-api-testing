package data;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class User {
    private String name;
    private Integer age;
    private String sex;
    private String zipCode;
}