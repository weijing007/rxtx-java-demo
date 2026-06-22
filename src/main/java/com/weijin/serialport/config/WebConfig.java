package com.weijin.serialport.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource(value = "classpath:application.properties", encoding = "utf-8")
public class WebConfig  implements WebMvcConfigurer  {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	@Value("${file.uploadFolder}")
	private String fileSavePath;

	@Value("${file.staticAccessPath}")
	private String filestaticAccessPath;

	/**
	 *
	 * 简要说明：允许跨域支持
	 *
	 * 创建时间：2019年5月5日 下午9:32:53
	 *
	 * @param 说明
	 * @return 说明
	 */
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.setAllowCredentials(true);
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/**
		 * 配置静态资源映射 意思是：如果访问的资源路径是以“/static”开头的， 就给我映射到本机的“E:/static/”这个文件夹内，去找你要的资源
		 * 注意：E:/static/ 后面的 “/”一定要带上
		 */
		 logger.info("Mapping accessible paths：" + fileSavePath);
		 registry.addResourceHandler("/webapi/**").addResourceLocations("file:" + filestaticAccessPath);
		 registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").addResourceLocations("classpath:/META-INF/resources/").setCachePeriod(0);
         //这里是对静态资
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// addPathPatterns 用于添加拦截规则
		// excludePathPatterns 用户排除拦截,发送短信，登录等不允许拦截
		//registry.addInterceptor(new MyWebApiInterceptor()).excludePathPatterns("/**");
		WebMvcConfigurer.super.addInterceptors(registry);
	}

}