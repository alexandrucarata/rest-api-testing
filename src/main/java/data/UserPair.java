package data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPair {
    private User userNewValues;
    private User userToChange;
}