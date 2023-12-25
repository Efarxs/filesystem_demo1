package exam.gcc.component;

import exam.gcc.business.service.FileService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;


/*初始化监听器，初始化系统相关数据*/
@Component("init")
public class InitDataListener implements InitializingBean, ServletContextAware {

	@Autowired
	private FileService fileService;
	@Override
	public void setServletContext(ServletContext arg0) {
		fileService.checkFileExist();
	}

	@Override
	public void afterPropertiesSet() {
		// TODO Auto-generated method stub
	}
}
