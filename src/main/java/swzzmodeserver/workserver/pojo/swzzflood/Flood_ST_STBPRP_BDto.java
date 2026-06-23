package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Flood_ST_STBPRP_BDto {
    private String STCD;//
    private String STNM;//
    private String RVNM;//
    private String HNNM;//
    private String BSNM;//
    private Double LGTD;//
    private Double LTTD;//
    private String ZSTCD;//
    private String TYPE;
    private String XZDZ;
}
