package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_WATERSTORAGE_BPojo {
    private String ID;
    private Double UPZ;
    private Double AREA;
    private Double S;
    private String TYPE;
    private String NOTE;
}