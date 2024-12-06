package cleverton.heusner.shared.constant.validation;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MovieValidation {

    public static final byte ISAN_SIZE = 26;
    public static final byte TITLE_MIN_SIZE = 1;
    public static final byte TITLE_MAX_SIZE = 40;
    public static final byte YEAR_SIZE = 4;
}
