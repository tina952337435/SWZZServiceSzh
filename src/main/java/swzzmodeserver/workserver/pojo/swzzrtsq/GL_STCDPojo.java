package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GL_STCDPojo {
    /**
     *主键id
     */
    private String ID;
    /**
     *测站编码
     */
    private String STCD;
    /**
     *路径名称
     */
    private String NAME;
    /**
     *路径地址
     */
    private String URL;
    /**
     *排序
     */
    private Double ORDERBY;
    /**
     *类型（站点配置、站点关联配置（STCD不用填，为主记录链接地址配置））
     */
    private String TYPE;
    /**
     *备用字段
     */
    private String STTP;

}
