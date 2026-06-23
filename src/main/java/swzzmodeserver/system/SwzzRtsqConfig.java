package swzzmodeserver.system;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"swzzmodeserver.workserver.data.swzzrtsq"},sqlSessionFactoryRef = "swzzrtsqserveSessionFactory")
public class SwzzRtsqConfig {
    @Autowired
    @Qualifier("swzzrtsqDataSource")
    public DataSource dataSource;

    @Bean
    public SqlSessionFactory swzzrtsqserveSessionFactory() throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/work/swzzrtsq/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }
    @Bean
    public SqlSessionTemplate swzzrtsqserveSession() throws Exception{
        SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(swzzrtsqserveSessionFactory());
        return sqlSessionTemplate;
    }
}
