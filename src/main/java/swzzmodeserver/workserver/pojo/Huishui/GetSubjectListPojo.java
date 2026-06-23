package swzzmodeserver.workserver.pojo.Huishui;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class GetSubjectListPojo {
    private String docid;
    private int sbjID;
    private int timeStepHydro;
    private  int timeStep;
}
