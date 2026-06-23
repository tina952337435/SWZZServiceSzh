package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MigrationHistoryPojo {
    private String MigrationId;
    private String ContextKey;
    private byte[] Model;
    private String ProductVersion;
}
