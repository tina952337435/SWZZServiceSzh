package swzzmodeserver.workserver.pojo.swzzzjk;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class WindyParam {
    private String tm;
    private String wind;
    private String WINDDIRNAME;
    private String BPRESSURE;
}
