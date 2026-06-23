package swzzmodeserver.workserver.pojo.zjtyphoon;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZJ_LANDPojo {
    private String id;
    private String info;
    private String landaddress;
    private String landtime;
    private Double lat;
    private Double lng;
    private String strong;
    private String tfbh;
}
