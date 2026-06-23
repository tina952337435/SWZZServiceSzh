package swzzmodeserver.workserver.pojo.Huishui;

import lombok.Data;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
@Data
public class GetAreaXSLPojo {
    private String id;
    private String name;
    private Double upz;//当前水位
    private BigDecimal xsl;//当前蓄水量
    private BigDecimal zuoxsl;//昨日蓄水量
    private BigDecimal jxsl;//距警戒水位还有多少蓄水量
    private BigDecimal bxsl;//距保证水位还有多少蓄水量
    private BigDecimal wrzxsl;//距警戒水位对应蓄水量
    private BigDecimal grzxsl;//距保证水位对应蓄水量
    private Double yl;//当前纳雨能力
    private String tm;//时间
}
