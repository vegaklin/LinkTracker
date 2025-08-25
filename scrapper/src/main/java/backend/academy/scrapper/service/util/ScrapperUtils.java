package backend.academy.scrapper.service.util;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ScrapperUtils {

    public static List<String> parseResultSetArray(Array rsArray) {
        try {
            return rsArray != null ? Arrays.asList((String[]) rsArray.getArray()) : List.of();
        } catch (SQLException ex) {
            return List.of();
        }
    }
}
